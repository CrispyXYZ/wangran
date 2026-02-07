package io.github.crispyxyz.wangran.dto;

import lombok.Data;

@Data
public class ServiceResponseDTO<T> {
    private T data;
}
