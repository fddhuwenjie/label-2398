package com.hotel;

import com.hotel.view.LoginFrame;
import javax.swing.*;

/**
 * 宾馆客房管理系统 - 主程序入口
 * 
 * 技术栈：Java Swing + JDBC + 面向对象
 * 包结构：
 *   - com.hotel.view    界面层（Swing）
 *   - com.hotel.dao     数据访问层（JDBC）
 *   - com.hotel.entity  实体类
 *   - com.hotel.service 业务逻辑层
 */
public class HotelApplication {
    public static void main(String[] args) {
        // 设置界面风格
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 启动登录界面
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
