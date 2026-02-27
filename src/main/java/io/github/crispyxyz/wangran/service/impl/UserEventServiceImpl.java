package io.github.crispyxyz.wangran.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.crispyxyz.wangran.mapper.UserEventMapper;
import io.github.crispyxyz.wangran.model.UserEvent;
import io.github.crispyxyz.wangran.service.UserEventService;
import org.springframework.stereotype.Service;

/**
 *
 * 针对表【user_event】的数据库操作Service实现
 *
 */
@Service
public class UserEventServiceImpl extends ServiceImpl<UserEventMapper, UserEvent> implements UserEventService {

}
