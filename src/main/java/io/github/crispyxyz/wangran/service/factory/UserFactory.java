package io.github.crispyxyz.wangran.service.factory;

import io.github.crispyxyz.wangran.model.User;
import io.github.crispyxyz.wangran.util.GenerationUtil;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class UserFactory {

    public User create(String phoneNumber, byte[] passwordSha256, @Nullable String username) {
        User user = new User();
        user.setPhoneNumber(phoneNumber);
        user.setPasswordSha256(passwordSha256);
        user.setUsername(Objects.requireNonNullElseGet(
            username,
            () -> GenerationUtil.generateUniqueUsername(User.USERNAME_PREFIX)
        ));
        return user;
    }
}
