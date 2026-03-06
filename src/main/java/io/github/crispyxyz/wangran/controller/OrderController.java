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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "订单接口")
public class OrderController {
    private final OrderService orderService;
    private final ModelMapper modelMapper;

    @UserOnly
    @PostMapping
    @Operation(summary = "创建订单", description = "返回订单数据，仅用户可以访问此接口")
    public BaseResponse<OrderResponse> createOrder(
        @AuthenticationPrincipal AppPrincipal principal,
        @RequestBody @Valid CreateOrderRequest request
    ) {
        OrderResponse orderResponse =
            modelMapper.map(orderService.createOrder(principal.getId(), request.getEventId()), OrderResponse.class);
        log.info(
            "购票成功，订单号：{}，用户ID：{}，票务ID：{}",
            orderResponse.getId(),
            principal.getId(),
            request.getEventId()
        );
        return ResponseUtil.success(orderResponse);
    }

    @UserOnly
    @PostMapping("/{orderId}/refund")
    @Operation(summary = "退订", description = "返回空数据，仅用户可以访问此接口")
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
    @Operation(summary = "获取自己的订单", description = "返回分页的订单数据，仅用户可访问")
    public BaseResponse<PageResponse<OrderResponse>> getSelfOrders(
        @AuthenticationPrincipal AppPrincipal principal,
        @Parameter(description = "当前页码，从1开始", example = "1") @RequestParam(defaultValue = "1") int page,
        @Parameter(description = "每页记录数", example = "10") @RequestParam(defaultValue = "10") int pageSize,
        @Parameter(description = "是否已退票", example = "false") @RequestParam(required = false) Boolean refunded
    ) {
        IPage<OrderResponse> pageInfo = orderService.getUserOrders(principal.getId(), page, pageSize, refunded);
        PageResponse<OrderResponse> pageResponse = new PageResponse<>(pageInfo);
        return ResponseUtil.success(pageResponse);
    }

    @MerchantOnly
    @GetMapping("/merchant")
    @Operation(summary = "获取商户的所有订单", description = "返回分页的订单数据，仅商户能访问此接口")
    public BaseResponse<PageResponse<OrderResponse>> getMerchantOrders(
        @AuthenticationPrincipal AppPrincipal principal,
        @Parameter(description = "当前页码，从1开始", example = "1") @RequestParam(defaultValue = "1") int page,
        @Parameter(description = "每页记录数", example = "10") @RequestParam(defaultValue = "10") int pageSize
    ) {
        IPage<OrderResponse> pageInfo = orderService.getMerchantOrders(principal.getId(), page, pageSize);
        PageResponse<OrderResponse> pageResponse = new PageResponse<>(pageInfo);
        return ResponseUtil.success(pageResponse);
    }

}
