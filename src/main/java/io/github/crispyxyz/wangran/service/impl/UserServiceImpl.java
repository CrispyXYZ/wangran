package io.github.crispyxyz.wangran.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.crispyxyz.wangran.mapper.UserMapper;
import io.github.crispyxyz.wangran.model.User;
import io.github.crispyxyz.wangran.service.UserService;
import org.springframework.stereotype.Service;

/**
 *
 * 针对表【user_table】的数据库操作Service实现
 *
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
implements UserService {

}




