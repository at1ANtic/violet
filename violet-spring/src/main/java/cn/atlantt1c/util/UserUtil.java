package cn.atlantt1c.util;

import cn.atlantt1c.exception.CustomException;
import cn.atlantt1c.model.common.Constant;
import cn.atlantt1c.model.entity.User;
import cn.atlantt1c.repository.UserRepository;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 获取当前登录用户工具类
 */
@Component
public class UserUtil {

    @Autowired
    private UserRepository userRepository;

    /**
     * 获取当前登录用户
     */
    public User getUser() {
        String token = SecurityUtils.getSubject().getPrincipal().toString();
        // 解密获得Account
        String account = JwtUtil.getClaim(token, Constant.ACCOUNT);
        User userDto = new User();
        userDto.setAccount(account);
        Optional<User> one = userRepository.findOne(Example.of(userDto));
        // 用户是否存在
        if (!one.isPresent()) {
            throw new CustomException("该帐号不存在(The account does not exist.)");
        }
        return one.get();
    }

    /**
     * 获取当前登录用户Id
     */
    public Integer getUserId() {
        return getUser().getId();
    }

    /**
     * 获取当前登录用户Token
     */
    public String getToken() {
        return SecurityUtils.getSubject().getPrincipal().toString();
    }

    /**
     * 获取当前登录用户Account
     */
    public String getAccount() {
        String token = SecurityUtils.getSubject().getPrincipal().toString();
        // 解密获得Account
        return JwtUtil.getClaim(token, Constant.ACCOUNT);
    }
}
