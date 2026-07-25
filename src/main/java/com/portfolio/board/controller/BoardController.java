package com.portfolio.board.controller;

import com.portfolio.board.dto.BoardDTO;
import com.portfolio.board.service.BoardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * React 프론트엔드가 Axios로 호출하는 REST API.
 * 응답은 전부 JSON. 인증/인가는 이후 단계(Spring Security + JWT)에서
 * 이 Controller 앞단의 Filter로 처리한다.
 */
@RestController
@RequestMapping("/api/boards")
public class BoardController {

    private final BoardService boardService; // 생성자 주입(DI)

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        List<BoardDTO> items = boardService.getList(keyword, page, size);
        int totalCount = boardService.getTotalCount(keyword);

        Map<String, Object> body = Map.of(
                "items", items,
                "totalCount", totalCount,
                "page", page,
                "size", size
        );
        return ResponseEntity.ok(body);
    }

    @GetMapping("/{boardNo}")
    public ResponseEntity<BoardDTO> get(@PathVariable Long boardNo) {
        return ResponseEntity.ok(boardService.getOne(boardNo));
    }

    @PostMapping
    public ResponseEntity<Map<String, Long>> create(@RequestBody BoardDTO dto) {
        Long boardNo = boardService.create(dto);
        return ResponseEntity.ok(Map.of("boardNo", boardNo));
    }

    @PutMapping("/{boardNo}")
    public ResponseEntity<Void> update(@PathVariable Long boardNo, @RequestBody BoardDTO dto) {
        boardService.update(boardNo, dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{boardNo}")
    public ResponseEntity<Void> delete(@PathVariable Long boardNo) {
        boardService.delete(boardNo);
        return ResponseEntity.noContent().build();
    }
}
