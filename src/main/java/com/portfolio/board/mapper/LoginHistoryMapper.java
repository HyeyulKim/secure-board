package com.portfolio.board.mapper;

import com.portfolio.board.vo.LoginHistoryVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LoginHistoryMapper {

    void insert(LoginHistoryVO history);

    List<LoginHistoryVO> selectList(@Param("offset") int offset, @Param("limit") int limit);
}
