package io.github.crispyxyz.wangran.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequestDTO {
    @NotBlank
    @Size(max = 50)
    private String identifier;

    @NotBlank
    @Size(min = 6, max = 50)
    private String password;
}
