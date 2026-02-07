package io.github.crispyxyz.wangran.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.crispyxyz.wangran.dto.*;
import io.github.crispyxyz.wangran.entity.Merchant;
import io.github.crispyxyz.wangran.entity.User;
import io.github.crispyxyz.wangran.exception.*;
import io.github.crispyxyz.wangran.mapper.MerchantMapper;
import io.github.crispyxyz.wangran.mapper.UserMapper;
import io.github.crispyxyz.wangran.service.AuthService;
import io.github.crispyxyz.wangran.util.GenerationUtil;
import io.github.crispyxyz.wangran.util.ValidateUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final ModelMapper modelMapper;
    private final UserMapper userMapper;
    private final MerchantMapper merchantMapper;

    @Autowired
    public AuthServiceImpl(ModelMapper modelMapper, UserMapper userMapper, MerchantMapper merchantMapper) {
        this.modelMapper = modelMapper;
        this.userMapper = userMapper;
        this.merchantMapper = merchantMapper;
    }

    /**
     * 用户/商户注册，不允许同一手机号重复注册
     *
     * @param registerRequestDTO 注册请求参数，包含手机号、密码和用户类型
     * @return 注册成功后的账户信息
     * @throws ResourceConflictException 当手机号已被注册时抛出
     */
    @Transactional
    @Override
    public AccountDTO register(RegisterRequestDTO registerRequestDTO) {
        String phoneNumber = registerRequestDTO.getPhoneNumber();

        if (existUser(phoneNumber) || existMerchant(phoneNumber)) {
            throw new ResourceConflictException("该手机号已被注册");
        }

        byte[] passwordSha256 = ValidateUtil.computeSha256(registerRequestDTO.getPassword());

        if (registerRequestDTO.isMerchant()) {
            Merchant merchant = new Merchant();
            merchant.setPhoneNumber(registerRequestDTO.getPhoneNumber());
            merchant.setPasswordSha256(passwordSha256);
            merchant.setApprovalStatus(0);
            merchantMapper.insert(merchant);
            return modelMapper.map(merchant, MerchantDTO.class);
        } else {
            User user = new User();
            user.setPhoneNumber(registerRequestDTO.getPhoneNumber());
            user.setPasswordSha256(passwordSha256);
            user.setUsername(GenerationUtil.generateUniqueUsername("user_"));
            userMapper.insert(user);
            return modelMapper.map(user, UserDTO.class);
        }
    }

    /**
     * 用户/商户登录验证。
     * 支持管理员登录、商户id、手机号登录。其中手机号优先匹配普通用户
     *
     * @param loginRequestDTO 登录请求参数，包含标识符(手机号或商户ID)和密码
     * @return 登录成功后的账户信息
     * @throws AuthException 当密码验证失败时抛出
     * @throws ResourceNotFoundException 当用户不存在时抛出
     * @throws MerchantApprovalException 当商户处于审核中或审核不通过时抛出
     */
    @Transactional(readOnly = true)
    @Override
    public AccountDTO login(LoginRequestDTO loginRequestDTO) {
        String identifier = loginRequestDTO.getIdentifier();
        String password = loginRequestDTO.getPassword();

        if ("AdminMaster".equals(identifier) && "AdminMaster".equals(password)) {
            log.info("管理员登录");
            return null;
        }

        if (identifier.startsWith("mid_")) {
            LambdaQueryWrapper<Merchant> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Merchant::getMerchantId, identifier);
            Merchant merchant = merchantMapper.selectOne(queryWrapper);
            if (merchant != null) {
                boolean success = ValidateUtil.verifySha256(password, merchant.getPasswordSha256());
                if (success) {
                    return modelMapper.map(merchant, MerchantDTO.class);
                } else {
                    throw new AuthException("密码错误");
                }
            }
            throw new ResourceNotFoundException("不存在该用户");
        }

        LambdaQueryWrapper<User> wrapperUser = new LambdaQueryWrapper<>();
        wrapperUser.eq(User::getPhoneNumber, identifier);

        User user = userMapper.selectOne(wrapperUser);

        if (user != null) {
            boolean success = ValidateUtil.verifySha256(password, user.getPasswordSha256());
            if (success) {
                return modelMapper.map(user, UserDTO.class);
            } else {
                throw new AuthException("密码错误");
            }
        }

        LambdaQueryWrapper<Merchant> wrapperMerchant = new LambdaQueryWrapper<>();
        wrapperMerchant.eq(Merchant::getPhoneNumber, identifier);

        Merchant merchant = merchantMapper.selectOne(wrapperMerchant);

        if (merchant != null) {
            boolean success = ValidateUtil.verifySha256(password, merchant.getPasswordSha256());
            if (!success) {
                throw new AuthException("密码错误");
            }

            if (merchant.getApprovalStatus() == 0) {
                throw new MerchantApprovalException("审核中，请等待");
            }
            if (merchant.getApprovalStatus() == 2) {
                throw new MerchantApprovalException("审核不通过，原因："+merchant.getRejectReason());
            }

            return modelMapper.map(merchant, MerchantDTO.class);
        }

        throw new ResourceNotFoundException("不存在该用户");
    }

    /**
     * 审核商户注册。
     * 审核通过则分配商户id和昵称，审核状态变更为1，并把拒绝原因设为空字符串。
     * 审核不通过则将审核状态变更为2，记录拒绝原因
     * @param reviewRequestDTO 审核请求参数，包含商户手机号和审核结果
     * @return 审核结果信息
     * @throws ResourceNotFoundException 当商户不存在时抛出
     * @throws SystemException 当数据库数据异常时抛出
     */
    @Override
    public ReviewResultDTO review(ReviewRequestDTO reviewRequestDTO) {
        String merchantPhoneNumber = reviewRequestDTO.getMerchantPhoneNumber();
        LambdaQueryWrapper<Merchant> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Merchant::getPhoneNumber, merchantPhoneNumber);

        Long count = merchantMapper.selectCount(queryWrapper);
        if (count == 0) {
            throw new ResourceNotFoundException("商户不存在");
        }
        if (count != 1) {
            throw new SystemException("数据库错误");
        }
        Merchant merchant = merchantMapper.selectOne(queryWrapper);

        ReviewResultDTO result = new ReviewResultDTO();
        result.setPhoneNumber(merchantPhoneNumber);

        if (reviewRequestDTO.isApproved()) {
            if (merchant.getMerchantId() == null) {
                String id = "mid_" + System.currentTimeMillis();
                merchant.setMerchantId(id);
            }

            if (merchant.getUsername() == null) {
                String username = GenerationUtil.generateUniqueUsername("merchant_");
                merchant.setUsername(username);
            }

            merchant.setApprovalStatus(1);
            merchant.setRejectReason("");
            merchantMapper.updateById(merchant);

            result.setApproved(true);
        } else {
            merchant.setApprovalStatus(2);
            merchant.setRejectReason(reviewRequestDTO.getRejectReason());
            merchantMapper.updateById(merchant);

            result.setApproved(false);
        }
        result.setMerchantId(merchant.getMerchantId());
        result.setUsername(merchant.getUsername());
        return result;
    }

    /**
     * 检查手机号是否已注册为用户
     * @param phoneNumber 手机号
     * @return 是否存在该用户
     */
    private boolean existUser(String phoneNumber) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhoneNumber, phoneNumber);

        Long count = userMapper.selectCount(queryWrapper);
        return count != null && count > 0;
    }

    /**
     * 检查手机号是否已注册为商户
     * @param phoneNumber 手机号
     * @return 是否存在该商户
     */
    private boolean existMerchant(String phoneNumber) {
        LambdaQueryWrapper<Merchant> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Merchant::getPhoneNumber, phoneNumber);

        Long count = merchantMapper.selectCount(queryWrapper);
        return count != null && count > 0;
    }
}
