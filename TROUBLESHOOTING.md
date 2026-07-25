# 트러블슈팅 기록

프로젝트 진행 중 겪은 이슈와 해결 과정을 기록합니다. 단순 에러 로그가 아니라
"왜 발생했는지 → 어떻게 원인을 좁혔는지 → 어떤 해결책을 택했고 왜인지"를 남겨
면접에서 트러블슈팅 경험을 구체적으로 설명할 수 있도록 정리합니다.

---

## 2026-07-25: 한글 데이터 INSERT 시 SQLSyntaxErrorException

### 증상

게시글 등록 API(`POST /api/boards`)에 한글 제목을 넣고 호출하면 500 에러 발생.

```
java.sql.SQLSyntaxErrorException: (conn=11) Incorrect string value: '\xEC\xB2\xAB \xEA\xB2...'
for column `board_db`.`board`.`title` at row 1
```

영문 제목으로는 정상 등록됨. 한글 포함 시에만 실패.

### 원인 분석

1. 에러 메시지의 `Incorrect string value`는 MySQL/MariaDB 계열에서 **컬럼이 지원하지
   않는 문자 인코딩의 값이 들어올 때** 나오는 전형적인 메시지라는 걸 확인
2. `\xEC\xB2\xAB`는 한글 "첫"의 UTF-8 바이트인데, 이걸 DB가 못 받아들인다는 건
   테이블 컬럼의 문자셋이 UTF-8 계열(utf8mb4)이 아니라는 뜻으로 추정
3. `sql/board_mariadb.sql`의 `CREATE TABLE`문을 확인해보니 문자셋을 명시하지 않았고,
   MariaDB 설치 시 기본 문자셋이 `latin1`(1바이트 문자만 지원)으로 되어 있어서
   테이블이 latin1로 생성된 것이 원인으로 확인됨
4. 즉 애플리케이션 코드(Controller/Service/Mapper) 문제가 아니라, **DDL 작성 시
   문자셋을 명시하지 않아 생긴 DB 스키마 레벨의 문제**

### 해결

기존 테이블의 문자셋을 utf8mb4로 변경:

```sql
ALTER TABLE board CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE board_file CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

추가로 JDBC 커넥션 자체도 UTF-8을 명시하도록 `application.yml`의 URL에 옵션 추가:

```
jdbc:mariadb://localhost:3306/board_db?useUnicode=true&characterEncoding=UTF-8
```

### 재발 방지

- `sql/board_mariadb.sql`의 `CREATE TABLE`문에 `DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci`를 명시적으로 추가해서, 이후 이 스크립트로 테이블을
  다시 만들어도 같은 문제가 재발하지 않도록 수정
- 향후 다른 테이블(회원, 첨부파일 등) 추가 시에도 DDL 작성 단계에서 문자셋 명시를
  체크리스트화

### 배운 점

- DB 서버/클라이언트/애플리케이션 3단 중 어느 하나만 UTF-8이어도 다른 하나가
  아니면 인코딩 문제가 발생할 수 있음 (Connection 문자셋, DB/테이블 문자셋,
  애플리케이션 문자셋을 모두 확인해야 함)
- 에러 메시지에 포함된 16진수 바이트 값(`\xEC\xB2\xAB`)을 UTF-8로 직접 디코딩해서
  실제 어떤 글자였는지 역추적한 것이, "테이블 문자셋 문제"라는 가설을 세우는 데
  결정적 단서가 됨

---

## 2026-07-25: 브루트포스 방어 로직에서 로그인 이력/계정 잠금이 DB에 반영되지 않음

### 증상

로그인을 5회 실패하면 API 응답으로는 "로그인 실패 횟수를 초과해 계정이 잠겼습니다"
메시지가 정상적으로 오는데, 실제 DB를 확인하면:
- `login_history` 테이블에 실패 기록이 쌓이지 않음
- `member` 테이블의 `enabled` 값이 `0`으로 바뀌지 않음

API 응답(메시지)과 DB 상태가 서로 다른, 겉으로는 "동작하는 것처럼 보이지만
실제로는 아무 것도 저장되지 않는" 상태였음.

### 원인 분석

1. `AuthService.login()` 메서드에 `@Transactional`이 붙어 있었고, 로그인 실패 시
   `IllegalArgumentException` 또는 `AccountLockedException`을 던지도록 구현되어 있었음
2. Spring의 `@Transactional`은 기본적으로 **RuntimeException이 메서드 밖으로 전파되면
   해당 트랜잭션 안에서 이뤄진 모든 DB 변경을 자동으로 롤백**한다는 사실을 놓치고 있었음
3. 로직 흐름상 "로그인 이력 INSERT → (5회째면) 계정 잠금 UPDATE → 실패를 알리기 위해
   예외 throw" 순서였는데, 마지막의 `throw`가 앞의 INSERT/UPDATE까지 전부 롤백시키고 있었음
4. 즉 "로그인 실패"라는 정상적인 비즈니스 흐름을 트랜잭션 매니저가 "시스템 오류"로
   오인해서 되돌려버린 것 — 예외 자체는 의도한 설계(실패를 Controller에 알리기 위함)였지만,
   그 부수효과로 감사 기록(audit log)까지 함께 사라진 게 문제

### 해결

`@Transactional(noRollbackFor = {...})`로, 이 두 예외가 발생해도 롤백하지 않도록 명시:

```java
@Transactional(noRollbackFor = {IllegalArgumentException.class, AccountLockedException.class})
public LoginResponse login(LoginRequest request, HttpServletRequest httpRequest) {
    ...
}
```

이렇게 하면 예외는 여전히 Controller까지 전파되어 사용자에게는 동일하게 실패 응답이
가지만, 그 과정에서 기록한 로그인 이력과 계정 잠금 상태는 커밋된 채로 유지됨.

### 재발 방지

- `@Transactional`을 붙일 때마다 "이 메서드가 예외를 던질 수 있는 경우, 그 예외가
  '정상적인 실패 흐름'인지 '진짜 시스템 오류'인지"를 구분해서 롤백 정책을 명시적으로
  결정하는 것을 체크리스트화
- 특히 "실패도 기록으로 남겨야 하는" 감사 로그/이력성 데이터는 예외 발생 여부와
  무관하게 커밋되어야 하므로, 이런 메서드는 `noRollbackFor` 지정이 거의 필수라는 점을
  설계 단계에서부터 염두에 둘 것

### 배운 점

- `@Transactional`의 기본 롤백 정책(RuntimeException 발생 시 전체 롤백)은
  "모 아니면 도" 방식이라, 예외가 나더라도 일부 변경은 남겨야 하는 경우(로그, 이력,
  카운터 등)에는 반드시 `noRollbackFor`/`rollbackFor`로 세밀하게 제어해야 함
- API 응답이 정상적으로 오는 것과 DB에 실제로 반영되는 것은 별개 문제일 수 있다는 것을
  직접 겪음 — 트랜잭션 경계 안에서 무슨 일이 일어나는지 항상 의식해야 함
