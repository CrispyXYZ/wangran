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
import io.github.crispyxyz.wangran.security.annotation.UserSelfOrAdmin;
import io.github.crispyxyz.wangran.security.annotation.AdminOnly;
import io.github.crispyxyz.wangran.service.AuthService;
import io.github.crispyxyz.wangran.service.UserService;
import io.github.crispyxyz.wangran.util.ResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class UserController {
    private final UserService userService;
    private final ModelMapperHelper modelMapperHelper;
    private final ModelMapper modelMapper;
    private final AuthService authService;

    @AdminOnly
    @GetMapping
    public BaseResponse<PageResponse<UserResponse>> getUsers(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int pageSize
    ) {
        IPage<User> pageInfo = userService.getPage(page, pageSize);
        PageResponse<UserResponse> pageResponse =
            new PageResponse<>(modelMapperHelper.mapPage(pageInfo, UserResponse.class));
        return ResponseUtil.success(pageResponse);
    }

    @UserSelfOrAdmin
    @GetMapping("/{id}")
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
    public BaseResponse<UserResponse> createUser(@Valid @RequestBody CreateAccountRequest request) {
        UserResponse userResponse =
            (UserResponse) authService.register(request.getPhoneNumber(), request.getPassword(), false);
        return ResponseUtil.success(userResponse);
    }

    @UserSelfOrAdmin
    @DeleteMapping("/{id}")
    public BaseResponse<Void> deleteUser(@PathVariable int id) {
        if (userService.removeById(id)) {
            return ResponseUtil.success(null);
        } else {
            throw new ResourceNotFoundException("找不到id为" + id + "的用户");
        }
    }

    @UserSelfOrAdmin
    @PatchMapping("/{id}")
    public BaseResponse<UserResponse> updateUser(
        @PathVariable int id,
        @Valid @RequestBody UpdateAccountRequest request
    ) {
        UserResponse userResponse = modelMapper.map(userService.partialUpdate(id, request), UserResponse.class);
        return ResponseUtil.success(userResponse);
    }

    @AdminOnly
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse<Void> importUser(@RequestParam("file") MultipartFile file) {
        userService.importUsers(file);
        return ResponseUtil.success(null);
    }
}
