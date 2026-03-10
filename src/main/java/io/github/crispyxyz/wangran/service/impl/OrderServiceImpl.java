package io.github.crispyxyz.wangran.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import io.github.crispyxyz.wangran.component.ModelMapperHelper;
import io.github.crispyxyz.wangran.exception.BusinessException;
import io.github.crispyxyz.wangran.exception.ResourceNotFoundException;
import io.github.crispyxyz.wangran.mapper.EventMapper;
import io.github.crispyxyz.wangran.mapper.UserEventMapper;
import io.github.crispyxyz.wangran.model.Event;
import io.github.crispyxyz.wangran.model.Organizer;
import io.github.crispyxyz.wangran.model.OrganizerEvent;
import io.github.crispyxyz.wangran.model.UserEvent;
import io.github.crispyxyz.wangran.response.OrderResponse;
import io.github.crispyxyz.wangran.security.AppPrincipal;
import io.github.crispyxyz.wangran.service.MerchantService;
import io.github.crispyxyz.wangran.service.OrderService;
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
    private final ModelMapperHelper modelMapperHelper;
    private final MerchantService merchantService;

    @Transactional
    @Override
    public UserEvent createOrder(Integer userId, int eventId) {
        LambdaQueryWrapper<UserEvent> existWrapper = Wrappers.lambdaQuery();
        existWrapper.eq(UserEvent::getUserId, userId)
                    .eq(UserEvent::getEventId, eventId)
                    .eq(UserEvent::getRefunded, 0);

        // 这里开始加悲观锁，防止并发超卖
        Event event = eventMapper.selectByIdForUpdate(eventId);
        if (userEventMapper.selectCount(existWrapper) > 0) {
            log.warn("购票失败，用户ID：{}，票务ID：{}，原因：重复购票", userId, eventId);
            throw new BusinessException("每人每场仅可购买一张票");
        }
        if (event == null) {
            log.warn("购票失败，用户ID：{}，票务ID：{}，原因：票务不存在", userId, eventId);
            throw new BusinessException("票务不存在");
        }

        merchantService.validateApprovalStatus(event.getMerchantId());

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

        // 校验完成，开始减少库存
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
        userEventMapper.insert(userEvent);
        log.info("购票成功，订单号：{}，用户ID：{}，票务ID：{}", userEvent.getId(), userId, eventId);

        return userEvent;
    }

    @Transactional
    @Override
    public void refundOrder(Integer userId, int orderId) {
        // 这里开始加悲观锁，防止重复购票/并发修改
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

        // 对票务记录加锁
        Event event = eventMapper.selectByIdForUpdate(userEvent.getEventId());
        if (userEvent.getRefunded() != null && userEvent.getRefunded() == 1) {
            log.warn("退票失败，用户ID：{}，订单号：{}，原因：订单已退票", userId, orderId);
            throw new BusinessException("订单已退票");
        }
        if (event == null) {
            log.warn("退票失败，用户ID：{}，订单号：{}，原因：票务不存在", userId, orderId);
            throw new BusinessException("票务不存在");
        }

        // 增加库存
        int rows = eventMapper.updateStockIncreaseById(event.getId());
        if (rows == 0) {
            log.warn("退票失败，用户ID：{}，订单号：{}，原因：库存回退失败", userId, orderId);
            throw new BusinessException("库存回退失败");
        }

        userEvent.setRefunded(1);
        userEventMapper.updateById(userEvent);
        log.info("退票成功，订单号：{}，用户ID：{}，票务ID：{}", orderId, userId, userEvent.getEventId());
    }

    @Transactional(readOnly = true)
    @Override
    public IPage<OrderResponse> getUserOrders(Integer userId, int page, int pageSize, Boolean refunded) {
        MPJLambdaWrapper<UserEvent> wrapper = getBaseWrapper().eq(UserEvent::getUserId, userId);

        return getOrderResponsePage(page, pageSize, refunded, wrapper);
    }

    @Transactional(readOnly = true)
    @Override
    public IPage<OrderResponse> getMerchantOrders(Integer merchantId, int page, int pageSize, Boolean refunded) {
        MPJLambdaWrapper<UserEvent> wrapper = getBaseWrapper().eq(Event::getMerchantId, merchantId);

        return getOrderResponsePage(page, pageSize, refunded, wrapper);
    }

    @Transactional(readOnly = true)
    @Override
    public IPage<OrderResponse> getOrders(int page, int pageSize, Boolean refunded) {
        MPJLambdaWrapper<UserEvent> wrapper = getBaseWrapper();

        return getOrderResponsePage(page, pageSize, refunded, wrapper);
    }

    @Transactional
    @Override
    public OrderResponse getById(AppPrincipal principal, int orderId) {
        int principalId = principal.getId();
        String type = principal.getType();
        MPJLambdaWrapper<UserEvent> wrapper = getBaseWrapper().eq(UserEvent::getId, orderId);
        if ("user".equals(type)) {
            wrapper.eq(UserEvent::getUserId, principalId);
        } else if ("merchant".equals(type)) {
            wrapper.eq(Event::getMerchantId, principalId);
        }
        UserEvent order = userEventMapper.selectOne(wrapper);
        if (order == null) {
            throw new ResourceNotFoundException("找不到id为" + orderId + "的订单，订单不存在或权限不足");
        }
        return modelMapperHelper.map(order, OrderResponse.class);
    }

    private Page<OrderResponse> getOrderResponsePage(
        int page,
        int pageSize,
        Boolean refunded,
        MPJLambdaWrapper<UserEvent> wrapper
    ) {
        if (refunded != null) {
            wrapper.eq(UserEvent::getRefunded, refunded);
        }
        IPage<UserEvent> pageInfo =
            userEventMapper.selectJoinPage(new Page<>(page, pageSize), UserEvent.class, wrapper);
        return modelMapperHelper.mapPage(pageInfo, OrderResponse.class);
    }

    /**
     * 获取基础查询wrapper，含订单所有字段，关联票务及其主办方
     */
    private MPJLambdaWrapper<UserEvent> getBaseWrapper() {
        return JoinWrappers.<UserEvent>lambda()
                           .selectAll(UserEvent.class)
                           .selectAssociation(
                               Event.class, UserEvent::getEventObject,
                               // 映射 Event 所有字段
                               ass -> ass.all()
                                         // 嵌套映射主办方集合
                                         .collection(Organizer.class, Event::getOrganizers)
                           )
                           .leftJoin(Event.class, Event::getId, UserEvent::getEventId)
                           .leftJoin(OrganizerEvent.class, OrganizerEvent::getEventId, Event::getId)
                           .leftJoin(Organizer.class, Organizer::getId, OrganizerEvent::getOrganizerId);
    }
}
