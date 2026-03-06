package io.github.crispyxyz.wangran.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.github.crispyxyz.wangran.component.ModelMapperHelper;
import io.github.crispyxyz.wangran.exception.ResourceNotFoundException;
import io.github.crispyxyz.wangran.model.Event;
import io.github.crispyxyz.wangran.request.CreateEventRequest;
import io.github.crispyxyz.wangran.request.UpdateEventRequest;
import io.github.crispyxyz.wangran.response.BaseResponse;
import io.github.crispyxyz.wangran.response.EventResponse;
import io.github.crispyxyz.wangran.response.PageResponse;
import io.github.crispyxyz.wangran.security.AppPrincipal;
import io.github.crispyxyz.wangran.security.annotation.MerchantOnly;
import io.github.crispyxyz.wangran.security.annotation.MerchantOrAdmin;
import io.github.crispyxyz.wangran.service.EventService;
import io.github.crispyxyz.wangran.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@Slf4j
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Tag(name = "票务接口")
public class EventController {
    private final EventService eventService;
    private final ModelMapper modelMapper;
    private final ModelMapperHelper modelMapperHelper;

    // TODO 检验 Organizer 是否存在，不然会报 java.sql.SQLIntegrityConstraintViolationException: fk
    @MerchantOnly
    @PostMapping
    @Operation(summary = "创建票务", description = "返回票务信息，仅商户可访问该接口")
    public BaseResponse<EventResponse> createEvent(
        @AuthenticationPrincipal AppPrincipal principal,
        @RequestBody @Valid CreateEventRequest request
    ) {
        int merchantId = principal.getId();
        Event event = eventService.create(merchantId, request);
        return ResponseUtil.success(modelMapper.map(event, EventResponse.class));
    }

    @MerchantOrAdmin
    @GetMapping
    @Operation(
        summary = "获取票务",
        description = "返回分页的票务信息，仅商户（返回自己创建的票务）和管理员（返回所有票务）可访问此接口"
    )
    public BaseResponse<PageResponse<EventResponse>> getEvents(
        @AuthenticationPrincipal AppPrincipal principal,
        @Parameter(description = "当前页码，从1开始", example = "1") @RequestParam(defaultValue = "1") int page,
        @Parameter(description = "每页记录数", example = "10") @RequestParam(defaultValue = "10") int pageSize
    ) {
        IPage<Event> pageInfo = eventService.getPage(page, pageSize, principal);
        PageResponse<EventResponse> pageResponse =
            new PageResponse<>(modelMapperHelper.mapPage(pageInfo, EventResponse.class));
        return ResponseUtil.success(pageResponse);
    }

    @MerchantOrAdmin
    @GetMapping("/{id}")
    @Operation(summary = "根据id获取票务", description = "返回票务信息，仅管理员或商户可以访问此接口")
    public BaseResponse<EventResponse> getEvent(@AuthenticationPrincipal AppPrincipal principal, @PathVariable int id) {
        Event event = eventService.getById(id, principal);
        if (event == null) {
            throw new ResourceNotFoundException("找不到 id 为" + id + "的票务，权限不足或票务不存在");
        }
        EventResponse eventResponse = modelMapper.map(event, EventResponse.class);
        return ResponseUtil.success(eventResponse);
    }

    @MerchantOnly
    @PatchMapping("/{id}")
    @Operation(summary = "根据id更新票务", description = "返回票务信息，仅商户可以访问此接口")
    public BaseResponse<EventResponse> updateEvent(
        @AuthenticationPrincipal AppPrincipal principal,
        @PathVariable int id,
        @Valid @RequestBody UpdateEventRequest request
    ) {
        EventResponse eventResponse =
            modelMapper.map(eventService.partialUpdate(principal, id, request), EventResponse.class);
        return ResponseUtil.success(eventResponse);
    }

    @MerchantOnly
    @DeleteMapping("/{id}")
    @Operation(summary = "根据id删除票务", description = "返回空数据，仅商户可以访问此接口")
    public BaseResponse<Void> deleteEvent(@AuthenticationPrincipal AppPrincipal principal, @PathVariable int id) {
        if (eventService.removeById(id, principal)) {
            return ResponseUtil.success(null);
        } else {
            throw new ResourceNotFoundException("找不到id为" + id + "的票务，权限不足或票务不存在");
        }
    }

    @SecurityRequirements
    @GetMapping("/public")
    @Operation(summary = "获取已上架的票务（公共接口，无权限控制）", description = "返回分页的票务，无访问权限控制")
    public BaseResponse<PageResponse<EventResponse>> getPublicEvents(
        @Parameter(description = "票务类型（演出/赛事）") @RequestParam(required = false) String eventType,
        @Parameter(description = "举办城市") @RequestParam(required = false) String city,
        @Parameter(description = "售票开始时间") @RequestParam(required = false) Instant startTime,
        @Parameter(description = "售票结束时间") @RequestParam(required = false) Instant endTime,
        @Parameter(description = "当前页码，从1开始", example = "1") @RequestParam(defaultValue = "1") int page,
        @Parameter(description = "每页记录数", example = "10") @RequestParam(defaultValue = "10") int pageSize
    ) {
        log.info("startTime={}, endTime={}", startTime, endTime);
        PageResponse<EventResponse> pageResponse =
            eventService.getPublicEvents(eventType, city, startTime, endTime, page, pageSize);
        return ResponseUtil.success(pageResponse);
    }

}
