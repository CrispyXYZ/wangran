package io.github.crispyxyz.wangran.controller;

import io.github.crispyxyz.wangran.model.Merchant;
import io.github.crispyxyz.wangran.request.LoginRequest;
import io.github.crispyxyz.wangran.request.RegisterRequest;
import io.github.crispyxyz.wangran.request.ReviewRequest;
import io.github.crispyxyz.wangran.response.AccountResponse;
import io.github.crispyxyz.wangran.response.BaseResponse;
import io.github.crispyxyz.wangran.response.LoginResponse;
import io.github.crispyxyz.wangran.service.AuthService;
import io.github.crispyxyz.wangran.service.MerchantService;
import io.github.crispyxyz.wangran.util.ResponseUtil;
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
 */
@RestController
@RequestMapping("/auth")
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class AuthController {

    private final AuthService authService;
    private final MerchantService merchantService;

    /**
     * 用户注册接口
     *
     * @param registerRequest 注册请求参数
     * @return 注册成功的账户信息
     */
    @PostMapping("/register")
    public ResponseEntity<BaseResponse<?>> register(@Valid @RequestBody RegisterRequest registerRequest) {
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
    public ResponseEntity<BaseResponse<?>> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("接收登录请求: {}", loginRequest);
        LoginResponse data = authService.login(loginRequest);
        log.info("登录请求成功: {}", data);
        return ResponseEntity.ok(ResponseUtil.success(data));
    }

    // TODO 转移到管理员模块

    /**
     * 审核接口
     *
     * @param reviewRequest 审核请求参数
     * @return 审核结果
     */
    @PostMapping("/review")
    public BaseResponse<Merchant> review(@Valid @RequestBody ReviewRequest reviewRequest) {
        log.info("接收审核请求: {}", reviewRequest);
        Merchant data = merchantService.reviewMerchant(
            reviewRequest.getMerchantPhoneNumber(),
            reviewRequest.getApproved(),
            reviewRequest.getRejectReason()
        );
        log.info("审核请求成功: {}", data);
        return ResponseUtil.success(data);
    }
}
