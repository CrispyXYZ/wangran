package io.github.crispyxyz.wangran.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateAccountRequest {
    /**
     * 不超过20位的手机号，纯数字，非空
     */
    @NotBlank
    @Size(max = 20)
    @Pattern(regexp = "^[0-9]+$", message = "手机号只能由数字构成")
    private String phoneNumber;

    /**
     * 密码，长度6-50，不能为null，可不填默认为wangran123
     */
    @Size(min = 6, max = 50)
    @NotNull
    private String password = "wangran123";
}
