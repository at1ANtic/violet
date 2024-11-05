package cn.atlantt1c.config.shiro;


import cn.atlantt1c.config.shiro.jwt.JwtToken;
import cn.atlantt1c.model.common.Constant;
import cn.atlantt1c.model.entity.Permission;
import cn.atlantt1c.model.entity.Role;
import cn.atlantt1c.model.entity.User;
import cn.atlantt1c.service.PermissionService;
import cn.atlantt1c.service.RoleService;
import cn.atlantt1c.util.JedisUtil;
import cn.atlantt1c.util.JwtUtil;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * 自定义Realm
 */
@Service
public class UserRealm extends AuthorizingRealm {

    @Resource
    private JedisUtil jedisUtil;
    @Resource
    private RoleService roleService;
    @Resource
    private PermissionService permissionService;

    @Override
    public boolean supports(AuthenticationToken authenticationToken) {
        return authenticationToken instanceof JwtToken;
    }

    /**
     * 只有当需要检测用户权限的时候才会调用此方法，例如checkRole,checkPermission之类的
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        String account = principalCollection.toString();
        User userDto = new User();
        userDto.setAccount(account);
        // 查询用户角色
        List<Role> roleDtos = roleService.findRolesByUser(userDto);
        for (Role roleDto : roleDtos) {
            if (roleDto != null) {
                // 添加角色
                simpleAuthorizationInfo.addRole(roleDto.getName());
                // 根据用户角色查询权限
                List<Permission> permissionDtos = permissionService.findPermissionByRole(roleDto);
                for (Permission permissionDto : permissionDtos) {
                    if (permissionDto != null) {
                        // 添加权限
                        simpleAuthorizationInfo.addStringPermission(permissionDto.getPerCode());
                    }
                }
            }
        }
        return simpleAuthorizationInfo;
    }

    /**
     * 默认使用此方法进行用户名正确与否验证，错误抛出异常即可。
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        String token = (String) authenticationToken.getCredentials();
        // 解密获得account，用于和数据库进行对比
        String account = JwtUtil.getClaim(token, Constant.ACCOUNT);
        // 帐号为空
        if (StringUtils.isEmpty(account)) {
            throw new AuthenticationException("Token中帐号为空(The account in Token is empty.)");
        }
        // 开始认证，要AccessToken认证通过，且Redis中存在RefreshToken，且两个Token时间戳一致
        if (JwtUtil.verify(token) && jedisUtil.exists(Constant.PREFIX_SHIRO_REFRESH_TOKEN + account)) {
            // 获取RefreshToken的时间戳
            String currentTimeMillisRedis = jedisUtil.getObject(Constant.PREFIX_SHIRO_REFRESH_TOKEN + account).toString();
            // 获取AccessToken时间戳，与RefreshToken的时间戳对比
            if (JwtUtil.getClaim(token, Constant.CURRENT_TIME_MILLIS).equals(currentTimeMillisRedis)) {
                return new SimpleAuthenticationInfo(account, token, "userRealm");
            }
        }
        throw new AuthenticationException("Token已过期(Token expired or incorrect.)");
    }

    @Override
    public String getAuthorizationCacheName() {
        return "authCache";
    }
}
