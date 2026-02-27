package io.github.crispyxyz.wangran.service;

import io.github.crispyxyz.wangran.request.LoginRequest;
import io.github.crispyxyz.wangran.response.AccountResponse;
import io.github.crispyxyz.wangran.response.LoginResponse;

/**
 * 认证服务接口，提供注册、审核和登录功能
 */
public interface AuthService {
    /**
     * 注册新账户
     *
     * @param phoneNumber 手机号
     * @param password    密码
     * @param isMerchant  是否为商户注册
     * @return 注册成功后的账户信息
     */
    AccountResponse register(String phoneNumber, String password, boolean isMerchant);

    /**
     * 账户登录验证
     *
     * @param loginRequest 登录请求参数
     * @return 登录成功后的 JWT token 和账户信息
     */
    LoginResponse login(LoginRequest loginRequest);
}
