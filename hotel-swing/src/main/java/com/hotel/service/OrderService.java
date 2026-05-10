package com.hotel.service;

import com.hotel.dao.DBUtil;
import com.hotel.dao.OrderDao;
import com.hotel.dao.RoomDao;
import com.hotel.entity.Order;
import com.hotel.entity.Room;
import com.hotel.util.LogUtil;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.Date;
import java.util.List;

/**
 * 订单业务逻辑层
 */
public class OrderService {
    private OrderDao orderDao = new OrderDao();
    private RoomDao roomDao = new RoomDao();

    /** 查询所有订单 */
    public List<Order> findAll() {
        return orderDao.findAll();
    }

    /** 根据ID查询订单 */
    public Order findById(Integer id) {
        return orderDao.findById(id);
    }

    /** 根据状态查询订单 */
    public List<Order> findByStatus(String status) {
        return orderDao.findByStatus(status);
    }

    /** 入住登记 */
    public Order checkIn(Order order) throws Exception {
        Room room = roomDao.findById(order.getRoomId());
        if (room == null) {
            throw new Exception("房间不存在");
        }
        if (!"AVAILABLE".equals(room.getStatus())) {
            throw new Exception("房间不可用，当前状态：" + room.getStatusName());
        }

        // 生成订单号
        String orderNo = "ORD" + System.currentTimeMillis();
        order.setOrderNo(orderNo);
        order.setCheckInTime(new Date());
        order.setStatus("CHECKED_IN");
        
        // 计算总价
        BigDecimal totalPrice = room.getPrice().multiply(new BigDecimal(order.getDays()));
        order.setTotalPrice(totalPrice);

        // 保存订单
        orderDao.insert(order);
        
        // 更新房间状态为已入住
        roomDao.updateStatus(room.getId(), "OCCUPIED");

        return orderDao.findByOrderNo(orderNo);
    }

    /** 退房结算 */
    public boolean checkOut(Integer orderId) throws Exception {
        Order order = orderDao.findById(orderId);
        if (order == null) {
            throw new Exception("订单不存在");
        }
        if ("CHECKED_OUT".equals(order.getStatus())) {
            throw new Exception("该订单已退房");
        }

        // 更新订单状态
        orderDao.checkOut(orderId);
        
        // 更新房间状态为空闲
        roomDao.updateStatus(order.getRoomId(), "AVAILABLE");

        return true;
    }

    /** 删除订单 */
    public boolean deleteOrder(Integer id) {
        return orderDao.deleteById(id) > 0;
    }

    /** 续住：延长退房日期，重新计算费用差额（同一事务） */
    public Order extendStay(Integer orderId, int extraDays) throws Exception {
        Order order = orderDao.findById(orderId);
        if (order == null) {
            throw new Exception("订单不存在");
        }
        if (!"CHECKED_IN".equals(order.getStatus())) {
            throw new Exception("只有已入住的订单才能续住");
        }
        if (extraDays <= 0) {
            throw new Exception("续住天数必须大于0");
        }

        Room room = roomDao.findById(order.getRoomId());
        if (room == null) {
            throw new Exception("关联房间不存在");
        }

        int newDays = order.getDays() + extraDays;
        BigDecimal newTotalPrice = room.getPrice().multiply(new BigDecimal(newDays));

        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            orderDao.updateForExtend(conn, orderId, newDays, newTotalPrice);

            conn.commit();
            LogUtil.info("续住成功: 订单ID=" + orderId + ", 原天数=" + order.getDays() + ", 续住=" + extraDays + ", 新天数=" + newDays);
        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (Exception ex) { LogUtil.error("续住事务回滚失败", ex); }
            }
            LogUtil.error("续住失败: 订单ID=" + orderId, e);
            throw new Exception("续住操作失败: " + e.getMessage());
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (Exception ignored) {}
                try { conn.close(); } catch (Exception ignored) {}
            }
        }

        return orderDao.findById(orderId);
    }

    /** 换房：从当前房间换到空闲房间，保留原入住时间，费用按新房间价格从换房当天起重新计算（同一事务） */
    public Order changeRoom(Integer orderId, Integer newRoomId) throws Exception {
        Order order = orderDao.findById(orderId);
        if (order == null) {
            throw new Exception("订单不存在");
        }
        if (!"CHECKED_IN".equals(order.getStatus())) {
            throw new Exception("只有已入住的订单才能换房");
        }
        if (order.getRoomId().equals(newRoomId)) {
            throw new Exception("新房间与当前房间相同，无需换房");
        }

        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            Room oldRoom = roomDao.findById(conn, order.getRoomId());
            if (oldRoom == null) {
                throw new Exception("原房间不存在");
            }

            Room newRoom = roomDao.findById(conn, newRoomId);
            if (newRoom == null) {
                throw new Exception("目标房间不存在");
            }
            if (!"AVAILABLE".equals(newRoom.getStatus())) {
                throw new Exception("目标房间不可用，当前状态：" + newRoom.getStatusName());
            }

            long checkInTime = order.getCheckInTime().getTime();
            long now = System.currentTimeMillis();
            long elapsedMillis = now - checkInTime;
            int elapsedDays = (int) Math.ceil(elapsedMillis / (1000.0 * 60 * 60 * 24));
            if (elapsedDays < 1) {
                elapsedDays = 1;
            }

            int remainDays = order.getDays() - elapsedDays;
            if (remainDays < 1) {
                remainDays = 1;
            }

            BigDecimal newTotalPrice = oldRoom.getPrice().multiply(new BigDecimal(elapsedDays))
                    .add(newRoom.getPrice().multiply(new BigDecimal(remainDays)));

            roomDao.updateStatus(conn, order.getRoomId(), "AVAILABLE");
            roomDao.updateStatus(conn, newRoomId, "OCCUPIED");
            orderDao.updateForChangeRoom(conn, orderId, newRoomId, newTotalPrice);

            conn.commit();
            LogUtil.info("换房成功: 订单ID=" + orderId + ", 原房间=" + oldRoom.getRoomNumber() + ", 新房间=" + newRoom.getRoomNumber());
        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (Exception ex) { LogUtil.error("换房事务回滚失败", ex); }
            }
            LogUtil.error("换房失败: 订单ID=" + orderId, e);
            throw new Exception("换房操作失败: " + e.getMessage());
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (Exception ignored) {}
                try { conn.close(); } catch (Exception ignored) {}
            }
        }

        return orderDao.findById(orderId);
    }
}
