package cn.atlantt1c.controller;

import cn.atlantt1c.model.common.Constant;
import cn.atlantt1c.model.common.ResponseBean;
import cn.atlantt1c.model.entity.User;
import cn.atlantt1c.repository.UserRepository;
import cn.atlantt1c.service.UserService;
import cn.atlantt1c.util.JedisUtil;
import cn.atlantt1c.util.JwtUtil;
import cn.atlantt1c.util.PasswordUtil;
import cn.atlantt1c.util.UserUtil;
import cn.atlantt1c.exception.CustomException;
import cn.atlantt1c.exception.CustomUnauthorizedException;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * UserController
 */
@RestController
@RequestMapping("/user")
public class UserController {

    /**
     * RefreshToken过期时间
     */
    @Value("${jwt.refresh-token-expire-time}")
    private String refreshTokenExpireTime;
    @Resource
    private UserUtil userUtil;
    @Resource
    private UserRepository userRepository;
    @Resource
    private JedisUtil jedisUtil;

    @Autowired
    private UserService userService;




//    /**
//     * 获取用户列表
//     */
//    @GetMapping
//    @RequiresPermissions(logical = Logical.AND, value = {"user:view"})
//    public ResponseBean user(BaseDto baseDto) {
//        if (baseDto.getPage() == null || baseDto.getRows() == null) {
//            baseDto.setPage(1);
//            baseDto.setRows(10);
//        }
//        Pageable pageable = PageRequest.of(baseDto.getPage() - 1, baseDto.getRows());
//        Page<User> page = userRepository.findAll(pageable);
//        if (page == null || page.getTotalElements() < 0) {
//            throw new CustomException("查询失败(Query Failure)");
//        }
//        Map<String, Object> result = new HashMap<>(16);
//        result.put("count", page.getTotalElements());
//        result.put("data", page.getContent());
//        return new ResponseBean(HttpStatus.OK.value(), "查询成功(Query was successful)", result);
//    }
//
    /**
     * 获取在线用户(查询Redis中的RefreshToken)
     */
    @GetMapping("/online")
    public ResponseBean online() {
        List<Object> users = new ArrayList<>();
        // 查询所有Redis键
        Set<String> keys = jedisUtil.keysS(Constant.PREFIX_SHIRO_REFRESH_TOKEN + "*");
        for (String key : keys) {
            if (jedisUtil.exists(key)) {
                // 根据:分割key，获取最后一个字符(帐号)
                String[] strArray = key.split(":");
                User user = userRepository.findByAccount(strArray[strArray.length - 1]);
                users.add(user);
            }
        }
        if (users == null || users.size() < 0) {
            throw new CustomException("查询失败(Query Failure)");
        }
        return new ResponseBean(HttpStatus.OK.value(), "查询成功(Query was successful)", users);
    }

    /**
     * 登录授权
     */
    @PostMapping("/login")
    public ResponseBean login(@RequestBody User user, HttpServletResponse httpServletResponse) {
        User userTemp = userRepository.findByAccount(user.getAccount());

        if (userTemp == null) {
            throw new CustomUnauthorizedException("该帐号不存在(The account does not exist.)");
        }

        boolean passwordMatch = PasswordUtil.checkPassword(user.getPassword(), userTemp.getPassword());
        System.out.println("Password match result: " + passwordMatch);  // 日志输出密码匹配结果

        if (passwordMatch) {
            String currentTimeMillis = String.valueOf(System.currentTimeMillis());

            // Redis 中存储 token 过期时间
            jedisUtil.setObject(Constant.PREFIX_SHIRO_REFRESH_TOKEN + user.getAccount(), currentTimeMillis,
                    Integer.parseInt(refreshTokenExpireTime));

            // 生成 JWT 令牌
            String token = JwtUtil.sign(user.getAccount(), currentTimeMillis);
            System.out.println("Generated token: " + token);  // 日志输出生成的 token

            // 在响应头中设置 token
            httpServletResponse.setHeader("Authorization", token);
            httpServletResponse.setHeader("Access-Control-Expose-Headers", "Authorization");

            return new ResponseBean(HttpStatus.OK.value(), "登录成功(Login Success.)", null);
        } else {
            throw new CustomUnauthorizedException("帐号或密码错误(Account or Password Error.)");
        }
    }


