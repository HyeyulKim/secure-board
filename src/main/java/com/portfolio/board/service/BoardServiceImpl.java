package com.portfolio.board.service;

import com.portfolio.board.dto.BoardDTO;
import com.portfolio.board.mapper.BoardMapper;
import com.portfolio.board.vo.BoardVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BoardServiceImpl implements BoardService {

    private final BoardMapper boardMapper;

    // 생성자 주입 (DI) — 필드 주입(@Autowired) 대신 사용하는 이유:
    // 1) 의존성이 컴파일 타임에 강제되어 누락을 방지
    // 2) 테스트 시 Mock 객체를 생성자로 쉽게 주입 가능
    // 3) final로 선언해 불변성 보장
    public BoardServiceImpl(BoardMapper boardMapper) {
        this.boardMapper = boardMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BoardDTO> getList(String keyword, int page, int size) {
        int offset = (page - 1) * size;
        List<BoardVO> list = boardMapper.selectList(keyword, offset, size);
        return list.stream()
                .map(BoardDTO::fromVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public int getTotalCount(String keyword) {
        return boardMapper.selectCount(keyword);
    }

    @Override
    @Transactional
    public BoardDTO getOne(Long boardNo) {
        // 상세 조회 시 조회수 증가까지 한 트랜잭션으로 묶음
        boardMapper.increaseViewCnt(boardNo);
        BoardVO vo = boardMapper.selectOne(boardNo);
        if (vo == null) {
            throw new IllegalArgumentException("게시글이 존재하지 않습니다. boardNo=" + boardNo);
        }
        return BoardDTO.fromVO(vo);
    }

    @Override
    @Transactional
    public Long create(BoardDTO dto) {
        BoardVO vo = dto.toVO();
        boardMapper.insert(vo);
        return vo.getBoardNo();
    }

    @Override
    @Transactional
    public void update(Long boardNo, BoardDTO dto) {
        BoardVO vo = dto.toVO();
        vo.setBoardNo(boardNo);
        int updated = boardMapper.update(vo);
        if (updated == 0) {
            throw new IllegalArgumentException("게시글이 존재하지 않습니다. boardNo=" + boardNo);
        }
    }

    @Override
    @Transactional
    public void delete(Long boardNo) {
        // TODO: 첨부파일 삭제 로직(board_file) 추가 시 이 메서드 안에서 함께 처리
        // 게시글 삭제와 첨부파일 삭제 중 하나라도 실패하면 전체 롤백되어야 하므로
        // 반드시 하나의 @Transactional 범위 안에서 처리한다.
        int deleted = boardMapper.delete(boardNo);
        if (deleted == 0) {
            throw new IllegalArgumentException("게시글이 존재하지 않습니다. boardNo=" + boardNo);
        }
    }
}
