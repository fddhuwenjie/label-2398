package com.hotel.view;

import com.hotel.entity.Order;
import com.hotel.entity.Room;
import com.hotel.service.OrderService;
import com.hotel.service.RoomService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * 订单管理面板（入住/退房）
 */
public class OrderPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private OrderService orderService = new OrderService();
    private RoomService roomService = new RoomService();
    private JComboBox<String> statusFilter;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public OrderPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initUI();
        loadData();
    }

    private void initUI() {
        // 工具栏
        JPanel toolBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JButton checkInBtn = new JButton("入住登记");
        JButton checkOutBtn = new JButton("退房结算");
        JButton extendBtn = new JButton("续住");
        JButton changeRoomBtn = new JButton("换房");
        JButton refreshBtn = new JButton("刷新");
        
        toolBar.add(checkInBtn);
        toolBar.add(checkOutBtn);
        toolBar.add(extendBtn);
        toolBar.add(changeRoomBtn);
        toolBar.add(new JLabel("    状态筛选："));
        statusFilter = new JComboBox<>(new String[]{"全部", "已入住", "已退房"});
        toolBar.add(statusFilter);
        toolBar.add(refreshBtn);
        add(toolBar, BorderLayout.NORTH);

        // 表格
        String[] columns = {"ID", "订单号", "房间号", "房型", "客人姓名", "身份证", "电话", "入住时间", "退房时间", "天数", "总价", "状态"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(25);
        table.getColumnModel().getColumn(0).setPreferredWidth(30);
        table.getColumnModel().getColumn(1).setPreferredWidth(120);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // 事件绑定
        checkInBtn.addActionListener(e -> showCheckInDialog());
        checkOutBtn.addActionListener(e -> doCheckOut());
        extendBtn.addActionListener(e -> showExtendDialog());
        changeRoomBtn.addActionListener(e -> showChangeRoomDialog());
        refreshBtn.addActionListener(e -> loadData());
        statusFilter.addActionListener(e -> loadData());
    }

    public void refreshData() {
        loadData();
    }

    private void loadData() {
        tableModel.setRowCount(0);
        String filter = (String) statusFilter.getSelectedItem();
        List<Order> orders;
        
        if ("全部".equals(filter)) {
            orders = orderService.findAll();
        } else {
            String status = "已入住".equals(filter) ? "CHECKED_IN" : "CHECKED_OUT";
            orders = orderService.findByStatus(status);
        }
        
        for (Order order : orders) {
            String roomTypeName = "";
            if ("SINGLE".equals(order.getRoomType())) roomTypeName = "单人间";
            else if ("DOUBLE".equals(order.getRoomType())) roomTypeName = "双人间";
            else if ("SUITE".equals(order.getRoomType())) roomTypeName = "套房";
            
            tableModel.addRow(new Object[]{
                order.getId(), order.getOrderNo(), order.getRoomNumber(), roomTypeName,
                order.getGuestName(), order.getGuestIdCard(), order.getGuestPhone(),
                order.getCheckInTime() != null ? sdf.format(order.getCheckInTime()) : "",
                order.getCheckOutTime() != null ? sdf.format(order.getCheckOutTime()) : "",
                order.getDays(), order.getTotalPrice(), order.getStatusName()
            });
        }
        if (orders.isEmpty()) {
            tableModel.addRow(new Object[]{"", "", "", "", "暂无数据", "", "", "", "", "", "", ""});
        }
    }

    private void showCheckInDialog() {
        // 获取可用房间
        List<Room> availableRooms = roomService.findByStatus("AVAILABLE");
        if (availableRooms.isEmpty()) {
            JOptionPane.showMessageDialog(this, "当前没有空闲房间", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "入住登记", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 房间选择下拉框
        JComboBox<String> roomBox = new JComboBox<>();
        for (Room room : availableRooms) {
            roomBox.addItem(room.getId() + " - " + room.getRoomNumber() + " (" + room.getRoomTypeName() + ") ￥" + room.getPrice() + "/天");
        }
        
        JTextField guestNameField = new JTextField(15);
        JTextField idCardField = new JTextField(15);
        JTextField phoneField = new JTextField(15);
        JSpinner daysSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 365, 1));
        JLabel priceLabel = new JLabel("￥" + availableRooms.get(0).getPrice());

        // 房间选择变化时更新价格
        roomBox.addActionListener(e -> {
            int idx = roomBox.getSelectedIndex();
            if (idx >= 0) {
                Room room = availableRooms.get(idx);
                int days = (Integer) daysSpinner.getValue();
                priceLabel.setText("￥" + room.getPrice().multiply(new java.math.BigDecimal(days)));
            }
        });
        daysSpinner.addChangeListener(e -> {
            int idx = roomBox.getSelectedIndex();
            if (idx >= 0) {
                Room room = availableRooms.get(idx);
                int days = (Integer) daysSpinner.getValue();
                priceLabel.setText("￥" + room.getPrice().multiply(new java.math.BigDecimal(days)));
            }
        });

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("选择房间："), gbc);
        gbc.gridx = 1;
        panel.add(roomBox, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("客人姓名："), gbc);
        gbc.gridx = 1;
        panel.add(guestNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("身份证号："), gbc);
        gbc.gridx = 1;
        panel.add(idCardField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("联系电话："), gbc);
        gbc.gridx = 1;
        panel.add(phoneField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("入住天数："), gbc);
        gbc.gridx = 1;
        panel.add(daysSpinner, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(new JLabel("预计总价："), gbc);
        gbc.gridx = 1;
        priceLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        priceLabel.setForeground(Color.RED);
        panel.add(priceLabel, gbc);

        JPanel btnPanel = new JPanel();
        JButton saveBtn = new JButton("确认入住");
        JButton cancelBtn = new JButton("取消");
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        panel.add(btnPanel, gbc);

        saveBtn.addActionListener(e -> {
            String guestName = guestNameField.getText().trim();
            String idCard = idCardField.getText().trim();
            String phone = phoneField.getText().trim();
            
            if (guestName.isEmpty() || idCard.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "请填写客人姓名和身份证号", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // 校验身份证号（18位，最后一位可以是X）
            if (!idCard.matches("^\\d{17}[\\dXx]$")) {
                JOptionPane.showMessageDialog(dialog, "身份证号格式不正确（应为18位）", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // 校验手机号（11位数字，1开头）
            if (!phone.isEmpty() && !phone.matches("^1\\d{10}$")) {
                JOptionPane.showMessageDialog(dialog, "手机号格式不正确（应为11位，1开头）", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                int idx = roomBox.getSelectedIndex();
                Room room = availableRooms.get(idx);
                
                Order order = new Order();
                order.setRoomId(room.getId());
                order.setGuestName(guestName);
                order.setGuestIdCard(idCard);
                order.setGuestPhone(phoneField.getText().trim());
                order.setDays((Integer) daysSpinner.getValue());

                Order result = orderService.checkIn(order);
                JOptionPane.showMessageDialog(dialog, 
                    "入住成功！\n订单号：" + result.getOrderNo() + "\n总价：￥" + result.getTotalPrice());
                dialog.dispose();
                loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        });
        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void doCheckOut() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "请先选择一条订单记录");
            return;
        }

        String status = (String) tableModel.getValueAt(row, 11);
        if ("已退房".equals(status)) {
            JOptionPane.showMessageDialog(this, "该订单已退房", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Integer id = (Integer) tableModel.getValueAt(row, 0);
        String orderNo = (String) tableModel.getValueAt(row, 1);
        Object totalPrice = tableModel.getValueAt(row, 10);

        int confirm = JOptionPane.showConfirmDialog(this, 
            "确认退房？\n订单号：" + orderNo + "\n应付金额：￥" + totalPrice, 
            "退房确认", JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                orderService.checkOut(id);
                JOptionPane.showMessageDialog(this, "退房成功！");
                loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showExtendDialog() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "请先选择一条订单记录");
            return;
        }

        String status = (String) tableModel.getValueAt(row, 11);
        if (!"已入住".equals(status)) {
            JOptionPane.showMessageDialog(this, "只有已入住的订单才能续住", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Integer id = (Integer) tableModel.getValueAt(row, 0);
        Order order = orderService.findById(id);
        if (order == null) {
            JOptionPane.showMessageDialog(this, "订单不存在", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Room room = roomService.findById(order.getRoomId());
        if (room == null) {
            JOptionPane.showMessageDialog(this, "关联房间不存在", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "续住办理", true);
        dialog.setSize(420, 300);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel currentInfoLabel = new JLabel("订单号：" + order.getOrderNo() + "  房间：" + order.getRoomNumber()
                + "  当前天数：" + order.getDays() + "天  当前总价：￥" + order.getTotalPrice());

        JSpinner extraDaysSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 365, 1));
        JLabel newTotalLabel = new JLabel("￥" + room.getPrice().multiply(new BigDecimal(order.getDays() + 1)));

        extraDaysSpinner.addChangeListener(e -> {
            int extraDays = (Integer) extraDaysSpinner.getValue();
            int newDays = order.getDays() + extraDays;
            BigDecimal newTotal = room.getPrice().multiply(new BigDecimal(newDays));
            newTotalLabel.setText("￥" + newTotal);
        });

        JLabel diffLabel = new JLabel("+ ￥" + room.getPrice());
        extraDaysSpinner.addChangeListener(e -> {
            int extraDays = (Integer) extraDaysSpinner.getValue();
            BigDecimal diff = room.getPrice().multiply(new BigDecimal(extraDays));
            diffLabel.setText("+ ￥" + diff);
        });

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(currentInfoLabel, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("续住天数："), gbc);
        gbc.gridx = 1;
        panel.add(extraDaysSpinner, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("房间单价："), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel("￥" + room.getPrice() + "/天"), gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("费用差额："), gbc);
        gbc.gridx = 1;
        diffLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        diffLabel.setForeground(new Color(0, 128, 0));
        panel.add(diffLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("新总价："), gbc);
        gbc.gridx = 1;
        newTotalLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        newTotalLabel.setForeground(Color.RED);
        panel.add(newTotalLabel, gbc);

        JPanel btnPanel = new JPanel();
        JButton confirmBtn = new JButton("确认续住");
        JButton cancelBtn = new JButton("取消");
        btnPanel.add(confirmBtn);
        btnPanel.add(cancelBtn);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        panel.add(btnPanel, gbc);

        confirmBtn.addActionListener(e -> {
            int extraDays = (Integer) extraDaysSpinner.getValue();
            int confirm = JOptionPane.showConfirmDialog(dialog,
                    "确认续住 " + extraDays + " 天？\n费用差额：+￥" + room.getPrice().multiply(new BigDecimal(extraDays)),
                    "续住确认", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    Order result = orderService.extendStay(id, extraDays);
                    JOptionPane.showMessageDialog(dialog,
                            "续住成功！\n新天数：" + result.getDays() + "天\n新总价：￥" + result.getTotalPrice());
                    dialog.dispose();
                    loadData();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showChangeRoomDialog() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "请先选择一条订单记录");
            return;
        }

        String status = (String) tableModel.getValueAt(row, 11);
        if (!"已入住".equals(status)) {
            JOptionPane.showMessageDialog(this, "只有已入住的订单才能换房", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Integer id = (Integer) tableModel.getValueAt(row, 0);
        Order order = orderService.findById(id);
        if (order == null) {
            JOptionPane.showMessageDialog(this, "订单不存在", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Room oldRoom = roomService.findById(order.getRoomId());
        if (oldRoom == null) {
            JOptionPane.showMessageDialog(this, "关联房间不存在", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<Room> availableRooms = roomService.findByStatus("AVAILABLE");
        if (availableRooms.isEmpty()) {
            JOptionPane.showMessageDialog(this, "当前没有空闲房间可供换房", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "换房办理", true);
        dialog.setSize(450, 350);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel currentInfoLabel = new JLabel("订单号：" + order.getOrderNo()
                + "  当前房间：" + oldRoom.getRoomNumber() + "(" + oldRoom.getRoomTypeName() + ") ￥" + oldRoom.getPrice() + "/天"
                + "  已住天数：" + order.getDays() + "天  当前总价：￥" + order.getTotalPrice());

        JComboBox<String> roomBox = new JComboBox<>();
        for (Room room : availableRooms) {
            roomBox.addItem(room.getId() + " - " + room.getRoomNumber() + " (" + room.getRoomTypeName() + ") ￥" + room.getPrice() + "/天");
        }

        JLabel newPriceLabel = new JLabel();
        updateChangeRoomPrice(newPriceLabel, order, oldRoom, availableRooms.get(0));

        roomBox.addActionListener(e -> {
            int idx = roomBox.getSelectedIndex();
            if (idx >= 0) {
                Room selectedRoom = availableRooms.get(idx);
                updateChangeRoomPrice(newPriceLabel, order, oldRoom, selectedRoom);
            }
        });

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(currentInfoLabel, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("选择新房间："), gbc);
        gbc.gridx = 1;
        panel.add(roomBox, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("原房间单价："), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel("￥" + oldRoom.getPrice() + "/天"), gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("新房间单价："), gbc);
        gbc.gridx = 1;
        JLabel newRoomPriceLabel = new JLabel("￥" + availableRooms.get(0).getPrice() + "/天");
        panel.add(newRoomPriceLabel, gbc);

        roomBox.addActionListener(e -> {
            int idx = roomBox.getSelectedIndex();
            if (idx >= 0) {
                Room selectedRoom = availableRooms.get(idx);
                newRoomPriceLabel.setText("￥" + selectedRoom.getPrice() + "/天");
            }
        });

        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("换房后总价："), gbc);
        gbc.gridx = 1;
        newPriceLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        newPriceLabel.setForeground(Color.RED);
        panel.add(newPriceLabel, gbc);

        JPanel btnPanel = new JPanel();
        JButton confirmBtn = new JButton("确认换房");
        JButton cancelBtn = new JButton("取消");
        btnPanel.add(confirmBtn);
        btnPanel.add(cancelBtn);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        panel.add(btnPanel, gbc);

        confirmBtn.addActionListener(e -> {
            int idx = roomBox.getSelectedIndex();
            Room selectedRoom = availableRooms.get(idx);
            int confirm = JOptionPane.showConfirmDialog(dialog,
                    "确认从 " + oldRoom.getRoomNumber() + " 换到 " + selectedRoom.getRoomNumber() + "？\n"
                            + "费用将按新房间价格从换房当天起重新计算",
                    "换房确认", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    Order result = orderService.changeRoom(id, selectedRoom.getId());
                    JOptionPane.showMessageDialog(dialog,
                            "换房成功！\n新房间：" + selectedRoom.getRoomNumber()
                                    + "\n新总价：￥" + result.getTotalPrice());
                    dialog.dispose();
                    loadData();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void updateChangeRoomPrice(JLabel label, Order order, Room oldRoom, Room newRoom) {
        long checkInTime = order.getCheckInTime().getTime();
        long now = System.currentTimeMillis();
        long elapsedMillis = now - checkInTime;
        int elapsedDays = (int) Math.ceil(elapsedMillis / (1000.0 * 60 * 60 * 24));
        if (elapsedDays < 1) elapsedDays = 1;
        int remainDays = order.getDays() - elapsedDays;
        if (remainDays < 1) remainDays = 1;

        BigDecimal newTotal = oldRoom.getPrice().multiply(new BigDecimal(elapsedDays))
                .add(newRoom.getPrice().multiply(new BigDecimal(remainDays)));
        label.setText("￥" + newTotal + " (已住" + elapsedDays + "天×￥" + oldRoom.getPrice()
                + " + 剩余" + remainDays + "天×￥" + newRoom.getPrice() + ")");
    }
}
