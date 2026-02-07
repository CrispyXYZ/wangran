package io.github.crispyxyz.wangran.dto;

import lombok.Data;

@Data
public class MerchantDTO implements AccountDTO {
    private int id;
    private String username;
    private String merchantId;
    private String phoneNumber;
    private String approvalStatus;
    private String rejectReason;
}
