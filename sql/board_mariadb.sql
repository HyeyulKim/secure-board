-- MariaDB용 게시판 테이블
-- 한글 저장을 위해 반드시 utf8mb4로 문자셋 지정 (기본값 사용 시 한글 INSERT 에러 발생)
CREATE TABLE board (
    board_no    BIGINT AUTO_INCREMENT PRIMARY KEY,
    title       VARCHAR(200)    NOT NULL,
    content     TEXT,
    writer      VARCHAR(50)     NOT NULL,
    view_cnt    INT             DEFAULT 0,
    created_at  DATETIME        DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 첨부파일 테이블
CREATE TABLE board_file (
    file_no     BIGINT AUTO_INCREMENT PRIMARY KEY,
    board_no    BIGINT          NOT NULL,
    origin_name VARCHAR(255)    NOT NULL,
    saved_name  VARCHAR(255)    NOT NULL,
    created_at  DATETIME        DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (board_no) REFERENCES board(board_no) ON DELETE CASCADE
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
