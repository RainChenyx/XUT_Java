package OOP1B;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

public class DBUtil_P {
    private static final String URL = "jdbc:mysql://localhost:3306/parking_db?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "123456";

    static {
        try {
            // 1. 连接到MySQL默认数据库，判断parking_db是否存在，不存在则创建
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/information_schema?useSSL=false&serverTimezone=UTC", USER, PASSWORD);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT SCHEMA_NAME FROM SCHEMATA WHERE SCHEMA_NAME = 'parking_db'");
            if (!rs.next()) {
                stmt.executeUpdate("CREATE DATABASE parking_db");
            }
            rs.close();
            stmt.close();
            conn.close();

            // 2. 连接到parking_db，判断表是否存在，不存在则创建
            Connection dbConn = DriverManager.getConnection(URL, USER, PASSWORD);
            Statement dbStmt = dbConn.createStatement();
            // 创建allowed_purposes表
            dbStmt.executeUpdate("CREATE TABLE IF NOT EXISTS allowed_purposes (id INT PRIMARY KEY AUTO_INCREMENT, purpose VARCHAR(50))");
            // 创建parking_spots表
            dbStmt.executeUpdate("CREATE TABLE IF NOT EXISTS parking_spots (id INT PRIMARY KEY AUTO_INCREMENT, license_plate VARCHAR(20), owner_name VARCHAR(50), purpose VARCHAR(50))");

            // 3. 判断allowed_purposes表是否有数据，没有则插入初始数据
            ResultSet rs2 = dbStmt.executeQuery("SELECT COUNT(*) FROM allowed_purposes");
            if (rs2.next() && rs2.getInt(1) == 0) {
                dbStmt.executeUpdate("INSERT INTO allowed_purposes (purpose) VALUES ('餐饮')");
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
