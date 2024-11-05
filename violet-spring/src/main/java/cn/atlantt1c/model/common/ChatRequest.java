package cn.atlantt1c.model.common;

public class ChatRequest {

    private String receiverId; // 接收方用户 ID
    private String content; // 聊天内容

    // Getter 和 Setter 方法
    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

