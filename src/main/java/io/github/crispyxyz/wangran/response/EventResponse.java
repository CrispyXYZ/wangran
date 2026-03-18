package io.github.crispyxyz.wangran.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@Schema(description = "票务返回数据")
public class EventResponse {
    @Schema(description = "id", example = "1")
    private int id;
    @Schema(description = "票务编号", example = "E12345678")
    private String eventCode;
    @Schema(description = "票务名称", example = "AAA演唱会")
    private String eventName;
    @Schema(description = "票务类型（演出/赛事）", example = "演出")
    private String eventType;
    @Schema(description = "举办时间", example = "2026-03-06T12:34:56.789Z")
    private Instant eventTime;
    @Schema(description = "举办城市", example = "威海")
    private String city;
    @Schema(description = "票价", example = "3.99")
    private BigDecimal price;
    @Schema(description = "（剩余）库存", example = "1")
    private int stock;
    @Schema(description = "是否已上架", example = "true")
    private boolean onShelf;
    @Schema(description = "销售开始时间", example = "2026-03-06T12:34:56.789Z")
    private Instant saleStartTime;
    @Schema(description = "销售结束时间", example = "2026-03-06T12:34:56.789Z")
    private Instant saleEndTime;
    @Schema(description = "商户id", example = "1")
    private int merchantId;
    @Schema(description = "主办方列表")
    private List<OrganizerResponse> organizers;
}
