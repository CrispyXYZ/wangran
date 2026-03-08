package io.github.crispyxyz.wangran.model.excel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.apache.fesod.sheet.annotation.ExcelProperty;

@Data
public class MerchantExcelData {
    @ExcelProperty("手机号")
    @NotBlank
    @Size(max = 20)
    @Pattern(regexp = "^[0-9]+$", message = "手机号只能由数字构成")
    private String phoneNumber;

    @ExcelProperty("昵称")
    @Size(max = 50)
    private String username;

    @ExcelProperty("密码")
    @Size(min = 6, max = 50)
    private String password;

    @ExcelProperty("商户编号")
    @Size(max = 20)
    @Pattern(regexp = "^mid_.*$", message = "商户编号应当以mid_开头")
    private String merchantCode;
}
