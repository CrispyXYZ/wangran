package io.github.crispyxyz.wangran.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.github.crispyxyz.wangran.model.UserEvent;
import io.github.crispyxyz.wangran.response.OrderResponse;

public interface OrderService {
    UserEvent createOrder(Integer userId, Integer eventId);

    void refundOrder(Integer userId, Integer orderId);

    IPage<OrderResponse> getUserOrders(Integer userId, int page, int pageSize, Boolean refunded);

    IPage<OrderResponse> getMerchantOrders(Integer merchantId, int page, int pageSize);
}
