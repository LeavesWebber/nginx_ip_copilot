# nginx_ip_copilot

> 这是一个 Nginx 工具，使用户能根据地理位置和在线ip黑名单来禁用特定 ip ，以及自己设置需要禁用的 ip。并有友好的 UI 界面

> 本文中的所有“用户”均指网站的管理者，我们设计的系统是一个提供给网站管理者的界面，使得其能够通过`ip:port`访问到这个管理界面并且进行系列操作

## 初步构思

### 功能

#### 用户登录模块

userid; userps；  json

##### 实现方式

方式1（高级）： mysql数据库匹配
方式2（简单）： 维护一个存放账号密码的 json 文件

#### 用户设置配置文件的路径

用户指定使用的网站配置文件路径，比如 `/etc/nginx/conf.d/mypapers.conf`

#### GepIP 筛选

屏蔽来自指定位置 ip 请求的功能，比如禁止某个国家的人访问我的网站
前端发送用户设置的位置代码给后端； json

##### 实现方式

方式1（简单）： 查询 ip 信息的 api：`https://api.vore.top/api/IPdata?ip=输入你要查询的ip地址`
方式2（高级）： 使用 ngx_http_geoip2 模块

#### 用户自定义 ip 过滤规则

前端发送给后端的字段：  array，一系列 ipv4 地址

#### 用户查询已有的规则

前端请求后端返回 array ，后端查询已有规则并且返回给前端，前端格式化成 table 给用户呈现

#### 用户删除已有规则

前端发送给后端的字段： 含有 ip 的字符串

#### （可选）调用 AbuseIPDB api ，禁止数据库里的高危 ip

