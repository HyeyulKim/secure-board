-- 회원 테이블
CREATE TABLE member (
    member_no   BIGINT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50)     NOT NULL UNIQUE,
    password    VARCHAR(200)    NOT NULL,   -- BCrypt 암호화된 값 저장
    nickname    VARCHAR(50)     NOT NULL,
    role        VARCHAR(20)     DEFAULT 'ROLE_USER' NOT NULL,  -- ROLE_USER / ROLE_ADMIN
    enabled     TINYINT(1)      DEFAULT 1 NOT NULL,            -- 0: 잠김, 1: 정상
    created_at  DATETIME        DEFAULT CURRENT_TIMESTAMP
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 로그인 시도 이력 테이블
CREATE TABLE login_history (
    history_no   BIGINT AUTO_INCREMENT PRIMARY KEY,
    username     VARCHAR(50)    NOT NULL,
    ip_address   VARCHAR(50),
    success      TINYINT(1)     NOT NULL,      -- 0: 실패, 1: 성공
    attempted_at DATETIME       DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_login_history_username (username, attempted_at)
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
