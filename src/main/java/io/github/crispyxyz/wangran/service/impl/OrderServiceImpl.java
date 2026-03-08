package io.github.crispyxyz.wangran.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import io.github.crispyxyz.wangran.component.ModelMapperHelper;
import io.github.crispyxyz.wangran.exception.BusinessException;
import io.github.crispyxyz.wangran.mapper.EventMapper;
import io.github.crispyxyz.wangran.mapper.UserEventMapper;
import io.github.crispyxyz.wangran.model.*;
import io.github.crispyxyz.wangran.response.OrderResponse;
import io.github.crispyxyz.wangran.service.MerchantService;
import io.github.crispyxyz.wangran.service.OrderService;
import io.github.crispyxyz.wangran.service.UserEventService;
import io.github.crispyxyz.wangran.util.GenerationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class OrderServiceImpl implements OrderService {

    private final EventMapper eventMapper;
    private final UserEventMapper userEventMapper;
    private final UserEventService userEventService;
    private final ModelMapperHelper modelMapperHelper;
    private final MerchantService merchantService;

    @Transactional
    @Override
    public UserEvent createOrder(Integer userId, Integer eventId) {
        LambdaQueryWrapper<UserEvent> existWrapper = Wrappers.lambdaQuery();
        existWrapper.eq(UserEvent::getUserId, userId)
                    .eq(UserEvent::getEventId, eventId)
                    .eq(UserEvent::getRefunded, 0);

        // 这里开始加锁
        Event event = eventMapper.selectByIdForUpdate(eventId);
        if (userEventMapper.selectCount(existWrapper) > 0) {
            log.warn("购票失败，用户ID：{}，票务ID：{}，原因：重复购票", userId, eventId);
            throw new BusinessException("每人每场仅可购买一张票");
        }
        if (event == null) {
            log.warn("购票失败，用户ID：{}，票务ID：{}，原因：票务不存在", userId, eventId);
            throw new BusinessException("票务不存在");
        }
        Merchant merchant = merchantService.getById(event.getMerchantId());
        if (merchant == null || merchant.getApprovalStatus() != Merchant.STATUS_APPROVED) {
            throw new BusinessException("该票务所属商户未通过审核，暂时无法购买");
        }

        Instant now = Instant.now();
        if (event.getOnShelf() == null || event.getOnShelf() == 0) {
            log.warn("购票失败，用户ID：{}，票务ID：{}，原因：票务未上架", userId, eventId);
            throw new BusinessException("票务未上架");
        }
        if (event.getSaleStartTime() != null && now.isBefore(event.getSaleStartTime())) {
            log.warn("购票失败，用户ID：{}，票务ID：{}，原因：售卖尚未开始", userId, eventId);
            throw new BusinessException("售卖尚未开始");
        }
        if (event.getSaleEndTime() != null && now.isAfter(event.getSaleEndTime())) {
            log.warn("购票失败，用户ID：{}，票务ID：{}，原因：售卖已结束", userId, eventId);
            throw new BusinessException("售卖已结束");
        }
        if (event.getStock() == null || event.getStock() <= 0) {
            log.warn("购票失败，用户ID：{}，票务ID：{}，原因：库存不足", userId, eventId);
            throw new BusinessException("库存不足");
        }
        int rows = eventMapper.updateStockDecreaseById(eventId);
        if (rows == 0) {
            log.warn("购票失败，用户ID：{}，票务ID：{}，原因：库存不足(并发)", userId, eventId);
            throw new BusinessException("库存不足");
        }

        UserEvent userEvent = new UserEvent();
        userEvent.setUserId(userId);
        userEvent.setEventId(eventId);
        userEvent.setCreateTime(Instant.now());
        userEvent.setRefunded(0);
        userEvent.setTicketCode(GenerationUtil.generateUniqueSequence("O"));
        userEvent.setEventObject(event);
        userEventService.save(userEvent);
        log.info("购票成功，订单号：{}，用户ID：{}，票务ID：{}", userEvent.getId(), userId, eventId);

        return userEvent;
    }

    @Transactional
    @Override
    public void refundOrder(Integer userId, Integer orderId) {
        // 这里开始加锁
        UserEvent userEvent = userEventMapper.selectByIdForUpdate(orderId);
        if (userEvent == null) {
            log.warn("退票失败，用户ID：{}，订单号：{}，原因：订单不存在", userId, orderId);
            throw new BusinessException("订单不存在");
        }
        if (!userEvent.getUserId()
                      .equals(userId)) {
            log.warn("退票失败，用户ID：{}，订单号：{}，原因：非本人订单", userId, orderId);
            throw new BusinessException("只能退自己的订单");
        }
        Event event = eventMapper.selectByIdForUpdate(userEvent.getEventId());
        if (userEvent.getRefunded() != null && userEvent.getRefunded() == 1) {
            log.warn("退票失败，用户ID：{}，订单号：{}，原因：订单已退票", userId, orderId);
            throw new BusinessException("订单已退票");
        }
        if (event == null) {
            log.warn("退票失败，用户ID：{}，订单号：{}，原因：票务不存在", userId, orderId);
            throw new BusinessException("票务不存在");
        }
        int rows = eventMapper.updateStockIncreaseById(event.getId());
        if (rows == 0) {
            log.warn("退票失败，用户ID：{}，订单号：{}，原因：库存回退失败", userId, orderId);
            throw new BusinessException("库存回退失败");
        }
        userEvent.setRefunded(1);
        userEventService.updateById(userEvent);
        log.info("退票成功，订单号：{}，用户ID：{}，票务ID：{}", orderId, userId, userEvent.getEventId());
    }

    @Transactional(readOnly = true)
    @Override
    public IPage<OrderResponse> getUserOrders(Integer userId, int page, int pageSize, Boolean refunded) {
        MPJLambdaWrapper<UserEvent> wrapper = getBaseWrapper().eq(UserEvent::getUserId, userId);

        if (refunded != null) {
            wrapper.eq(UserEvent::getRefunded, refunded);
        }
        IPage<UserEvent> pageInfo =
            userEventMapper.selectJoinPage(new Page<>(page, pageSize), UserEvent.class, wrapper);

        return modelMapperHelper.mapPage(pageInfo, OrderResponse.class);
    }

    @Transactional(readOnly = true)
    @Override
    public IPage<OrderResponse> getMerchantOrders(Integer merchantId, int page, int pageSize) {
        MPJLambdaWrapper<UserEvent> wrapper = getBaseWrapper().eq(Event::getMerchantId, merchantId);
        IPage<UserEvent> pageInfo =
            userEventMapper.selectJoinPage(new Page<>(page, pageSize), UserEvent.class, wrapper);
        return modelMapperHelper.mapPage(pageInfo, OrderResponse.class);
    }

    /**
     * 获取基础查询wrapper，含订单所有字段，关联票务
     */
    private MPJLambdaWrapper<UserEvent> getBaseWrapper() {
        return JoinWrappers.<UserEvent>lambda()
                           .selectAll(UserEvent.class)
                           .selectAssociation(
                               Event.class,
                               UserEvent::getEventObject,
                               ass -> ass.all()                           // 映射 Event 所有字段
                                         .collection(Organizer.class, Event::getOrganizers) // 嵌套映射主办方集合
                           )
                           .leftJoin(Event.class, Event::getId, UserEvent::getEventId)
                           .leftJoin(OrganizerEvent.class, OrganizerEvent::getEventId, Event::getId)
                           .leftJoin(Organizer.class, Organizer::getId, OrganizerEvent::getOrganizerId);
    }
}
