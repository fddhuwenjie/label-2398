package com.hotel.dao;

import com.hotel.entity.User;
import com.hotel.util.LogUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户数据访问层 - 使用JDBC操作数据库
 */
public class UserDao {

    /** 根据用户名查询用户 */
    public User findByUsername(String username) {
        String sql = "SELECT * FROM user WHERE username = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            LogUtil.error("查询用户失败: " + username, e);
        }
        return null;
    }

    /** 查询所有用户 */
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM user ORDER BY create_time DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            LogUtil.error("查询所有用户失败", e);
        }
        return users;
    }

    /** 添加用户 */
    public int insert(User user) {
        String sql = "INSERT INTO user (username, password, real_name, phone, role) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getRealName());
            stmt.setString(4, user.getPhone());
            stmt.setString(5, user.getRole() != null ? user.getRole() : "RECEPTION");
            int result = stmt.executeUpdate();
            LogUtil.info("添加用户成功: " + user.getUsername());
            return result;
        } catch (SQLException e) {
            LogUtil.error("添加用户失败: " + user.getUsername(), e);
        }
        return 0;
    }

    /** 更新用户 */
    public int update(User user) {
        String sql = "UPDATE user SET real_name = ?, phone = ?, role = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getRealName());
            stmt.setString(2, user.getPhone());
            stmt.setString(3, user.getRole());
            stmt.setInt(4, user.getId());
            int result = stmt.executeUpdate();
            LogUtil.info("更新用户成功: ID=" + user.getId());
            return result;
        } catch (SQLException e) {
            LogUtil.error("更新用户失败: ID=" + user.getId(), e);
        }
        return 0;
    }

    /** 更新密码 */
    public int updatePassword(Integer id, String password) {
        String sql = "UPDATE user SET password = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, password);
            stmt.setInt(2, id);
            int result = stmt.executeUpdate();
            LogUtil.info("更新密码成功: ID=" + id);
            return result;
        } catch (SQLException e) {
            LogUtil.error("更新密码失败: ID=" + id, e);
        }
        return 0;
    }

    /** 删除用户 */
    public int deleteById(Integer id) {
        String sql = "DELETE FROM user WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int result = stmt.executeUpdate();
            LogUtil.info("删除用户成功: ID=" + id);
            return result;
        } catch (SQLException e) {
            LogUtil.error("删除用户失败: ID=" + id, e);
        }
        return 0;
    }

    /** 将ResultSet映射为User对象 */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setRealName(rs.getString("real_name"));
        user.setPhone(rs.getString("phone"));
        user.setRole(rs.getString("role"));
        user.setCreateTime(rs.getTimestamp("create_time"));
        user.setUpdateTime(rs.getTimestamp("update_time"));
        return user;
    }
}
