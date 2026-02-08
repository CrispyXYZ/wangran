package io.github.crispyxyz.wangran.util;

import io.github.crispyxyz.wangran.exception.SystemException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class ValidateUtil {
    /**
     * 计算字符串的SHA-256哈希值
     * @param input 输入字符串
     * @return SHA-256哈希值的字节数组
     * @throws SystemException 当SHA-256算法不可用时抛出
     */
    public static byte[] computeSha256(String input) {
        return computeSha256(input.getBytes());
    }

    /**
     * 计算字节数组的SHA-256哈希值
     * @param input 输入字节数组
     * @return SHA-256哈希值的字节数组
     * @throws SystemException 当SHA-256算法不可用时抛出
     */
    public static byte[] computeSha256(byte[] input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(input);
        } catch (NoSuchAlgorithmException e) {
            throw new SystemException("SHA-256算法不可用");
        }
    }

    /**
     * 验证字符串的SHA-256哈希值是否匹配
     * @param input 输入字符串
     * @param expectedHash 预期的哈希值
     * @return 匹配返回true，否则返回false
     * @throws SystemException 当SHA-256算法不可用时抛出
     */
    public static boolean verifySha256(String input, byte[] expectedHash) {
        byte[] hash = computeSha256(input);
        return Arrays.equals(hash, expectedHash);
    }
}
