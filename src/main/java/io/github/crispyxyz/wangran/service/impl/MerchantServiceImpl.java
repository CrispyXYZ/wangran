package io.github.crispyxyz.wangran.service.impl;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import io.github.crispyxyz.wangran.component.MerchantExcelListener;
import io.github.crispyxyz.wangran.exception.ResourceNotFoundException;
import io.github.crispyxyz.wangran.exception.SystemException;
import io.github.crispyxyz.wangran.mapper.MerchantMapper;
import io.github.crispyxyz.wangran.model.Merchant;
import io.github.crispyxyz.wangran.model.excel.MerchantExcelData;
import io.github.crispyxyz.wangran.request.UpdateAccountRequest;
import io.github.crispyxyz.wangran.service.MerchantService;
import io.github.crispyxyz.wangran.util.GenerationUtil;
import lombok.RequiredArgsConstructor;
import org.apache.fesod.sheet.FesodSheet;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 *
 * 针对表【merchant】的数据库操作Service实现
 *
 */
@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class MerchantServiceImpl extends BaseEntityService<MerchantMapper, Merchant> implements MerchantService {

    private final ModelMapper modelMapper;

    @Transactional
    @Override
    public Merchant partialUpdate(int id, UpdateAccountRequest request) {
        return updateBuilder(id).setUnique(Merchant::getPhoneNumber, request.getPhoneNumber(), "该手机号已被占用")
                                .setUnique(Merchant::getUsername, request.getUsername(), "该昵称已被占用")
                                .setPassword(Merchant::getPasswordSha256, request.getPassword())
                                .execute();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existPhoneNumber(String phoneNumber) {
        return isFieldConflict(Merchant::getPhoneNumber, phoneNumber, null);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existUsername(String username) {
        return isFieldConflict(Merchant::getUsername, username, null);
    }

    @Transactional
    @Override
    public Merchant reviewMerchant(String phoneNumber, boolean approved, String rejectReason) {
        // 根据手机号查询商户
        Merchant merchant = lambdaQuery().eq(Merchant::getPhoneNumber, phoneNumber)
                                         .one();

        if (merchant == null) {
            throw new ResourceNotFoundException("商户不存在");
        }

        // 处理审核结果
        if (approved) {
            approve(merchant);
        } else {
            reject(merchant, rejectReason);
        }
        updateById(merchant);

        // TODO log.debug("审核处理成功，merchantPhoneNumber={}", phoneNumber);
        return merchant;
    }

    @Transactional
    @Override
    public Merchant create(String phoneNumber, byte[] passwordSha256) {
        return create(phoneNumber, passwordSha256, false);
    }

    @Transactional
    @Override
    public Merchant createByAdmin(String phoneNumber, byte[] passwordSha256) {
        return create(phoneNumber, passwordSha256, true);
    }

    @Transactional(readOnly = true)
    @Override
    public Merchant findByPhoneNumber(String phoneNumber) {
        return lambdaQuery().eq(Merchant::getPhoneNumber, phoneNumber)
                            .one();
    }

    @Transactional(readOnly = true)
    @Override
    public Merchant findByMerchantCode(String merchantCode) {
        return lambdaQuery().eq(Merchant::getMerchantCode, merchantCode)
                            .one();
    }

    @Transactional
    @Override
    public void importMerchants(MultipartFile file) {
        try {
            MerchantExcelListener listener = new MerchantExcelListener(modelMapper, this);
            FesodSheet.read(file.getInputStream(), MerchantExcelData.class, listener)
                      .sheet()
                      .doRead();
        } catch (IOException e) {
            throw new SystemException(e.getMessage());
        }
    }

    @Override
    protected SFunction<Merchant, ?> getIdField() {
        return Merchant::getId;
    }

    private Merchant create(String phoneNumber, byte[] passwordSha256, boolean autoApprove) {
        Merchant merchant = new Merchant();
        merchant.setPhoneNumber(phoneNumber);
        merchant.setPasswordSha256(passwordSha256);

        if (autoApprove) {
            merchant.setMerchantCode(GenerationUtil.generateUniqueSequence("mid_"));
            merchant.setUsername(GenerationUtil.generateUniqueUsername("merchant_"));
            merchant.setApprovalStatus(Merchant.STATUS_APPROVED);
            merchant.setRejectReason("");
        } else {
            merchant.setApprovalStatus(Merchant.STATUS_PENDING);
        }

        save(merchant);
        return merchant;
    }


    private void reject(Merchant merchant, String rejectReason) {
        // 审核不通过
        // TODO log.debug("审核不通过，merchantPhoneNumber={}", phoneNumber);

        merchant.setApprovalStatus(Merchant.STATUS_REJECTED);
        merchant.setRejectReason(rejectReason);
    }

    private void approve(Merchant merchant) {
        // 审核通过
        // TODO log.debug("审核通过，merchantPhoneNumber={}", phoneNumber);

        // 生成商户id
        if (merchant.getMerchantCode() == null) {
            String id = GenerationUtil.generateUniqueSequence("mid_");
            merchant.setMerchantCode(id);
        }

        // 生成昵称
        if (merchant.getUsername() == null) {
            String username = GenerationUtil.generateUniqueUsername("merchant_");
            merchant.setUsername(username);
        }

        // 其它属性
        merchant.setApprovalStatus(Merchant.STATUS_APPROVED);
        merchant.setRejectReason("");
    }
}
