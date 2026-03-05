package io.github.crispyxyz.wangran.controller;

import io.github.crispyxyz.wangran.request.LoginRequest;
import io.github.crispyxyz.wangran.request.RegisterRequest;
import io.github.crispyxyz.wangran.response.AccountResponse;
import io.github.crispyxyz.wangran.response.BaseResponse;
import io.github.crispyxyz.wangran.response.LoginResponse;
import io.github.crispyxyz.wangran.service.AuthService;
import io.github.crispyxyz.wangran.util.ResponseUtil;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证相关接口控制器：处理用户注册、登录和审核等认证流程
 * 权限说明：此接口将对所有访客开放
 */
@RestController
@RequestMapping("/auth")
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@SecurityRequirements
public class AuthController {

    private final AuthService authService;

    /**
     * 用户注册接口
     *
     * @param registerRequest 注册请求参数
     * @return 注册成功的账户信息
     */
    @PostMapping("/register")
    public ResponseEntity<BaseResponse<AccountResponse>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        log.info("接收注册请求: {}", registerRequest);
        AccountResponse data = authService.register(
            registerRequest.getPhoneNumber(),
            registerRequest.getPassword(),
            registerRequest.getMerchant()
        );
        log.info("注册请求成功: {}", data);
        return ResponseEntity.ok(ResponseUtil.success(data));
    }

    /**
     * 用户登录接口
     *
     * @param loginRequest 登录请求参数
     * @return 登录成功的账户信息
     */
    @PostMapping("/login")
    public ResponseEntity<BaseResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("接收登录请求: {}", loginRequest);
        LoginResponse data = authService.login(loginRequest);
        log.info("登录请求成功: {}", data);
        return ResponseEntity.ok(ResponseUtil.success(data));
    }
}
