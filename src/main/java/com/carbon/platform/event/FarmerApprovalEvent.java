package com.carbon.platform.event;

public class FarmerApprovalEvent {
    private final Long userId;
    private final String email;
    private final String status;
    private final String comments;

    public FarmerApprovalEvent(Long userId, String email, String status, String comments) {
        this.userId = userId;
        this.email = email;
        this.status = status;
        this.comments = comments;
    }

    public Long getUserId() { return userId; }
    public String getEmail() { return email; }
    public String getStatus() { return status; }
    public String getComments() { return comments; }
}
