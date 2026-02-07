package io.github.crispyxyz.wangran.dto;

import lombok.Data;

@Data
public class UserDTO implements AccountDTO {
    private int id;
    private String username;
    private String phoneNumber;
}
