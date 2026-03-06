package io.github.crispyxyz.wangran.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateOrganizerRequest {
    @Size(max = 40)
    @Schema(description = "主办方名称", example = "想象力有限公司")
    private String name;

    @Size(max = 20)
    @Pattern(regexp = "^[0-9]+$", message = "手机号只能由数字构成")
    @Schema(description = "主办方电话号码", example = "12345678888")
    private String phoneNumber;

    @Size(max = 40)
    @Schema(description = "主办方联系地址", example = "翻斗大街翻斗花园二号楼1001")
    private String address;
}
