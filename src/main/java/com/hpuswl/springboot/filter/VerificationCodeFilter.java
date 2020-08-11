package com.hpuswl.springboot.filter;

import com.google.code.kaptcha.Constants;
import com.hpuswl.springboot.authentication.SecurityAuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 专门用于校验验证码的过滤器
 */
public class VerificationCodeFilter extends OncePerRequestFilter {

    private AuthenticationFailureHandler authenticationFailureHandler = new MyAuthenticationFailureHandler();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 非登陆请求不校验验证码
        if(!"/auth/login".equals(request.getRequestURI())){
            filterChain.doFilter(request, response);
        }else{
            try {
                verificationCode(request);
                filterChain.doFilter(request, response);
            } catch (VerificationCodeException e) {
                authenticationFailureHandler.onAuthenticationFailure(request, response, e);
            }
        }
    }

    private void verificationCode(HttpServletRequest request) throws VerificationCodeException{
        String requestCode = request.getParameter("captcha");
        HttpSession session = request.getSession();
        String savedCode = (String)session.getAttribute(Constants.KAPTCHA_SESSION_KEY);
        if(!StringUtils.isEmpty(savedCode)){
            // 随手清除验证码，无论是失败，还是成功。客户端应在登陆失败时刷新验证码
            session.removeAttribute(Constants.KAPTCHA_SESSION_KEY);
        }
        //校验不通过，抛出异常
        if(StringUtils.isEmpty(requestCode) || StringUtils.isEmpty(savedCode) || !requestCode.equals(savedCode)){
            throw new VerificationCodeException();
        }
    }
}
