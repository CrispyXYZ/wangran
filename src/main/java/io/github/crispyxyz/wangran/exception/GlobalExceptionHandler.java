package io.github.crispyxyz.wangran.exception;

import io.github.crispyxyz.wangran.response.BaseResponse;
import io.github.crispyxyz.wangran.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.stream.Collectors;

/**
 * 全局异常处理器：在 Controller 层处理抛出的各种异常，返回规范的 HTTP 响应
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(SystemException.class)
    public ResponseEntity<BaseResponse<Void>> handleSystemException(SystemException e) {
        log.error("系统异常：", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body(ResponseUtil.error(e));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<BaseResponse<Void>> handleResourceNotFoundException(ResourceNotFoundException e) {
        log.warn("资源未找到： {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body(ResponseUtil.error(e));
    }

    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<BaseResponse<Void>> handleResourceConflictException(ResourceConflictException e) {
        log.warn("资源冲突： {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                             .body(ResponseUtil.error(e));
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<BaseResponse<Void>> handleAuthException(AuthException e) {
        log.warn("认证异常： {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                             .body(ResponseUtil.error(e));
    }

    @ExceptionHandler(MerchantApprovalException.class)
    public ResponseEntity<BaseResponse<Void>> handleMerchantApprovalException(MerchantApprovalException e) {
        log.warn("商户审核异常： {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                             .body(ResponseUtil.error(e));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        // 拼接所有校验失败字段的错误信息
        String msg = "请求参数验证失败：" + e.getBindingResult()
                                            .getAllErrors()
                                            .stream()
                                            .map(err -> {
                                                if (err instanceof FieldError fieldError) {
                                                    return fieldError.getField() + ": " +
                                                           fieldError.getDefaultMessage();
                                                }
                                                return err.getDefaultMessage();
                                            })
                                            .collect(Collectors.joining("; "));

        log.warn(msg);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body(ResponseUtil.error(msg));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<BaseResponse<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        String msg = "请求参数错误：" + e.getMessage();
        log.warn(msg);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body(ResponseUtil.error(msg));
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<BaseResponse<Void>> handleHttpMediaTypeNotSupportedException(
        HttpMediaTypeNotSupportedException e
    ) {
        String msg = "不支持的Content-Type类型，请检查Content-Type是否为下列之一： " + e.getSupportedMediaTypes();
        log.warn(msg);
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                             .body(ResponseUtil.error(msg));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<BaseResponse<Void>> handleMethodArgumentTypeMismatchException(
        MethodArgumentTypeMismatchException e
    ) {
        log.warn("匹配异常： {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body(ResponseUtil.error(e.getMessage()));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<BaseResponse<Void>> handleBusinessException(BusinessException e) {
        log.warn("业务异常： {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                             .body(ResponseUtil.error(e.getMessage()));
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<BaseResponse<Void>> handleNoHandlerFoundException(NoHandlerFoundException e) {
        log.warn("请求路径不存在： {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body(ResponseUtil.error("请求路径不存在"));
    }
}
