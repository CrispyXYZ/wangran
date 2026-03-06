package io.github.crispyxyz.wangran.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "登录返回数据")
public class LoginResponse {
    @Schema(description = "token，用于Bearer认证", example = "aaa.bbb.ccc")
    String token;
    @Schema(description = "账户数据")
    AccountResponse account;
}
