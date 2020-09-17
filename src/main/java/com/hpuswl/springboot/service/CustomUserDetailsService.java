package com.hpuswl.springboot.service;

import com.hpuswl.springboot.entity.User;
import com.hpuswl.springboot.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements AuthenticationUserDetailsService<CasAssertionAuthenticationToken> {

    @Autowired
    private UserMapper userMapper;

    private final static String NOOP = "{noop}";

    @Override
    public UserDetails loadUserDetails(CasAssertionAuthenticationToken token) throws UsernameNotFoundException {
        // 结合具体的逻辑去实现用户认证，并返回继承UserDetails的用户对象;
        System.out.println("当前的用户名是："+token.getName());

        //从数据库尝试读取用户
        User user = userMapper.findByUserName(token.getName());
        // 用户不存在，抛出异常
        if(user == null){
            throw new UsernameNotFoundException("用户不存在");
        }
        user.setPassword(NOOP+user.getPassword());
        //将数据库形式的roles解析为UserDetails的权限集
        //AuthorityUtils.commaSeparatedStringToAuthorityList是SpringSecurity提供的，该方法用于将逗号隔开的权限集字符串切割成可用权限对象列表
        user.setAuthorities(AuthorityUtils.commaSeparatedStringToAuthorityList(user.getRoles()));
        return user;
    }
}
