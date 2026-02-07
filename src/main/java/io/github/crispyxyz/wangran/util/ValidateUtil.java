package io.github.crispyxyz.wangran.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class ValidateUtil {
    public static byte[] computeSha256(String input) {
        return computeSha256(input.getBytes());
    }

    public static byte[] computeSha256(byte[] input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(input);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256算法不可用", e);
        }
    }

    public static boolean verifySha256(String input, byte[] expectedHash) {
        byte[] hash = computeSha256(input);
        return Arrays.equals(hash, expectedHash);
    }
}
