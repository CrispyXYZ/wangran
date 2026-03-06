package io.github.crispyxyz.wangran.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
public class CreateEventRequest {
    @NotBlank
    @Size(max = 40)
    @Schema(description = "票务名称", example = "XXXX演唱会")
    private String eventName;

    @NotBlank
    @Size(max = 10)
    @Schema(description = "票务类型（演出/赛事）", example = "演出")
    private String eventType;

    @NotNull
    @Future
    @Schema(description = "举办时间")
    private Instant eventTime;

    @NotBlank
    @Size(max = 10)
    @Schema(description = "举办城市", example = "威海")
    private String city;

    @NotNull
    @Digits(integer = 8, fraction = 2)
    @PositiveOrZero
    @Schema(description = "票价，最大99999999.99", example = "3.99")
    private BigDecimal price;

    @NotNull
    @Schema(description = "库存总量", example = "160")
    private Integer stock;

    @NotNull
    @Future
    @Schema(description = "销售开始时间")
    private Instant saleStartTime;

    @NotNull
    @Future
    @Schema(description = "销售结束时间")
    private Instant saleEndTime;

    @NotEmpty
    @Schema(description = "主办方id列表，非空非null整数组", example = "[1]")
    private List<@NotNull Integer> organizers;
}
