package com.carbon.platform.event;

public class PracticeApprovalEvent {
    private final Long userId;
    private final String parcelName;
    private final String status;
    private final String comments;

    public PracticeApprovalEvent(Long userId, String parcelName, String status, String comments) {
        this.userId = userId;
        this.parcelName = parcelName;
        this.status = status;
        this.comments = comments;
    }

    public Long getUserId() { return userId; }
    public String getParcelName() { return parcelName; }
    public String getStatus() { return status; }
    public String getComments() { return comments; }
}
