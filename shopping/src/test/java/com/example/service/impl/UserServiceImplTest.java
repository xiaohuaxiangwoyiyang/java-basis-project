package com.example.service.impl;

import com.example.ShoppingApplicationTests;
import com.example.pojo.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class UserServiceImplTest extends ShoppingApplicationTests {

    @Autowired
    private UserServiceImpl userService;

    @Test
    public void register() {
        User user = new User("jack", "123456", "543234@qq.com");
        userService.register(user);
    }
}