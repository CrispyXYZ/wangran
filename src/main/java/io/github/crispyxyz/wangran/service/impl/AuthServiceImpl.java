package io.github.crispyxyz.wangran.service.impl;

import io.github.crispyxyz.wangran.exception.*;
import io.github.crispyxyz.wangran.model.Merchant;
import io.github.crispyxyz.wangran.model.User;
import io.github.crispyxyz.wangran.request.LoginRequest;
import io.github.crispyxyz.wangran.response.AccountResponse;
import io.github.crispyxyz.wangran.response.LoginResponse;
import io.github.crispyxyz.wangran.response.MerchantResponse;
import io.github.crispyxyz.wangran.response.UserResponse;
import io.github.crispyxyz.wangran.service.AuthService;
import io.github.crispyxyz.wangran.service.MerchantService;
import io.github.crispyxyz.wangran.service.UserService;
import io.github.crispyxyz.wangran.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class AuthServiceImpl implements AuthService {

    private final ModelMapper modelMapper;
    private final UserService userService;
    private final MerchantService merchantService;

    /**
     * 用户/商户注册，不允许同一手机号重复注册
     *
     * @param phoneNumber 手机号
     * @param password    密码
     * @param isMerchant  是否为商户注册
     * @return 注册成功后的账户信息
     * @throws ResourceConflictException 当手机号已被注册时抛出
     * @throws SystemException           当SHA-256算法不可用时
     */
    @Transactional
    @Override
    public AccountResponse register(String phoneNumber, String password, boolean isMerchant) {
        log.debug("开始处理注册，phoneNumber={}", phoneNumber);

        // 检查手机号是否已被注册
        if (userService.existPhoneNumber(phoneNumber) || merchantService.existPhoneNumber(phoneNumber)) {
            throw new ResourceConflictException("该手机号已被注册");
        }

        // 对输入的密码进行 SHA-256 编码
        byte[] passwordSha256 = SecurityUtil.computeSha256(password);

        if (isMerchant) {
            Merchant merchant = merchantService.create(phoneNumber, passwordSha256);
            return modelMapper.map(merchant, MerchantResponse.class);
        } else {
            User user = userService.create(phoneNumber, passwordSha256);
            return modelMapper.map(user, UserResponse.class);
        }
    }

    /**
     * 用户/商户登录验证。
     * 支持管理员登录、商户id、手机号登录。其中手机号优先匹配普通用户
     *
     * @param loginRequest 登录请求参数，包含标识符(手机号或商户ID)和密码
     * @return 登录成功后的 JWT token 和账户信息
     * @throws AuthException             当密码验证失败时抛出
     * @throws ResourceNotFoundException 当用户不存在时抛出
     * @throws MerchantApprovalException 当商户处于审核中或审核不通过时抛出
     * @throws SystemException           当SHA-256算法不可用时
     */
    @Transactional(readOnly = true)
    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        String identifier = loginRequest.getIdentifier();
        String password = loginRequest.getPassword();
        log.debug("开始处理登录，identifier={}", identifier);

        // 判断管理员登录
        if ("AdminMaster".equals(identifier) && "AdminMaster".equals(password)) {
            log.debug("登录为管理员成功，identifier={}", identifier);
            // 成功
            return new LoginResponse(SecurityUtil.createJwtToken(0, "admin"), null);
        }

        // 判断商户 id 登录
        if (identifier.startsWith("mid_")) {
            log.debug("商户id登录，identifier={}", identifier);
            // 通过id获取数据库中的对应商户
            Merchant merchant = merchantService.findByMerchantId(identifier);

            if (merchant != null) {
                return handleMerchantLogin(merchant, password);
            }
        }

        // 商户 id 匹配失败，尝试普通用户手机号登录
        User user = userService.findByPhoneNumber(identifier);
        if (user != null) {
            return handleUserLogin(user, password);
        }

        // 普通用户手机号匹配失败，尝试商户手机号登录
        Merchant merchant = merchantService.findByPhoneNumber(identifier);
        if (merchant != null) {
            return handleMerchantLogin(merchant, password);
        }

        // 所有登录方式均失败，用户不存在
        throw new ResourceNotFoundException("不存在该用户");
    }

    private @NonNull LoginResponse handleUserLogin(User user, String password) {
        // 匹配密码
        boolean success = SecurityUtil.verifySha256(password, user.getPasswordSha256());
        if (!success) {
            throw new AuthException("密码错误");
        }
        // 成功
        return new LoginResponse(
            SecurityUtil.createJwtToken(user.getId(), "user"),
                                 modelMapper.map(user, UserResponse.class)
        );
    }

    private @NonNull LoginResponse handleMerchantLogin(Merchant merchant, String password) {
        // 先验证密码
        boolean success = SecurityUtil.verifySha256(password, merchant.getPasswordSha256());
        if (!success) {
            throw new AuthException("密码错误");
        }

        // 验证审核状态
        if (merchant.getApprovalStatus() == 0) {
            throw new MerchantApprovalException("审核中，请等待");
        }
        if (merchant.getApprovalStatus() == 2) {
            throw new MerchantApprovalException("审核不通过，原因：" + merchant.getRejectReason());
        }

        // 成功
        return new LoginResponse(
            SecurityUtil.createJwtToken(merchant.getId(), "merchant"),
            modelMapper.map(merchant, MerchantResponse.class)
        );
    }
}
