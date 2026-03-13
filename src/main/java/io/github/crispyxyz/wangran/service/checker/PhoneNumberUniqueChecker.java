package io.github.crispyxyz.wangran.service.checker;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.crispyxyz.wangran.exception.ResourceConflictException;
import io.github.crispyxyz.wangran.mapper.MerchantMapper;
import io.github.crispyxyz.wangran.mapper.UserMapper;
import io.github.crispyxyz.wangran.model.Merchant;
import io.github.crispyxyz.wangran.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class PhoneNumberUniqueChecker {
    private final UserMapper userMapper;
    private final MerchantMapper merchantMapper;

    public void checkCreate(String phoneNumber) {
        if (existsUser(phoneNumber) || existsMerchant(phoneNumber)) {
            throw new ResourceConflictException("该手机号已被注册");
        }
    }

    public <T> void checkUpdate(String phoneNumber, Class<T> clazz, Integer currentId) {
        if (User.class.equals(clazz)) {
            if (existsUserExcludingId(phoneNumber, currentId)) {
                throw new ResourceConflictException("手机号已被其他用户占用");
            }
            if (existsMerchant(phoneNumber)) {
                throw new ResourceConflictException("手机号已被商户占用");
            }
        } else {
            if (existsMerchantExcludingId(phoneNumber, currentId)) {
                throw new ResourceConflictException("手机号已被其他商户占用");
            }
            if (existsUser(phoneNumber)) {
                throw new ResourceConflictException("手机号已被用户占用");
            }
        }
    }

    private boolean existsUser(String phoneNumber) {
        return userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getPhoneNumber, phoneNumber)) > 0;
    }

    private boolean existsMerchant(String phoneNumber) {
        return
            merchantMapper.selectCount(new LambdaQueryWrapper<Merchant>().eq(Merchant::getPhoneNumber, phoneNumber)) >
            0;
    }

    private boolean existsUserExcludingId(String phoneNumber, Integer excludeId) {
        return userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getPhoneNumber, phoneNumber)
                                                                    .ne(User::getId, excludeId)) > 0;
    }

    private boolean existsMerchantExcludingId(String phoneNumber, Integer excludeId) {
        return merchantMapper.selectCount(new LambdaQueryWrapper<Merchant>().eq(Merchant::getPhoneNumber, phoneNumber)
                                                                            .ne(Merchant::getId, excludeId)) > 0;
    }

}
