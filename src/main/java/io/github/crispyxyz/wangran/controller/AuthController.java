package io.github.crispyxyz.wangran.controller;

import io.github.crispyxyz.wangran.dto.LoginRequestDTO;
import io.github.crispyxyz.wangran.dto.RegisterRequestDTO;
import io.github.crispyxyz.wangran.dto.ReviewRequestDTO;
import io.github.crispyxyz.wangran.service.AuthService;
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
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDTO registerRequestDTO) {
        var data = authService.register(registerRequestDTO).getData();
        return ResponseEntity.ok(data);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        var data = authService.login(loginRequestDTO).getData();
        return ResponseEntity.ok(data);
    }

    @PostMapping("/review")
    public ResponseEntity<?> review(@Valid @RequestBody ReviewRequestDTO reviewRequestDTO) {
        var data = authService.review(reviewRequestDTO).getData();
        return ResponseEntity.ok(data);
    }
}
