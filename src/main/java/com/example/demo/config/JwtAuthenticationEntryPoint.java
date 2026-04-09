package com.example.demo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.demo.dto.ApiResponse;
import com.example.demo.exception.JwtAuthenticationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        JwtAuthenticationException jwtException =
                (JwtAuthenticationException) request.getAttribute(JwtAuthenticationFilter.JWT_EXCEPTION_ATTR);

        ApiResponse<Void> apiResponse;
        if (jwtException != null) {
            log.info("JWT认证失败: {} - {}", jwtException.getErrorCode(), jwtException.getMessage());
            apiResponse = ApiResponse.error(401, jwtException.getMessage());
        } else {
            log.info("未授权访问: {}", authException.getMessage());
            String message = authException.getMessage();
            if (message == null || message.isEmpty()) {
                message = "未授权，请先登录";
            }
            apiResponse = ApiResponse.error(401, message);
        }

        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}
