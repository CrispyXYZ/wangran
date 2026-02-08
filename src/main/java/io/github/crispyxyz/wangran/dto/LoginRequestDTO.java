package io.github.crispyxyz.wangran.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 接收登录请求数据
 */
@Data
public class LoginRequestDTO {
    /**
     * 标识符，即手机号或商户id，也可以是管理员账号，不超过50字符，非空
     */
    @NotBlank
    @Size(max = 50)
    private String identifier;

    /**
     * 密码，长度6-50，非空
     */
    @NotBlank
    @Size(min = 6, max = 50)
    private String password;
}
