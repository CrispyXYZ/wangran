package io.github.crispyxyz.wangran.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 接收登录请求数据
 */
@Data
public class LoginRequest {
    /**
     * 标识符，即手机号或商户id，也可以是管理员账号，不超过50字符，非空
     */
    @NotBlank
    @Size(max = 50)
    @Schema(description = "标识符，即手机号或商户编号，也可以是管理员账号", example = "mid_123456789")
    private String identifier;

    /**
     * 密码，长度6-50，非空
     */
    @NotBlank
    @Size(min = 6, max = 50)
    @Schema(description = "密码（明文）", example = "P@SsW0rD")
    private String password;
}
