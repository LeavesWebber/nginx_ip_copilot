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
先确认后端backend文件夹下pom.xml中的依赖导入没问题。
### 2.2 数据库
可用Navicat Premium等相关数据库管理工具。  
mysql数据库连接，连接名称随意，主机localhost，端口3306，用户名root，密码123。  
连接后创建数据库（名为backend，字符集选择了utf8mb4，排序规则选择了utf8mb4_general_ci）。  
### 2.3 运行
运行的Java类是backend/src/main/java/org/example/backend/BackendApplication.java。  
运行后，将监听本地的8088端口，等待并处理来自该端口的HTTP请求。  
首次运行会在自动数据库中建表。
