package com.hotel.dao;

import com.hotel.entity.Room;
import com.hotel.util.LogUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 客房数据访问层 - 使用JDBC操作数据库
 */
public class RoomDao {

    /** 查询所有客房 */
    public List<Room> findAll() {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM room ORDER BY CAST(room_number AS UNSIGNED)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                rooms.add(mapResultSetToRoom(rs));
            }
        } catch (SQLException e) {
            LogUtil.error("查询所有客房失败", e);
        }
        return rooms;
    }

    /** 根据ID查询客房 */
    public Room findById(Integer id) {
        String sql = "SELECT * FROM room WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRoom(rs);
                }
            }
        } catch (SQLException e) {
            LogUtil.error("查询客房失败: ID=" + id, e);
        }
        return null;
    }

    /** 根据房间号查询客房 */
    public Room findByRoomNumber(String roomNumber) {
        String sql = "SELECT * FROM room WHERE room_number = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, roomNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRoom(rs);
                }
            }
        } catch (SQLException e) {
            LogUtil.error("查询客房失败: 房间号=" + roomNumber, e);
        }
        return null;
    }

    /** 根据状态查询客房 */
    public List<Room> findByStatus(String status) {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM room WHERE status = ? ORDER BY CAST(room_number AS UNSIGNED)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    rooms.add(mapResultSetToRoom(rs));
                }
            }
        } catch (SQLException e) {
            LogUtil.error("按状态查询客房失败: " + status, e);
        }
        return rooms;
    }

    /** 添加客房 */
    public int insert(Room room) {
        String sql = "INSERT INTO room (room_number, room_type, price, status, description) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, room.getRoomNumber());
            stmt.setString(2, room.getRoomType());
            stmt.setBigDecimal(3, room.getPrice());
            stmt.setString(4, room.getStatus() != null ? room.getStatus() : "AVAILABLE");
            stmt.setString(5, room.getDescription());
            int result = stmt.executeUpdate();
            LogUtil.info("添加客房成功: " + room.getRoomNumber());
            return result;
        } catch (SQLException e) {
            LogUtil.error("添加客房失败: " + room.getRoomNumber(), e);
        }
        return 0;
    }

    /** 更新客房信息 */
    public int update(Room room) {
        String sql = "UPDATE room SET room_type = ?, price = ?, status = ?, description = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, room.getRoomType());
            stmt.setBigDecimal(2, room.getPrice());
            stmt.setString(3, room.getStatus());
            stmt.setString(4, room.getDescription());
            stmt.setInt(5, room.getId());
            int result = stmt.executeUpdate();
            LogUtil.info("更新客房成功: ID=" + room.getId());
            return result;
        } catch (SQLException e) {
            LogUtil.error("更新客房失败: ID=" + room.getId(), e);
        }
        return 0;
    }

    /** 更新客房状态 */
    public int updateStatus(Integer id, String status) {
        String sql = "UPDATE room SET status = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, id);
            int result = stmt.executeUpdate();
            LogUtil.info("更新客房状态成功: ID=" + id + ", 状态=" + status);
            return result;
        } catch (SQLException e) {
            LogUtil.error("更新客房状态失败: ID=" + id, e);
        }
        return 0;
    }

    /** 删除客房 */
    public int deleteById(Integer id) {
        String sql = "DELETE FROM room WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int result = stmt.executeUpdate();
            LogUtil.info("删除客房成功: ID=" + id);
            return result;
        } catch (SQLException e) {
            LogUtil.error("删除客房失败: ID=" + id, e);
        }
        return 0;
    }

    /** 将ResultSet映射为Room对象 */
    private Room mapResultSetToRoom(ResultSet rs) throws SQLException {
        Room room = new Room();
        room.setId(rs.getInt("id"));
        room.setRoomNumber(rs.getString("room_number"));
        room.setRoomType(rs.getString("room_type"));
        room.setPrice(rs.getBigDecimal("price"));
        room.setStatus(rs.getString("status"));
        room.setDescription(rs.getString("description"));
        room.setCreateTime(rs.getTimestamp("create_time"));
        room.setUpdateTime(rs.getTimestamp("update_time"));
        return room;
    }
}
