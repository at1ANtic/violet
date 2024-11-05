package cn.atlantt1c.service;

import java.util.List;
import java.util.Map;

public interface ChatService {
    /**
     * 根据 token 获取用户的好友列表和聊天组 UID
     *
     * @param token JWT token
     * @return 返回包含 friendsIds 和 chatGroupUids 的数据
     */
    Map<String, Object> getUserInfoByToken(String token);
    List<String> getUsernamesByIds(List<Integer> ids);
    void writeChatRecord(String token, String receiverId, String content); // 添加这一行
    List<String> getChatRecords(String senderId, String receiverId, int limit);
    void deleteChatRecords(String senderId, String receiverId);
}
