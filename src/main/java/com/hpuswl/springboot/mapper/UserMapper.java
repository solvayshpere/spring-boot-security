package com.hpuswl.springboot.mapper;

import com.hpuswl.springboot.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface UserMapper {

    @Select("select * from tb_user where username=#{username}")
    User findByUserName(@Param("username") String username);

}
