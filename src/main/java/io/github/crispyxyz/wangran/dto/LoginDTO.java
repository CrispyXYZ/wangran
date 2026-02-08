package io.github.crispyxyz.wangran.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginDTO {
    AccountDTO account;
    String token;
}
