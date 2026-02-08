package io.github.crispyxyz.wangran.util;

import io.github.crispyxyz.wangran.dto.ResponseDTO;
import io.github.crispyxyz.wangran.exception.BusinessException;

public class ResponseUtil {
    /**
     * 构建成功响应
     *
     * @param data 响应数据
     * @return 包含成功标识和数据的响应对象
     */
    public static <T> ResponseDTO<T> success(T data) {
        return new ResponseDTO<>(true, "success", data);
    }

    /**
     * 构建错误响应
     *
     * @param exception 业务异常
     * @return 包含异常信息的响应对象
     */
    public static ResponseDTO<?> error(BusinessException exception) {
        return error(exception.getMessage());
    }

    /**
     * 构建错误响应
     *
     * @param message 错误消息
     * @return 包含错误消息的响应对象
     */
    public static ResponseDTO<?> error(String message) {
        return new ResponseDTO<>(false, message, null);
    }
}
