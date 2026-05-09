package com.hotel.entity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单实体类
 */
public class Order {
    private Integer id;
    private String orderNo;
    private Integer roomId;
    private String guestName;
    private String guestIdCard;
    private String guestPhone;
    private Date checkInTime;
    private Date checkOutTime;
    private Integer days;
    private BigDecimal totalPrice;
    private String status;        // CHECKED_IN/CHECKED_OUT
    private Date createTime;
    private Date updateTime;
    
    // 关联字段（非数据库字段）
    private String roomNumber;
    private String roomType;

    public Order() {}

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public Integer getRoomId() { return roomId; }
    public void setRoomId(Integer roomId) { this.roomId = roomId; }
    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }
    public String getGuestIdCard() { return guestIdCard; }
    public void setGuestIdCard(String guestIdCard) { this.guestIdCard = guestIdCard; }
    public String getGuestPhone() { return guestPhone; }
    public void setGuestPhone(String guestPhone) { this.guestPhone = guestPhone; }
    public Date getCheckInTime() { return checkInTime; }
    public void setCheckInTime(Date checkInTime) { this.checkInTime = checkInTime; }
    public Date getCheckOutTime() { return checkOutTime; }
    public void setCheckOutTime(Date checkOutTime) { this.checkOutTime = checkOutTime; }
    public Integer getDays() { return days; }
    public void setDays(Integer days) { this.days = days; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }

    /** 获取状态中文名 */
    public String getStatusName() {
        return "CHECKED_IN".equals(status) ? "已入住" : "已退房";
    }
}
