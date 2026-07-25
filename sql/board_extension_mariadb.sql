-- ============================================
-- 댓글 (대댓글 포함, 자기참조 구조)
-- ============================================
CREATE TABLE comment (
    comment_no        BIGINT AUTO_INCREMENT PRIMARY KEY,
    board_no          BIGINT          NOT NULL,
    parent_comment_no BIGINT          NULL,   -- NULL이면 최상위 댓글, 값이 있으면 그 댓글의 대댓글
    writer            VARCHAR(50)     NOT NULL,
    content           VARCHAR(1000)   NOT NULL,
    deleted           TINYINT(1)      DEFAULT 0 NOT NULL,  -- 소프트 삭제 (대댓글 있는 댓글은 물리삭제 대신 처리)
    created_at        DATETIME        DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (board_no) REFERENCES board(board_no) ON DELETE CASCADE,
    FOREIGN KEY (parent_comment_no) REFERENCES comment(comment_no) ON DELETE CASCADE,
    INDEX idx_comment_board (board_no)
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- ============================================
-- 좋아요 (회원 1명당 게시글 1개에 1번만 가능하도록 UNIQUE 제약)
-- ============================================
CREATE TABLE board_like (
    board_no    BIGINT          NOT NULL,
    member_no   BIGINT          NOT NULL,
    created_at  DATETIME        DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (board_no, member_no),
    FOREIGN KEY (board_no) REFERENCES board(board_no) ON DELETE CASCADE,
    FOREIGN KEY (member_no) REFERENCES member(member_no) ON DELETE CASCADE
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- ============================================
-- 카테고리 (게시글 1개당 카테고리 1개 - 1:N)
-- ============================================
CREATE TABLE category (
    category_no BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(50)     NOT NULL UNIQUE
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

ALTER TABLE board ADD COLUMN category_no BIGINT NULL;
ALTER TABLE board ADD FOREIGN KEY (category_no) REFERENCES category(category_no);

-- ============================================
-- 태그 (게시글 1개당 태그 여러개 - N:M)
-- ============================================
CREATE TABLE tag (
    tag_no  BIGINT AUTO_INCREMENT PRIMARY KEY,
    name    VARCHAR(50)     NOT NULL UNIQUE
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE board_tag (
    board_no    BIGINT      NOT NULL,
    tag_no      BIGINT      NOT NULL,
    PRIMARY KEY (board_no, tag_no),
    FOREIGN KEY (board_no) REFERENCES board(board_no) ON DELETE CASCADE,
    FOREIGN KEY (tag_no) REFERENCES tag(tag_no) ON DELETE CASCADE
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 검색 고도화용 인덱스 (제목+내용 LIKE 검색 성능 개선)
ALTER TABLE board ADD FULLTEXT INDEX ft_board_title_content (title, content);
