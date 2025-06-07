package OOP1B;

import javax.swing.*;
import java.sql.*;

public class DBUtil_AC {
    private static final String URL = "jdbc:mysql://localhost:3306/accommodation_db?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "123456";

    static {
        try {
            // 1. 连接到MySQL默认数据库，判断accommodation_db是否存在，不存在则创建
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/information_schema?useSSL=false&serverTimezone=UTC", USER, PASSWORD);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT SCHEMA_NAME FROM SCHEMATA WHERE SCHEMA_NAME = 'accommodation_db'");
            if (!rs.next()) {
                stmt.executeUpdate("CREATE DATABASE accommodation_db");
            }
            rs.close();
            stmt.close();
            conn.close();

            // 2. 连接到accommodation_db，判断表是否存在，不存在则创建
            Connection dbConn = DriverManager.getConnection(URL, USER, PASSWORD);
            Statement dbStmt = dbConn.createStatement();
            // 创建rooms表
            dbStmt.executeUpdate("CREATE TABLE IF NOT EXISTS rooms (id INT PRIMARY KEY AUTO_INCREMENT, type VARCHAR(255), price DOUBLE, quantity INT)");
            // 创建reservations表
            dbStmt.executeUpdate("CREATE TABLE IF NOT EXISTS reservations (id INT PRIMARY KEY AUTO_INCREMENT, contact_name VARCHAR(255), room_type VARCHAR(255), days INT, check_in_date DATE, companions TEXT, total_price DOUBLE)");

            // 3. 判断rooms表是否有数据，没有则插入初始数据
            ResultSet rs2 = dbStmt.executeQuery("SELECT COUNT(*) FROM rooms");
            if (rs2.next() && rs2.getInt(1) == 0) {
                dbStmt.executeUpdate("INSERT INTO rooms (type, price, quantity) VALUES ('单人间', 100, 5)");
                dbStmt.executeUpdate("INSERT INTO rooms (type, price, quantity) VALUES ('双人间', 200, 3)");
                dbStmt.executeUpdate("INSERT INTO rooms (type, price, quantity) VALUES ('套房', 300, 2)");
            }
            rs2.close();
            dbStmt.close();
            dbConn.close();
        } catch (Exception e) {
            throw new RuntimeException("数据库初始化失败", e);
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
}