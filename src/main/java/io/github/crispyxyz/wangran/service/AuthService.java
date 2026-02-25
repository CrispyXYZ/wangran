package io.github.crispyxyz.wangran.service;

import io.github.crispyxyz.wangran.request.LoginRequest;
import io.github.crispyxyz.wangran.request.RegisterRequest;
import io.github.crispyxyz.wangran.request.ReviewRequest;
import io.github.crispyxyz.wangran.response.AccountResponse;
import io.github.crispyxyz.wangran.response.LoginResponse;
import io.github.crispyxyz.wangran.response.ReviewResponse;

/**
 * 认证服务接口，提供注册、审核和登录功能
 */
public interface AuthService {
    /**
     * 注册新账户
     *
     * @param registerRequest 注册请求参数
     * @return 注册成功后的账户信息
     */
    AccountResponse register(RegisterRequest registerRequest);

    /**
     * 账户登录验证
     *
     * @param loginRequest 登录请求参数
     * @return 登录成功后的 JWT token 和账户信息
     */
    LoginResponse login(LoginRequest loginRequest);

    /**
     * 审核商户注册申请
     *
     * @param reviewRequest 审核请求参数
     * @return 审核结果信息
     */
    ReviewResponse review(ReviewRequest reviewRequest);
}
