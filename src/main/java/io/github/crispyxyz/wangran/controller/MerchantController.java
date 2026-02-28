package io.github.crispyxyz.wangran.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.github.crispyxyz.wangran.component.ModelMapperHelper;
import io.github.crispyxyz.wangran.exception.ResourceNotFoundException;
import io.github.crispyxyz.wangran.model.Merchant;
import io.github.crispyxyz.wangran.request.CreateAccountRequest;
import io.github.crispyxyz.wangran.request.UpdateAccountRequest;
import io.github.crispyxyz.wangran.response.BaseResponse;
import io.github.crispyxyz.wangran.response.MerchantResponse;
import io.github.crispyxyz.wangran.response.PageResponse;
import io.github.crispyxyz.wangran.service.AuthService;
import io.github.crispyxyz.wangran.service.MerchantService;
import io.github.crispyxyz.wangran.util.ResponseUtil;
import io.github.crispyxyz.wangran.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/merchants")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class MerchantController {
    private final MerchantService merchantService;
    private final ModelMapperHelper modelMapperHelper;
    private final ModelMapper modelMapper;
    private final AuthService authService;
    // TODO 取代authService？

    @GetMapping
    public BaseResponse<PageResponse<MerchantResponse>> getMerchants(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int pageSize
    ) {
        IPage<Merchant> pageInfo = merchantService.getPage(page, pageSize);
        PageResponse<MerchantResponse> pageResponse =
            new PageResponse<>(modelMapperHelper.mapPage(pageInfo, MerchantResponse.class));
        return ResponseUtil.success(pageResponse);
    }

    @GetMapping("/{id}")
    public BaseResponse<MerchantResponse> getMerchant(@PathVariable int id) {
        Merchant merchant = merchantService.getById(id);
        if (merchant == null) {
            throw new ResourceNotFoundException("找不到id为" + id + "的商户");
        }
        MerchantResponse merchantResponse = modelMapper.map(merchant, MerchantResponse.class);
        return ResponseUtil.success(merchantResponse);
    }

    @PostMapping
    public BaseResponse<MerchantResponse> createMerchant(@Valid @RequestBody CreateAccountRequest request) {
        Merchant merchant =
            merchantService.createByAdmin(request.getPhoneNumber(), SecurityUtil.computeSha256(request.getPassword()));
        MerchantResponse merchantResponse = modelMapper.map(merchant, MerchantResponse.class);
        return ResponseUtil.success(merchantResponse);
    }

    @DeleteMapping("/{id}")
    public BaseResponse<Void> deleteMerchant(@PathVariable int id) {
        if (merchantService.removeById(id)) {
            return ResponseUtil.success(null);
        } else {
            throw new ResourceNotFoundException("找不到id为" + id + "的商户");
        }
    }

    @PatchMapping("/{id}")
    public BaseResponse<MerchantResponse> updateMerchant(
        @PathVariable int id,
        @Valid @RequestBody UpdateAccountRequest request
    ) {
        // TODO 可能需要拒绝未审核商户修改个人信息
        MerchantResponse merchantResponse =
            modelMapper.map(merchantService.partialUpdate(id, request), MerchantResponse.class);
        return ResponseUtil.success(merchantResponse);
    }

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse<Void> importMerchant(@RequestParam("file") MultipartFile file) {
        merchantService.importMerchants(file);
        return ResponseUtil.success(null);
    }
}
