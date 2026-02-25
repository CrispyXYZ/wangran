package io.github.crispyxyz.wangran.response;

import lombok.Data;

@Data
public class UserResponse implements AccountResponse {
    private int id;
    private String username;
    private String phoneNumber;
}
