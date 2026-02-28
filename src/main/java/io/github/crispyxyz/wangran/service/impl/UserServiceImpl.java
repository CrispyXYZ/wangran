package io.github.crispyxyz.wangran.service.impl;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import io.github.crispyxyz.wangran.mapper.UserMapper;
import io.github.crispyxyz.wangran.model.User;
import io.github.crispyxyz.wangran.request.UpdateAccountRequest;
import io.github.crispyxyz.wangran.service.UserService;
import io.github.crispyxyz.wangran.util.GenerationUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * 针对表【user_table】的数据库操作Service实现
 *
 */
@Service
public class UserServiceImpl extends BaseEntityService<UserMapper, User> implements UserService {

    @Transactional
    @Override
    public User partialUpdate(int id, UpdateAccountRequest request) {
        return updateBuilder(id).setUnique(User::getPhoneNumber, request.getPhoneNumber(), "该手机号已被占用")
                                .setUnique(User::getUsername, request.getUsername(), "该昵称已被占用")
                                .setPassword(User::getPasswordSha256, request.getPassword())
                                .execute();
    }

    @Override
    public boolean existPhoneNumber(String phoneNumber) {
        return isFieldConflict(User::getPhoneNumber, phoneNumber, null);
    }

    @Override
    public boolean existUsername(String username) {
        return isFieldConflict(User::getUsername, username, null);
    }

    @Override
    public User create(String phoneNumber, byte[] passwordSha256) {
        User user = new User();
        user.setPhoneNumber(phoneNumber);
        user.setPasswordSha256(passwordSha256);
        user.setUsername(GenerationUtil.generateUniqueUsername("user_"));
        save(user);
        return user;
    }

    @Override
    public User findByPhoneNumber(String phoneNumber) {
        return lambdaQuery().eq(User::getPhoneNumber, phoneNumber)
                            .one();
    }

    @Override
    protected SFunction<User, ?> getIdField() {
        return User::getId;
    }
}
