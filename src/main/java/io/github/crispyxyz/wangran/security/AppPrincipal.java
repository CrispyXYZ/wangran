package io.github.crispyxyz.wangran.security;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AppPrincipal {
    private String type;
    private Integer id;
}
