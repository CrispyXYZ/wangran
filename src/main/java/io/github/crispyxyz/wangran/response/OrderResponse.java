package io.github.crispyxyz.wangran.response;

import lombok.Data;

import java.time.Instant;

@Data
public class OrderResponse {
    private Integer id;
    private String ticketCode;
    private Integer refunded;
    private Integer userId;
    private Integer eventId;
    private Instant createTime;
    private OrderEventResponse eventObject;
}
