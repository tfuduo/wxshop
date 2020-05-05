package com.dahuntun.wxshop.dao;

import com.dahuntun.wxshop.generate.User;
import com.dahuntun.wxshop.generate.UserExample;
import com.dahuntun.wxshop.generate.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserDAO {
    private final UserMapper userMapper;

    @Autowired
    public UserDAO(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public void insertUser(User user) {
        userMapper.insert(user);
    }

    public User getUserByTel(String tel) {
        UserExample userExample = new UserExample();
        userExample.createCriteria().andTelEqualTo(tel);
        return userMapper.selectByExample(userExample).get(0);
    }
}
