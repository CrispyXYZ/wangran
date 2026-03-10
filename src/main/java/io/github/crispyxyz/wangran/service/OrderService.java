package io.github.crispyxyz.wangran.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.github.crispyxyz.wangran.model.UserEvent;
import io.github.crispyxyz.wangran.response.OrderResponse;
import io.github.crispyxyz.wangran.security.AppPrincipal;

public interface OrderService {
    UserEvent createOrder(Integer userId, int eventId);

    void refundOrder(Integer userId, int orderId);

    IPage<OrderResponse> getUserOrders(Integer userId, int page, int pageSize, Boolean refunded);

    IPage<OrderResponse> getMerchantOrders(Integer merchantId, int page, int pageSize, Boolean refunded);

    OrderResponse getById(AppPrincipal principal, int orderId);

    IPage<OrderResponse> getOrders(int page, int pageSize, Boolean refunded);
}
