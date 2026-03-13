package io.github.crispyxyz.wangran.component;

import io.github.crispyxyz.wangran.model.excel.UserExcelData;
import io.github.crispyxyz.wangran.service.AccountCreationService;

public class UserExcelListener extends AbstractExcelListener<UserExcelData> {
    public UserExcelListener(AccountCreationService creationService) {
        super(creationService::importUsersFromExcel);
    }
}