package com.portfolio.board.mypage;

import com.portfolio.board.mapper.MemberMapper;
import com.portfolio.board.vo.MemberVO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MyPageService {

    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;

    public MyPageService(MemberMapper memberMapper, PasswordEncoder passwordEncoder) {
        this.memberMapper = memberMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public ProfileResponse getProfile(String username) {
        MemberVO member = findActiveMember(username);
        return ProfileResponse.fromVO(member);
    }

    @Transactional
    public void updateProfile(String username, ProfileUpdateRequest request) {
        findActiveMember(username); // 존재 확인
        memberMapper.updateProfile(username, request.getNickname(), null);
    }

    @Transactional
    public void changePassword(String username, PasswordChangeRequest request) {
        MemberVO member = findActiveMember(username);

        if (member.getProvider() != null) {
            throw new IllegalStateException("소셜 로그인 계정은 비밀번호를 변경할 수 없습니다.");
        }
        if (!passwordEncoder.matches(request.getCurrentPassword(), member.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        String encoded = passwordEncoder.encode(request.getNewPassword());
        memberMapper.updatePassword(username, encoded);
    }

    @Transactional
    public void withdraw(String username, WithdrawRequest request) {
        MemberVO member = findActiveMember(username);

        if (member.getProvider() == null
                && !passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        memberMapper.withdraw(username);
    }

    private MemberVO findActiveMember(String username) {
        MemberVO member = memberMapper.findByUsername(username);
        if (member == null || member.isWithdrawn()) {
            throw new IllegalArgumentException("존재하지 않는 계정입니다.");
        }
        return member;
    }
}
