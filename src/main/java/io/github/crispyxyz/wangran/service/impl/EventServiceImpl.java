package io.github.crispyxyz.wangran.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.crispyxyz.wangran.mapper.EventMapper;
import io.github.crispyxyz.wangran.model.Event;
import io.github.crispyxyz.wangran.service.EventService;
import org.springframework.stereotype.Service;

/**
 *
 * 针对表【event_table】的数据库操作Service实现
 *
 */
@Service
public class EventServiceImpl extends ServiceImpl<EventMapper, Event> implements EventService {

}
