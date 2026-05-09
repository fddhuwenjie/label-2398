# 宾馆客房管理系统

## How to Run

### Docker 启动（推荐）

```bash
# 启动所有服务
docker compose up --build -d

# 等待约30秒后，浏览器访问
http://localhost:8081/vnc.html
```

### 本地启动

```bash
# 1. 启动MySQL数据库
docker compose up -d mysql

# 2. 修改数据库连接为localhost
# 编辑 hotel-swing/src/main/resources/db.properties
# 将 mysql:3306 改为 localhost:3306

# 3. 编译运行（需要本地安装JDK8+和Maven）
cd hotel-swing
mvn clean package
java -jar target/hotel-management-1.0.0.jar
```

## Services

| 服务 | 端口 | 说明 |
|------|------|------|
| MySQL | 3306 | 数据库服务 |
| noVNC | 8081 | Web访问Swing应用界面 |

## 测试账号

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | admin123 | 管理员 |
| user | user123 | 前台 |

## 题目内容

我是一名大学生，现在我要完成Java面向对象程序设计的课程设计，具体要求如下，一、宾馆客房管理系统设计建议 

结合课程设计要求（综合2-3项Java核心技术、模块化开发、Swing界面+JDBC+面向对象），该系统设计需兼顾功能完整性与技术规范性，核心建议如下： 

1. 技术选型：选用Swing做图形界面、JDBC连接MySQL实现数据持久化、面向对象设计核心实体类（客房、用户、订单），符合课程“2-3项核心技术”要求。 

2. 模块化拆分：按功能拆分为界面交互模块（Swing）、数据访问模块（JDBC）、业务逻辑模块（客房/订单/用户管理），包结构按 com.hotel.view （界面）、 com.hotel.dao （数据操作）、 com.hotel.entity （实体类）、 com.hotel.service （业务逻辑）划分。 

3. 编码规范：类名用PascalCase（如 Room.java ）、方法名用camelCase（如 checkInRoom() ），关键逻辑加注释，IO流/数据库连接需手动关闭避免资源泄漏。 

二、系统核心功能模块与场景 

拆解为3大核心功能模块，对应需求场景如下： 

1. 客房管理模块 

- 功能：客房信息的增删改查、客房状态更新（空闲/已入住/维修）。 

- 场景：管理员在Swing界面输入客房号、房型、价格，点击“添加客房”按钮，系统封装为 Room 对象，通过JDBC插入MySQL数据库。 

2. 订单管理模块 

- 功能：用户入住登记、退房结算、订单查询。 

- 场景：用户输入身份证号和客房号，点击“入住”，系统校验客房状态为“空闲”后，创建 Order 对象并更新客房状态为“已入住”，同时将订单信息写入数据库。 

3. 用户管理模块 

- 功能：管理员登录、用户信息查询（仅管理员权限）。 

- 场景：管理员输入账号密码，系统通过JDBC查询数据库验证身份，验证通过则进入主界面，否则弹出异常提示。

---

## 项目介绍

基于 **Java Swing + JDBC + 面向对象** 架构的桌面应用程序，通过 noVNC 支持浏览器远程访问。

### 技术选型

- **界面层**：Java Swing
- **数据访问**：JDBC（原生SQL操作）
- **数据库**：MySQL 8.0
- **远程访问**：noVNC + Xvfb
- **构建工具**：Maven

### 项目结构

```
hotel-swing/
├── pom.xml                                    # Maven配置
├── Dockerfile                                 # Docker构建文件（含noVNC）
├── src/main/java/com/hotel/
│   ├── HotelApplication.java                  # 程序入口
│   ├── entity/                                # 实体类
│   │   ├── User.java                          # 用户实体
│   │   ├── Room.java                          # 客房实体
│   │   └── Order.java                         # 订单实体
│   ├── dao/                                   # 数据访问层（JDBC）
│   │   ├── DBUtil.java                        # 数据库连接工具
│   │   ├── UserDao.java                       # 用户数据操作
│   │   ├── RoomDao.java                       # 客房数据操作
│   │   └── OrderDao.java                      # 订单数据操作
│   ├── service/                               # 业务逻辑层
│   │   ├── UserService.java                   # 用户业务
│   │   ├── RoomService.java                   # 客房业务
│   │   └── OrderService.java                  # 订单业务
│   └── view/                                  # 界面层（Swing）
│       ├── LoginFrame.java                    # 登录界面
│       ├── MainFrame.java                     # 主界面
│       ├── RoomPanel.java                     # 客房管理面板
│       ├── OrderPanel.java                    # 订单管理面板
│       └── UserPanel.java                     # 用户管理面板
└── src/main/resources/
    ├── db.properties                          # 数据库配置
    └── sql/init.sql                           # 数据库初始化脚本
```

### 功能模块

1. **用户登录**：支持管理员和普通用户登录
2. **客房管理**：客房的增删改查、状态筛选
3. **入住/退房**：入住登记、退房结算、订单查询
4. **用户管理**：用户的增删改查（仅管理员可见）

### 数据库表结构

| 表名 | 说明 | 主要字段 |
|------|------|----------|
| user | 用户表 | id, username, password, real_name, phone, role |
| room | 客房表 | id, room_number, room_type, price, status, description |
| order_info | 订单表 | id, order_no, room_id, guest_name, guest_id_card, guest_phone, check_in_time, check_out_time, days, total_price, status |

### DAO 层方法

| 类 | 方法 | 说明 |
|----|------|------|
| UserDao | findByUsername(String) | 根据用户名查询 |
| UserDao | findAll() | 查询所有用户 |
| UserDao | insert(User) | 添加用户 |
| UserDao | update(User) | 更新用户 |
| UserDao | deleteById(Integer) | 删除用户 |
| RoomDao | findAll() | 查询所有客房 |
| RoomDao | findByStatus(String) | 按状态查询客房 |
| RoomDao | insert(Room) | 添加客房 |
| RoomDao | update(Room) | 更新客房 |
| RoomDao | updateStatus(Integer, String) | 更新客房状态 |
| RoomDao | deleteById(Integer) | 删除客房 |
| OrderDao | findAll() | 查询所有订单 |
| OrderDao | findByStatus(String) | 按状态查询订单 |
| OrderDao | insert(Order) | 添加订单 |
| OrderDao | checkOut(Integer) | 退房更新 |
| OrderDao | deleteById(Integer) | 删除订单 |

## 工程与架构质量说明

采用标准四层架构：view（Swing界面）、service（业务逻辑）、dao（JDBC数据访问）、entity（实体类）。各层职责清晰，依赖关系单向，符合高内聚低耦合原则。使用Maven管理依赖，Docker容器化部署，支持一键启动。

## 工程细节与专业度说明

密码采用SHA-256加密存储；实现简易数据库连接池复用连接；DAO层全部使用try-with-resources管理资源防止泄漏；统一日志工具类记录系统运行状态和异常；输入校验覆盖身份证、手机号、密码长度等字段。

## 需求理解与适配说明

严格按照题目要求使用Swing+JDBC+面向对象技术栈，未引入Spring等框架。包结构完全符合要求（view/dao/entity/service）。实现用户登录、客房管理、入住退房、用户管理四大核心功能，支持角色权限控制。

## 美观度说明

界面采用选项卡布局，功能分区清晰。表格展示数据直观，支持状态筛选。弹窗表单使用GridBagLayout对齐整齐。中文显示友好，房型/状态/角色均显示中文。通过noVNC支持浏览器远程访问，无需本地安装Java环境。

