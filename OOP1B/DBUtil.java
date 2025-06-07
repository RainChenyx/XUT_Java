package OOP1B;

import javax.swing.*;
import java.sql.*;

public class DBUtil {
    private static final String URL = "jdbc:mysql://localhost:3306/restaurant_db?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "123456";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL 驱动加载失败", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "数据库连接失败，请检查是否启动MySql或者账号密码是否正确\n" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            throw e;
        }
    }

    public static void initDatabaseAndTable() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/?useSSL=false&serverTimezone=UTC", USER, PASSWORD);
             Statement stmt = conn.createStatement()) {
            // 创建数据库
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS restaurant_db DEFAULT CHARACTER SET utf8mb4");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "数据库创建失败: " + e.getMessage());
        }

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            // 创建表
            String createTableSQL = "CREATE TABLE IF NOT EXISTS menu (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT," +
                    "name VARCHAR(100)," +
                    "price DECIMAL(10,2)," +
                    "sell_count INT)";
            stmt.executeUpdate(createTableSQL);

            // 检查表是否有数据
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM menu");
            rs.next();
            int count = rs.getInt(1);
            if (count == 0) {
                // 插入初始数据
                String[] initData = {
                    "INSERT INTO menu (name, price, sell_count) VALUES ('四味小吃拼', 39.90, 0)",
                    "INSERT INTO menu (name, price, sell_count) VALUES ('全家桶', 98.00, 0)",
                    "INSERT INTO menu (name, price, sell_count) VALUES ('香辣鸡翅', 12.90, 0)",
                    "INSERT INTO menu (name, price, sell_count) VALUES ('黄金鸡块', 14.00, 0)",
                    "INSERT INTO menu (name, price, sell_count) VALUES ('芝士土豆泥', 9.99, 0)",
                    "INSERT INTO menu (name, price, sell_count) VALUES ('香辣鸡腿堡', 20.90, 0)",
                    "INSERT INTO menu (name, price, sell_count) VALUES ('新奥尔良鸡', 21.50, 0)",
                    "INSERT INTO menu (name, price, sell_count) VALUES ('拿铁', 11.00, 0)"
                };
                for (String sql : initData) {
                    stmt.executeUpdate(sql);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "表格创建或数据插入失败: " + e.getMessage());
        }
    }
}