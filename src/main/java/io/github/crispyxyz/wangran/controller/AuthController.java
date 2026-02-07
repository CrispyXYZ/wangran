package io.github.crispyxyz.wangran.controller;

import io.github.crispyxyz.wangran.dto.*;
import io.github.crispyxyz.wangran.service.AuthService;
import io.github.crispyxyz.wangran.util.ResponseUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO<?>> register(@Valid @RequestBody RegisterRequestDTO registerRequestDTO) {
        AccountDTO data = authService.register(registerRequestDTO);
        return ResponseEntity.ok(ResponseUtil.success(data));
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDTO<?>> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        AccountDTO data = authService.login(loginRequestDTO);
        return ResponseEntity.ok(ResponseUtil.success(data));
    }

    @PostMapping("/review")
    public ResponseEntity<ResponseDTO<?>> review(@Valid @RequestBody ReviewRequestDTO reviewRequestDTO) {
        ReviewResultDTO data = authService.review(reviewRequestDTO);
        return ResponseEntity.ok(ResponseUtil.success(data));
    }
}
