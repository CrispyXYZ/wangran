package io.github.crispyxyz.wangran.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.crispyxyz.wangran.exception.ResourceConflictException;
import io.github.crispyxyz.wangran.exception.ResourceNotFoundException;
import io.github.crispyxyz.wangran.mapper.UserMapper;
import io.github.crispyxyz.wangran.model.User;
import io.github.crispyxyz.wangran.request.UpdateAccountRequest;
import io.github.crispyxyz.wangran.service.UserService;
import io.github.crispyxyz.wangran.util.SecurityUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * 针对表【user_table】的数据库操作Service实现
 *
 */
@Service
public class UserServiceImpl extends BaseUniqueCheckService<UserMapper, User>
implements UserService {

    @Override
    public IPage<User> getUsers(int page, int pageSize) {
        Page<User> pageInfo = Page.of(page, pageSize);
        return this.page(pageInfo);
    }

    @Transactional
    @Override
    public User partialUpdate(int id, UpdateAccountRequest request) {
        LambdaUpdateWrapper<User> wrapper = Wrappers.lambdaUpdate(User.class)
                                                    .eq(User::getId, id);

        if (request.getPhoneNumber() != null) {
            if (isFieldConflict(User::getPhoneNumber, request.getPhoneNumber(), id)) {
                throw new ResourceConflictException("该手机号已被占用");
            }
            wrapper.set(User::getPhoneNumber, request.getPhoneNumber());
        }
        if (request.getUsername() != null) {
            if (isFieldConflict(User::getUsername, request.getUsername(), id)) {
                throw new ResourceConflictException("该昵称已被占用");
            }
            wrapper.set(User::getUsername, request.getUsername());
        }
        if (request.getPassword() != null) {
            byte[] passwordSha256 = SecurityUtil.computeSha256(request.getPassword());
            wrapper.set(User::getPasswordSha256, passwordSha256);
        }

        if (!update(wrapper)) {
            throw new ResourceNotFoundException("该用户不存在");
        }

        return this.getById(id);
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
    protected SFunction<User, ?> getIdField() {
        return User::getId;
    }
}




