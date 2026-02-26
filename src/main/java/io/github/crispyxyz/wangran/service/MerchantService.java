package io.github.crispyxyz.wangran.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import io.github.crispyxyz.wangran.model.Merchant;
import io.github.crispyxyz.wangran.request.UpdateAccountRequest;
import jakarta.validation.Valid;

/**
 *
 * 针对表【merchant】的数据库操作Service
 *
 */
public interface MerchantService extends IService<Merchant> {
    IPage<Merchant> getUsers(int page, int pageSize);

    void partialUpdate(int id, @Valid UpdateAccountRequest request);

    boolean existPhoneNumber(String phoneNumber);

    boolean existUsername(String username);
}
