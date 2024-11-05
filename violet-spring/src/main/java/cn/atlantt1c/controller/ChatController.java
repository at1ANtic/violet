package cn.atlantt1c.controller;

import cn.atlantt1c.config.WebSocketMessageHandler;
import cn.atlantt1c.model.common.AddFriendRequest;
import cn.atlantt1c.model.common.ChatRequest;
import cn.atlantt1c.model.common.ResponseBean;
import cn.atlantt1c.service.ChatService;
import cn.atlantt1c.service.FriendshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.context.event.EventListener;

import java.util.List;
import java.util.Map;

/**
 * ChatController
 */
@RestController
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private WebSocketMessageHandler webSocketMessageHandler;

    @Autowired
    private ChatService chatService;

    @Autowired
    private FriendshipService friendshipService;



    // WebSocket连接建立的监听器
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());
        String sessionId = headers.getSessionId();
        System.out.println("WebSocket connected: " + sessionId);
        // 这里你可以保存用户的sessionId
    }

    // WebSocket连接断开的监听器
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        System.out.println("WebSocket disconnected: " + sessionId);
        // 这里你可以处理用户断开连接的逻辑
    }


    @GetMapping("/info")
    public ResponseBean getUserInfo(@RequestHeader("Authorization") String token) {
        Map<String, Object> userInfo = chatService.getUserInfoByToken(token);
        if (userInfo != null) {
            return new ResponseBean(200, "Success", userInfo);
        } else {
            return new ResponseBean(404, "User not found", null);
        }
    }

    @PostMapping("/addFriend")
    public ResponseBean addFriend(
            @RequestHeader("Authorization") String token,
            @RequestBody AddFriendRequest request) {
        try {
            friendshipService.addFriend(token, request.getTargetId(), request.getTargetAccount());
            return new ResponseBean(200, "Friend added successfully", null);
        } catch (IllegalArgumentException e) {
            return new ResponseBean(400, e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseBean(500, "Internal server error", null);
        }
    }

    @PostMapping("/removeFriend")
    public ResponseBean removeFriend(
            @RequestHeader("Authorization") String token,
            @RequestBody AddFriendRequest request) {
        try {
            friendshipService.removeFriend(token, request.getTargetId(), request.getTargetAccount());
            return new ResponseBean(200, "Friend removed successfully", null);
        } catch (IllegalArgumentException e) {
            return new ResponseBean(400, e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseBean(500, "Internal server error", null);
        }
    }

    @PostMapping("/send")
    public ResponseBean sendMessage(
            @RequestHeader("Authorization") String token,
            @RequestBody ChatRequest chatRequest
    ) {
        try {
            String receiverId = chatRequest.getReceiverId();
            String content = chatRequest.getContent();
            chatService.writeChatRecord(token, receiverId, content);
            webSocketMessageHandler.sendMessageToUser(receiverId, content);
            return new ResponseBean(200, "Message sent successfully", null);
        } catch (Exception e) {
            return new ResponseBean(500, "Internal server error", null);
        }
    }

    @GetMapping("/records")
    public ResponseBean getRecords(
            @RequestHeader("Authorization") String token,
            @RequestParam String receiverId,
            @RequestParam int limit) {
        List<String> records = chatService.getChatRecords(token, receiverId, limit);
        return new ResponseBean(200, "Success", records);
    }

    @DeleteMapping("/deleteRecords")
    public ResponseBean deleteRecords(
            @RequestHeader("Authorization") String token,
            @RequestParam String receiverId) {
        chatService.deleteChatRecords(token, receiverId);
        return new ResponseBean(200, "Chat records deleted successfully", null);
    }

    private String getUserIdFromToken(String token) {
        return "exampleUserId"; // 示例
    }
}
