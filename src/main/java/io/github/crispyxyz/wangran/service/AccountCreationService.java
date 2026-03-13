package io.github.crispyxyz.wangran.service;

import io.github.crispyxyz.wangran.model.Merchant;
import io.github.crispyxyz.wangran.model.User;

public interface AccountCreationService {
    User createUser(String phoneNumber, String password);

    Merchant createMerchant(String phoneNumber, String password, boolean autoApprove);
}
