package com.hotel.entity;

import java.util.Date;

/**
 * 用户实体类
 */
public class User {
    private Integer id;
    private String username;
    private String password;
    private String realName;
    private String phone;
    private String role;  // ADMIN/RECEPTION
    private Date createTime;
    private Date updateTime;

    public User() {}

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRealName() { return realName; }
    public void setRealName(String realName) { this.realName = realName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }

    public boolean isAdmin() {
        return "ADMIN".equals(this.role);
    }

    public String getRoleName() {
        return "ADMIN".equals(this.role) ? "管理员" : "前台";
    }
}
