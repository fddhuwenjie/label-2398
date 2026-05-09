package com.hotel.view;

import com.hotel.entity.User;
import com.hotel.service.UserService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * 用户管理面板（仅管理员可见）
 */
public class UserPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private UserService userService = new UserService();
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public UserPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initUI();
        loadData();
    }

    private void initUI() {
        // 工具栏
        JPanel toolBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JButton addBtn = new JButton("添加用户");
        JButton editBtn = new JButton("编辑");
        JButton deleteBtn = new JButton("删除");
        JButton refreshBtn = new JButton("刷新");
        
        toolBar.add(addBtn);
        toolBar.add(editBtn);
        toolBar.add(deleteBtn);
        toolBar.add(refreshBtn);
        add(toolBar, BorderLayout.NORTH);

        // 表格
        String[] columns = {"ID", "用户名", "真实姓名", "电话", "角色", "创建时间"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(25);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // 事件绑定
        addBtn.addActionListener(e -> showAddDialog());
        editBtn.addActionListener(e -> showEditDialog());
        deleteBtn.addActionListener(e -> deleteUser());
        refreshBtn.addActionListener(e -> loadData());
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<User> users = userService.findAll();
        for (User user : users) {
            tableModel.addRow(new Object[]{
                user.getId(), user.getUsername(), user.getRealName(), user.getPhone(),
                "ADMIN".equals(user.getRole()) ? "管理员" : "前台",
                user.getCreateTime() != null ? sdf.format(user.getCreateTime()) : ""
            });
        }
        if (users.isEmpty()) {
            tableModel.addRow(new Object[]{"", "", "暂无数据", "", "", ""});
        }
    }

    private void showAddDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "添加用户", true);
        dialog.setSize(350, 300);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField usernameField = new JTextField(15);
        JPasswordField passwordField = new JPasswordField(15);
        JTextField realNameField = new JTextField(15);
        JTextField phoneField = new JTextField(15);
        JComboBox<String> roleBox = new JComboBox<>(new String[]{"前台", "管理员"});

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("用户名："), gbc);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("密码："), gbc);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("真实姓名："), gbc);
        gbc.gridx = 1;
        panel.add(realNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("电话："), gbc);
        gbc.gridx = 1;
        panel.add(phoneField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("角色："), gbc);
        gbc.gridx = 1;
        panel.add(roleBox, gbc);

        JPanel btnPanel = new JPanel();
        JButton saveBtn = new JButton("保存");
        JButton cancelBtn = new JButton("取消");
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        panel.add(btnPanel, gbc);

        saveBtn.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "用户名和密码不能为空", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (password.length() < 6) {
                JOptionPane.showMessageDialog(dialog, "密码长度不能少于6位", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String phone = phoneField.getText().trim();
            if (!phone.isEmpty() && !phone.matches("^1\\d{10}$")) {
                JOptionPane.showMessageDialog(dialog, "手机号格式不正确（应为11位，1开头）", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }

            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setRealName(realNameField.getText().trim());
            user.setPhone(phone);
            String roleText = (String) roleBox.getSelectedItem();
            user.setRole("管理员".equals(roleText) ? "ADMIN" : "RECEPTION");

            if (userService.addUser(user)) {
                JOptionPane.showMessageDialog(dialog, "添加成功");
                dialog.dispose();
                loadData();
            } else {
                JOptionPane.showMessageDialog(dialog, "用户名已存在", "错误", JOptionPane.ERROR_MESSAGE);
            }
        });
        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showEditDialog() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "请先选择一条记录");
            return;
        }

        Integer id = (Integer) tableModel.getValueAt(row, 0);
        String username = (String) tableModel.getValueAt(row, 1);
        String realName = (String) tableModel.getValueAt(row, 2);
        String phone = (String) tableModel.getValueAt(row, 3);
        String roleName = (String) tableModel.getValueAt(row, 4);
        String role = "管理员".equals(roleName) ? "ADMIN" : "RECEPTION";

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "编辑用户", true);
        dialog.setSize(350, 280);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField realNameField = new JTextField(realName != null ? realName : "", 15);
        JTextField phoneField = new JTextField(phone != null ? phone : "", 15);
        JComboBox<String> roleBox = new JComboBox<>(new String[]{"前台", "管理员"});
        roleBox.setSelectedItem("ADMIN".equals(role) ? "管理员" : "前台");

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("用户名："), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(username), gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("真实姓名："), gbc);
        gbc.gridx = 1;
        panel.add(realNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("电话："), gbc);
        gbc.gridx = 1;
        panel.add(phoneField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("角色："), gbc);
        gbc.gridx = 1;
        panel.add(roleBox, gbc);

        JPanel btnPanel = new JPanel();
        JButton saveBtn = new JButton("保存");
        JButton cancelBtn = new JButton("取消");
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        panel.add(btnPanel, gbc);

        saveBtn.addActionListener(e -> {
            String phoneValue = phoneField.getText().trim();
            if (!phoneValue.isEmpty() && !phoneValue.matches("^1\\d{10}$")) {
                JOptionPane.showMessageDialog(dialog, "手机号格式不正确（应为11位，1开头）", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }

            User user = new User();
            user.setId(id);
            user.setRealName(realNameField.getText().trim());
            user.setPhone(phoneValue);
            String roleText = (String) roleBox.getSelectedItem();
            user.setRole("管理员".equals(roleText) ? "ADMIN" : "RECEPTION");

            if (userService.updateUser(user)) {
                JOptionPane.showMessageDialog(dialog, "更新成功");
                dialog.dispose();
                loadData();
            }
        });
        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void deleteUser() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "请先选择一条记录");
            return;
        }

        String username = (String) tableModel.getValueAt(row, 1);
        if ("admin".equals(username)) {
            JOptionPane.showMessageDialog(this, "不能删除管理员账号", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "确定要删除用户 " + username + " 吗？", "确认", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            Integer id = (Integer) tableModel.getValueAt(row, 0);
            if (userService.deleteUser(id)) {
                JOptionPane.showMessageDialog(this, "删除成功");
                loadData();
            }
        }
    }
}
