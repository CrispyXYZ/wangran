package io.github.crispyxyz.wangran.util;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class GenerationUtil {
    /**
     * 生成唯一昵称，此方法基于uuid
     *
     * @param prefix 前缀
     * @return 前缀与uuid拼接的字符串
     */
    public static String generateUniqueUsername(String prefix) {
        return prefix + UUID.randomUUID()
                            .toString()
                            .replace("-", "");
    }

    /**
     * 生成唯一序列
     *
     * @param prefix 前缀
     * @return 前缀、当前时间、随机数的拼接
     */
    public static String generateUniqueSequence(String prefix) {
        long timestamp = System.currentTimeMillis();
        int random = ThreadLocalRandom.current()
                                      .nextInt(1000);
        String randomStr = "%03d".formatted(random);
        return prefix + timestamp + randomStr;
    }

}
