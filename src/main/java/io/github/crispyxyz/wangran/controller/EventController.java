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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class EventController {
    private final EventService eventService;
    private final ModelMapper modelMapper;
    private final ModelMapperHelper modelMapperHelper;

    // TODO 检验 Organizer 是否存在
    @MerchantOnly
    @PostMapping
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
    public BaseResponse<PageResponse<EventResponse>> getEvents(
        @AuthenticationPrincipal AppPrincipal principal,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int pageSize
    ) {
        IPage<Event> pageInfo = eventService.getPage(page, pageSize, principal);
        PageResponse<EventResponse> pageResponse =
            new PageResponse<>(modelMapperHelper.mapPage(pageInfo, EventResponse.class));
        return ResponseUtil.success(pageResponse);
    }

    @MerchantOrAdmin
    @GetMapping("/{id}")
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
    public BaseResponse<Void> deleteEvent(@AuthenticationPrincipal AppPrincipal principal, @PathVariable int id) {
        if (eventService.removeById(id, principal)) {
            return ResponseUtil.success(null);
        } else {
            throw new ResourceNotFoundException("找不到id为" + id + "的票务，权限不足或票务不存在");
        }
    }

}
