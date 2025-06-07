package OOP1B;

import javax.swing.*;
import java.sql.*;

public class DBUtil_AD {
    private static final String URL = "jdbc:mysql://localhost:3306/hotel_management?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "123456";
    static {
        try {
            // 1. 连接到MySQL默认数据库，判断hotel_management是否存在，不存在则创建
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/information_schema?useSSL=false&serverTimezone=UTC", USER, PASSWORD);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT SCHEMA_NAME FROM SCHEMATA WHERE SCHEMA_NAME = 'hotel_management'");
            if (!rs.next()) {
                stmt.executeUpdate("CREATE DATABASE hotel_management");
            }
            rs.close();
            stmt.close();
            conn.close();

            // 2. 连接到hotel_management，判断表是否存在，不存在则创建
            Connection dbConn = DriverManager.getConnection(URL, USER, PASSWORD);
            Statement dbStmt = dbConn.createStatement();
            // 创建staff表
            dbStmt.executeUpdate("CREATE TABLE IF NOT EXISTS staff (id INT PRIMARY KEY AUTO_INCREMENT, name VARCHAR(100), position VARCHAR(50))");

            // 3. 判断staff表是否有数据，没有则插入初始数据
            ResultSet rs2 = dbStmt.executeQuery("SELECT COUNT(*) FROM staff");
            if (rs2.next() && rs2.getInt(1) == 0) {
                dbStmt.executeUpdate("INSERT INTO staff (name, position) VALUES ('张三', '客房服务人员')");
                dbStmt.executeUpdate("INSERT INTO staff (name, position) VALUES ('李庚洋', '前台服务员')");
                dbStmt.executeUpdate("INSERT INTO staff (name, position) VALUES ('陈曦', '门童')");
                dbStmt.executeUpdate("INSERT INTO staff (name, position) VALUES ('孙博能', '引导员')");
                dbStmt.executeUpdate("INSERT INTO staff (name, position) VALUES ('王鹤霖', '大厅经理')");
                dbStmt.executeUpdate("INSERT INTO staff (name, position) VALUES ('孙铭阳', '其他管理人员')");
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
