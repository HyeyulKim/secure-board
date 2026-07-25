package com.portfolio.board.auth;

public class LoginResponse {
    private String token;
    private String username;
    private String nickname;
    private String role;

    public LoginResponse(String token, String username, String nickname, String role) {
        this.token = token;
        this.username = username;
        this.nickname = nickname;
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public String getUsername() {
        return username;
    }

    public String getNickname() {
        return nickname;
    }

    public String getRole() {
        return role;
    }
}
