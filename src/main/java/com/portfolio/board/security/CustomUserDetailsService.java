package com.portfolio.board.security;

import com.portfolio.board.mapper.MemberMapper;
import com.portfolio.board.vo.MemberVO;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberMapper memberMapper;

    public CustomUserDetailsService(MemberMapper memberMapper) {
        this.memberMapper = memberMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        MemberVO member = memberMapper.findByUsername(username);
        if (member == null) {
            throw new UsernameNotFoundException("존재하지 않는 계정입니다: " + username);
        }

        return new User(
                member.getUsername(),
                member.getPassword(),
                member.isEnabled(),
                true, true, true,
                List.of(new SimpleGrantedAuthority(member.getRole()))
        );
    }
}
