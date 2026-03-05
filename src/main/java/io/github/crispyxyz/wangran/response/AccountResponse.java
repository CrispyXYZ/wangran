package io.github.crispyxyz.wangran.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(subTypes = {UserResponse.class, MerchantResponse.class})
public interface AccountResponse {
}
