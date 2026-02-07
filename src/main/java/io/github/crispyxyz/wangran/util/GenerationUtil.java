package io.github.crispyxyz.wangran.util;

import java.util.UUID;

public class GenerationUtil {
    public static String generateUniqueUsername(String prefix) {
        return prefix + UUID.randomUUID().toString().replace("-", "");
    }
}
