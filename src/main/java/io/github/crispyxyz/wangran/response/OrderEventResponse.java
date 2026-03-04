package io.github.crispyxyz.wangran.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
public class OrderEventResponse {
    private int id;
    private String eventCode;
    private String eventName;
    private String eventType;
    private Instant eventTime;
    private String city;
    private BigDecimal price;
    private Instant saleStartTime;
    private Instant saleEndTime;
    private int merchantId;
    private List<OrganizerResponse> organizers;
}
