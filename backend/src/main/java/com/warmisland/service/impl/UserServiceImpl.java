package com.warmisland.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.warmisland.entity.User;
import com.warmisland.mapper.UserMapper;
import com.warmisland.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Override
    public User findByUsername(String username) {
        return getBaseMapper().findByUsername(username);
    }
}
