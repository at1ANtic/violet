package cn.atlantt1c.util;

import java.util.UUID;

public class UidUtil {

    // 生成随机的UID
    public static String generateUid() {
        return UUID.randomUUID().toString();
    }

    public static void main(String[] args) {
        // 测试生成UID
        String uid = generateUid();
        System.out.println("Generated UID: " + uid);
    }
}

