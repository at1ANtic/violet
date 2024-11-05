package cn.atlantt1c.service;

import cn.atlantt1c.model.entity.User;
import cn.atlantt1c.util.JwtUtil;
import cn.atlantt1c.util.NumberUtil;
import cn.atlantt1c.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
public class FriendshipService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void addFriend(String token, Integer targetId, String targetAccount) throws JsonProcessingException {
        // 校验 token
        if (!jwtUtil.verify(token)) {
            throw new IllegalArgumentException("Invalid token");
        }

        // 获取用户账户
        String account = jwtUtil.getClaim(token, "account");

        // 根据账户查找当前用户
        User user = userRepository.findByAccount(account);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        // 根据目标用户的 id 查找目标用户
        User targetUser = userRepository.findById(targetId).orElse(null);
        if (targetUser == null) {
            throw new IllegalArgumentException("Target user with ID not found");
        }

        // 验证目标用户账户是否匹配
        if (!targetAccount.equals(targetUser.getAccount())) {
            throw new IllegalArgumentException("Target account does not match the provided ID");
        }

        // 检查 ID 是否相同
        if (user.getId().equals(targetUser.getId())) {
            throw new IllegalArgumentException("Cannot add yourself as a friend");
        }

        // 更新好友列表
        updateFriendLists(user, targetUser);

        // 创建聊天记录文件
        createChatRecordFile(user.getId(), targetUser.getId());
    }

    public void removeFriend(String token, Integer targetId, String targetAccount) throws JsonProcessingException {
        // 校验 token
        if (!jwtUtil.verify(token)) {
            throw new IllegalArgumentException("Invalid token");
        }

        // 获取用户账户
        String account = jwtUtil.getClaim(token, "account");

        // 根据账户查找当前用户
        User user = userRepository.findByAccount(account);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        // 根据目标用户的 id 查找目标用户
        User targetUser = userRepository.findById(targetId).orElse(null);
        if (targetUser == null) {
            throw new IllegalArgumentException("Target user with ID not found");
        }

        // 验证目标用户账户是否匹配
        if (!targetAccount.equals(targetUser.getAccount())) {
            throw new IllegalArgumentException("Target account does not match the provided ID");
        }

        // 检查 ID 是否相同
        if (user.getId().equals(targetUser.getId())) {
            throw new IllegalArgumentException("Cannot remove yourself as a friend");
        }

        // 更新好友列表
        updateFriendListsOnRemoval(user, targetUser);

        // 删除聊天记录文件
        deleteChatRecordFile(user.getId(), targetUser.getId());
    }

    private void updateFriendLists(User user, User targetUser) throws JsonProcessingException {
        // 获取当前好友列表
        List<Integer> userFriends = NumberUtil.extractNumbers(user.getFriendsIds());
        List<Integer> targetUserFriends = NumberUtil.extractNumbers(targetUser.getFriendsIds());

        // 添加双方 ID
        if (!userFriends.contains(targetUser.getId())) {
            userFriends.add(targetUser.getId());
        }
        if (!targetUserFriends.contains(user.getId())) {
            targetUserFriends.add(user.getId());
        }

        // 保存更新后的好友列表
        user.setFriendsIds(NumberUtil.convertNumbersToQuotedStrings(userFriends));
        targetUser.setFriendsIds(NumberUtil.convertNumbersToQuotedStrings(targetUserFriends));

        userRepository.save(user);
        userRepository.save(targetUser);
    }

    private void updateFriendListsOnRemoval(User user, User targetUser) throws JsonProcessingException {
        // 获取当前好友列表
        List<Integer> userFriends = NumberUtil.extractNumbers(user.getFriendsIds());
        List<Integer> targetUserFriends = NumberUtil.extractNumbers(targetUser.getFriendsIds());

        // 删除双方 ID
        userFriends.remove(targetUser.getId());
        targetUserFriends.remove(user.getId());

        // 保存更新后的好友列表
        user.setFriendsIds(NumberUtil.convertNumbersToQuotedStrings(userFriends));
        targetUser.setFriendsIds(NumberUtil.convertNumbersToQuotedStrings(targetUserFriends));

        userRepository.save(user);
        userRepository.save(targetUser);
    }

    private void createChatRecordFile(Integer userId, Integer targetUserId) {
        String fileName = userId + "_" + targetUserId + ".txt";
        File file = new File("ChatRecords/" + fileName); // 文件夹名改为英文
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteChatRecordFile(Integer userId, Integer targetUserId) {
        String fileName = userId + "_" + targetUserId + ".txt";
        File file = new File("ChatRecords/" + fileName); // 文件夹名改为英文
        if (file.exists()) {
            file.delete();
        }
    }
}
