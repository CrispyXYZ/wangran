package io.github.crispyxyz.wangran.service.impl;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import io.github.crispyxyz.wangran.component.UserExcelListener;
import io.github.crispyxyz.wangran.exception.SystemException;
import io.github.crispyxyz.wangran.mapper.UserMapper;
import io.github.crispyxyz.wangran.model.User;
import io.github.crispyxyz.wangran.model.excel.UserExcelData;
import io.github.crispyxyz.wangran.request.UpdateAccountRequest;
import io.github.crispyxyz.wangran.service.UserService;
import io.github.crispyxyz.wangran.util.GenerationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.sheet.FesodSheet;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 *
 * 针对表【user_table】的数据库操作Service实现
 *
 */
@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Slf4j
public class UserServiceImpl extends BaseEntityService<UserMapper, User> implements UserService {

    private final ModelMapper modelMapper;

    @Transactional
    @Override
    public User partialUpdate(int id, UpdateAccountRequest request) {
        User user = updateBuilder(id).setUnique(User::getPhoneNumber, request.getPhoneNumber(), "该手机号已被占用")
                                     .setUnique(User::getUsername, request.getUsername(), "该昵称已被占用")
                                     .setPassword(User::getPasswordSha256, request.getPassword())
                                     .execute();
        log.info("用户信息更新，用户ID：{}", id);
        return user;
    }

    @Transactional(readOnly = true)
    @Override
    public boolean existPhoneNumber(String phoneNumber) {
        return isFieldConflict(User::getPhoneNumber, phoneNumber, null);
    }

    @Transactional(readOnly = true)
    @Override
    public boolean existUsername(String username) {
        return isFieldConflict(User::getUsername, username, null);
    }

    @Transactional
    @Override
    public User create(String phoneNumber, byte[] passwordSha256) {
        User user = new User();
        user.setPhoneNumber(phoneNumber);
        user.setPasswordSha256(passwordSha256);
        user.setUsername(GenerationUtil.generateUniqueUsername("user_"));
        save(user);
        log.info("新建用户，用户ID：{}，手机号：{}", user.getId(), phoneNumber);
        return user;
    }

    @Transactional(readOnly = true)
    @Override
    public User findByPhoneNumber(String phoneNumber) {
        return lambdaQuery().eq(User::getPhoneNumber, phoneNumber)
                            .one();
    }

    @Transactional
    @Override
    public void importUsers(MultipartFile file) {
        try {
            UserExcelListener listener = new UserExcelListener(modelMapper, this);
            FesodSheet.read(file.getInputStream(), UserExcelData.class, listener)
                      .sheet()
                      .doRead();
            log.info("批量导入用户成功");
        } catch (IOException e) {
            log.error("批量导入用户失败", e);
            throw new SystemException(e.getMessage());
        }
    }

    @Override
    protected SFunction<User, ?> getIdField() {
        return User::getId;
    }
}
