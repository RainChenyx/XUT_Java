-- 创建数据库
CREATE DATABASE IF NOT EXISTS goban;

-- 使用数据库
USE goban;

-- 创建用户表
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 插入初始用户数据
INSERT INTO users (username, password) VALUES ('1', '1');
INSERT INTO users (username, password) VALUES ('2', '2'); 