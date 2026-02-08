package io.github.crispyxyz.wangran.interceptor;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.crispyxyz.wangran.util.ResponseUtil;
import io.github.crispyxyz.wangran.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Component
@Slf4j
public class JwtInterceptor implements HandlerInterceptor {
    private static final String AUTH_HEADER = "Authorization";
    private static final String AUTH_PREFIX = "Bearer ";

    /**
     * 用于 JSON 序列化
     */
    private final ObjectMapper objectMapper;

    @Autowired
    public JwtInterceptor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        // 解析相关参数
        String token = request.getHeader(AUTH_HEADER);
        String requestURI = request.getRequestURI();
        log.debug("触发Interceptor，requestURI={}, token={}", requestURI, token);

        // 检查 Token 是否存在及其格式
        if (token == null || !token.startsWith(AUTH_PREFIX)) {
            log.warn("请求未提供Token");
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "未提供 Token");
            return false;
        }

        // 去除前缀
        token = token.substring(AUTH_PREFIX.length());

        try {
            // 验证并解析
            DecodedJWT decodedJWT = SecurityUtil.verifyJwtToken(token);
            log.debug("JWT已解析, subject={}, role={}", decodedJWT.getSubject(), decodedJWT.getClaim("role").asString());

            // 非审核接口直接放行（虽然目前不存在）
            if (!"/auth/review".equals(requestURI)) {
                return true;
            }

            // 处理审核接口，需要 role=admin
            String role = decodedJWT.getClaim("role").asString();
            if ("admin".equals(role)) {
                return true;
            }

            log.warn("请求权限不足");
            sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "权限不足");
            return false;
        } catch (TokenExpiredException e) {
            // 处理过期异常
            log.warn("过期的 Token");
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "过期的 Token");
            return false;
        } catch (JWTVerificationException e) {
            // 处理其它异常
            log.warn("无效的 Token");
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "无效的 Token");
            return false;
        }
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(ResponseUtil.error(message)));
    }
}
