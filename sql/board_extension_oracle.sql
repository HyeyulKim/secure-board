-- ============================================
-- 댓글 (대댓글 포함, 자기참조 구조)
-- ============================================
CREATE TABLE comment (
    comment_no        NUMBER          PRIMARY KEY,
    board_no          NUMBER          NOT NULL REFERENCES board(board_no) ON DELETE CASCADE,
    parent_comment_no NUMBER          NULL REFERENCES comment(comment_no) ON DELETE CASCADE,
    writer            VARCHAR2(50)    NOT NULL,
    content           VARCHAR2(1000)  NOT NULL,
    deleted           NUMBER(1)       DEFAULT 0 NOT NULL,
    created_at        DATE            DEFAULT SYSDATE
);
CREATE SEQUENCE comment_seq START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE INDEX idx_comment_board ON comment(board_no);

-- ============================================
-- 좋아요
-- ============================================
CREATE TABLE board_like (
    board_no    NUMBER          NOT NULL REFERENCES board(board_no) ON DELETE CASCADE,
    member_no   NUMBER          NOT NULL REFERENCES member(member_no) ON DELETE CASCADE,
    created_at  DATE            DEFAULT SYSDATE,
    PRIMARY KEY (board_no, member_no)
);

-- ============================================
-- 카테고리
-- ============================================
CREATE TABLE category (
    category_no NUMBER          PRIMARY KEY,
    name        VARCHAR2(50)    NOT NULL UNIQUE
);
CREATE SEQUENCE category_seq START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

ALTER TABLE board ADD category_no NUMBER NULL;
ALTER TABLE board ADD CONSTRAINT fk_board_category FOREIGN KEY (category_no) REFERENCES category(category_no);

-- ============================================
-- 태그
-- ============================================
CREATE TABLE tag (
    tag_no  NUMBER          PRIMARY KEY,
    name    VARCHAR2(50)    NOT NULL UNIQUE
);
CREATE SEQUENCE tag_seq START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

CREATE TABLE board_tag (
    board_no    NUMBER      NOT NULL REFERENCES board(board_no) ON DELETE CASCADE,
    tag_no      NUMBER      NOT NULL REFERENCES tag(tag_no) ON DELETE CASCADE,
    PRIMARY KEY (board_no, tag_no)
);

-- 검색 고도화: Oracle은 MariaDB의 FULLTEXT와 달리 Oracle Text 옵션이 필요.
-- 부트캠프/개인 프로젝트 범위에서는 과한 설정이라, 우선 title/content에 대한
-- 일반 인덱스 + LIKE 검색으로 대체하고, 필요 시 CTXSYS.CONTEXT 인덱스로 확장.
CREATE INDEX idx_board_title ON board(title);
