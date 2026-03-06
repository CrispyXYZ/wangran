package io.github.crispyxyz.wangran.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "普通用户数据")
public class UserResponse implements AccountResponse {
    @Schema(description = "id", example = "1")
    private int id;
    @Schema(description = "昵称", example = "qwertyuiop")
    private String username;
    @Schema(description = "手机号", example = "12345678888")
    private String phoneNumber;
}
