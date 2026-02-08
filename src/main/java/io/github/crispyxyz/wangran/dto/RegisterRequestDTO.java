package io.github.crispyxyz.wangran.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 接收注册请求数据
 */
@Data
public class RegisterRequestDTO {
    /**
     * 不超过20位的手机号，纯数字，非空
     */
    @NotBlank
    @Size(max = 20)
    @Pattern(regexp = "^[0-9]+$", message = "手机号只能由数字构成")
    private String phoneNumber;

    /**
     * 密码，长度6-50，非空
     */
    @NotBlank
    @Size(min = 6, max = 50)
    private String password;

    /**
     * 是否为商户，非空
     */
    @NotNull
    private Boolean merchant;
}
