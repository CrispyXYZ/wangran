package io.github.crispyxyz.wangran.handler;

import io.github.crispyxyz.wangran.dto.ResponseDTO;
import io.github.crispyxyz.wangran.exception.*;
import io.github.crispyxyz.wangran.util.ResponseUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(SystemException.class)
    public ResponseEntity<ResponseDTO<?>> handleSystemException(SystemException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseUtil.error(e));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ResponseDTO<?>> handleResourceNotFoundException(ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseUtil.error(e));
    }

    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<ResponseDTO<?>> handleResourceConflictException(ResourceConflictException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ResponseUtil.error(e));
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ResponseDTO<?>> handleAuthException(AuthException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseUtil.error(e));
    }

    @ExceptionHandler(MerchantApprovalException.class)
    public ResponseEntity<ResponseDTO<?>> handleMerchantApprovalException(MerchantApprovalException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseUtil.error(e));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDTO<?>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String msg = "请求参数验证失败：" + e.getBindingResult().getAllErrors().stream().map(err -> {
            if (err instanceof FieldError fieldError) {
                return fieldError.getField() + ": " + fieldError.getDefaultMessage();
            }
            return err.getDefaultMessage();
        }).collect(Collectors.joining("; "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtil.error(msg));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ResponseDTO<?>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseUtil.error("请求参数错误：" + e.getMessage()));
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ResponseDTO<?>> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(ResponseUtil.error("不支持的Content-Type类型，请检查Content-Type是否为下列之一： " + e.getSupportedMediaTypes()));
    }
}
