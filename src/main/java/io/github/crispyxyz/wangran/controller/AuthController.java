package io.github.crispyxyz.wangran.controller;

import io.github.crispyxyz.wangran.dto.*;
import io.github.crispyxyz.wangran.service.AuthService;
import io.github.crispyxyz.wangran.util.ResponseUtil;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证相关接口控制器：处理用户注册、登录和审核等认证流程
 */
@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 用户注册接口
     *
     * @param registerRequestDTO 注册请求参数
     * @return 注册成功的账户信息
     */
    @PostMapping("/register")
    public ResponseEntity<ResponseDTO<?>> register(@Valid @RequestBody RegisterRequestDTO registerRequestDTO) {
        log.info("接收注册请求: {}", registerRequestDTO);
        AccountDTO data = authService.register(registerRequestDTO);
        log.info("注册请求成功: {}", data);
        return ResponseEntity.ok(ResponseUtil.success(data));
    }

    /**
     * 用户登录接口
     *
     * @param loginRequestDTO 登录请求参数
     * @return 登录成功的账户信息
     */
    @PostMapping("/login")
    public ResponseEntity<ResponseDTO<?>> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        log.info("接收登录请求: {}", loginRequestDTO);
        LoginDTO data = authService.login(loginRequestDTO);
        log.info("登录请求成功: {}", data);
        return ResponseEntity.ok(ResponseUtil.success(data));
    }

    /**
     * 审核接口
     *
     * @param reviewRequestDTO 审核请求参数
     * @return 审核结果
     */
    @PostMapping("/review")
    public ResponseEntity<ResponseDTO<?>> review(@Valid @RequestBody ReviewRequestDTO reviewRequestDTO) {
        log.info("接收审核请求: {}", reviewRequestDTO);
        ReviewResultDTO data = authService.review(reviewRequestDTO);
        log.info("审核请求成功: {}", data);
        return ResponseEntity.ok(ResponseUtil.success(data));
    }
}
