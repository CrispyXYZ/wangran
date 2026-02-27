package io.github.crispyxyz.wangran.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import io.github.crispyxyz.wangran.model.Merchant;
import io.github.crispyxyz.wangran.model.User;
import io.github.crispyxyz.wangran.request.UpdateAccountRequest;

/**
 *
 * 针对表【user_table】的数据库操作Service
 *
 */
public interface UserService extends IService<User> {
    IPage<User> getUsers(int page, int pageSize);

    User partialUpdate(int id, UpdateAccountRequest request);

    boolean existPhoneNumber(String phoneNumber);

    boolean existUsername(String username);

    User create(String phoneNumber, byte[] passwordSha256);

    User findByPhoneNumber(String phoneNumber);
}
