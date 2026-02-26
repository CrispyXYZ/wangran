package io.github.crispyxyz.wangran.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import io.github.crispyxyz.wangran.model.User;
import io.github.crispyxyz.wangran.request.UpdateAccountRequest;
import jakarta.validation.Valid;

/**
 *
 * 针对表【user_table】的数据库操作Service
 *
 */
public interface UserService extends IService<User> {
    IPage<User> getUsers(int page, int pageSize);

    User partialUpdate(int id, @Valid UpdateAccountRequest request);

    boolean existPhoneNumber(String phoneNumber);

    boolean existUsername(String username);
}
