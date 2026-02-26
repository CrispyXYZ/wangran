package io.github.crispyxyz.wangran.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateAccountRequest {
    @Size(max = 20)
    private String phoneNumber;

    @Size(min = 6, max = 50)
    private String password;

    @Size(max = 50)
    private String username;
}
