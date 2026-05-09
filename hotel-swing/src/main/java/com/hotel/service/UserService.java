package com.hotel.service;

import com.hotel.dao.UserDao;
import com.hotel.entity.User;
import com.hotel.util.LogUtil;
import com.hotel.util.PasswordUtil;
import java.util.List;

/**
 * 用户业务逻辑层
 */
public class UserService {
    private UserDao userDao = new UserDao();

    /** 用户登录验证 */
    public User login(String username, String password) {
        User user = userDao.findByUsername(username);
        if (user != null && PasswordUtil.verify(password, user.getPassword())) {
            LogUtil.info("用户登录成功: " + username);
            return user;
        }
        LogUtil.warn("用户登录失败: " + username);
        return null;
    }

    /** 查询所有用户 */
    public List<User> findAll() {
        return userDao.findAll();
    }

    /** 添加用户 */
    public boolean addUser(User user) {
        if (userDao.findByUsername(user.getUsername()) != null) {
            LogUtil.warn("添加用户失败，用户名已存在: " + user.getUsername());
            return false;
        }
        // 密码加密存储
        user.setPassword(PasswordUtil.encrypt(user.getPassword()));
        return userDao.insert(user) > 0;
    }

    /** 更新用户 */
    public boolean updateUser(User user) {
        return userDao.update(user) > 0;
    }

    /** 更新密码 */
    public boolean updatePassword(Integer id, String password) {
        // 密码加密存储
        return userDao.updatePassword(id, PasswordUtil.encrypt(password)) > 0;
    }

    /** 删除用户 */
    public boolean deleteUser(Integer id) {
        return userDao.deleteById(id) > 0;
    }
}
