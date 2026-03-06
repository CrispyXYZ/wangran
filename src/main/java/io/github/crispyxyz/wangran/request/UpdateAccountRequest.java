package io.github.crispyxyz.wangran.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateAccountRequest {
    @Size(max = 20)
    @Pattern(regexp = "^[0-9]+$", message = "手机号只能由数字构成")
    @Schema(description = "手机号", example = "12345678888")
    private String phoneNumber;

    @Size(min = 6, max = 50)
    @Schema(description = "密码（明文）", example = "P@SsW0rD")
    private String password;

    @Size(max = 50)
    @Schema(description = "昵称", example = "qwertyuiop")
    private String username;
}
