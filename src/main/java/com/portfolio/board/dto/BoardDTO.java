package com.portfolio.board.dto;

import com.portfolio.board.vo.BoardVO;

import java.time.LocalDateTime;

/**
 * React 프론트(JSON)와 주고받는 DTO.
 * VO(DB 엔티티)와 분리해서, DB 컬럼 구조가 바뀌어도 API 스펙에 영향이 없게 한다.
 * 등록/수정 요청(request)과 조회 응답(response) 양쪽에 사용.
 */
public class BoardDTO {

    private Long boardNo;
    private String title;
    private String content;
    private String writer;
    private int viewCnt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public BoardDTO() {
    }

    /** VO(Entity) → DTO 변환 */
    public static BoardDTO fromVO(BoardVO vo) {
        BoardDTO dto = new BoardDTO();
        dto.boardNo = vo.getBoardNo();
        dto.title = vo.getTitle();
        dto.content = vo.getContent();
        dto.writer = vo.getWriter();
        dto.viewCnt = vo.getViewCnt();
        dto.createdAt = vo.getCreatedAt();
        dto.updatedAt = vo.getUpdatedAt();
        return dto;
    }

    /** DTO(요청) → VO(Entity) 변환 */
    public BoardVO toVO() {
        BoardVO vo = new BoardVO();
        vo.setBoardNo(this.boardNo);
        vo.setTitle(this.title);
        vo.setContent(this.content);
        vo.setWriter(this.writer);
        return vo;
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
