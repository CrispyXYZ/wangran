package io.github.crispyxyz.wangran.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class UpdateEventRequest {
    @Size(max = 40)
    private String eventName;

    @Size(max = 10)
    private String eventType;

    @Future
    private Date eventTime;

    @Size(max = 10)
    private String city;

    @Digits(integer = 8, fraction = 2)
    @PositiveOrZero
    private BigDecimal price;

    private Integer stock;

    private Boolean onShelf;

    @Future
    private Date saleStartTime;

    @Future
    private Date saleEndTime;

    @Size(min = 1)
    private List<@NotNull Integer> organizers;
}
