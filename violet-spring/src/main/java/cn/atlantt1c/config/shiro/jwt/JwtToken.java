package cn.atlantt1c.config.shiro.jwt;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * JwtToken
 */
public class JwtToken implements AuthenticationToken {
    /**
     * Token
     */
    private final String token;

    public JwtToken(String token) {
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}
