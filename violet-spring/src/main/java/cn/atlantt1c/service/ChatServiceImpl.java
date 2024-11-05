package cn.atlantt1c.service;

import cn.atlantt1c.config.WebSocketMessageHandler;
import cn.atlantt1c.util.JwtUtil;
import cn.atlantt1c.model.entity.User;
import cn.atlantt1c.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static cn.atlantt1c.util.NumberUtil.extractNumbers;

@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private final WebSocketMessageHandler webSocketMessageHandler;


    @Autowired
    public ChatServiceImpl(WebSocketMessageHandler webSocketMessageHandler) {
        this.webSocketMessageHandler = webSocketMessageHandler;
    }

    @Override
    public Map<String, Object> getUserInfoByToken(String token) {
        // 校验 token
        if (!jwtUtil.verify(token)) {
            throw new IllegalArgumentException("无效的 token");
        }

        // 获取用户账户
        String account = jwtUtil.getClaim(token, "account");

        // 根据账户查找用户
        User user = userRepository.findByAccount(account);

        if (user == null) {
            throw new IllegalArgumentException("用户未找到");
        }

        Map<String, Object> response = new HashMap<>();
        // 获取好友列表
        List<Integer> numbers = extractNumbers(user.getFriendsIds());
        List<String> userNames = userRepository.findUsernamesByIds(numbers);
        response.put("friendsIds", user.getFriendsIds());
        response.put("friendsName", formatFriendIds(userNames));
        response.put("chatGroupids", user.getChatGroupIds());

        return response;
    }

    public List<String> getUsernamesByIds(List<Integer> ids) {
        return userRepository.findUsernamesByIds(ids);
    }

    private String formatFriendIds(List<String> friendsIds) {
        return friendsIds.stream()
                .map(id -> id.replace("\n", "\\"))
                .collect(Collectors.joining(","));
    }

    // 写入聊天记录
    public void writeChatRecord(String token, String receiverId, String content) {
        if (!jwtUtil.verify(token)) {
            throw new IllegalArgumentException("无效的 token");
        }

        // 获取用户账户
        String account = jwtUtil.getClaim(token, "account");

        // 从 token 中获取发送方用户 ID
        Integer senderId = userRepository.findIdByAccount(account);

        // 生成两个可能的文件名
        String chatPair1 = senderId + "_" + receiverId;
        String chatPair2 = receiverId + "_" + senderId;

        String filePath1 = "ChatRecords/" + chatPair1 + ".txt";
        String filePath2 = "ChatRecords/" + chatPair2 + ".txt";

        try {
            // 检查第一个文件是否存在
            File file1 = new File(filePath1);
            if (file1.exists()) {
                writeToFile(filePath1, senderId, content);
            } else {
                // 如果第一个文件不存在，检查第二个文件
                File file2 = new File(filePath2);
                if (file2.exists()) {
                    writeToFile(filePath2, senderId, content);
                } else {
                    // 如果两个文件都不存在，则创建第一个文件并写入
                    writeToFile(filePath1, senderId, content);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 获取聊天记录
    public List<String> getChatRecords(String token, String receiverId, int limit) {
        if (!jwtUtil.verify(token)) {
            throw new IllegalArgumentException("Invalid token");
        }

        // Extract account from token and retrieve senderId
        String account = jwtUtil.getClaim(token, "account");
        Integer senderId = userRepository.findIdByAccount(account);

        if (senderId == null) {
            throw new IllegalArgumentException("Sender not found");
        }

        // Create possible chat file names
        String chatPair1 = senderId + "_" + receiverId;
        String chatPair2 = receiverId + "_" + senderId;

        String filePath1 = "ChatRecords/" + chatPair1 + ".txt";
        String filePath2 = "ChatRecords/" + chatPair2 + ".txt";

        List<String> records = new ArrayList<>();

        // Try to read chat records from either of the two possible files
        try {
            if (Files.exists(Paths.get(filePath1))) {
                records = Files.readAllLines(Paths.get(filePath1), StandardCharsets.UTF_8);
            } else if (Files.exists(Paths.get(filePath2))) {
                records = Files.readAllLines(Paths.get(filePath2), StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            // Log the error for debugging purposes
            System.err.println("Error reading chat records: " + e.getMessage());
            return Collections.emptyList();  // Return an empty list if reading fails
        }

        // Return all records if limit is -1, otherwise return the last 'limit' records
        return (limit == -1 || records.size() <= limit) ? records : records.subList(records.size() - limit, records.size());
    }

    // 删除聊天记录
    public void deleteChatRecords(String token, String receiverId) {
        if (!jwtUtil.verify(token)) {
            throw new IllegalArgumentException("Invalid token");
        }

        // Extract account from token and retrieve senderId
        String account = jwtUtil.getClaim(token, "account");
        Integer senderId = userRepository.findIdByAccount(account);

        if (senderId == null) {
            throw new IllegalArgumentException("Sender not found");
        }

        // 创建可能的聊天记录文件名
        String chatPair1 = senderId + "_" + receiverId;
        String chatPair2 = receiverId + "_" + senderId;

        String filePath1 = "ChatRecords/" + chatPair1 + ".txt";
        String filePath2 = "ChatRecords/" + chatPair2 + ".txt";

        // 删除文件的逻辑
        boolean deleted = false;
        try {
            File file1 = new File(filePath1);
            File file2 = new File(filePath2);

            if (file1.exists() && file1.delete()) {
                System.out.println("聊天记录文件已成功删除: " + filePath1);
                deleted = true;
            }

            if (file2.exists() && file2.delete()) {
                System.out.println("聊天记录文件已成功删除: " + filePath2);
                deleted = true;
            }

            if (!deleted) {
                System.out.println("没有找到聊天记录文件: " + filePath1 + " 或 " + filePath2);
            }

        } catch (Exception e) {
            System.err.println("删除聊天记录文件时出错: " + e.getMessage());
        }
    }

    private void writeToFile(String filePath, Integer senderId, String content) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            writer.write("[" + timestamp + "] [" + senderId + "]: " + content);
            writer.newLine();
        }
    }

    public void sendMessage(String senderId, String receiverId, String content) {
        String message = "[" + senderId + "]: " + content;
    }


}
