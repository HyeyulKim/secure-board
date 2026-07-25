-- Oracle용 게시판 테이블
CREATE TABLE board (
    board_no    NUMBER          PRIMARY KEY,
    title       VARCHAR2(200)   NOT NULL,
    content     CLOB,
    writer      VARCHAR2(50)    NOT NULL,
    view_cnt    NUMBER          DEFAULT 0,
    created_at  DATE            DEFAULT SYSDATE,
    updated_at  DATE
);

-- Oracle은 AUTO_INCREMENT가 없으므로 시퀀스로 PK 채번
CREATE SEQUENCE board_seq
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

-- 첨부파일 테이블 (게시글 삭제 시 트랜잭션으로 함께 삭제할 대상)
CREATE TABLE board_file (
    file_no     NUMBER          PRIMARY KEY,
    board_no    NUMBER          NOT NULL REFERENCES board(board_no) ON DELETE CASCADE,
    origin_name VARCHAR2(255)   NOT NULL,
    saved_name  VARCHAR2(255)   NOT NULL,
    created_at  DATE            DEFAULT SYSDATE
);

CREATE SEQUENCE board_file_seq
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;
