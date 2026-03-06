package io.github.crispyxyz.wangran.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.Instant;

@Data
@Schema(description = "订单数据")
public class OrderResponse {
    @Schema(description = "id", example = "1")
    private int id;
    @Schema(description = "订单编号", example = "O12345678")
    private String ticketCode;
    @Schema(description = "是否已退票", example = "false")
    private boolean refunded;
    @Schema(description = "订票的用户id", example = "1")
    private int userId;
    @Schema(description = "票务id", example = "1")
    private int eventId;
    @Schema(description = "创建时间", example = "2026-03-06T12:34:56.789Z")
    private Instant createTime;
    @Schema(description = "票务数据")
    private OrderEventResponse eventObject;
}
