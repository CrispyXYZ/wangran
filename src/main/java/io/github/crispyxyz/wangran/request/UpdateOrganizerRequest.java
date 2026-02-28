package io.github.crispyxyz.wangran.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateOrganizerRequest {
    @Size(max = 40)
    private String name;

    @Size(max = 20)
    @Pattern(regexp = "^[0-9]+$", message = "手机号只能由数字构成")
    private String phoneNumber;

    @Size(max = 40)
    private String address;
}
