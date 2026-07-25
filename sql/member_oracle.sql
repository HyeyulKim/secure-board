-- 회원 테이블
CREATE TABLE member (
    member_no   NUMBER          PRIMARY KEY,
    username    VARCHAR2(50)    NOT NULL UNIQUE,
    password    VARCHAR2(200)   NOT NULL,   -- BCrypt 암호화된 값 저장
    nickname    VARCHAR2(50)    NOT NULL,
    role        VARCHAR2(20)    DEFAULT 'ROLE_USER' NOT NULL,  -- ROLE_USER / ROLE_ADMIN
    enabled     NUMBER(1)       DEFAULT 1 NOT NULL,            -- 0: 잠김, 1: 정상
    created_at  DATE            DEFAULT SYSDATE
);

CREATE SEQUENCE member_seq
    START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

-- 로그인 시도 이력 테이블
CREATE TABLE login_history (
    history_no  NUMBER          PRIMARY KEY,
    username    VARCHAR2(50)    NOT NULL,
    ip_address  VARCHAR2(50),
    success     NUMBER(1)       NOT NULL,      -- 0: 실패, 1: 성공
    attempted_at DATE           DEFAULT SYSDATE
);

CREATE SEQUENCE login_history_seq
    START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

CREATE INDEX idx_login_history_username ON login_history(username, attempted_at);
