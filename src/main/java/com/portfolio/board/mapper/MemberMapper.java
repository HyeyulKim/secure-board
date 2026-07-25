package com.portfolio.board.mapper;

import com.portfolio.board.vo.MemberVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MemberMapper {

    MemberVO findByUsername(@Param("username") String username);

    int insert(MemberVO member);

    int updateEnabled(@Param("username") String username, @Param("enabled") boolean enabled);
}
