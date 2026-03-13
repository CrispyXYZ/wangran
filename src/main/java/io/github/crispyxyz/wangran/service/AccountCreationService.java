package io.github.crispyxyz.wangran.service;

import io.github.crispyxyz.wangran.model.Merchant;
import io.github.crispyxyz.wangran.model.User;
import io.github.crispyxyz.wangran.model.excel.MerchantExcelData;
import io.github.crispyxyz.wangran.model.excel.UserExcelData;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface AccountCreationService {
    User createUser(String phoneNumber, String password);

    User createUser(String phoneNumber, String password, @Nullable String username);

    Merchant createMerchant(String phoneNumber, String password, boolean autoApprove);

    Merchant createMerchant(
        String phoneNumber,
        String password,
        boolean autoApprove,
        @Nullable String username,
        @Nullable String merchantCode
    );

    void importUsersFromExcel(List<UserExcelData> list);

    void importMerchantsFromExcel(List<MerchantExcelData> list);
}
