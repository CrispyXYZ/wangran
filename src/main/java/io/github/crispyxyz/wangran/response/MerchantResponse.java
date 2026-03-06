package io.github.crispyxyz.wangran.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "商户数据")
public class MerchantResponse implements AccountResponse {
    @Schema(description = "id", example = "1")
    private int id;
    @Schema(description = "昵称", example = "qwertyuiop")
    private String username;
    @Schema(description = "商户编号", example = "mid_12345678")
    private String merchantCode;
    @Schema(description = "手机号", example = "12345678888")
    private String phoneNumber;
    @Schema(description = "审核状态", examples = {"PENDING", "APPROVED", "REJECTED"})
    private String approvalStatus;
    @Schema(description = "驳回理由", example = "暂不开放注册")
    private String rejectReason;
}
