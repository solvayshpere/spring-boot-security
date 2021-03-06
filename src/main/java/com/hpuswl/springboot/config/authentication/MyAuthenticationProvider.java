package com.hpuswl.springboot.config.authentication;

import com.hpuswl.springboot.filter.VerificationCodeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

//@Component
public class MyAuthenticationProvider extends DaoAuthenticationProvider {

    // 把构造方法注入 UerDetailsService 和 PasswordEncoder
    public MyAuthenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.setUserDetailsService(userDetailsService);
        this.setPasswordEncoder(passwordEncoder);
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        // 获取详细信息
        MyWebAuthenticationDetails details = (MyWebAuthenticationDetails) authentication.getDetails();

        // 一旦发现验证码不正确，就立刻抛出相应的异常信息
        if (!details.getImageCodeIsRight()) {
            throw new VerificationCodeException();
        }

        // 调用父类方法完成密码验证
        super.additionalAuthenticationChecks(userDetails, authentication);
    }

}
