package cn.atlantt1c.config.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Slf4j
@Configuration
public class JedisConfig {

    @Value("${redis.host}")
    private String host;

    @Value("${redis.port}")
    private int port;

    @Value("${redis.password:}") // 使用默认值空字符串，避免密码未配置时报错
    private String password;

    @Value("${redis.timeout:2000}") // 默认超时时间 2000ms
    private int timeout;

    @Bean
    public JedisPool redisPoolFactory() {
        try {
            JedisPoolConfig poolConfig = new JedisPoolConfig();
            // 设置池的配置
            poolConfig.setMaxTotal(10);
            poolConfig.setMaxIdle(5);
            poolConfig.setMinIdle(1);

            JedisPool jedisPool = new JedisPool(poolConfig, host, port, timeout, password);
            log.info("初始化Redis连接池JedisPool成功!");
            return jedisPool;
        } catch (Exception e) {
            log.error("初始化Redis连接池JedisPool异常:{}", e.getMessage());
        }
        return null;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
