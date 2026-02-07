package io.github.crispyxyz.wangran.util;

import io.github.crispyxyz.wangran.dto.ResponseDTO;
import io.github.crispyxyz.wangran.exception.BusinessException;

public class ResponseUtil {
    public static <T> ResponseDTO<T> success(T data) {
        return new ResponseDTO<>(true, "success", data);
    }

    public static ResponseDTO<?> error(BusinessException exception) {
        return error(exception.getMessage());
    }

    public static ResponseDTO<?> error(String message) {
        return new ResponseDTO<>(false, message, null);
    }
}
