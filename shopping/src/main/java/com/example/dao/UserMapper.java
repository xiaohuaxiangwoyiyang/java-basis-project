package com.example.dao;

import com.example.pojo.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    int insertSelective(User record);
    int countByUsername(String username);
    int countByEmail(String email);
    User selectByUsername(String username);
}
