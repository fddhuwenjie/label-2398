package com.hotel.dao;

import com.hotel.entity.Order;
import com.hotel.util.LogUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 订单数据访问层 - 使用JDBC操作数据库
 */
public class OrderDao {

    /** 查询所有订单（关联房间信息） */
    public List<Order> findAll() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.*, r.room_number, r.room_type FROM order_info o " +
                     "LEFT JOIN room r ON o.room_id = r.id ORDER BY o.create_time DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                orders.add(mapResultSetToOrder(rs));
            }
        } catch (SQLException e) {
            LogUtil.error("查询所有订单失败", e);
        }
        return orders;
    }

    /** 根据ID查询订单 */
    public Order findById(Integer id) {
        String sql = "SELECT o.*, r.room_number, r.room_type FROM order_info o " +
                     "LEFT JOIN room r ON o.room_id = r.id WHERE o.id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToOrder(rs);
                }
            }
        } catch (SQLException e) {
            LogUtil.error("查询订单失败: ID=" + id, e);
        }
        return null;
    }

    /** 根据订单号查询订单 */
    public Order findByOrderNo(String orderNo) {
        String sql = "SELECT o.*, r.room_number, r.room_type FROM order_info o " +
                     "LEFT JOIN room r ON o.room_id = r.id WHERE o.order_no = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, orderNo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToOrder(rs);
                }
            }
        } catch (SQLException e) {
            LogUtil.error("查询订单失败: 订单号=" + orderNo, e);
        }
        return null;
    }

    /** 根据状态查询订单 */
    public List<Order> findByStatus(String status) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.*, r.room_number, r.room_type FROM order_info o " +
                     "LEFT JOIN room r ON o.room_id = r.id WHERE o.status = ? ORDER BY o.create_time DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapResultSetToOrder(rs));
                }
            }
        } catch (SQLException e) {
            LogUtil.error("按状态查询订单失败: " + status, e);
        }
        return orders;
    }

    /** 添加订单 */
    public int insert(Order order) {
        String sql = "INSERT INTO order_info (order_no, room_id, guest_name, guest_id_card, guest_phone, " +
                     "check_in_time, days, total_price, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, order.getOrderNo());
            stmt.setInt(2, order.getRoomId());
            stmt.setString(3, order.getGuestName());
            stmt.setString(4, order.getGuestIdCard());
            stmt.setString(5, order.getGuestPhone());
            stmt.setTimestamp(6, new Timestamp(order.getCheckInTime().getTime()));
            stmt.setInt(7, order.getDays());
            stmt.setBigDecimal(8, order.getTotalPrice());
            stmt.setString(9, order.getStatus() != null ? order.getStatus() : "CHECKED_IN");
            int result = stmt.executeUpdate();
            LogUtil.info("添加订单成功: " + order.getOrderNo());
            return result;
        } catch (SQLException e) {
            LogUtil.error("添加订单失败: " + order.getOrderNo(), e);
        }
        return 0;
    }

    /** 更新订单（退房） */
    public int checkOut(Integer id) {
        String sql = "UPDATE order_info SET status = 'CHECKED_OUT', check_out_time = NOW() WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int result = stmt.executeUpdate();
            LogUtil.info("退房成功: 订单ID=" + id);
            return result;
        } catch (SQLException e) {
            LogUtil.error("退房失败: 订单ID=" + id, e);
        }
        return 0;
    }
    
    /** 续住更新订单（事务内使用） */
    public int extendStayForTransaction(Connection conn, Integer orderId, Integer newDays, java.math.BigDecimal newTotalPrice) throws SQLException {
        String sql = "UPDATE order_info SET days = ?, total_price = ?, update_time = NOW() WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, newDays);
            stmt.setBigDecimal(2, newTotalPrice);
            stmt.setInt(3, orderId);
            int result = stmt.executeUpdate();
            LogUtil.info("续住更新订单成功: 订单ID=" + orderId + ", 新天数=" + newDays);
            return result;
        }
    }
    
    /** 换房更新订单（事务内使用） */
    public int changeRoomForTransaction(Connection conn, Integer orderId, Integer newRoomId, java.math.BigDecimal newTotalPrice, Integer newDays) throws SQLException {
        String sql = "UPDATE order_info SET room_id = ?, total_price = ?, days = ?, update_time = NOW() WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, newRoomId);
            stmt.setBigDecimal(2, newTotalPrice);
            stmt.setInt(3, newDays);
            stmt.setInt(4, orderId);
            int result = stmt.executeUpdate();
            LogUtil.info("换房更新订单成功: 订单ID=" + orderId + ", 新房间ID=" + newRoomId);
            return result;
        }
    }
    
    /** 根据ID查询订单（事务内使用） */
    public Order findByIdForTransaction(Connection conn, Integer id) throws SQLException {
        String sql = "SELECT o.*, r.room_number, r.room_type FROM order_info o " +
                     "LEFT JOIN room r ON o.room_id = r.id WHERE o.id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToOrder(rs);
                }
            }
        }
        return null;
    }

    /** 删除订单 */
    public int deleteById(Integer id) {
        String sql = "DELETE FROM order_info WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int result = stmt.executeUpdate();
            LogUtil.info("删除订单成功: ID=" + id);
            return result;
        } catch (SQLException e) {
            LogUtil.error("删除订单失败: ID=" + id, e);
        }
        return 0;
    }

    /** 将ResultSet映射为Order对象 */
    private Order mapResultSetToOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setId(rs.getInt("id"));
        order.setOrderNo(rs.getString("order_no"));
        order.setRoomId(rs.getInt("room_id"));
        order.setGuestName(rs.getString("guest_name"));
        order.setGuestIdCard(rs.getString("guest_id_card"));
        order.setGuestPhone(rs.getString("guest_phone"));
        order.setCheckInTime(rs.getTimestamp("check_in_time"));
        order.setCheckOutTime(rs.getTimestamp("check_out_time"));
        order.setDays(rs.getInt("days"));
        order.setTotalPrice(rs.getBigDecimal("total_price"));
        order.setStatus(rs.getString("status"));
        order.setCreateTime(rs.getTimestamp("create_time"));
        order.setUpdateTime(rs.getTimestamp("update_time"));
        order.setRoomNumber(rs.getString("room_number"));
        order.setRoomType(rs.getString("room_type"));
        return order;
    }
}
