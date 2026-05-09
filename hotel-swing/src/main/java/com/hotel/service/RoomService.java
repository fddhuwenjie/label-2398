package com.hotel.service;

import com.hotel.dao.RoomDao;
import com.hotel.entity.Room;
import java.util.List;

/**
 * 客房业务逻辑层
 */
public class RoomService {
    private RoomDao roomDao = new RoomDao();

    /** 查询所有客房 */
    public List<Room> findAll() {
        return roomDao.findAll();
    }

    /** 根据ID查询客房 */
    public Room findById(Integer id) {
        return roomDao.findById(id);
    }

    /** 根据房间号查询客房 */
    public Room findByRoomNumber(String roomNumber) {
        return roomDao.findByRoomNumber(roomNumber);
    }

    /** 根据状态查询客房 */
    public List<Room> findByStatus(String status) {
        return roomDao.findByStatus(status);
    }

    /** 添加客房 */
    public boolean addRoom(Room room) {
        if (roomDao.findByRoomNumber(room.getRoomNumber()) != null) {
            return false;  // 房间号已存在
        }
        return roomDao.insert(room) > 0;
    }

    /** 更新客房 */
    public boolean updateRoom(Room room) {
        return roomDao.update(room) > 0;
    }

    /** 更新客房状态 */
    public boolean updateStatus(Integer id, String status) {
        return roomDao.updateStatus(id, status) > 0;
    }

    /** 删除客房 */
    public boolean deleteRoom(Integer id) {
        return roomDao.deleteById(id) > 0;
    }
}
