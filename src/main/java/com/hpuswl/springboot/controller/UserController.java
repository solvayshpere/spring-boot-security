package com.hpuswl.springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user/api")
public class UserController {
    @GetMapping("hello")
    public String hello(){
        return "hello, user";
    }

    @Autowired
    private SessionRegistry sessionRegistry;
    /**
     * 获取系统在线用户数量
     * @return
     */
    @GetMapping("userCount")
    public Integer getOnlineUserNum(){
        List<String> list = sessionRegistry.getAllPrincipals().stream()
                .filter(u -> !sessionRegistry.getAllSessions(u, false).isEmpty())
                .map(Object::toString)
                .collect(Collectors.toList());
        return list.size();
    }
}
