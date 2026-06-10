# 苍穹外卖 (Sky Take-out)

基于 Spring Boot 2.7.3 的外卖点餐平台，包含管理端和用户端（微信小程序）两大模块。

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 2.7.3 | 基础框架 |
| MyBatis | 2.2.0 | ORM |
| Druid | 1.2.1 | 数据库连接池 |
| PageHelper | 1.3.0 | 物理分页 |
| MySQL | 5.7+ | 关系型数据库 |
| Redis | — | 缓存（Spring Cache + RedisTemplate） |
| JWT (jjwt) | 0.9.1 | 管理端/用户端双端认证 |
| Spring Security | — | Argon2 密码哈希 |
| MinIO | 8.6.0 | 文件/图片对象存储 |
| WebSocket | — | 实时消息推送（来单提醒/催单） |
| Knife4j | 3.0.2 | Swagger API 文档 |
| Apache POI | 3.16 | Excel 报表导出 |
| Guava | 32.1.2 | RateLimiter 令牌桶限流 |
| WeChat Pay API v3 | 0.4.8 | 微信支付对接 |
| Aliyun OSS SDK | 3.10.2 | 阿里云 OSS（备用） |
| AspectJ | 1.9.4 | AOP 自动填充审计字段 |

## 模块结构

```
sky-take-out/
├── sky-common/     # 公共模块：常量、异常、工具类、JWT、属性配置、结果封装
├── sky-pojo/       # 领域对象：Entity（11个）、DTO（21个）、VO（17个）
└── sky-server/     # 主应用：Controller、Service、Mapper、配置、拦截器、AOP、WebSocket、定时任务
```

## 核心功能

### 管理端

- **员工管理** — CRUD、启用/禁用、分页查询、密码修改，登录限流（0.5 rps/IP）
- **分类管理** — 菜品分类 & 套餐分类，启用/禁用、排序
- **菜品管理** — CRUD（含多口味）、批量删除、上下架，变更时 Redis 缓存失效
- **套餐管理** — CRUD（含关联菜品）、启用/禁用，Spring Cache 缓存驱逐
- **订单管理** — 订单查询、接单/拒单/取消/派送/完成，实时 WebSocket 提醒
- **数据统计** — 营业额统计、用户统计、订单统计、销量 Top10 排名
- **报表导出** — 基于 Apache POI 模板导出 30 天运营数据 Excel
- **工作台** — 今日营业额、有效订单数、订单完成率、客单价、新增用户数

### 用户端（微信小程序）

- **微信登录** — wx.login 获取 code → openid → JWT 签发，首次登录自动注册
- **菜品浏览** — 按分类查看菜品（Redis 缓存，仅展示已启用的）
- **套餐浏览** — 按分类查看套餐（Spring Cache，仅展示已启用的）
- **购物车** — 添加/减少菜品或套餐、查看购物车、清空购物车
- **下单支付** — 地址校验 + 购物车校验 → 创建订单 → 微信支付
- **订单管理** — 订单历史、取消、再来一单、催单（WebSocket 推送）
- **地址管理** — 收货地址 CRUD，省市区三级联动，默认地址

### 订单生命周期

```
待支付(1) → 待接单(2) → 已接单(3) → 派送中(4) → 已完成(5)
    ↓                      ↓           ↓
  取消(6)               取消(6)      取消(6)
```

### 定时任务

- 待支付订单超过 15 分钟自动取消
- 派送中订单超过 1 小时自动完成

### WebSocket 实时推送

- 用户下单 → 管理端收到新订单提醒（type=1）
- 用户催单 → 管理端收到催单提醒（type=2）

## 快速开始

### 环境要求

- JDK 8+
- Maven 3.6+
- MySQL 5.7+
- Redis
- MinIO

### 配置

项目提供 `application-dev.yml` 和 `application-prod.yml` 两个 profile。

**需要修改的配置项：**

| 配置项 | 说明 |
|--------|------|
| `spring.datasource` | MySQL 连接地址和账号密码 |
| `spring.redis` | Redis 连接地址和密码 |
| `sky.minio` | MinIO 端点、accessKey、secretKey、bucket |
| `sky.wechat` | 微信小程序 AppID 和 Secret |
| `sky.jwt.admin-secret` / `sky.jwt.user-secret` | JWT 签名密钥 |

### 构建运行

```bash
# 打包（跳过测试）
mvn clean package -DskipTests

# 启动
java -jar sky-server/target/sky-server-*.jar --spring.profiles.active=dev
```

默认端口：**8080**

### 访问 API 文档

启动后浏览器打开：`http://localhost:8080/doc.html`

Knife4j 文档分两组：
- **管理端接口** — `/admin/**` 路径下的接口
- **用户端接口** — `/user/**` 路径下的接口

## 认证方式

| 端 | 登录接口 | Token 传递 |
|----|---------|-----------|
| 管理端 | `POST /admin/employee/login` | Header: `token` |
| 用户端 | `POST /user/user/login` | Header: `authentication` |

默认测试账号：`admin` / `ggbondyingxiong`

## 数据库

数据库名：`sky_take_out`，共 11 张表：

| 表名 | 说明 |
|------|------|
| `employee` | 员工 |
| `category` | 分类（菜品分类/套餐分类） |
| `dish` | 菜品 |
| `dish_flavor` | 菜品口味 |
| `setmeal` | 套餐 |
| `setmeal_dish` | 套餐-菜品关联 |
| `orders` | 订单 |
| `order_detail` | 订单明细 |
| `shopping_cart` | 购物车 |
| `user` | 用户 |
| `address_book` | 收货地址 |

## 部署

通过 GitHub Actions 自动部署（`.github/workflows/deploy.yml`）：

1. 推送代码到 `master` 分支触发
2. SCP 源码到远程服务器
3. 远程执行 Maven 打包
4. 停止旧进程，启动新 JAR（prod profile）

敏感信息通过 GitHub Secrets 注入环境变量。

## 项目包结构

```
com.sky
├── SkyApplication.java          # 启动类
├── controller/
│   ├── admin/                   # 管理端控制器（9个）
│   │   ├── EmployeeController   #   员工管理
│   │   ├── CategoryController   #   分类管理
│   │   ├── DishController       #   菜品管理
│   │   ├── SetmealController    #   套餐管理
│   │   ├── OrderController      #   订单管理
│   │   ├── ReportController     #   数据报表
│   │   ├── WorkSpaceController  #   工作台
│   │   ├── ShopController       #   店铺状态
│   │   └── CommonController     #   文件上传
│   └── user/                    # 用户端控制器（9个）
│       ├── UserController       #   用户登录
│       ├── CategoryController   #   分类浏览
│       ├── DishController       #   菜品浏览
│       ├── SetmealController    #   套餐浏览
│       ├── ShoppingCartController # 购物车
│       ├── OrderController      #   订单操作
│       ├── AddressBookController #  地址管理
│       ├── ShopController       #   店铺状态
│       └── PayNotifyController  #   支付回调
├── service/                     # 服务接口 + impl 实现
├── mapper/                      # MyBatis Mapper 接口（11个）
├── config/                      # Spring 配置类
├── interceptor/                 # JWT 拦截器（管理端/用户端）
├── aspect/                      # @AutoFill AOP 自动填充审计字段
├── handler/                     # 全局异常处理器
├── task/                        # 定时任务（订单超时处理）
└── websocket/                   # WebSocket 服务端
```
