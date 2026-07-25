package com.portfolio.board.service;

import com.portfolio.board.dto.BoardDTO;

import java.util.List;

public interface BoardService {

    List<BoardDTO> getList(String keyword, int page, int size);

    int getTotalCount(String keyword);

    BoardDTO getOne(Long boardNo);

    Long create(BoardDTO dto);

    void update(Long boardNo, BoardDTO dto);

    void delete(Long boardNo);
}
