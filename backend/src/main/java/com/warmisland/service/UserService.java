package com.warmisland.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.warmisland.entity.User;

public interface UserService extends IService<User> {

    User findByUsername(String username);
}
