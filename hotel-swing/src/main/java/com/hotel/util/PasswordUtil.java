package com.hotel.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 密码工具类 - 使用SHA-256加密
 */
public class PasswordUtil {
    
    /**
     * 使用SHA-256加密密码
     */
    public static String encrypt(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256算法不可用", e);
        }
    }
    
    /**
     * 验证密码
     */
    public static boolean verify(String rawPassword, String encryptedPassword) {
        return encrypt(rawPassword).equals(encryptedPassword);
    }
}
