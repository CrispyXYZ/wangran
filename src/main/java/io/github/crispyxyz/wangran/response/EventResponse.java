package io.github.crispyxyz.wangran.response;

import io.github.crispyxyz.wangran.model.UserEvent;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class EventResponse {
    private int id;
    private String eventCode;
    private String eventName;
    private String eventType;
    private Date eventTime;
    private String city;
    private BigDecimal price;
    private int stock;
    private boolean onShelf;
    private Date saleStartTime;
    private Date saleEndTime;
    private int merchantId;
    private List<OrganizerResponse> organizers;
    private List<UserEvent> userEvents;
}
