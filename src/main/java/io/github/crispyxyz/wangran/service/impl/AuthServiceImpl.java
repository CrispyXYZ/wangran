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
import io.github.crispyxyz.wangran.util.SecurityUtil;
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
     * @throws SystemException           当SHA-256算法不可用时
     */
    @Transactional
    @Override
    public AccountDTO register(RegisterRequestDTO registerRequestDTO) {
        String phoneNumber = registerRequestDTO.getPhoneNumber();
        log.debug("开始处理注册，phoneNumber={}", phoneNumber);

        // 检查手机号是否已被注册
        if (existUser(phoneNumber) || existMerchant(phoneNumber)) {
            throw new ResourceConflictException("该手机号已被注册");
        }

        // 对输入的密码进行 SHA-256 编码
        byte[] passwordSha256 = SecurityUtil.computeSha256(registerRequestDTO.getPassword());

        if (registerRequestDTO.getMerchant()) {
            // 商户注册逻辑
            Merchant merchant = new Merchant();
            merchant.setPhoneNumber(registerRequestDTO.getPhoneNumber());
            merchant.setPasswordSha256(passwordSha256);
            merchant.setApprovalStatus(0);
            merchantMapper.insert(merchant);

            log.debug("注册为商户成功，phoneNumber={}", phoneNumber);
            return modelMapper.map(merchant, MerchantDTO.class);
        } else {
            // 用户注册逻辑
            User user = new User();
            user.setPhoneNumber(registerRequestDTO.getPhoneNumber());
            user.setPasswordSha256(passwordSha256);
            user.setUsername(GenerationUtil.generateUniqueUsername("user_"));
            userMapper.insert(user);

            log.debug("注册为普通用户成功，phoneNumber={}", phoneNumber);
            return modelMapper.map(user, UserDTO.class);
        }
    }

    /**
     * 用户/商户登录验证。
     * 支持管理员登录、商户id、手机号登录。其中手机号优先匹配普通用户
     *
     * @param loginRequestDTO 登录请求参数，包含标识符(手机号或商户ID)和密码
     * @return 登录成功后的 JWT token 和账户信息
     * @throws AuthException             当密码验证失败时抛出
     * @throws ResourceNotFoundException 当用户不存在时抛出
     * @throws MerchantApprovalException 当商户处于审核中或审核不通过时抛出
     * @throws SystemException           当SHA-256算法不可用时
     */
    @Transactional(readOnly = true)
    @Override
    public LoginDTO login(LoginRequestDTO loginRequestDTO) {
        String identifier = loginRequestDTO.getIdentifier();
        String password = loginRequestDTO.getPassword();
        log.debug("开始处理登录，identifier={}", identifier);

        // 判断管理员登录
        if ("AdminMaster".equals(identifier) && "AdminMaster".equals(password)) {
            log.debug("登录为管理员成功，identifier={}", identifier);
            // 成功
            return new LoginDTO(null, SecurityUtil.createJwtToken("AdminMaster", "admin"));
        }

        // 判断商户 id 登录，注意：此处不再判断商户的审核状态，因为具有商户id的商户一定通过了审核
        if (identifier.startsWith("mid_")) {
            log.debug("商户id登录，identifier={}", identifier);
            // 通过id获取数据库中的对应商户
            LambdaQueryWrapper<Merchant> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Merchant::getMerchantId, identifier);
            Merchant merchant = merchantMapper.selectOne(queryWrapper);

            if (merchant == null) {
                throw new ResourceNotFoundException("不存在该用户");
            }

            // 验证密码
            boolean success = SecurityUtil.verifySha256(password, merchant.getPasswordSha256());
            if (!success) {
                throw new AuthException("密码错误");
            }
            // 成功
            log.debug("商户id登录成功，identifier={}", identifier);

            return new LoginDTO(modelMapper.map(merchant, MerchantDTO.class), SecurityUtil.createJwtToken(merchant.getUsername(), "merchant"));
        }

        // 商户 id 匹配失败，尝试普通用户手机号登录
        LambdaQueryWrapper<User> wrapperUser = new LambdaQueryWrapper<>();
        wrapperUser.eq(User::getPhoneNumber, identifier);

        User user = userMapper.selectOne(wrapperUser);

        if (user != null) {
            log.debug("普通用户手机号登录，identifier={}", identifier);
            // 匹配密码
            boolean success = SecurityUtil.verifySha256(password, user.getPasswordSha256());
            if (!success) {
                throw new AuthException("密码错误");
            }
            // 成功
            log.debug("普通用户手机号登录成功，identifier={}", identifier);
            return new LoginDTO(modelMapper.map(user, UserDTO.class), SecurityUtil.createJwtToken(user.getUsername(), "user"));
        }

        // 普通用户手机号匹配失败，尝试商户手机号登录
        LambdaQueryWrapper<Merchant> wrapperMerchant = new LambdaQueryWrapper<>();
        wrapperMerchant.eq(Merchant::getPhoneNumber, identifier);

        Merchant merchant = merchantMapper.selectOne(wrapperMerchant);

        if (merchant != null) {
            log.debug("商户手机号登录，identifier={}", identifier);
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
            log.debug("商户手机号登录成功，identifier={}", identifier);
            return new LoginDTO(modelMapper.map(merchant, MerchantDTO.class), SecurityUtil.createJwtToken(merchant.getUsername(), "merchant"));
        }

        // 所有登录方式均失败，用户不存在
        throw new ResourceNotFoundException("不存在该用户");
    }

    /**
     * 审核商户注册。
     * 审核通过则分配商户id和昵称，审核状态变更为1，并把拒绝原因设为空字符串。
     * 审核不通过则将审核状态变更为2，记录拒绝原因
     *
     * @param reviewRequestDTO 审核请求参数，包含商户手机号和审核结果
     * @return 审核结果信息
     * @throws ResourceNotFoundException 当商户不存在时抛出
     * @throws SystemException           当SHA-256算法不可用时
     */
    @Override
    public ReviewResultDTO review(ReviewRequestDTO reviewRequestDTO) {
        String merchantPhoneNumber = reviewRequestDTO.getMerchantPhoneNumber();
        log.debug("开始处理审核，merchantPhoneNumber={}", merchantPhoneNumber);

        // 根据手机号查询商户
        LambdaQueryWrapper<Merchant> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Merchant::getPhoneNumber, merchantPhoneNumber);

        Merchant merchant = merchantMapper.selectOne(queryWrapper);

        if (merchant == null) {
            throw new ResourceNotFoundException("商户不存在");
        }


        // 处理审核结果
        if (reviewRequestDTO.getApproved()) {
            // 审核通过
            log.debug("审核通过，merchantPhoneNumber={}", merchantPhoneNumber);

            // 生成商户id
            if (merchant.getMerchantId() == null) {
                String id = GenerationUtil.generateUniqueMerchantId();
                merchant.setMerchantId(id);
            }

            // 生成昵称
            if (merchant.getUsername() == null) {
                String username = GenerationUtil.generateUniqueUsername("merchant_");
                merchant.setUsername(username);
            }

            // 其它属性
            merchant.setApprovalStatus(1);
            merchant.setRejectReason("");

            merchantMapper.updateById(merchant);
        } else {
            // 审核不通过
            log.debug("审核不通过，merchantPhoneNumber={}", merchantPhoneNumber);

            merchant.setApprovalStatus(2);
            merchant.setRejectReason(reviewRequestDTO.getRejectReason());

            merchantMapper.updateById(merchant);
        }

        // 生成返回数据
        ReviewResultDTO result = new ReviewResultDTO();
        result.setPhoneNumber(merchantPhoneNumber);
        result.setApproved(reviewRequestDTO.getApproved());
        result.setMerchantId(merchant.getMerchantId());
        result.setUsername(merchant.getUsername());

        log.debug("审核处理成功，merchantPhoneNumber={}", merchantPhoneNumber);
        return result;
    }


    /**
     * 检查手机号是否已注册为用户
     *
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
     *
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
