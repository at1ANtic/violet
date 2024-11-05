package cn.atlantt1c.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {
    // 加密密码
    public static String hashPassword(String plainTextPassword) {
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
    }

    // 校验密码
    public static boolean checkPassword(String plainTextPassword, String hashedPassword) {
        return BCrypt.checkpw(plainTextPassword, hashedPassword);
    }

    public static void main(String[] args) {
        String password = "aa";
        String hashed = hashPassword(password);
        System.out.println("Hashed: " + hashed);

        String aaa = "$2a$10$3rzgUTAANktcS89zSt3F3uN3ME8YeIIz81nmTYL/jxkfDRNXs.abm";
        boolean isPasswordMatch = checkPassword(password, aaa);
        System.out.println("Password match: " + isPasswordMatch);
    }
}