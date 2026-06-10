# 苍穹外卖 (Sky Take-out)

基于 Spring Boot 的外卖点餐平台，包含管理后台和用户端（微信小程序）两大模块。

## 技术栈

| 技术 | 说明 |
|------|------|
| Spring Boot 2.7.3 | 基础框架 |
| MyBatis + PageHelper | ORM + 分页 |
| MySQL | 关系型数据库 |
| Redis | 缓存（Spring Cache + RedisTemplate） |
| JWT (jjwt) | 双端认证（管理端/用户端） |
| Spring Security | Argon2 密码哈希 |
| MinIO | 文件/图片存储 |
| WebSocket | 实时消息推送（来单提醒/催单） |
| Knife4j (Swagger) | API 文档 |
| Apache POI | Excel 报表导出 |
| Guava RateLimiter | 接口限流 |
| Maven | 构建工具 |

## 模块结构

```
sky-take-out/
├── sky-common/     # 公共模块：常量、异常、工具类、JWT、结果封装
├── sky-pojo/       # 领域对象：Entity、DTO、VO
└── sky-server/     # 主应用：Controller、Service、Mapper、配置、拦截器、AOP、WebSocket
```

## 核心功能

### 管理端

- **员工管理**：CRUD、启用/禁用、分页查询、密码修改
- **分类管理**：菜品分类 & 套餐分类，启用/禁用、排序
- **菜品管理**：菜品 CRUD（含口味）、批量删除、上下架，变更加缓存失效
- **套餐管理**：套餐 CRUD，Spring Cache 缓存驱逐
- **订单管理**：订单查询、接单/拒单/取消/派送/完成，实时 WebSocket 提醒
- **数据统计**：营业额/用户/订单统计，销量 Top10，Excel 报表导出
- **工作台**：今日运营数据概览

### 用户端

- **微信登录**：基于微信 OAuth 的授权登录
- **浏览菜品**：按分类查看菜品/套餐（Redis 缓存）
- **购物车**：添加/删除菜品或套餐
- **下单支付**：提交订单，微信支付对接
- **订单管理**：订单历史、取消、再来一单
- **地址管理**：收货地址 CRUD，省市区联动

### 定时任务

- 待支付订单超时 15 分钟自动取消
- 派送中订单 1 小时后自动完成

### 实时推送

- WebSocket 向管理端推送新订单通知和催单提醒

## 快速开始

### 环境要求

- JDK 8+
- Maven 3.6+
- MySQL 5.7+
- Redis
- MinIO

### 配置文件

项目有 `application-dev.yml` 和 `application-prod.yml` 两个 profile，需配置：

- 数据库连接和账号
- Redis 连接和密码
- MinIO 地址和访问凭证
- 微信小程序 AppID / Secret

### 构建运行

```bash
# 打包
mvn clean package -DskipTests

# 运行（sky-server/target/ 目录下）
java -jar sky-server-*.jar --spring.profiles.active=dev
```

默认端口：`8080`
API 文档：`http://localhost:8080/doc.html`

## API 文档

启动后访问 Knife4j 文档页面：

- 管理端接口：`/doc.html`（分组：管理端接口）
- 用户端接口：`/doc.html`（分组：用户端接口）

## 数据库

数据库名 `sky_take_out`，主要数据表：

| 表名 | 说明 |
|------|------|
| employee | 员工 |
| category | 分类 |
| dish / dish_flavor | 菜品/口味 |
| setmeal / setmeal_dish | 套餐/套餐菜品关联 |
| orders / order_detail | 订单/订单明细 |
| shopping_cart | 购物车 |
| user | 用户 |
| address_book | 地址簿 |

## 部署

通过 GitHub Actions 自动部署，push 到 `master` 分支触发：

1. 源码 SCP 到远程服务器
2. 服务器端执行 Maven 打包
3. 杀掉旧进程后启动新 JAR

