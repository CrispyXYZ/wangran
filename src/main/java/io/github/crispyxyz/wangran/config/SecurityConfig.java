package io.github.crispyxyz.wangran.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.crispyxyz.wangran.security.JwtFilter;
import io.github.crispyxyz.wangran.util.ResponseUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final ObjectMapper objectMapper;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth.requestMatchers("/auth/**")
                                               .permitAll()
                                               .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**")
                                               .permitAll()
                                               .requestMatchers("/events/public")
                                               .permitAll()
                                               .anyRequest()
                                               .authenticated())
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(customizer -> customizer.accessDeniedHandler(((request, response, authException) -> {
                                                           log.warn("拒绝访问：{} {}", request.getMethod(),
                                                            request.getRequestURI());
                                                           response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                                                           response.setContentType("application/json;charset=UTF-8");
                                                           response.getWriter()
                                                                   .write(objectMapper.writeValueAsString(ResponseUtil.error("权限不足")));
                                                       }))
                                                       .authenticationEntryPoint((request, response, authException) -> {
                                                           log.warn(
                                                               "认证失败：{} {}",
                                                               request.getMethod(),
                                                               request.getRequestURI()
                                                           );
                                                           response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                                           response.setContentType("application/json;charset=UTF-8");
                                                           response.getWriter()
                                                                   .write(objectMapper.writeValueAsString(ResponseUtil.error(
                                                                       "认证失败")));
                                                       }));
        return http.build();
    }
}
