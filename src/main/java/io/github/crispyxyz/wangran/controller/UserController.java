package io.github.crispyxyz.wangran.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.github.crispyxyz.wangran.component.ModelMapperHelper;
import io.github.crispyxyz.wangran.exception.ResourceNotFoundException;
import io.github.crispyxyz.wangran.model.User;
import io.github.crispyxyz.wangran.request.CreateAccountRequest;
import io.github.crispyxyz.wangran.request.UpdateAccountRequest;
import io.github.crispyxyz.wangran.response.BaseResponse;
import io.github.crispyxyz.wangran.response.PageResponse;
import io.github.crispyxyz.wangran.response.UserResponse;
import io.github.crispyxyz.wangran.security.annotation.AdminOnly;
import io.github.crispyxyz.wangran.security.annotation.UserSelfOrAdmin;
import io.github.crispyxyz.wangran.service.AuthService;
import io.github.crispyxyz.wangran.service.UserService;
import io.github.crispyxyz.wangran.util.ResponseUtil;
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

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Slf4j
@Tag(name = "普通用户接口")
public class UserController {
    private final UserService userService;
    private final ModelMapperHelper modelMapperHelper;
    private final ModelMapper modelMapper;
    private final AuthService authService;

    @AdminOnly
    @GetMapping
    @Operation(summary = "获取用户", description = "返回分页的用户数据，仅管理员可访问")
    public BaseResponse<PageResponse<UserResponse>> getUsers(
        @Parameter(description = "当前页码，从1开始", example = "1") @RequestParam(defaultValue = "1") int page,
        @Parameter(description = "每页记录数", example = "10") @RequestParam(defaultValue = "10") int pageSize
    ) {
        IPage<User> pageInfo = userService.getPage(page, pageSize);
        PageResponse<UserResponse> pageResponse =
            new PageResponse<>(modelMapperHelper.mapPage(pageInfo, UserResponse.class));
        return ResponseUtil.success(pageResponse);
    }

    @UserSelfOrAdmin
    @GetMapping("/{id}")
    @Operation(summary = "根据id获取用户", description = "返回用户数据，仅管理员或用户本人可以访问")
    public BaseResponse<UserResponse> getUser(@PathVariable int id) {
        User user = userService.getById(id);
        if (user == null) {
            throw new ResourceNotFoundException("找不到id为" + id + "的用户");
        }
        UserResponse userResponse = modelMapper.map(user, UserResponse.class);
        return ResponseUtil.success(userResponse);
    }

    @AdminOnly
    @PostMapping
    @Operation(
        summary = "创建用户", description = "返回用户数据，默认密码wangran123，仅管理员可访问，若要公共注册请访问/auth里的公共接口"
    )
    public BaseResponse<UserResponse> createUser(@Valid @RequestBody CreateAccountRequest request) {
        if (request.getPassword() == null) {
            request.setPassword("wangran123");
        }
        UserResponse userResponse =
            (UserResponse) authService.register(request.getPhoneNumber(), request.getPassword(), false);
        log.info("创建用户成功，手机号：{}", request.getPhoneNumber());
        return ResponseUtil.success(userResponse);
    }

    @UserSelfOrAdmin
    @DeleteMapping("/{id}")
    @Operation(summary = "根据id删除用户", description = "返回空数据，仅管理员或用户本人可访问")
    public BaseResponse<Void> deleteUser(@PathVariable int id) {
        if (userService.removeById(id)) {
            log.info("删除用户成功，用户ID：{}", id);
            return ResponseUtil.success(null);
        } else {
            log.warn("删除用户失败，用户ID：{}，原因：用户不存在", id);
            throw new ResourceNotFoundException("找不到id为" + id + "的用户");
        }
    }

    @UserSelfOrAdmin
    @PatchMapping("/{id}")
    @Operation(summary = "根据id更新用户", description = "返回用户数据，仅管理员或用户本人可以访问")
    public BaseResponse<UserResponse> updateUser(
        @PathVariable int id,
        @Valid @RequestBody UpdateAccountRequest request
    ) {
        UserResponse userResponse = modelMapper.map(userService.partialUpdate(id, request), UserResponse.class);
        log.info("更新用户信息成功，用户ID：{}", id);
        return ResponseUtil.success(userResponse);
    }

    @AdminOnly
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "批量导入用户数据（表格）", description = "返回空数据，仅管理员可以访问")
    public BaseResponse<Void> importUser(@RequestParam("file") MultipartFile file) {
        try {
            userService.importUsers(file);
            log.info("批量导入用户成功");
            return ResponseUtil.success(null);
        } catch (Exception e) {
            log.error("批量导入用户失败", e);
            throw e;
        }
    }
}
