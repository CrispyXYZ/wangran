package io.github.crispyxyz.wangran.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
public class CreateEventRequest {
    @NotBlank
    @Size(max = 40)
    private String eventName;

    @NotBlank
    @Size(max = 10)
    private String eventType;

    @NotNull
    @Future
    private Instant eventTime;

    @NotBlank
    @Size(max = 10)
    private String city;

    @NotNull
    @Digits(integer = 8, fraction = 2)
    @PositiveOrZero
    private BigDecimal price;

    @NotNull
    private Integer stock;

    @NotNull
    @Future
    private Instant saleStartTime;

    @NotNull
    @Future
    private Instant saleEndTime;

    @NotEmpty
    private List<@NotNull Integer> organizers;
}
