package io.github.crispyxyz.wangran.model.excel;

import lombok.Data;
import org.apache.fesod.sheet.annotation.ExcelProperty;

@Data
public class MerchantExcelData {
    @ExcelProperty("手机号")
    private String phoneNumber;

    @ExcelProperty("昵称")
    private String username;

    @ExcelProperty("密码")
    private String password;

    @ExcelProperty({"商户编号", "商户ID"})
    private String merchantCode;
}
