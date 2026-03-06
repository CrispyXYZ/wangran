package io.github.crispyxyz.wangran.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "统一返回格式")
public class BaseResponse<T> {
    @Schema(description = "是否成功", example = "true")
    private boolean success;
    @Schema(description = "消息，成功时总是为 success", example = "success")
    private String message;
    @Schema(description = "数据，失败时总是为 null")
    private T data;
}
