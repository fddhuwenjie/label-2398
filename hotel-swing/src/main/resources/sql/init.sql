-- 宾馆客房管理系统数据库初始化脚本

CREATE DATABASE IF NOT EXISTS hotel_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE hotel_db;

SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码',
    `real_name` VARCHAR(50) COMMENT '真实姓名',
    `phone` VARCHAR(20) COMMENT '手机号',
    `role` VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '角色: ADMIN/USER',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 客房表
CREATE TABLE IF NOT EXISTS `room` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `room_number` VARCHAR(20) NOT NULL UNIQUE COMMENT '房间号',
    `room_type` VARCHAR(30) NOT NULL COMMENT '房型: SINGLE/DOUBLE/SUITE',
    `price` DECIMAL(10,2) NOT NULL COMMENT '价格/天',
    `status` VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE' COMMENT '状态: AVAILABLE/OCCUPIED/MAINTENANCE',
    `description` VARCHAR(200) COMMENT '描述',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客房表';

-- 订单表
CREATE TABLE IF NOT EXISTS `order_info` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `order_no` VARCHAR(50) NOT NULL UNIQUE COMMENT '订单号',
    `room_id` INT NOT NULL COMMENT '房间ID',
    `guest_name` VARCHAR(50) NOT NULL COMMENT '入住人姓名',
    `guest_id_card` VARCHAR(20) NOT NULL COMMENT '身份证号',
    `guest_phone` VARCHAR(20) COMMENT '联系电话',
    `check_in_time` DATETIME NOT NULL COMMENT '入住时间',
    `check_out_time` DATETIME COMMENT '退房时间',
    `days` INT NOT NULL DEFAULT 1 COMMENT '入住天数',
    `total_price` DECIMAL(10,2) NOT NULL COMMENT '总价',
    `status` VARCHAR(20) NOT NULL DEFAULT 'CHECKED_IN' COMMENT '状态: CHECKED_IN/CHECKED_OUT',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`room_id`) REFERENCES `room`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 初始化管理员账号（密码使用SHA-256加密）
INSERT INTO `user` (`username`, `password`, `real_name`, `role`) VALUES
('admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', '系统管理员', 'ADMIN'),
('user', 'e606e38b0d8c19b24cf0ee3808183162ea7cd63ff7912dbb22b5e803286b4446', '前台人员', 'RECEPTION');

-- 初始化客房数据
INSERT INTO `room` (`room_number`, `room_type`, `price`, `status`, `description`) VALUES
('101', 'SINGLE', 188.00, 'AVAILABLE', '标准单人间，配备空调、电视、独立卫浴'),
('102', 'SINGLE', 188.00, 'AVAILABLE', '标准单人间，配备空调、电视、独立卫浴'),
('201', 'DOUBLE', 288.00, 'AVAILABLE', '豪华双人间，配备空调、电视、独立卫浴、迷你吧'),
('202', 'DOUBLE', 288.00, 'AVAILABLE', '豪华双人间，配备空调、电视、独立卫浴、迷你吧'),
('301', 'SUITE', 588.00, 'AVAILABLE', '总统套房，配备客厅、卧室、独立卫浴、迷你吧'),
('302', 'SUITE', 588.00, 'MAINTENANCE', '总统套房，正在维修中');
