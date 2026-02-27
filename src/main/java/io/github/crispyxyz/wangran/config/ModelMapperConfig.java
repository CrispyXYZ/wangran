package io.github.crispyxyz.wangran.config;

import io.github.crispyxyz.wangran.model.Merchant;
import io.github.crispyxyz.wangran.response.MerchantResponse;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // 审核状态转换，将数据库中的整数状态码转换为字符串
        Converter<Integer, String> approvalStatusConverter = context -> {
            Integer source = context.getSource();
            if (source == null)
                return null;

            return switch (source) {
                case Merchant.STATUS_PENDING -> "PENDING";
                case Merchant.STATUS_APPROVED -> "APPROVED";
                case Merchant.STATUS_REJECTED -> "REJECTED";
                default -> "ERROR";
            };
        };

        // 配置 Merchant 到 MerchantResponse 的映射规则
        modelMapper.typeMap(Merchant.class, MerchantResponse.class)
                   .addMappings(mapper -> mapper.using(approvalStatusConverter)
                                                .map(Merchant::getApprovalStatus, MerchantResponse::setApprovalStatus));

        return modelMapper;
    }
}
