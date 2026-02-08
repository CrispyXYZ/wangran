package io.github.crispyxyz.wangran.service;

import io.github.crispyxyz.wangran.dto.*;

/**
 * 认证服务接口，提供注册、审核和登录功能
 */
public interface AuthService {
    /**
     * 注册新账户
     *
     * @param registerRequestDTO 注册请求参数
     * @return 注册成功后的账户信息
     */
    AccountDTO register(RegisterRequestDTO registerRequestDTO);

    /**
     * 账户登录验证
     *
     * @param loginRequestDTO 登录请求参数
     * @return 登录成功后的 JWT token 和账户信息
     */
    LoginDTO login(LoginRequestDTO loginRequestDTO);

    /**
     * 审核商户注册申请
     *
     * @param reviewRequestDTO 审核请求参数
     * @return 审核结果信息
     */
    ReviewResultDTO review(ReviewRequestDTO reviewRequestDTO);
}
