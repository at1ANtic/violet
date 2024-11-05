package cn.atlantt1c.model.common;

public class AddFriendRequest {
    private Integer targetId;
    private String targetAccount;

    // Getters and Setters
    public Integer getTargetId() {
        return targetId;
    }

    public void setTargetId(Integer targetId) {
        this.targetId = targetId;
    }

    public String getTargetAccount() {
        return targetAccount;
    }

    public void setTargetAccount(String targetAccount) {
        this.targetAccount = targetAccount;
    }
}