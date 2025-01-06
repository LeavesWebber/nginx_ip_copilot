# nginx_ip_copilot
## 功能
### 用户登录模块
userid; userps；  json
#### 实现方式
- [ ] （高级）mysql数据库匹配
- [ ] 维护一个存放账号密码的 json 文件

### GepIP 筛选
发送用户设置的位置代码； json


### 用户自定义 ip 过滤规则
发送的字段：  array，一系列 ipv4 地址



### 用户查询已有的规则
请求返回 array ，前端格式化成 table 给用户

### 用户删除已有规则
发送的字段： ip 的字符串

### （可选功能）调用 BadIP api ，禁止数据库里的高危 ip
返回布尔值 boolen
[badIP]([AbuseIPDB - IP address abuse reports - Making the Internet safer, one IP at a time](https://www.abuseipdb.com/))

## 项目实现
### 前端
vue + js

### 后端
python

#### 1. 维护`block_ips.conf` 文件  

#### 2. 自动给 nginx config 文件导入配置文件

/etc/nginx/conf.d

*.conf
