package io.github.crispyxyz.wangran.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 接收管理员进行商户审核时前端提交的数据
 */
@Data
public class ReviewRequestDTO {

    /**
     * 商户手机号
     */
    @NotBlank
    @Size(max = 20)
    private String merchantPhoneNumber;

    /**
     * 审核是否通过。此字段为 true 时，应当忽略 rejectReason 的值；反之应当提供非空的 rejectReason
     */
    @NotNull
    private Boolean approved;

    /**
     * 驳回理由。当 approved 为 true 时，此字段的值应当被忽略
     */
    @Size(max = 50)
    private String rejectReason;
}
