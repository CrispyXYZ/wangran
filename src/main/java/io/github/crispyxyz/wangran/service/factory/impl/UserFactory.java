package io.github.crispyxyz.wangran.service.factory.impl;

import io.github.crispyxyz.wangran.model.User;
import io.github.crispyxyz.wangran.service.factory.AccountFactory;
import io.github.crispyxyz.wangran.util.GenerationUtil;
import org.springframework.stereotype.Component;

@Component
public class UserFactory implements AccountFactory<User> {

    @Override
    public User create(String phoneNumber, byte[] passwordSha256, boolean autoApprove) {
        User user = new User();
        user.setPhoneNumber(phoneNumber);
        user.setPasswordSha256(passwordSha256);
        user.setUsername(GenerationUtil.generateUniqueUsername(User.USERNAME_PREFIX));
        return user;
    }

    public User create(String phoneNumber, byte[] passwordSha256) {
        return create(phoneNumber, passwordSha256, false);
    }
}
