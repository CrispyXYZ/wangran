package io.github.crispyxyz.wangran.config;

import io.github.crispyxyz.wangran.interceptor.JwtInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// TODO 改用Filter进行权限拦截
//@Configuration
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class InterceptorConfig implements WebMvcConfigurer {
    private final JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 拦截器配置
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                    "/auth/login",
                    "/auth/register",
                    "/swagger-ui.html",
                    "/swagger-ui/**",
                    "/v3/api" + "-docs/**",
                    "/favicon.ico"
                );
    }
}
