package io.github.crispyxyz.wangran.model.excel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.apache.fesod.sheet.annotation.ExcelProperty;

@Data
public class UserExcelData {
    @ExcelProperty("手机号")
    @NotBlank
    @Size(max = 20)
    @Pattern(regexp = "^[0-9]+$", message = "手机号只能由数字构成")
    private String phoneNumber;

    @ExcelProperty("昵称")
    @NotBlank
    @Size(max = 50)
    private String username;

    @ExcelProperty("密码")
    @NotBlank
    @Size(min = 6, max = 50)
    private String password;
}