    @GetMapping("/info")
    public Map<String, Object> getUserInfo(@RequestHeader("Authorization") String token) {
        return userService.getUserInfoByToken(token);
    }

//    /**
//     * 获取指定用户
//     */
//    @GetMapping("/{id}")
//    @RequiresPermissions(value = {"user:view"})
//    public ResponseBean findById(@PathVariable("id") Integer id) {
//        User user = userRepository.findById(id).orElse(null);
//        if (user == null) {
//            throw new CustomException("查询失败(Query Failure)");
//        }
//        return new ResponseBean(HttpStatus.OK.value(), "查询成功(Query was successful)", user);
//    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ResponseBean register(@RequestBody User user) {
        // 检查是否已存在该帐号
        User userTemp = userRepository.findByAccount(user.getAccount());
        if (userTemp != null && !StringUtils.isEmpty(userTemp.getPassword())) {
            throw new CustomUnauthorizedException("该帐号已存在(Account exist.)");
        }
        // 设置注册时间
        user.setRegTime(new Date());
        // 对密码进行哈希处理
        user.setPassword(PasswordUtil.hashPassword(user.getPassword()));
        user.setFriendsIds("[\"0\"]");
        user.setChatGroupIds("[\"0\"]");
        // 保存用户
        userRepository.save(user);
        return new ResponseBean(HttpStatus.OK.value(), "注册成功(Registration Success)", user);
    }

    /**
     * 更新用户
     */
    @PutMapping
    @RequiresPermissions(logical = Logical.AND, value = {"user:edit"})
    public ResponseBean update(@RequestBody User user) {
        User userTemp = userRepository.findByAccount(user.getAccount());
        if (userTemp == null) {
            throw new CustomUnauthorizedException("该帐号不存在(Account not exist.)");
        } else {
            user.setId(userTemp.getId());
        }
        if (!userTemp.getPassword().equals(user.getPassword())) {
            user.setPassword(PasswordUtil.hashPassword(user.getPassword()));
        }
        userRepository.save(user);
        return new ResponseBean(HttpStatus.OK.value(), "更新成功(Update Success)", user);
    }

//    /**
//     * 删除用户
//     */
//    @DeleteMapping("/{id}")
//    @RequiresPermissions(logical = Logical.AND, value = {"user:edit"})
//    public ResponseBean delete(@PathVariable("id") Integer id) {
//        User user = userRepository.findById(id).orElse(null);
//        if (user == null) {
//            throw new CustomException("删除失败，ID不存在(Deletion Failed. ID does not exist.)");
//        }
//        userRepository.delete(user);
//        return new ResponseBean(HttpStatus.OK.value(), "删除成功(Delete Success)", null);
//    }
//
//    /**
//     * 剔除在线用户
//     */
//    @DeleteMapping("/online/{id}")
//    @RequiresPermissions(logical = Logical.AND, value = {"user:edit"})
//    public ResponseBean deleteOnline(@PathVariable("id") Integer id) {
//        User user = userRepository.findById(id).orElse(null);
//        if (user == null || !jedisUtil.exists(Constant.PREFIX_SHIRO_REFRESH_TOKEN + user.getAccount())) {
//            throw new CustomException("剔除失败，Account不存在(Deletion Failed. Account does not exist.)");
//        }
//        jedisUtil.delKey(Constant.PREFIX_SHIRO_REFRESH_TOKEN + user.getAccount());
//        return new ResponseBean(HttpStatus.OK.value(), "剔除成功(Delete Success)", null);
//    }
    /**
     * 测试登录状态
     */
    @GetMapping("/hello")
    @RequiresAuthentication
    public ResponseBean hello() {
        return new ResponseBean(HttpStatus.OK.value(), "登录成功(Login Success)", "Hello, you are logged in!");
    }
}
