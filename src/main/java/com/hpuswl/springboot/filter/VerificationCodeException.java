package com.hpuswl.springboot.filter;

import org.springframework.security.core.AuthenticationException;

public class VerificationCodeException extends AuthenticationException {
    public VerificationCodeException() {
        super("图片验证码校验失败");
    }
}
