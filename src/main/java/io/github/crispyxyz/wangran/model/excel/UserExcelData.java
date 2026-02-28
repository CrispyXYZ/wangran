package io.github.crispyxyz.wangran.model.excel;

import lombok.Data;
import org.apache.fesod.sheet.annotation.ExcelProperty;

@Data
public class UserExcelData {
    @ExcelProperty("手机号")
    private String phoneNumber;

    @ExcelProperty("昵称")
    private String username;

    @ExcelProperty("密码")
    private String password;
}
