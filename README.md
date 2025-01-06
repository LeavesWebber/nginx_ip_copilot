# nginx_ip_copilot

![](https://kiss1314.top:5555/d/webImage/20250106105220.png)

---
> 这是一个 Nginx 工具，使用户能根据地理位置和在线ip黑名单来禁用特定 ip ，并有友好的 UI 界面

> 本文中的所有“用户”均指网站的管理者，我们设计的系统是一个提供给网站管理者的界面 

## 功能
### 用户登录模块
userid; userps；  json
#### 实现方式
方式1（高级）： mysql数据库匹配
方式2（简单）： 维护一个存放账号密码的 json 文件

### GepIP 筛选  
屏蔽来自指定位置 ip 请求的功能，比如禁止某个国家的人访问我的网站
前端发送用户设置的位置代码给后端； json

#### 实现方式

方式1（简单）： 查询 ip 信息的 api：`https://api.vore.top/api/IPdata?ip=输入你要查询的ip地址`
方式2（高级）： 使用 ngx_http_geoip2 模块

### 用户自定义 ip 过滤规则
前端发送给后端的字段：  array，一系列 ipv4 地址

### 用户查询已有的规则
前端请求后端返回 array ，后端查询已有规则并且返回给前端，前端格式化成 table 给用户呈现

### 用户删除已有规则
前端发送给后端的字段： 含有 ip 的字符串

### （可选功能）调用 AbuseIPDB api ，禁止数据库里的高危 ip
前端返回给后端布尔值 boolen，后端接收后决定 enable 或者 disable 这个功能  
用到的api 网站：[AbuseIPDB](https://www.abuseipdb.com/)  


## 项目实现
### 前端


### 后端


#### 1. 维护`block_ips.conf` 文件  

#### 2. 自动给 nginx config 文件导入配置文件