前端返回给后端布尔值 boolen，后端接收后决定 enable 或者 disable 这个功能
用到的api 网站：[AbuseIPDB](https://www.abuseipdb.com/)

### 细节

#### 前端

##### 技术

- Vue3 + TypeScript
- Element Plus UI 框架
- Axios 处理 API 请求
- Vuex 状态管理

#### 后端

##### 技术

- Python FastAPI
- Mysql 数据库 （可选）
- Redis 缓存 （可选）
- 使用 Docker 容器化部署 （可选）

##### 关键

###### 1. 维护`block_ips.conf` 文件

所有的规则信息可以存在`block_ips.conf`里

###### 2. 自动给 nginx config 文件导入配置文件

通过`include /path/to/block_ips.conf`把文件集成到原先设定的路径（比如`/etc/nginx/conf.d/mypapers.conf`）

# 项目框架

## 进度安排

- 第一阶段：

基础用户认证
Nginx 配置文件管理
简单的 IP 规则管理

- 第二阶段：

GeoIP 功能
IP 规则批量管理
基础数据可视化

- 第三阶段：

高危 IP 防护
高级数据可视化

## 系统架构图

![img.png](./README_IMAGE/img.png)

## 前后端交互

![img_1.png](./README_IMAGE/img_1.png)

## 接口设计

认证相关接口

```
POST   /api/auth/login           # 用户登录
POST   /api/auth/logout          # 用户登出
GET    /api/auth/info            # 获取当前用户信息
```

Nginx配置相关接口

```
GET    /api/nginx/config         # 获取当前配置
POST   /api/nginx/config/path    # 设置nginx配置文件路径
POST   /api/nginx/reload         # 重新加载nginx配置
GET    /api/nginx/status         # 获取nginx运行状态
GET    /api/nginx/backups        # 获取配置备份列表
POST   /api/nginx/backups/restore/{backup_id}  # 恢复指定备份
```

IP规则管理接口

```
GET    /api/ip-rules            # 获取所有IP规则
POST   /api/ip-rules            # 添加新的IP规则
DELETE /api/ip-rules/{rule_id}  # 删除指定IP规则
PUT    /api/ip-rules/{rule_id}  # 修改指定IP规则
POST   /api/ip-rules/batch      # 批量添加IP规则
DELETE /api/ip-rules/batch      # 批量删除IP规则
```

地理位置规则接口

```
GET    /api/geo-rules           # 获取所有地理位置规则
POST   /api/geo-rules           # 添加新的地理位置规则
DELETE /api/geo-rules/{rule_id} # 删除指定地理位置规则
PUT    /api/geo-rules/{rule_id} # 修改指定地理位置规则
```

AbuseIPDB相关接口

```
POST   /api/abuseipdb/enable    # 启用AbuseIPDB功能
POST   /api/abuseipdb/disable   # 禁用AbuseIPDB功能
GET    /api/abuseipdb/status    # 获取AbuseIPDB功能状态
GET    /api/abuseipdb/blacklist # 获取当前AbuseIPDB黑名单
```

系统管理接口

```
GET    /api/system/logs         # 获取操作日志
GET    /api/system/stats        # 获取系统统计信息
```

### 项目结构

```
nginx_ip_copilot/
├── frontend/                         # 前端项目目录
│   ├── src/
│   │   ├── assets/                  # 静态资源
│   │   ├── components/              # Vue组件
│   │   │   ├── Login.vue           # 登录组件
│   │   │   ├── Dashboard.vue       # 主面板
│   │   │   ├── IPRules.vue         # IP规则管理
│   │   │   ├── GeoRules.vue        # 地理位置规则
│   │   │   └── Settings.vue        # 设置页面
│   │   ├── api/                    # API请求
│   │   │   └── index.js
│   │   ├── store/                  # Vuex状态管理
│   │   └── router/                 # Vue路由
│   └── package.json
│
├── backend/                         # 后端项目目录
│   ├── app/
│   │   ├── api/                    # API路由
│   │   │   ├── __init__.py
│   │   │   ├── auth.py            # 认证相关
│   │   │   ├── ip_rules.py        # IP规则相关
│   │   │   └── geo_rules.py       # 地理规则相关
│   │   ├── core/                  # 核心功能
│   │   │   ├── __init__.py
│   │   │   ├── config.py          # 配置管理
│   │   │   ├── security.py        # 安全相关
│   │   │   └── dependencies.py    # 依赖注入
│   │   ├── services/              # 业务逻辑
│   │   │   ├── __init__.py
│   │   │   ├── nginx_service.py   # Nginx配置服务
│   │   │   ├── geoip_service.py   # GeoIP服务
│   │   │   └── abuseipdb_service.py  # AbuseIPDB服务
│   │   ├── models/                # 数据模型
│   │   │   ├── __init__.py
│   │   │   └── schemas.py         # Pydantic模型
│   │   └── utils/                 # 工具函数
│   │       ├── __init__.py
│   │       ├── nginx_parser.py    # Nginx配置解析
│   │       └── validators.py      # 数据验证
│   ├── tests/                     # 测试目录
│   │   ├── __init__.py
│   │   ├── test_api/
│   │   └── test_services/
│   ├── alembic/                   # 数据库迁移
│   ├── logs/                      # 日志目录
│   ├── requirements.txt           # 依赖包
│   └── main.py                    # 入口文件
│
├── nginx/                         # Nginx配置模板
│   ├── templates/
│   │   ├── ip_block.conf.template
│   │   └── geo_block.conf.template
│   └── scripts/
│       └── reload.sh
│
├── docker/                        # Docker配置
│   ├── frontend/
│   │   └── Dockerfile
│   └── backend/
│       └── Dockerfile
│
├── docs/                          # 文档
│   ├── api.md
│   ├── deployment.md
│   └── development.md
│
├── docker-compose.yml            # Docker编排配置
├── .env.example                  # 环境变量示例
├── .gitignore
└── README.md
```

### 数据结构

```json
{
  "ip_rules": [
    {
      "id": "rule_001",
      "ip": "1.2.3.4",
      "type": "single_ip",
      "comment": "手动添加的封禁IP",
      "created_at": "2024-03-20T10:00:00Z",
      "status": "active"
    },
    {
      "id": "rule_002",
      "ip_range": "192.168.1.0/24",
      "type": "ip_range",
      "comment": "封禁整个子网",
      "created_at": "2024-03-20T10:00:00Z",
      "status": "active"
    }
  ],
  "geo_rules": [
    {
      "id": "geo_001",
      "country_code": "XX",
      "comment": "封禁某个国家",
      "created_at": "2024-03-20T10:00:00Z",
      "status": "active"
    }
  ]
}
```
