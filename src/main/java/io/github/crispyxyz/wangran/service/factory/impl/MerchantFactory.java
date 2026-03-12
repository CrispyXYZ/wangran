package io.github.crispyxyz.wangran.service.factory.impl;

import io.github.crispyxyz.wangran.model.Merchant;
import io.github.crispyxyz.wangran.service.factory.AccountFactory;
import io.github.crispyxyz.wangran.util.GenerationUtil;
import org.springframework.stereotype.Component;

@Component
public class MerchantFactory implements AccountFactory<Merchant> {

    @Override
    public Merchant create(String phoneNumber, byte[] passwordSha256, boolean autoApprove) {
        Merchant merchant = new Merchant();
        merchant.setPhoneNumber(phoneNumber);
        merchant.setPasswordSha256(passwordSha256);
        if (autoApprove) {
            merchant.setMerchantCode(GenerationUtil.generateUniqueSequence(Merchant.CODE_PREFIX));
            merchant.setUsername(GenerationUtil.generateUniqueUsername(Merchant.USERNAME_PREFIX));
            merchant.setApprovalStatus(Merchant.STATUS_APPROVED);
        } else {
            merchant.setApprovalStatus(Merchant.STATUS_PENDING);
        }
        merchant.setRejectReason("");
        return merchant;
    }
}
