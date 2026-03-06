package io.github.crispyxyz.wangran.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateOrderRequest {
    @NotNull
    @Schema(description = "票务id", example = "1")
    private Integer eventId;
}
