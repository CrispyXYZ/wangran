package io.github.crispyxyz.wangran.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.github.crispyxyz.wangran.component.ModelMapperHelper;
import io.github.crispyxyz.wangran.exception.ResourceNotFoundException;
import io.github.crispyxyz.wangran.model.Merchant;
import io.github.crispyxyz.wangran.request.CreateAccountRequest;
import io.github.crispyxyz.wangran.request.ReviewRequest;
import io.github.crispyxyz.wangran.request.UpdateAccountRequest;
import io.github.crispyxyz.wangran.response.BaseResponse;
import io.github.crispyxyz.wangran.response.MerchantResponse;
import io.github.crispyxyz.wangran.response.PageResponse;
import io.github.crispyxyz.wangran.security.annotation.AdminOnly;
import io.github.crispyxyz.wangran.security.annotation.MerchantSelfOrAdmin;
import io.github.crispyxyz.wangran.service.AuthService;
import io.github.crispyxyz.wangran.service.MerchantService;
import io.github.crispyxyz.wangran.util.ResponseUtil;
import io.github.crispyxyz.wangran.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "商户接口")
@Slf4j
@RestController
@RequestMapping("/merchants")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class MerchantController {
    private final MerchantService merchantService;
    private final ModelMapperHelper modelMapperHelper;
    private final ModelMapper modelMapper;
    private final AuthService authService;
    // TODO 取代authService？

    @AdminOnly
    @GetMapping
    @Operation(summary = "获取商户信息", description = "返回分页的商户信息，仅管理员可访问此接口")
    public BaseResponse<PageResponse<MerchantResponse>> getMerchants(
        @Parameter(description = "当前页码，从1开始", example = "1") @RequestParam(defaultValue = "1") int page,
        @Parameter(description = "每页记录数", example = "10") @RequestParam(defaultValue = "10") int pageSize
    ) {
        IPage<Merchant> pageInfo = merchantService.getPage(page, pageSize);
        PageResponse<MerchantResponse> pageResponse =
            new PageResponse<>(modelMapperHelper.mapPage(pageInfo, MerchantResponse.class));
        return ResponseUtil.success(pageResponse);
    }

    @MerchantSelfOrAdmin
    @GetMapping("/{id}")
    @Operation(summary = "根据id获取商户", description = "返回商户信息，仅管理员或商户本人可以访问")
    public BaseResponse<MerchantResponse> getMerchant(@PathVariable int id) {
        Merchant merchant = merchantService.getById(id);
        if (merchant == null) {
            throw new ResourceNotFoundException("找不到id为" + id + "的商户");
        }
        MerchantResponse merchantResponse = modelMapper.map(merchant, MerchantResponse.class);
        return ResponseUtil.success(merchantResponse);
    }

    @AdminOnly
    @PostMapping
    @Operation(
        summary = "创建商户",
        description = "返回商户信息，默认密码wangran123，仅管理员可以访问，若需要公共注册请访问/auth内的公共接口"
    )
    public BaseResponse<MerchantResponse> createMerchant(@Valid @RequestBody CreateAccountRequest request) {
        if (request.getPassword() == null) {
            request.setPassword("wangran123");
        }
        Merchant merchant =
            merchantService.createByAdmin(request.getPhoneNumber(), SecurityUtil.computeSha256(request.getPassword()));
        MerchantResponse merchantResponse = modelMapper.map(merchant, MerchantResponse.class);
        return ResponseUtil.success(merchantResponse);
    }

    @MerchantSelfOrAdmin
    @DeleteMapping("/{id}")
    @Operation(summary = "根据id删除商户", description = "返回空数据，仅商户本人或管理员可以访问此接口")
    public BaseResponse<Void> deleteMerchant(@PathVariable int id) {
        if (merchantService.removeById(id)) {
            return ResponseUtil.success(null);
        } else {
            throw new ResourceNotFoundException("找不到id为" + id + "的商户");
        }
    }

    @MerchantSelfOrAdmin
    @PatchMapping("/{id}")
    @Operation(summary = "根据id更新商户", description = "返回商户信息，仅商户本人或管理员可以访问")
    public BaseResponse<MerchantResponse> updateMerchant(
        @PathVariable int id,
        @Valid @RequestBody UpdateAccountRequest request
    ) {
        // TODO 可能需要拒绝未审核商户修改个人信息
        MerchantResponse merchantResponse =
            modelMapper.map(merchantService.partialUpdate(id, request), MerchantResponse.class);
        return ResponseUtil.success(merchantResponse);
    }

    @AdminOnly
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "批量导入商户（表格文件）", description = "返回空数据，仅管理员可以访问")
    public BaseResponse<Void> importMerchant(@RequestParam("file") MultipartFile file) {
        merchantService.importMerchants(file);
        return ResponseUtil.success(null);
    }

    /**
     * 审核接口
     * TODO 拒绝时应检验检验RejectReason不为null
     *
     * @param reviewRequest 审核请求参数
     * @return 商户信息
     */
    @AdminOnly
    @PostMapping("/review")
    @Operation(summary = "审核商户", description = "返回商户信息，仅管理员可访问")
    public BaseResponse<MerchantResponse> review(@Valid @RequestBody ReviewRequest reviewRequest) {
        log.info("接收审核请求: {}", reviewRequest);
        Merchant data = merchantService.reviewMerchant(
            reviewRequest.getMerchantPhoneNumber(),
            reviewRequest.getApproved(),
            reviewRequest.getRejectReason()
        );
        log.info("审核请求成功: {}", data);
        return ResponseUtil.success(modelMapper.map(data, MerchantResponse.class));
    }
}
