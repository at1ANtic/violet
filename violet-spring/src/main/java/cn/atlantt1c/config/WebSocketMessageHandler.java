package cn.atlantt1c.config;

import cn.atlantt1c.repository.UserRepository;
import cn.atlantt1c.util.JwtUtil;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketMessageHandler extends TextWebSocketHandler {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    // 存储 WebSocket 会话和用户ID的映射
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Autowired
    public WebSocketMessageHandler(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    // 建立 WebSocket 连接时调用
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
/*        String token = session.getHandshakeHeaders().get("Authorization").get(0); // 获取 Authorization 头*/

        String token = extractTokenFromUri(session.getUri());
        if (!jwtUtil.verify(token)) {
            throw new IllegalArgumentException("无效的 token");
        }

        // 获取用户账户并查找用户ID
        String account = jwtUtil.getClaim(token, "account");
        Integer userId = userRepository.findIdByAccount(account);

        // 将用户ID和 WebSocket 会话存储起来
        sessions.put(userId.toString(), session);
        System.out.println("Connected: " + userId);
    }
    private String extractTokenFromUri(URI uri) {
        String query = uri.getQuery();
        if (query != null) {
            String[] params = query.split("&");
            for (String param : params) {
                if (param.startsWith("token=")) {
                    return param.substring("token=".length());
                }
            }
        }
        return null; // 或抛出异常，根据需要
    }

    

    // 处理收到的消息
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 解析消息 (假设消息格式为JSON)
        String payload = message.getPayload();
        // 处理消息逻辑，发送消息给指定用户
    }

    // 连接关闭时调用
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // 移除存储的 WebSocket 会话
        sessions.values().remove(session);
        System.out.println("Connection closed: " + session.getId());
    }

    // 发送消息给指定用户
    public void sendMessageToUser(String userId, String message) throws Exception {
        WebSocketSession session = sessions.get(userId);
        if (session != null && session.isOpen()) {
            session.sendMessage(new TextMessage(message));
        } else {
            System.out.println("User not connected: " + userId);
        }
    }
}

