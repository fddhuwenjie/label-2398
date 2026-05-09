# 宾馆客房管理系统

基于 **Java Swing + JDBC + 面向对象** 架构的桌面应用程序。

## 技术选型

- **界面层**：Java Swing
- **数据访问**：JDBC（原生SQL操作）
- **数据库**：MySQL 8.0
- **构建工具**：Maven

## 项目结构

```
hotel-swing/
├── pom.xml                                    # Maven配置
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

## 功能模块

1. **用户登录**：支持管理员和普通用户登录
2. **客房管理**：客房的增删改查、状态筛选
3. **入住/退房**：入住登记、退房结算、订单查询
4. **用户管理**：用户的增删改查（仅管理员可见）

## 运行步骤

### 1. 初始化数据库

```sql
-- 执行 src/main/resources/sql/init.sql
mysql -u root -p < src/main/resources/sql/init.sql
```

### 2. 配置数据库连接

编辑 `src/main/resources/db.properties`：

```properties
db.url=jdbc:mysql://localhost:3306/hotel_db?useSSL=false&serverTimezone=Asia/Shanghai
db.username=root
db.password=your_password
db.driver=com.mysql.cj.jdbc.Driver
```

### 3. 编译运行

```bash
cd hotel-swing
mvn clean compile exec:java -Dexec.mainClass="com.hotel.HotelApplication"
```

或打包后运行：

```bash
mvn clean package
java -jar target/hotel-management-1.0.0.jar
```

## 默认账号

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | admin123 | 管理员 |
| user | user123 | 普通用户 |
