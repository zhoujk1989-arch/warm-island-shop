package com.warmisland.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.warmisland.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("SELECT * FROM users WHERE username = #{username} AND deleted = 0")
    User findByUsername(String username);
}
