package io.github.crispyxyz.wangran.component;

import io.github.crispyxyz.wangran.model.excel.MerchantExcelData;
import io.github.crispyxyz.wangran.service.AccountCreationService;

public class MerchantExcelListener extends AbstractExcelListener<MerchantExcelData> {
    public MerchantExcelListener(AccountCreationService creationService) {
        super(creationService::importMerchantsFromExcel);
    }
}
