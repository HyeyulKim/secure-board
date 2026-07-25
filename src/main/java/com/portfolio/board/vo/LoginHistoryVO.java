package com.portfolio.board.vo;

import java.time.LocalDateTime;

public class LoginHistoryVO {

    private Long historyNo;
    private String username;
    private String ipAddress;
    private boolean success;
    private LocalDateTime attemptedAt;

    public LoginHistoryVO() {
    }

    public LoginHistoryVO(String username, String ipAddress, boolean success) {
        this.username = username;
        this.ipAddress = ipAddress;
        this.success = success;
    }

    public Long getHistoryNo() {
        return historyNo;
    }

    public void setHistoryNo(Long historyNo) {
        this.historyNo = historyNo;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public LocalDateTime getAttemptedAt() {
        return attemptedAt;
    }

    public void setAttemptedAt(LocalDateTime attemptedAt) {
        this.attemptedAt = attemptedAt;
    }
}
