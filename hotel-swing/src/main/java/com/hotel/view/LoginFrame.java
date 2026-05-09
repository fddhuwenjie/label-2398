package com.hotel.view;

import com.hotel.entity.User;
import com.hotel.service.UserService;
import javax.swing.*;
import java.awt.*;

/**
 * 登录界面
 */
public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private UserService userService = new UserService();

    public LoginFrame() {
        initUI();
    }

    private void initUI() {
        setTitle("宾馆客房管理系统 - 登录");
        setSize(400, 280);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // 标题
        JLabel titleLabel = new JLabel("宾馆客房管理系统", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 22));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // 表单面板
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("用户名："), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        usernameField = new JTextField(15);
        formPanel.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(new JLabel("密  码："), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        passwordField = new JPasswordField(15);
        formPanel.add(passwordField, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton loginBtn = new JButton("登 录");
        JButton exitBtn = new JButton("退 出");
        loginBtn.setPreferredSize(new Dimension(80, 30));
        exitBtn.setPreferredSize(new Dimension(80, 30));
        buttonPanel.add(loginBtn);
        buttonPanel.add(exitBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // 事件绑定
        loginBtn.addActionListener(e -> doLogin());
        exitBtn.addActionListener(e -> System.exit(0));
        passwordField.addActionListener(e -> doLogin());

        add(mainPanel);
    }

    private void doLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入用户名和密码", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        User user = userService.login(username, password);
        if (user != null) {
            dispose();
            new MainFrame(user).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "用户名或密码错误", "登录失败", JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
        }
    }
}
