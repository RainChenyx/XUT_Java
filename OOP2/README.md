# 五子棋游戏 MySQL 数据库配置指南

本项目支持使用MySQL数据库存储用户信息。以下是配置步骤：

## 新特性

### 1. 自动端口选择
- 服务器现在会自动查找可用端口(8888-8897)
- 客户端会自动尝试连接这些端口
- 避免了"Address already in use: bind"错误

### 2. 增强的MySQL驱动加载
- 支持多种驱动加载方式，提高兼容性
- 自动检测lib目录中的MySQL连接器
- 提供详细的错误诊断信息

### 3. 便捷启动脚本
- `start_server.bat` - 启动服务器，自动设置正确的类路径
- `start_client.bat` - 启动客户端，无需手动配置

## 准备工作

### 1. 安装MySQL数据库

如果您还没有安装MySQL，请先从以下网址下载并安装：
https://dev.mysql.com/downloads/mysql/

安装时请记住您设置的root用户密码，稍后需要使用。

### 2. 添加MySQL连接器

项目需要MySQL JDBC驱动才能连接到数据库：

1. 下载MySQL连接器：
   - 访问：https://dev.mysql.com/downloads/connector/j/
   - 选择"Platform Independent"，下载ZIP文件
   - 解压后找到`mysql-connector-j-8.0.xx.jar`文件

2. 将JAR文件放入项目：
   - 在项目目录创建`lib`文件夹（如果不存在）：`E:\java项目\Goban\OOP\lib\`
   - 将下载的JAR文件复制到这个目录

3. 在IDEA中添加依赖：
   - 打开项目
   - File → Project Structure → Libraries
   - 点击"+"按钮，选择"Java"
   - 浏览并选择刚才复制的JAR文件
   - 点击"OK"→"Apply"→"OK"

### 3. 配置数据库连接信息

打开`OOP/src/Gobang/DBUtil.java`文件，修改以下连接信息：

```java
private static final String URL = "jdbc:mysql://localhost:3306/?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
private static final String USER = "root";           // 修改为您的MySQL用户名
private static final String PASSWORD = "123456";     // 修改为您的MySQL密码
```

## 创建数据库和表

程序会自动创建所需的数据库和表，但您也可以手动执行SQL脚本：

1. 打开MySQL命令行或任何MySQL客户端工具
2. 执行`OOP/src/Gobang/goban_user.sql`中的SQL语句

## 运行程序

### 使用启动脚本（推荐）

1. 确保MySQL服务已启动
2. 双击`start_server.bat`启动服务器
3. 双击`start_client.bat`启动客户端

### 使用IDE

1. 确保MySQL服务已启动
2. 先运行服务器：`GobangServer.java`
3. 再运行客户端：`GobangClient.java`

## 故障排除

如果遇到数据库连接问题：

1. **MySQL驱动加载失败**
   - 检查lib目录中是否有mysql-connector-j-8.0.xx.jar文件
   - 确认JAR已在IDEA中添加为依赖
   - 使用启动脚本`start_server.bat`自动设置类路径

2. **无法连接到数据库**
   - 确认MySQL服务是否已启动
   - 检查用户名和密码是否正确
   - 检查MySQL服务器是否接受TCP/IP连接

3. **端口被占用**
   - 服务器现在会自动尝试8888-8897端口范围
   - 如果所有端口都被占用，请检查是否有多个GobangServer实例运行
   - 或在`GobangServer.java`中修改DEFAULT_PORT和MAX_PORT_ATTEMPTS

## 降级到内存模式

如果数据库不可用，程序会自动降级为内存模式运行，用户数据将保存在服务器内存中，重启服务器后数据会丢失。 