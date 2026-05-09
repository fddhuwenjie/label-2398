package com.hotel.view;

import com.hotel.entity.User;
import javax.swing.*;
import java.awt.*;

/**
 * 主界面 - 使用选项卡布局
 */
public class MainFrame extends JFrame {
    private User currentUser;

    public MainFrame(User user) {
        this.currentUser = user;
        initUI();
    }

    private void initUI() {
        setTitle("宾馆客房管理系统 - " + currentUser.getRealName() + 
                 " [" + (currentUser.isAdmin() ? "管理员" : "前台") + "]");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 创建选项卡面板
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("微软雅黑", Font.PLAIN, 14));

        // 添加各功能面板
        OrderPanel orderPanel = new OrderPanel();
        tabbedPane.addTab("入住/退房", orderPanel);
        
        // 管理员才能看到客房管理和用户管理
        RoomPanel roomPanel = null;
        if (currentUser.isAdmin()) {
            roomPanel = new RoomPanel();
            tabbedPane.addTab("客房管理", roomPanel);
            tabbedPane.addTab("用户管理", new UserPanel());
        }

        // 切换选项卡时自动刷新数据
        final RoomPanel finalRoomPanel = roomPanel;
        tabbedPane.addChangeListener(e -> {
            Component selected = tabbedPane.getSelectedComponent();
            if (selected instanceof OrderPanel) {
                orderPanel.refreshData();
            } else if (selected instanceof RoomPanel && finalRoomPanel != null) {
                finalRoomPanel.refreshData();
            }
        });

        add(tabbedPane, BorderLayout.CENTER);

        // 底部状态栏
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logoutBtn = new JButton("退出登录");
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
        statusBar.add(logoutBtn);
        add(statusBar, BorderLayout.SOUTH);
    }
}
