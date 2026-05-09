package com.hotel.service;

import com.hotel.dao.OrderDao;
import com.hotel.dao.RoomDao;
import com.hotel.entity.Order;
import com.hotel.entity.Room;
import java.math.BigDecimal;
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
}
