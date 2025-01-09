# nginx_ip_copilot

> 这是一个 Nginx 工具，使用户能根据地理位置和在线ip黑名单来禁用特定 ip ，以及自己设置需要禁用的 ip。并有友好的 UI 界面

> 本文中的所有“用户”均指网站的管理者，我们设计的系统是一个提供给网站管理者的界面，使得其能够通过`ip:port`访问到这个管理界面并且进行系列操作

# 项目使用方法
## 1. 启动前端
### 1.1 首先进入前端目录
```bash
cd frontend
```

### 1.2 安装所需要的包
```bash
npm install
```
**提示：前置条件**  
项目需要 node 版本为 `22.04LTS`，你可以使用 `node -v` 查看当前版本。  
若你不知道啥是 node 或者不懂怎么切换版本，参考  
[我写的这篇教程](https://kiss1314.top/a9272d17422f/)

### 1.3 启动开发服务器（我用了内置的 vite）
```bash
npm run dev
```

### 1.4 一些可能啰嗦的说明
由于还没有对接后端，又不想把用户名和密码硬编码在程序里（这样做后患无穷）  
所以我使用了 mock 模拟了后端的通信  
比如用户名和密码，暂时都是 `admin`。  
你可以在 [frontend/src/api/mock.ts](frontend/src/api/mock.ts) 里修改这些东西。

## 2. 启动后端
### 2.1 依赖导入
先安装好 [pom.xml](/backend/pom.xml) 中的依赖项

### 2.2 数据库
请手动创建名称为 `backend` 的 mysql 数据库，首次运行会在自动数据库中建表。  
后端端口，数据库名，数据库账号和密码都可以在 [application.properties](backend/src/main/resources/application.properties) 中手动输入

### 2.3 运行
运行的 Java 入口程序是 [BackendApplication.java](backend/src/main/java/org/example/backend/BackendApplication.java)  
运行后，将监听本地的8088端口，等待并处理来自该端口的HTTP请求。


## 项目构思

### 功能

#### 用户登录模块
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

- java spring boot
- Mysql 数据库 （可选）


##### 关键

###### 1. 维护`block_ips.conf` 文件

所有的规则信息可以存在`block_ips.conf`里

###### 2. 自动给 nginx config 文件导入配置文件

通过`include /path/to/block_ips.conf`把文件集成到原先设定的路径（比如`/etc/nginx/conf.d/mypapers.conf`）

# 项目框架

## 系统架构图

![img.png](./README_IMAGE/img.png)

## 前后端交互

![img_1.png](./README_IMAGE/img_1.png)


