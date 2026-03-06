package io.github.crispyxyz.wangran.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateOrganizerRequest {
    @Size(max = 40)
    @NotBlank
    @Schema(description = "主办方名称", example = "想象力有限公司")
    private String name;

    @Size(max = 20)
    @NotBlank
    @Pattern(regexp = "^[0-9]+$", message = "手机号只能由数字构成")
    @Schema(description = "主办方联系电话", example = "12345678888")
    private String phoneNumber;

    @Size(max = 40)
    @NotBlank
    @Schema(description = "主办方详细地址", example = "翻斗大街翻斗花园二号楼1001")
    private String address;
}