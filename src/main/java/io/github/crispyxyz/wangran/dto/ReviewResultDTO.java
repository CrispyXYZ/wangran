package io.github.crispyxyz.wangran.dto;

import lombok.Data;

@Data
public class ReviewResultDTO {
    /**
     * 审核是否通过
     */
    private Boolean approved;

    /**
     * 审核通过时生成的商户ID
     */
    private String merchantId;

    /**
     * 审核通过时生成的用户名
     */
    private String username;

    /**
     * 商户手机号
     */
    private String phoneNumber;

}