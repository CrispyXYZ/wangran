package io.github.crispyxyz.wangran.response;

import lombok.Data;

@Data
public class ReviewResponse {
    /**
     * 审核是否通过
     */
    private boolean approved;

    /**
     * 审核通过时生成的商户编号
     */
    private String merchantCode;

    /**
     * 审核通过时生成的用户名
     */
    private String username;

    /**
     * 商户手机号
     */
    private String phoneNumber;

}