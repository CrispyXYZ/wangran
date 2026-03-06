package io.github.crispyxyz.wangran.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import io.github.crispyxyz.wangran.exception.BusinessException;
import io.github.crispyxyz.wangran.exception.ResourceNotFoundException;
import io.github.crispyxyz.wangran.mapper.EventMapper;
import io.github.crispyxyz.wangran.model.Event;
import io.github.crispyxyz.wangran.model.Merchant;
import io.github.crispyxyz.wangran.model.Organizer;
import io.github.crispyxyz.wangran.model.OrganizerEvent;
import io.github.crispyxyz.wangran.request.CreateEventRequest;
import io.github.crispyxyz.wangran.request.UpdateEventRequest;
import io.github.crispyxyz.wangran.response.EventResponse;
import io.github.crispyxyz.wangran.response.PageResponse;
import io.github.crispyxyz.wangran.security.AppPrincipal;
import io.github.crispyxyz.wangran.service.EventService;
import io.github.crispyxyz.wangran.service.MerchantService;
import io.github.crispyxyz.wangran.service.OrganizerEventService;
import io.github.crispyxyz.wangran.service.OrganizerService;
import io.github.crispyxyz.wangran.util.GenerationUtil;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * 针对表【event_table】的数据库操作Service实现
 *
 */
