package cn.atlantt1c.model.common;

/**
 * 常量
 */
public class Constant {

    private Constant() {
    }

    /**
     * redis-OK
     */
    public static final String OK = "OK";
    /**
     * redis-key-前缀-shiro:refresh_token:
     */
    public static final String PREFIX_SHIRO_REFRESH_TOKEN = "shiro:refresh_token:";

    /**
     * JWT-account:
     */
    public static final String ACCOUNT = "account";

    /**
     * JWT-currentTimeMillis:
     */
    public static final String CURRENT_TIME_MILLIS = "ts";

}
