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
import io.github.crispyxyz.wangran.model.Event;
import io.github.crispyxyz.wangran.model.UserEvent;
import io.github.crispyxyz.wangran.response.OrderResponse;
import io.github.crispyxyz.wangran.service.OrderService;
import io.github.crispyxyz.wangran.service.UserEventService;
import io.github.crispyxyz.wangran.util.GenerationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class OrderServiceImpl implements OrderService {

    private final EventMapper eventMapper;
    private final UserEventMapper userEventMapper;
    private final UserEventService userEventService;
    private final ModelMapperHelper modelMapperHelper;

    @Transactional
    @Override
    public UserEvent createOrder(Integer userId, Integer eventId) {
        LambdaQueryWrapper<UserEvent> existWrapper = Wrappers.lambdaQuery();
        existWrapper.eq(UserEvent::getUserId, userId)
                    .eq(UserEvent::getEventId, eventId)
                    .eq(UserEvent::getRefunded, 0);
        if (userEventMapper.selectCount(existWrapper) > 0) {
            throw new BusinessException("每人每场仅可购买一张票");
        }

        Event event = eventMapper.selectByIdForUpdate(eventId);
        if (event == null) {
            throw new BusinessException("票务不存在");
        }

        Instant now = Instant.now();
        if (event.getOnShelf() == null || event.getOnShelf() == 0) {
            throw new BusinessException("票务未上架");
        }
        if (event.getSaleStartTime() != null && now.isBefore(event.getSaleStartTime())) {
            throw new BusinessException("售卖尚未开始");
        }
        if (event.getSaleEndTime() != null && now.isAfter(event.getSaleEndTime())) {
            throw new BusinessException("售卖已结束");
        }

        if (event.getStock() == null || event.getStock() <= 0) {
            throw new BusinessException("库存不足");
        }

        int rows = eventMapper.updateStockDecreaseById(eventId);
        if (rows == 0) {
            throw new BusinessException("库存不足");
        }

        UserEvent userEvent = new UserEvent();
        userEvent.setUserId(userId);
        userEvent.setEventId(eventId);
        userEvent.setCreateTime(Instant.now());
        userEvent.setRefunded(0);
        userEvent.setTicketCode(GenerationUtil.generateUniqueSequence("O"));
        userEventService.save(userEvent);

        return userEvent;
    }

    @Transactional
    @Override
    public void refundOrder(Integer userId, Integer orderId) {
        UserEvent userEvent = userEventService.getById(orderId);
        if (userEvent == null) {
            throw new BusinessException("订单不存在");
        }
        if (!userEvent.getUserId()
                      .equals(userId)) {
            throw new BusinessException("只能退自己的订单");
        }
        if (userEvent.getRefunded() != null && userEvent.getRefunded() == 1) {
            throw new BusinessException("订单已退票");
        }

        Event event = eventMapper.selectByIdForUpdate(userEvent.getEventId());
        if (event == null) {
            throw new BusinessException("票务不存在");
        }

        int rows = eventMapper.updateStockIncreaseById(event.getId());
        if (rows == 0) {
            throw new BusinessException("库存回退失败");
        }

        userEvent.setRefunded(1);
        userEventService.updateById(userEvent);
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

    private MPJLambdaWrapper<UserEvent> getBaseWrapper() {
        return JoinWrappers.<UserEvent>lambda()
                           .selectAll(UserEvent.class)
                           .selectAssociation(Event.class, UserEvent::getEventObject)
                           .leftJoin(Event.class, Event::getId, UserEvent::getEventId);
    }
}
