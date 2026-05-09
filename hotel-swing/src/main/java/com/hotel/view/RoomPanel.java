package com.hotel.view;

import com.hotel.entity.Room;
import com.hotel.service.RoomService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * 客房管理面板
 */
public class RoomPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private RoomService roomService = new RoomService();
    private JComboBox<String> statusFilter;

    public RoomPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initUI();
        loadData();
    }

    private void initUI() {
        // 工具栏
        JPanel toolBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JButton addBtn = new JButton("添加客房");
        JButton editBtn = new JButton("编辑");
        JButton deleteBtn = new JButton("删除");
        JButton refreshBtn = new JButton("刷新");
        
        toolBar.add(addBtn);
        toolBar.add(editBtn);
        toolBar.add(deleteBtn);
        toolBar.add(new JLabel("    状态筛选："));
        statusFilter = new JComboBox<>(new String[]{"全部", "空闲", "已入住", "维修中"});
        toolBar.add(statusFilter);
        toolBar.add(refreshBtn);
        add(toolBar, BorderLayout.NORTH);

        // 表格
        String[] columns = {"ID", "房间号", "房型", "价格(元/天)", "状态", "描述"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(25);
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(5).setPreferredWidth(200);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // 事件绑定
        addBtn.addActionListener(e -> showAddDialog());
        editBtn.addActionListener(e -> showEditDialog());
        deleteBtn.addActionListener(e -> deleteRoom());
        refreshBtn.addActionListener(e -> loadData());
        statusFilter.addActionListener(e -> loadData());
    }

    public void refreshData() {
        loadData();
    }

    private void loadData() {
        tableModel.setRowCount(0);
        String filter = (String) statusFilter.getSelectedItem();
        List<Room> rooms;
        
        if ("全部".equals(filter)) {
            rooms = roomService.findAll();
        } else {
            String status = "空闲".equals(filter) ? "AVAILABLE" : 
                           "已入住".equals(filter) ? "OCCUPIED" : "MAINTENANCE";
            rooms = roomService.findByStatus(status);
        }
        
        for (Room room : rooms) {
            tableModel.addRow(new Object[]{
                room.getId(), room.getRoomNumber(), room.getRoomTypeName(),
                room.getPrice(), room.getStatusName(), room.getDescription()
            });
        }
        if (rooms.isEmpty()) {
            tableModel.addRow(new Object[]{"", "", "", "暂无数据", "", ""});
        }
    }

    private void showAddDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "添加客房", true);
        dialog.setSize(350, 280);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField roomNumberField = new JTextField(15);
        JComboBox<String> roomTypeBox = new JComboBox<>(new String[]{"单人间", "双人间", "套房"});
        JTextField priceField = new JTextField(15);
        JTextArea descArea = new JTextArea(3, 15);

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("房间号："), gbc);
        gbc.gridx = 1;
        panel.add(roomNumberField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("房型："), gbc);
        gbc.gridx = 1;
        panel.add(roomTypeBox, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("价格："), gbc);
        gbc.gridx = 1;
        panel.add(priceField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("描述："), gbc);
        gbc.gridx = 1;
        panel.add(new JScrollPane(descArea), gbc);

        JPanel btnPanel = new JPanel();
        JButton saveBtn = new JButton("保存");
        JButton cancelBtn = new JButton("取消");
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        panel.add(btnPanel, gbc);

        saveBtn.addActionListener(e -> {
            try {
                Room room = new Room();
                room.setRoomNumber(roomNumberField.getText().trim());
                String typeText = (String) roomTypeBox.getSelectedItem();
                String typeCode = "单人间".equals(typeText) ? "SINGLE" : "双人间".equals(typeText) ? "DOUBLE" : "SUITE";
                room.setRoomType(typeCode);
                room.setPrice(new BigDecimal(priceField.getText().trim()));
                room.setDescription(descArea.getText().trim());
                room.setStatus("AVAILABLE");

                if (roomService.addRoom(room)) {
                    JOptionPane.showMessageDialog(dialog, "添加成功");
                    dialog.dispose();
                    loadData();
                } else {
                    JOptionPane.showMessageDialog(dialog, "房间号已存在", "错误", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "请输入正确的价格", "错误", JOptionPane.ERROR_MESSAGE);
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
        Room room = roomService.findById(id);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "编辑客房", true);
        dialog.setSize(350, 300);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JComboBox<String> roomTypeBox = new JComboBox<>(new String[]{"单人间", "双人间", "套房"});
        roomTypeBox.setSelectedItem(room.getRoomTypeName());
        JTextField priceField = new JTextField(room.getPrice().toString(), 15);
        JComboBox<String> statusBox = new JComboBox<>(new String[]{"空闲", "已入住", "维修中"});
        statusBox.setSelectedItem(room.getStatusName());
        JTextArea descArea = new JTextArea(room.getDescription(), 3, 15);

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("房间号："), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(room.getRoomNumber()), gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("房型："), gbc);
        gbc.gridx = 1;
        panel.add(roomTypeBox, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("价格："), gbc);
        gbc.gridx = 1;
        panel.add(priceField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("状态："), gbc);
        gbc.gridx = 1;
        panel.add(statusBox, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("描述："), gbc);
        gbc.gridx = 1;
        panel.add(new JScrollPane(descArea), gbc);

        JPanel btnPanel = new JPanel();
        JButton saveBtn = new JButton("保存");
        JButton cancelBtn = new JButton("取消");
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        panel.add(btnPanel, gbc);

        saveBtn.addActionListener(e -> {
            try {
                String typeText = (String) roomTypeBox.getSelectedItem();
                String typeCode = "单人间".equals(typeText) ? "SINGLE" : "双人间".equals(typeText) ? "DOUBLE" : "SUITE";
                room.setRoomType(typeCode);
                room.setPrice(new BigDecimal(priceField.getText().trim()));
                String statusText = (String) statusBox.getSelectedItem();
                String statusCode = "空闲".equals(statusText) ? "AVAILABLE" : "已入住".equals(statusText) ? "OCCUPIED" : "MAINTENANCE";
                room.setStatus(statusCode);
                room.setDescription(descArea.getText().trim());

                if (roomService.updateRoom(room)) {
                    JOptionPane.showMessageDialog(dialog, "更新成功");
                    dialog.dispose();
                    loadData();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "请输入正确的价格", "错误", JOptionPane.ERROR_MESSAGE);
            }
        });
        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void deleteRoom() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "请先选择一条记录");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "确定要删除该客房吗？", "确认", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            Integer id = (Integer) tableModel.getValueAt(row, 0);
            if (roomService.deleteRoom(id)) {
                JOptionPane.showMessageDialog(this, "删除成功");
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "删除失败，可能存在关联订单", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
