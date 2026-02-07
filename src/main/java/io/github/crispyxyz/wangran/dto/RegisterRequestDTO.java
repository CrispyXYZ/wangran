package io.github.crispyxyz.wangran.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequestDTO {
    @NotBlank
    @Size(max = 20)
    private String phoneNumber;

    @NotBlank
    @Size(min = 6, max = 50)
    private String password;

    private boolean merchant;
}
