package io.github.crispyxyz.wangran.service.impl;

import io.github.crispyxyz.wangran.mapper.MerchantMapper;
import io.github.crispyxyz.wangran.mapper.UserMapper;
import io.github.crispyxyz.wangran.model.Merchant;
import io.github.crispyxyz.wangran.model.User;
import io.github.crispyxyz.wangran.model.excel.MerchantExcelData;
import io.github.crispyxyz.wangran.model.excel.UserExcelData;
import io.github.crispyxyz.wangran.service.AccountCreationService;
import io.github.crispyxyz.wangran.service.checker.PhoneNumberUniqueChecker;
import io.github.crispyxyz.wangran.service.factory.MerchantFactory;
import io.github.crispyxyz.wangran.service.factory.UserFactory;
import io.github.crispyxyz.wangran.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class AccountCreationServiceImpl implements AccountCreationService {
    private final PhoneNumberUniqueChecker checker;
    private final UserFactory userFactory;
    private final MerchantFactory merchantFactory;
    private final UserMapper userMapper;
    private final MerchantMapper merchantMapper;

    @Transactional
    @Override
    public User createUser(String phoneNumber, String password) {
        return createUser(phoneNumber, password, null);
    }

    @Transactional
    @Override
    public User createUser(String phoneNumber, String password, @Nullable String username) {
        checker.checkCreate(phoneNumber);
        User user = userFactory.create(phoneNumber, SecurityUtil.computeSha256(password), username);
        userMapper.insert(user);
        return user;
    }

    @Transactional
    @Override
    public Merchant createMerchant(String phoneNumber, String password, boolean autoApprove) {
        return createMerchant(phoneNumber, password, autoApprove, null, null);
    }

    @Transactional
    @Override
    public Merchant createMerchant(
        String phoneNumber,
        String password,
        boolean autoApprove,
        @Nullable String username,
        @Nullable String merchantCode
    ) {
        checker.checkCreate(phoneNumber);
        Merchant merchant = merchantFactory.create(
            phoneNumber,
            SecurityUtil.computeSha256(password),
            autoApprove,
            username,
            merchantCode
        );
        merchantMapper.insert(merchant);
        return merchant;
    }

    @Transactional
    @Override
    public void importUsersFromExcel(List<UserExcelData> list) {
        list.forEach(data -> createUser(data.getPhoneNumber(), data.getPassword(), data.getUsername()));
    }

    @Transactional
    @Override
    public void importMerchantsFromExcel(List<MerchantExcelData> list) {
        list.forEach(data -> createMerchant(
            data.getPhoneNumber(),
            data.getPassword(),
            true,
            data.getUsername(),
            data.getPhoneNumber()
        ));
    }
}
