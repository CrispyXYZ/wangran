package io.github.crispyxyz.wangran.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.github.crispyxyz.wangran.request.CreateOrderRequest;
import io.github.crispyxyz.wangran.response.BaseResponse;
import io.github.crispyxyz.wangran.response.OrderResponse;
import io.github.crispyxyz.wangran.response.PageResponse;
import io.github.crispyxyz.wangran.security.AppPrincipal;
import io.github.crispyxyz.wangran.security.annotation.MerchantOnly;
import io.github.crispyxyz.wangran.security.annotation.UserOnly;
import io.github.crispyxyz.wangran.service.OrderService;
import io.github.crispyxyz.wangran.util.ResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@RestController
@RequestMapping("/orders")
@Slf4j
public class OrderController {
    private final OrderService orderService;
    private final ModelMapper modelMapper;

    @UserOnly
    @PostMapping
    public BaseResponse<OrderResponse> createOrder(
        @AuthenticationPrincipal AppPrincipal principal,
        @RequestBody @Valid CreateOrderRequest request
    ) {
        OrderResponse orderResponse =
            modelMapper.map(orderService.createOrder(principal.getId(), request.getEventId()), OrderResponse.class);
        log.info("购票成功，订单号：{}，用户ID：{}，票务ID：{}", orderResponse.getId(), principal.getId(), request.getEventId());
        return ResponseUtil.success(orderResponse);
    }

    @UserOnly
    @PostMapping("/{orderId}/refund")
    public BaseResponse<Void> refundOrder(
        @AuthenticationPrincipal AppPrincipal principal,
        @PathVariable Integer orderId
    ) {
        try {
            orderService.refundOrder(principal.getId(), orderId);
            log.info("退票成功，订单号：{}，用户ID：{}", orderId, principal.getId());
            return ResponseUtil.success(null);
        } catch (Exception e) {
            log.warn("退票失败，订单号：{}，用户ID：{}，原因：{}", orderId, principal.getId(), e.getMessage());
            throw e;
        }
    }

    // TODO 需要修复 organizers 为空的问题
    @UserOnly
    @GetMapping
    public BaseResponse<PageResponse<OrderResponse>> getSelfOrders(
        @AuthenticationPrincipal AppPrincipal principal,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int pageSize,
        @RequestParam(required = false) Boolean refunded
    ) {
        IPage<OrderResponse> pageInfo = orderService.getUserOrders(principal.getId(), page, pageSize, refunded);
        PageResponse<OrderResponse> pageResponse = new PageResponse<>(pageInfo);
        return ResponseUtil.success(pageResponse);
    }

    @MerchantOnly
    @GetMapping("/merchant")
    public BaseResponse<PageResponse<OrderResponse>> getMerchantOrders(
        @AuthenticationPrincipal AppPrincipal principal,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int pageSize
    ) {
        IPage<OrderResponse> pageInfo = orderService.getMerchantOrders(principal.getId(), page, pageSize);
        PageResponse<OrderResponse> pageResponse = new PageResponse<>(pageInfo);
        return ResponseUtil.success(pageResponse);
    }

}
