package io.github.crispyxyz.wangran.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import io.github.crispyxyz.wangran.model.Merchant;
import io.github.crispyxyz.wangran.request.UpdateAccountRequest;

/**
 *
 * 针对表【merchant】的数据库操作Service
 *
 */
public interface MerchantService extends IService<Merchant> {
    IPage<Merchant> getMerchants(int page, int pageSize);

    Merchant partialUpdate(int id, UpdateAccountRequest request);

    boolean existPhoneNumber(String phoneNumber);

    boolean existUsername(String username);

    Merchant reviewMerchant(String phoneNumber, boolean approved, String rejectReason);

    Merchant create(String phoneNumber, byte[] passwordSha256);

    Merchant createByAdmin(String phoneNumber, byte[] passwordSha256);

    Merchant findByPhoneNumber(String phoneNumber);

    Merchant findByMerchantId(String merchantId);
}
