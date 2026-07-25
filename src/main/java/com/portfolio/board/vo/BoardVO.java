package com.portfolio.board.vo;

import java.time.LocalDateTime;

/**
 * DB 테이블(board)과 1:1로 매핑되는 VO(Entity).
 * MyBatis resultMap이 이 클래스의 필드에 컬럼값을 채워준다.
 * API 응답에는 이 객체를 그대로 노출하지 않고 BoardDTO로 변환해서 내보낸다.
 */
public class BoardVO {

    private Long boardNo;
    private String title;
    private String content;
    private String writer;
    private int viewCnt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public BoardVO() {
    }

    public Long getBoardNo() {
        return boardNo;
    }

    public void setBoardNo(Long boardNo) {
        this.boardNo = boardNo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public int getViewCnt() {
        return viewCnt;
    }

    public void setViewCnt(int viewCnt) {
        this.viewCnt = viewCnt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
