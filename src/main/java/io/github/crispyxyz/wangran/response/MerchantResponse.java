package io.github.crispyxyz.wangran.response;

import lombok.Data;

@Data
public class MerchantResponse implements AccountResponse {
    private int id;
    private String username;
    private String merchantId;
    private String phoneNumber;
    private String approvalStatus;
    private String rejectReason;
}
