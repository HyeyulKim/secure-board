package com.portfolio.board.mapper;

import com.portfolio.board.vo.BoardVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 실제 SQL은 BoardMapper.xml(resources/mapper)에 정의한다.
 * 인터페이스와 XML의 namespace/id가 매칭되어 MyBatis가 자동으로 구현체를 만들어준다.
 */
@Mapper
public interface BoardMapper {

    List<BoardVO> selectList(@Param("keyword") String keyword,
                              @Param("offset") int offset,
                              @Param("limit") int limit);

    int selectCount(@Param("keyword") String keyword);

    BoardVO selectOne(@Param("boardNo") Long boardNo);

    void increaseViewCnt(@Param("boardNo") Long boardNo);

    // Oracle: 시퀀스로 채번된 boardNo가 파라미터 객체에 채워짐
    // MariaDB: AUTO_INCREMENT로 생성된 boardNo가 파라미터 객체에 채워짐 (useGeneratedKeys)
    int insert(BoardVO board);

    int update(BoardVO board);

    int delete(@Param("boardNo") Long boardNo);
}
