package io.github.crispyxyz.wangran.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "主办方信息")
public class OrganizerResponse {
    @Schema(description = "id", example = "1")
    private int id;
    @Schema(description = "主办方名称", example = "想象力有限公司")
    private String name;
    @Schema(description = "主办方联系电话", example = "12345678888")
    private String phoneNumber;
    @Schema(description = "主办方联系地址", example = "翻斗大街翻斗花园二号楼1001")
    private String address;
}
