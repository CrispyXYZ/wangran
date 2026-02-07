package io.github.crispyxyz.wangran.config;

import io.github.crispyxyz.wangran.dto.MerchantDTO;
import io.github.crispyxyz.wangran.entity.Merchant;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        Converter<Integer, String> approvalStatusConverter = context -> {
            Integer source = context.getSource();
            if (source == null) return null;

            return switch (source) {
                case 0 -> "PENDING";
                case 1 -> "APPROVED";
                case 2 -> "REJECTED";
                default -> "ERROR";
            };
        };

        modelMapper.typeMap(Merchant.class, MerchantDTO.class)
        .addMappings(mapper -> mapper.using(approvalStatusConverter)
        .map(Merchant::getApprovalStatus, MerchantDTO::setApprovalStatus));

        return modelMapper;
    }
}
