package io.github.crispyxyz.wangran.service.factory;

import io.github.crispyxyz.wangran.model.Merchant;
import io.github.crispyxyz.wangran.util.GenerationUtil;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class MerchantFactory {

    public Merchant create(
        String phoneNumber,
        byte[] passwordSha256,
        boolean autoApprove,
        @Nullable String username,
        @Nullable String merchantCode
    ) {
        Merchant merchant = new Merchant();
        merchant.setPhoneNumber(phoneNumber);
        merchant.setPasswordSha256(passwordSha256);
        if (autoApprove) {
            merchant.setMerchantCode(Objects.requireNonNullElseGet(
                merchantCode,
                () -> GenerationUtil.generateUniqueSequence(Merchant.CODE_PREFIX)
            ));
            merchant.setUsername(Objects.requireNonNullElseGet(
                username,
                () -> GenerationUtil.generateUniqueUsername(Merchant.USERNAME_PREFIX)
            ));
            merchant.setApprovalStatus(Merchant.STATUS_APPROVED);
        } else {
            merchant.setApprovalStatus(Merchant.STATUS_PENDING);
        }
        merchant.setRejectReason("");
        return merchant;
    }
}
