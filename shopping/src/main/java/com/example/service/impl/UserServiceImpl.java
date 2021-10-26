package com.example.service.impl;

import com.example.dao.UserMapper;
import com.example.enums.ResponseEnum;
import com.example.enums.RoleEnum;
import com.example.pojo.User;
import com.example.service.IUserService;
import com.example.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;

import static com.example.enums.ResponseEnum.*;

@Slf4j
@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;
    /**
     * 注册
     * @param user
     */
    @Override
    public ResponseVo<User> register(User user) {
        int countByUserName = userMapper.countByUsername(user.getUsername());
        log.info("countByUserName = {}", countByUserName);
        // 用户名不能重复
        if (countByUserName > 0) {
            return ResponseVo.error(USERNAME_EXIST);
        }

        // emial不能重复
        int countByEmail = userMapper.countByEmail(user.getEmail());
        if (countByEmail > 0) {
            return ResponseVo.error(EMAIL_EXIST);
        }

        // 设置用户角色
        user.setRole(RoleEnum.CUSTOMER.getCode());

        //MD5算法摘要
        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes(StandardCharsets.UTF_8)));
        // 写入数据库
        int resultCount = userMapper.insertSelective(user);
        if (resultCount == 0) {
            return ResponseVo.error(ERROR);
        }
        return ResponseVo.success();
    }

    /**
     * 登录
     * @param username
     * @param password
     * @return
     */
    @Override
    public ResponseVo<User> login(String username, String password) {
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            // 用户不存在
            return ResponseVo.error(USERNAME_OR_PASSWORD_ERROR);
        }
        if(user.getPassword().equalsIgnoreCase(DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8)))) {
            // 密码错误
            return ResponseVo.error(USERNAME_OR_PASSWORD_ERROR);
        }
        user.setPassword("");
        return ResponseVo.success(user);
    }
}
