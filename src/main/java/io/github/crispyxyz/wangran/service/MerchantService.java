package io.github.crispyxyz.wangran.service;

import io.github.crispyxyz.wangran.model.Merchant;
import io.github.crispyxyz.wangran.request.UpdateAccountRequest;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * 针对表【merchant】的数据库操作Service
 *
 */
public interface MerchantService extends EntityService<Merchant> {
    Merchant partialUpdate(int id, UpdateAccountRequest request);

    Merchant reviewMerchant(String phoneNumber, boolean approved, String rejectReason);

    Merchant findByPhoneNumber(String phoneNumber);

    Merchant findByMerchantCode(String merchantCode);

    void importMerchants(MultipartFile file);

    void validateApprovalStatus(int id);
}
