package io.github.crispyxyz.wangran.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
public class UpdateEventRequest {
    @Size(max = 40)
    @Schema(description = "票务名称", example = "XXXX演唱会（更新）")
    private String eventName;

    @Size(max = 10)
    @Schema(description = "票务类型（演出/赛事）", example = "演出")
    private String eventType;

    @Future
    @Schema(description = "举办时间")
    private Instant eventTime;

    @Size(max = 10)
    @Schema(description = "举办城市", example = "威海")
    private String city;

    @Digits(integer = 8, fraction = 2)
    @PositiveOrZero
    @Schema(description = "票价，最大99999999.99", example = "3.99")
    private BigDecimal price;

    @Schema(description = "库存总量", example = "160")
    private Integer stock;

    @Schema(description = "是否上架（上架后无法修改）", example = "true")
    private Boolean onShelf;

    @Future
    @Schema(description = "销售开始时间")
    private Instant saleStartTime;

    @Future
    @Schema(description = "销售结束时间")
    private Instant saleEndTime;

    @Size(min = 1)
    @Schema(description = "主办方id列表，非空非null整数组", example = "[1]")
    private List<@NotNull Integer> organizers;
}