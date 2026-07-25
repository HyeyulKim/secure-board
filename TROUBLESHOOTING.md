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
