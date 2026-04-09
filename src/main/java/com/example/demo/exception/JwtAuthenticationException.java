package com.example.demo.exception;

import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public class JwtAuthenticationException extends AuthenticationException {
    private final String errorCode;

    public JwtAuthenticationException(String errorCode, String msg) {
        super(msg);
        this.errorCode = errorCode;
    }

    public JwtAuthenticationException(String errorCode, String msg, Throwable cause) {
        super(msg, cause);
        this.errorCode = errorCode;
    }

    public static JwtAuthenticationException tokenExpired() {
        return new JwtAuthenticationException("TOKEN_EXPIRED", "Token已过期");
    }

    public static JwtAuthenticationException tokenInvalid() {
        return new JwtAuthenticationException("TOKEN_INVALID", "Token格式无效");
    }

    public static JwtAuthenticationException tokenMalformed() {
        return new JwtAuthenticationException("TOKEN_MALFORMED", "Token格式错误");
    }

    public static JwtAuthenticationException tokenSignatureInvalid() {
        return new JwtAuthenticationException("TOKEN_SIGNATURE_INVALID", "Token签名验证失败");
    }

    public static JwtAuthenticationException tokenMissing() {
        return new JwtAuthenticationException("TOKEN_MISSING", "未提供Token");
    }

    public static JwtAuthenticationException tokenUnsupported() {
        return new JwtAuthenticationException("TOKEN_UNSUPPORTED", "不支持的Token类型");
    }
}
