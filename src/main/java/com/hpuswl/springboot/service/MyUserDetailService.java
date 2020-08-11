package com.hpuswl.springboot.service;

import com.hpuswl.springboot.entity.User;
import com.hpuswl.springboot.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MyUserDetailService implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    private final static String NOOP = "{noop}";

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //从数据库尝试读取用户
        User user = userMapper.findByUserName(username);
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

    //自定实现权限的转换
    private List<GrantedAuthority> generateAuthorities(String roles){
        List<GrantedAuthority> authorities =new ArrayList<>();
        if(roles != null && !"".equals(roles)){
            String[] roleArray = roles.split(",");

            for(String role : roleArray){
                authorities.add(new SimpleGrantedAuthority(role));
            }
        }

        return authorities;
    }
}
