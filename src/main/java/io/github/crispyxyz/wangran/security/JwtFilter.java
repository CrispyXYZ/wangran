package io.github.crispyxyz.wangran.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.crispyxyz.wangran.util.ResponseUtil;
import io.github.crispyxyz.wangran.util.SecurityUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class JwtFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;
    private static final Map<String, String> TYPE_ROLE_MAP =
        Map.of("user", "ROLE_USER", "merchant", "ROLE_MERCHANT", "admin", "ROLE_ADMIN");

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        String token = extractToken(request);
        if (token == null || token.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            DecodedJWT jwt = SecurityUtil.verifyJwtToken(token);
            Integer userId = jwt.getClaim("userId").asInt();
            String type = jwt.getClaim("type").asString();
            String role = TYPE_ROLE_MAP.get(type);

            if (role == null) {
                SecurityContextHolder.clearContext();
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "认证失败");
                return;
            }

            List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(role));
            AppPrincipal principal = new AppPrincipal(type, userId);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(principal, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            log.warn("认证失败：", e);
            SecurityContextHolder.clearContext();
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "认证失败：" + e.getMessage());
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter()
                .write(objectMapper.writeValueAsString(ResponseUtil.error(message)));
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && !bearerToken.isBlank() && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
