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
import io.github.crispyxyz.wangran.service.OrganizerService;
import io.github.crispyxyz.wangran.util.ResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/organizers")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class OrganizerController {
    private final OrganizerService organizerService;
    private final ModelMapperHelper modelMapperHelper;
    private final ModelMapper modelMapper;

    @GetMapping
    public BaseResponse<PageResponse<OrganizerResponse>> getOrganizers(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int pageSize
    ) {
        IPage<Organizer> pageInfo = organizerService.getPage(page, pageSize);
        PageResponse<OrganizerResponse> pageResponse =
            new PageResponse<>(modelMapperHelper.mapPage(pageInfo, OrganizerResponse.class));
        return ResponseUtil.success(pageResponse);
    }

    @GetMapping("/{id}")
    public BaseResponse<OrganizerResponse> getOrganizer(@PathVariable int id) {
        Organizer organizer = organizerService.getById(id);
        if (organizer == null) {
            throw new ResourceNotFoundException("找不到id为" + id + "的主办方");
        }
        OrganizerResponse organizerResponse = modelMapper.map(organizer, OrganizerResponse.class);
        return ResponseUtil.success(organizerResponse);
    }

    @PostMapping
    public BaseResponse<OrganizerResponse> createOrganizer(@Valid @RequestBody CreateOrganizerRequest request) {
        Organizer organizer =
            organizerService.create(request.getName(), request.getPhoneNumber(), request.getAddress());
        return ResponseUtil.success(modelMapper.map(organizer, OrganizerResponse.class));
    }

    @DeleteMapping("/{id}")
    public BaseResponse<Void> deleteOrganizer(@PathVariable int id) {
        if (organizerService.removeById(id)) {
            return ResponseUtil.success(null);
        } else {
            throw new ResourceNotFoundException("找不到id为" + id + "的主办方");
        }
    }

    @PatchMapping("/{id}")
    public BaseResponse<OrganizerResponse> updateOrganizer(
        @PathVariable int id,
        @Valid @RequestBody UpdateOrganizerRequest request
    ) {
        OrganizerResponse organizerResponse =
            modelMapper.map(organizerService.partialUpdate(id, request), OrganizerResponse.class);
        return ResponseUtil.success(organizerResponse);
    }
}
