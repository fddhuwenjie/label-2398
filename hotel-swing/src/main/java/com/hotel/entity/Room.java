package com.hotel.entity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 客房实体类
 */
public class Room {
    private Integer id;
    private String roomNumber;
    private String roomType;      // SINGLE/DOUBLE/SUITE
    private BigDecimal price;
    private String status;        // AVAILABLE/OCCUPIED/MAINTENANCE
    private String description;
    private Date createTime;
    private Date updateTime;

    public Room() {}

    public Room(String roomNumber, String roomType, BigDecimal price) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.price = price;
        this.status = "AVAILABLE";
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }

    /** 获取房型中文名 */
    public String getRoomTypeName() {
        switch (roomType) {
            case "SINGLE": return "单人间";
            case "DOUBLE": return "双人间";
            case "SUITE": return "套房";
            default: return roomType;
        }
    }

    /** 获取状态中文名 */
    public String getStatusName() {
        switch (status) {
            case "AVAILABLE": return "空闲";
            case "OCCUPIED": return "已入住";
            case "MAINTENANCE": return "维修中";
            default: return status;
        }
    }
}
