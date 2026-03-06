package io.github.crispyxyz.wangran.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 接收管理员进行商户审核时前端提交的数据
 */
@Data
public class ReviewRequest {

    /**
     * 商户手机号
     */
    @NotBlank
    @Size(max = 20)
    @Schema(description = "商户手机号", example = "12345678888")
    private String merchantPhoneNumber;

    /**
     * 审核是否通过。此字段为 true 时，应当忽略 rejectReason 的值；反之应当提供非空的 rejectReason
     */
    @NotNull
    @Schema(description = "审核是否通过", example = "true")
    private Boolean approved;

    /**
     * 驳回理由。当 approved 为 true 时，此字段的值应当被忽略
     */
    @Size(max = 50)
    @Schema(description = "驳回理由", example = "暂不开放注册")
    private String rejectReason;
}
