package io.github.crispyxyz.wangran.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.github.crispyxyz.wangran.component.ModelMapperHelper;
import io.github.crispyxyz.wangran.exception.ResourceNotFoundException;
import io.github.crispyxyz.wangran.model.Organizer;
import io.github.crispyxyz.wangran.request.CreateOrganizerRequest;
import io.github.crispyxyz.wangran.request.UpdateOrganizerRequest;
import io.github.crispyxyz.wangran.response.BaseResponse;
import io.github.crispyxyz.wangran.response.OrganizerResponse;
import io.github.crispyxyz.wangran.response.PageResponse;
import io.github.crispyxyz.wangran.security.annotation.AdminOnly;
import io.github.crispyxyz.wangran.security.annotation.MerchantOrAdmin;
import io.github.crispyxyz.wangran.service.OrganizerService;
import io.github.crispyxyz.wangran.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "主办方接口")
@RestController
@RequestMapping("/organizers")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class OrganizerController {
    private final OrganizerService organizerService;
    private final ModelMapperHelper modelMapperHelper;
    private final ModelMapper modelMapper;

    @MerchantOrAdmin
    @GetMapping
    @Operation(summary = "获取主办方", description = "返回分页的主办方数据，仅商户或管理员可访问")
    public BaseResponse<PageResponse<OrganizerResponse>> getOrganizers(
        @Parameter(description = "当前页码，从1开始", example = "1") @RequestParam(defaultValue = "1") int page,
        @Parameter(description = "每页记录数", example = "10") @RequestParam(defaultValue = "10") int pageSize
    ) {
        IPage<Organizer> pageInfo = organizerService.getPage(page, pageSize);
        PageResponse<OrganizerResponse> pageResponse =
            new PageResponse<>(modelMapperHelper.mapPage(pageInfo, OrganizerResponse.class));
        return ResponseUtil.success(pageResponse);
    }

    @MerchantOrAdmin
    @GetMapping("/{id}")
    @Operation(summary = "根据id获取主办方", description = "返回主办方数据，仅商户或管理员可以访问")
    public BaseResponse<OrganizerResponse> getOrganizer(@PathVariable int id) {
        Organizer organizer = organizerService.getById(id);
        if (organizer == null) {
            throw new ResourceNotFoundException("找不到id为" + id + "的主办方");
        }
        OrganizerResponse organizerResponse = modelMapper.map(organizer, OrganizerResponse.class);
        return ResponseUtil.success(organizerResponse);
    }

    @AdminOnly
    @PostMapping
    @Operation(summary = "创建主办方", description = "返回主办方数据，仅管理员可以访问")
    public BaseResponse<OrganizerResponse> createOrganizer(@Valid @RequestBody CreateOrganizerRequest request) {
        Organizer organizer =
            organizerService.create(request.getName(), request.getPhoneNumber(), request.getAddress());
        return ResponseUtil.success(modelMapper.map(organizer, OrganizerResponse.class));
    }

    @AdminOnly
    @DeleteMapping("/{id}")
    @Operation(summary = "删除主办方", description = "返回空数据，仅管理员可以访问")
    public BaseResponse<Void> deleteOrganizer(@PathVariable int id) {
        if (organizerService.removeById(id)) {
            return ResponseUtil.success(null);
        } else {
            throw new ResourceNotFoundException("找不到id为" + id + "的主办方");
        }
    }

    @AdminOnly
    @PatchMapping("/{id}")
    @Operation(summary = "根据id更新主办方", description = "返回主办方数据，仅管理员可以访问")
    public BaseResponse<OrganizerResponse> updateOrganizer(
        @PathVariable int id,
        @Valid @RequestBody UpdateOrganizerRequest request
    ) {
        OrganizerResponse organizerResponse =
            modelMapper.map(organizerService.partialUpdate(id, request), OrganizerResponse.class);
        return ResponseUtil.success(organizerResponse);
    }
}
