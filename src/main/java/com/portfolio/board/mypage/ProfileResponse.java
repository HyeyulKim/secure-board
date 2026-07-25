package com.portfolio.board.mypage;

import com.portfolio.board.vo.MemberVO;

import java.time.LocalDateTime;

public class ProfileResponse {
    private String username;
    private String nickname;
    private String profileImage;
    private String role;
    private LocalDateTime createdAt;

    public static ProfileResponse fromVO(MemberVO vo) {
        ProfileResponse res = new ProfileResponse();
        res.username = vo.getUsername();
        res.nickname = vo.getNickname();
        res.profileImage = vo.getProfileImage();
        res.role = vo.getRole();
        res.createdAt = vo.getCreatedAt();
        return res;
    }

    public String getUsername() {
        return username;
    }

    public String getNickname() {
        return nickname;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public String getRole() {
        return role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
