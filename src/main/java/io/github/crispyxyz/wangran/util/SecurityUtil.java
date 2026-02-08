package io.github.crispyxyz.wangran.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.crispyxyz.wangran.exception.SystemException;
import lombok.extern.slf4j.Slf4j;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;

@Slf4j
public class SecurityUtil {

    private static final String JWT_KEY = resolveSecretKey();

    private static String resolveSecretKey() {
        String envKey = System.getenv("JWT_SECRET_KEY");

        if (envKey != null) {
            log.info("正在使用环境变量中的JWT_SECRET_KEY");
            return envKey;
        }

        log.warn("从环境变量获取JWT_SECRET_KEY失败，正在使用测试密钥");
        return "test-key@1145141919810";
    }

    /**
     * 计算字符串的SHA-256哈希值
     *
     * @param input 输入字符串
     * @return SHA-256哈希值的字节数组
     * @throws SystemException 当SHA-256算法不可用时抛出
     */
    public static byte[] computeSha256(String input) {
        return computeSha256(input.getBytes());
    }

    /**
     * 计算字节数组的SHA-256哈希值
     *
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
     *
     * @param input        输入字符串
     * @param expectedHash 预期的哈希值
     * @return 匹配返回true，否则返回false
     * @throws SystemException 当SHA-256算法不可用时抛出
     */
    public static boolean verifySha256(String input, byte[] expectedHash) {
        byte[] hash = computeSha256(input);
        return Arrays.equals(hash, expectedHash);
    }

    /**
     * 创建 JWT token，包含昵称、角色、时间信息
     * @param username 昵称
     * @param role 角色
     * @return JWT token 字符串
     */
    public static String createJwtToken(String username, String role) {
        return JWT.create()
                  .withSubject(username)
                  .withIssuedAt(new Date())
                  .withExpiresAt(new Date(System.currentTimeMillis() + 60 * 1000))
                  // 1 分钟（用于测试）
                  .withClaim("role", role)
                  .sign(Algorithm.HMAC256(JWT_KEY));
    }

    /**
     * 验证并解析 JWT token
     *
     * @param token JWT token 字符串
     * @return 解码后的 JWT 对象
     * @throws JWTVerificationException JWT token 无效时抛出
     */
    public static DecodedJWT verifyJwtToken(String token) {
        return JWT.require(Algorithm.HMAC256(JWT_KEY)).build().verify(token);
    }
}
