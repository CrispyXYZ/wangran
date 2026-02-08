package io.github.crispyxyz.wangran.util;

import java.util.UUID;

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
     * 生成唯一商户id，此方法基于当前时间
     *
     * @return mid_与当前时间的拼接
     */
    public static String generateUniqueMerchantId() {
        return "mid_" + System.currentTimeMillis();
    }

}
