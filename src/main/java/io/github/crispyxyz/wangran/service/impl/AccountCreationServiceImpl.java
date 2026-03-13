package io.github.crispyxyz.wangran.service.impl;

import io.github.crispyxyz.wangran.model.Merchant;
import io.github.crispyxyz.wangran.model.User;
import io.github.crispyxyz.wangran.service.AccountCreationService;
import io.github.crispyxyz.wangran.service.MerchantService;
import io.github.crispyxyz.wangran.service.UserService;
import io.github.crispyxyz.wangran.service.checker.PhoneNumberUniqueChecker;
import io.github.crispyxyz.wangran.service.factory.impl.MerchantFactory;
import io.github.crispyxyz.wangran.service.factory.impl.UserFactory;
import io.github.crispyxyz.wangran.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class AccountCreationServiceImpl implements AccountCreationService {
    private final PhoneNumberUniqueChecker checker;
    private final UserFactory userFactory;
    private final UserService userService;
    private final MerchantFactory merchantFactory;
    private final MerchantService merchantService;

    @Transactional
    @Override
    public User createUser(String phoneNumber, String password) {
        checker.checkCreate(phoneNumber);
        User user = userFactory.create(phoneNumber, SecurityUtil.computeSha256(password));
        userService.save(user);
        return user;
    }

    @Transactional
    @Override
    public Merchant createMerchant(String phoneNumber, String password, boolean autoApprove) {
        checker.checkCreate(phoneNumber);
        Merchant merchant = merchantFactory.create(phoneNumber, SecurityUtil.computeSha256(password), autoApprove);
        merchantService.save(merchant);
        return merchant;
    }
}
