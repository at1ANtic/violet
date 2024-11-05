package cn.atlantt1c.service;

import cn.atlantt1c.model.entity.User;
import cn.atlantt1c.repository.UserRepository;
import cn.atlantt1c.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;
    public void updateUsernameById(Integer id, String newUsername) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setUsername(newUsername);  // 修改用户名
        userRepository.save(user);      // 保存修改后的用户对象
    }

    /**
     * 处理根据 token 获取用户信息的业务逻辑
     * @param token 前端传递的 JWT token
     * @return 包含用户 id 和用户名的 Map
     */
    public Map<String, Object> getUserInfoByToken(String token) {
        // 验证 token 是否有效
        if (!jwtUtil.verify(token)) {
            throw new IllegalArgumentException("无效的 token");
        }

        // 从 token 中获取用户账户
        String account = jwtUtil.getClaim(token, "account");

        // 根据账户查询用户 ID 和 username
        Integer id = userRepository.findIdByAccount(account);
        String username = userRepository.findByAccount(account).getUsername();

        // 将 ID 和用户名封装到 Map 中返回
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", id);
        userInfo.put("username", username);

        return userInfo;
    }
}
