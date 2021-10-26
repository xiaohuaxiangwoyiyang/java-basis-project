package com.example.controller;

import com.example.consts.MallConst;
import com.example.form.UserLoginUserForm;
import com.example.form.UserRegisterForm;
import com.example.pojo.User;
import com.example.service.IUserService;
import com.example.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Slf4j
@RestController
public class UserController {

    @Autowired
    public IUserService userService;

    @PostMapping("/user/register")
    public ResponseVo<User> register(@Valid @RequestBody UserRegisterForm userForm) {
        User user = new User();

        log.info("user = {}", user);
        BeanUtils.copyProperties(userForm, user);
        return userService.register(user);
    }

    @PostMapping("/user/login")
    public ResponseVo<User> login(@Valid @RequestBody UserLoginUserForm userLoginUser, HttpSession session) {
        ResponseVo<User> userResponseVo = userService.login(userLoginUser.getUsername(), userLoginUser.getPassword());
        // 设置session
        session.setAttribute(MallConst.CURRENT_USER, userResponseVo.getData());
        log.info("data = {}", userResponseVo.getData());
        log.info("/login sessionId = {}", session.getId());
        return userResponseVo;
    }

    @GetMapping("/user")
    public ResponseVo<User> userInfo(HttpSession session) {
        User user = (User) session.getAttribute(MallConst.CURRENT_USER);
        log.info("user = {}", user);
        return ResponseVo.success(user);
    }

    @PostMapping("/user/logout")
    public ResponseVo logout(HttpSession session) {
        session.removeAttribute(MallConst.CURRENT_USER);
        return ResponseVo.success();
    }

}
