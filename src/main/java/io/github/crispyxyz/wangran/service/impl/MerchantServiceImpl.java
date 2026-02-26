package io.github.crispyxyz.wangran.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.crispyxyz.wangran.mapper.MerchantMapper;
import io.github.crispyxyz.wangran.model.Merchant;
import io.github.crispyxyz.wangran.request.UpdateAccountRequest;
import io.github.crispyxyz.wangran.service.MerchantService;
import org.springframework.stereotype.Service;
// TODO 未实现

/**
 *
 * 针对表【merchant】的数据库操作Service实现
 *
 */
@Service
public class MerchantServiceImpl extends ServiceImpl<MerchantMapper, Merchant>
implements MerchantService {

    @Override
    public IPage<Merchant> getUsers(int page, int pageSize) {
        return null;
    }

    @Override
    public void partialUpdate(int id, UpdateAccountRequest request) {

    }

    @Override
    public boolean existPhoneNumber(String phoneNumber) {
        return false;
    }

    @Override
    public boolean existUsername(String username) {
        return false;
    }
}




