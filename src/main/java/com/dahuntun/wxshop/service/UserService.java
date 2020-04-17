package com.dahuntun.wxshop.service;

import com.dahuntun.wxshop.dao.UserDAO;
import com.dahuntun.wxshop.generate.User;
import org.apache.ibatis.exceptions.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class UserService {
    private final UserDAO userDAO;

    @Autowired
    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public User createUserIfNotExist(String tel) {
        User user = new User();
        user.setTel(tel);
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());
        try {
            userDAO.insertUser(user);
        } catch (PersistenceException e) {
            return userDAO.getUserByTel(tel);
        }
        return user;
    }

    /**
     * 根据电话返回用户，若用户不存在，返回null
     * @param tel 请求的电话号码
     * @return User
     */
    public Optional<User> getUserByTel(String tel) {
        return Optional.ofNullable(userDAO.getUserByTel(tel));
    }
}
