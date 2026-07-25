# 1단계: 게시판 CRUD 기본 골격

## 포함된 것
- `sql/` — Oracle용, MariaDB용 DDL (둘 다 실행 가능하게 분리)
- `vo/BoardVO.java` — DB 매핑 전용 Entity
- `dto/BoardDTO.java` — API 요청/응답 전용 DTO (VO와 분리, 상호 변환 메서드 포함)
- `mapper/BoardMapper.java` + `resources/mapper/BoardMapper.xml` — MyBatis 매퍼 (resultMap으로 객체 매핑)
- `service/BoardService.java`, `BoardServiceImpl.java` — 생성자 주입(DI), `@Transactional` 적용
- `controller/BoardController.java` — `@RestController`로 JSON API 제공 (React와 바로 연동 가능)

## 지금 바로 할 수 있는 것
1. 로컬 DB에 `sql/board_mariadb.sql` (또는 Oracle용) 실행해서 테이블 생성
2. Spring Boot 프로젝트에 이 패키지들을 그대로 붙여넣기 (`pom.xml`에 mybatis-spring-boot-starter, DB 드라이버 의존성 추가 필요)
3. `application.yml`에 DB 커넥션 정보 + MyBatis mapper-locations 설정
4. Postman으로 `GET/POST/PUT/DELETE /api/boards` 호출 테스트

## 다음 단계 (2단계)
- 지금 이 코드는 이미 `@RestController` + JSON 구조라서 2단계(REST API 전환)가 사실상 끝나있는 상태
- 3단계로 바로 넘어가서 React 프론트에서 이 API를 Axios로 호출하는 화면을 만들면 됨

## 확인이 필요한 부분
- `BoardMapper.xml`의 `insert` 구문은 현재 MariaDB(`useGeneratedKeys`) 기준. Oracle로 개발한다면
  XML 안에 주석 처리된 `<selectKey>` 버전으로 교체해야 함