@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class EventServiceImpl extends BaseEntityService<EventMapper, Event> implements EventService {

    private final OrganizerService organizerService;
    private final OrganizerEventService organizerEventService;
    private final ModelMapper modelMapper;
    private final MerchantService merchantService;

    private static void validateTime(Instant start, Instant end) {
        if (start != null && end != null) {
            if (!start.isBefore(end)) {
                throw new BusinessException("售票开始时间必须早于结束时间");
            }
        }
    }

    @Override
    protected SFunction<Event, ?> getIdField() {
        return Event::getId;
    }

    @Transactional
    @Override
    public Event create(int merchantId, CreateEventRequest request) {
        validateTime(request.getSaleStartTime(), request.getSaleEndTime());

        Merchant merchant = merchantService.getById(merchantId);
        if (merchant == null || merchant.getApprovalStatus() != Merchant.STATUS_APPROVED) {
            throw new BusinessException("商户未通过审核，无法创建票务");
        }

        validateOrganizersExistence(request.getOrganizers());

        List<Organizer> organizers = new ArrayList<>();

        Event event = modelMapper.map(request, Event.class);
        event.setMerchantId(merchantId);
        event.setEventCode(GenerationUtil.generateUniqueSequence("E"));
        event.setOrganizers(organizers);
        this.save(event);

        for (int organizer : request.getOrganizers()) {
            OrganizerEvent relation = new OrganizerEvent();
            relation.setOrganizerId(organizer);
            relation.setEventId(event.getId());
            organizerEventService.save(relation);
            organizers.add(organizerService.getById(organizer));
        }
        event.setOnShelf(0);
        log.info("创建票务成功，票务ID：{}，商户ID：{}，名称：{}", event.getId(), merchantId, event.getEventName());
        return event;
    }

    @Transactional(readOnly = true)
    @Override
    public IPage<Event> getPage(int page, int pageSize, AppPrincipal principal) {
        MPJLambdaWrapper<Event> wrapper = getBaseWrapper();

        applyPrincipalFilter(wrapper, principal);

        return baseMapper.selectJoinPage(new Page<>(page, pageSize), Event.class, wrapper);
    }

    @Transactional(readOnly = true)
    @Override
    public Event getById(int id, AppPrincipal principal) {
        MPJLambdaWrapper<Event> wrapper = getBaseWrapper().eq(Event::getId, id);

        applyPrincipalFilter(wrapper, principal);

        return baseMapper.selectJoinOne(Event.class, wrapper);
    }

    @Transactional
    @Override
    public Event partialUpdate(AppPrincipal principal, int id, UpdateEventRequest request) {
        // 这里同时完成了验证id和权限
        Event event = getById(id, principal);
        if (event.getOnShelf() == 1) {
            log.warn("票务更新失败，已上架，票务ID：{}", id);
            throw new BusinessException("票务已上架，无法修改");
        }

        validateTime(request.getSaleStartTime(), request.getSaleEndTime());

        updateBuilder(id).set(Event::getEventName, request.getEventName())
                         .set(Event::getEventType, request.getEventType())
                         .set(Event::getEventTime, request.getEventTime())
                         .set(Event::getCity, request.getCity())
                         .set(Event::getPrice, request.getPrice())
                         .set(Event::getStock, request.getStock())
                         .set(Event::getOnShelf, request.getOnShelf())
                         .set(Event::getSaleStartTime, request.getSaleStartTime())
                         .set(Event::getSaleEndTime, request.getSaleEndTime())
                         .execute();

        if (request.getOrganizers() != null) {
            validateOrganizersExistence(request.getOrganizers());
            updateEventOrganizers(id, request.getOrganizers());
        }

        log.info("更新票务信息，票务ID：{}，新名称：{}", id, request.getEventName());
        return getById(id, principal);
    }

    @Transactional
    @Override
    public boolean removeById(int id, AppPrincipal principal) {
        // 这里同时完成了验证id和权限
        Event event = getById(id, principal);
        if (event.getOnShelf() == 1) {
            log.warn("票务删除失败，已上架，票务ID：{}", id);
            throw new BusinessException("票务已上架，无法修改");
        }

        boolean result = this.removeById(id);
        if (!result) {
            log.warn("票务删除失败，票务ID：{}，未找到记录", id);
            return false;
        }
        removeEventOrganizerRelation(id);
        log.info("删除票务成功，票务ID：{}", id);
        return true;
    }

    @Transactional(readOnly = true)
    @Override
    public PageResponse<EventResponse> getPublicEvents(
        String eventType,
        String city,
        Instant startTime,
        Instant endTime,
        int page,
        int pageSize
    ) {
        MPJLambdaWrapper<Event> wrapper = getBaseWrapper().eq(Event::getOnShelf, 1)
                                                          .eq(Event::getDeleted, 0);
        if (eventType != null && !eventType.isEmpty()) {
            wrapper.eq(Event::getEventType, eventType);
        }
        if (city != null && !city.isEmpty()) {
            wrapper.eq(Event::getCity, city);
        }
        if (startTime != null) {
            wrapper.ge(Event::getEventTime, startTime);
        }
        if (endTime != null) {
            wrapper.le(Event::getEventTime, endTime);
        }
        IPage<Event> pageInfo = baseMapper.selectJoinPage(new Page<>(page, pageSize), Event.class, wrapper);
        return new PageResponse<>(pageInfo.convert(e -> modelMapper.map(e, EventResponse.class)));
    }

    private void validateOrganizersExistence(List<@NotNull Integer> organizerIds) {
        List<Organizer> existing = organizerService.listByIds(organizerIds);
        if (existing.size() != organizerIds.size()) {
            throw new ResourceNotFoundException("部分主办方不存在");
        }
    }

    private void removeEventOrganizerRelation(int id) {
        LambdaQueryWrapper<OrganizerEvent> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(OrganizerEvent::getEventId, id);
        organizerEventService.remove(wrapper);
    }

    private void updateEventOrganizers(int eventId, List<Integer> organizerIds) {
        removeEventOrganizerRelation(eventId);

        for (Integer organizerId : organizerIds) {
            OrganizerEvent relation = new OrganizerEvent();
            relation.setEventId(eventId);
            relation.setOrganizerId(organizerId);
            organizerEventService.save(relation);
        }
    }

    /**
     * 获取基础wrapper，票务所有字段，关联主办方
     */
    private MPJLambdaWrapper<Event> getBaseWrapper() {
        return JoinWrappers.<Event>lambda()
                           .selectAll(Event.class)
                           .selectCollection(Organizer.class, Event::getOrganizers)
                           .leftJoin(OrganizerEvent.class, OrganizerEvent::getEventId, Event::getId)
                           .leftJoin(Organizer.class, Organizer::getId, OrganizerEvent::getOrganizerId);
    }

    private void applyPrincipalFilter(MPJLambdaWrapper<Event> wrapper, AppPrincipal principal) {
        wrapper.eq("merchant".equals(principal.getType()), Event::getMerchantId, principal.getId());
    }


}
