package io.github.crispyxyz.wangran.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.github.crispyxyz.wangran.model.Event;
import io.github.crispyxyz.wangran.request.CreateEventRequest;
import io.github.crispyxyz.wangran.request.UpdateEventRequest;
import io.github.crispyxyz.wangran.security.AppPrincipal;

/**
 *
 * 针对表【event_table】的数据库操作Service
 *
 */
public interface EventService extends EntityService<Event> {

    Event create(int merchantId, CreateEventRequest request);

    IPage<Event> getPage(int page, int pageSize, AppPrincipal principal);

    Event getById(int id, AppPrincipal principal);

    Event partialUpdate(AppPrincipal principal, int id, UpdateEventRequest request);

    boolean removeById(int id, AppPrincipal principal);
}
