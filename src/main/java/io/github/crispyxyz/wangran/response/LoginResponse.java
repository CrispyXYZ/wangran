package io.github.crispyxyz.wangran.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    String token;
    AccountResponse account;
}
