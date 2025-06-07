package Gobang;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.*;

/**
 * 数据库连接工具类
 */
public class DBUtil {
    // 数据库连接信息
    private static final String URL = "jdbc:mysql://localhost:3306/gobang?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "123456"; // 根据实际情况修改密码
    
    // 数据库名称
    private static final String DB_NAME = "goban";
    
    // 驱动加载标志
    private static boolean driverLoaded = false;
    
    static {
        try {
            // 尝试多种方式加载驱动
            tryLoadDriver();
        } catch (Exception e) {
            System.out.println("驱动加载过程中发生异常：" + e.getMessage());
            e.printStackTrace();
            driverLoaded = false;
        }
    }
    
    /**
     * 尝试多种方式加载MySQL驱动
     */
    private static void tryLoadDriver() {
        // 方法1：直接通过Class.forName加载
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            driverLoaded = true;
            System.out.println("MySQL驱动加载成功 (方法1)");
            return;
        } catch (ClassNotFoundException e) {
            System.out.println("方法1加载MySQL驱动失败：" + e.getMessage());
        }
        
        // 方法2：尝试从lib目录动态加载
        try {
            File libDir = new File("OOP/lib");
            if (!libDir.exists() || !libDir.isDirectory()) {
                // 尝试相对于当前工作目录
                libDir = new File("lib");
            }
            
            if (libDir.exists() && libDir.isDirectory()) {
                for (File file : libDir.listFiles()) {
                    if (file.getName().contains("mysql-connector") && file.getName().endsWith(".jar")) {
                        System.out.println("尝试从JAR加载MySQL驱动: " + file.getAbsolutePath());
                        try {
                            URL jarUrl = file.toURI().toURL();
                            URLClassLoader urlClassLoader = new URLClassLoader(
                                    new URL[]{jarUrl},
                                    DBUtil.class.getClassLoader()
                            );
                            Class.forName("com.mysql.cj.jdbc.Driver", true, urlClassLoader);
                            driverLoaded = true;
                            System.out.println("MySQL驱动加载成功 (方法2)");
                            return;
                        } catch (Exception ex) {
                            System.out.println("方法2无法加载JAR中的驱动: " + ex.getMessage());
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("方法2加载驱动时发生错误：" + e.getMessage());
        }
        
        // 方法3：使用服务提供者机制（JDBC4+自动加载）
        try {
            // 尝试直接获取连接，JDBC 4.0会自动尝试加载驱动
            Connection testConn = null;
            try {
                testConn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/test?useSSL=false&connectTimeout=1000", 
                        USER, PASSWORD);
                driverLoaded = true;
                System.out.println("MySQL驱动加载成功 (方法3)");
            } catch (SQLException se) {
                // 检查是否是驱动问题还是连接问题
                if (se.getMessage().contains("No suitable driver")) {
                    System.out.println("方法3无法找到MySQL驱动");
                } else {
                    // 如果是其他SQL异常，说明驱动可能已加载，但连接测试失败
                    driverLoaded = true;
                    System.out.println("MySQL驱动可能已加载，但连接测试失败: " + se.getMessage());
                }
            } finally {
                if (testConn != null) {
                    testConn.close();
                }
            }
        } catch (Exception e) {
            System.out.println("方法3尝试加载驱动时发生错误：" + e.getMessage());
        }
        
        // 最终结果
        if (!driverLoaded) {
            System.out.println("所有加载MySQL驱动的方法都失败，请检查环境配置");
            System.out.println("请确保mysql-connector-j.jar已添加到项目中");
            System.out.println("建议通过命令行启动时使用 -cp 或 -classpath 参数指定JAR位置");
        }
    }
    
    /**
     * 打印类路径信息，用于调试
     */
    public static void printClasspathInfo() {
        try {
            System.out.println("MySQL驱动版本检查:");
            try {
                Class<?> driverClass = Class.forName("com.mysql.cj.jdbc.Driver");
                System.out.println("成功加载MySQL驱动类: " + driverClass.getName());
                System.out.println("驱动类加载器: " + driverClass.getClassLoader());
                
                // 获取驱动版本信息
                try {
                    Driver driver = (Driver) driverClass.getDeclaredConstructor().newInstance();
                    System.out.println("驱动版本: " + driver.getMajorVersion() + "." + driver.getMinorVersion());
                } catch (Exception e) {
                    System.out.println("无法获取驱动版本: " + e.getMessage());
                }
            } catch (ClassNotFoundException e) {
                System.out.println("找不到MySQL驱动类");
            }
            
            System.out.println("\n类路径信息:");
            String classpath = System.getProperty("java.class.path");
            String[] paths = classpath.split(System.getProperty("path.separator"));
            for (String path : paths) {
                System.out.println(path);
            }
            
            // 检查lib目录
            File libDir = new File("OOP/lib");
            if (!libDir.exists() || !libDir.isDirectory()) {
                libDir = new File("lib"); // 尝试相对路径
            }
            
            if (libDir.exists() && libDir.isDirectory()) {
                System.out.println("\nlib目录文件列表:");
                File[] files = libDir.listFiles();
                if (files != null && files.length > 0) {
                    for (File file : files) {
                        System.out.println(file.getAbsolutePath() + " (" + (file.length() / 1024) + "KB)");
                    }
                } else {
                    System.out.println("lib目录为空");
                }
            } else {
                System.out.println("\nlib目录不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 检查数据库连接是否可用
     */
    public static boolean isConnected() {
        if (!driverLoaded) {
            System.out.println("驱动未加载，无法连接数据库");
            return false;
        }
        
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            if (conn != null) {
                System.out.println("成功连接到MySQL服务器");
                return true;
            } else {
                System.out.println("连接创建失败，但未抛出异常");
                return false;
            }
        } catch (SQLException e) {
            System.out.println("无法连接到MySQL服务器: " + e.getMessage());
            return false;
        } finally {
            close(conn, null, null);
        }
    }
    
    /**
     * 获取数据库连接
     */
    public static Connection getConnection() {
        if (!driverLoaded) {
            System.out.println("驱动未加载，无法获取连接");
            return null; // 驱动未加载，直接返回null
        }
        
        try {
            // 建立与MySQL服务器的连接
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            
            // 检查goban数据库是否存在，不存在则创建
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
                System.out.println("数据库创建或已存在: " + DB_NAME);
            }
            
            // 切换到goban数据库
            conn.setCatalog(DB_NAME);
            return conn;
        } catch (SQLException e) {
            System.out.println("数据库连接失败：" + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 关闭数据库资源
     */
    public static void close(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 初始化数据库和表
     * 如果数据库或表不存在，则创建
     */
    public static boolean initDatabase() {
        if (!driverLoaded) {
            System.out.println("驱动未加载，无法初始化数据库");
            return false;
        }
        
        Connection conn = null;
        Statement stmt = null;
        
        try {
            // 获取到已切换到goban数据库的连接
            conn = getConnection();
            if (conn == null) {
                System.out.println("无法连接到数据库，初始化失败");
                return false;
            }
            
            stmt = conn.createStatement();
            
            // 创建用户表
            String createTableSQL = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "username VARCHAR(50) NOT NULL UNIQUE," +
                    "password VARCHAR(50) NOT NULL," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
            stmt.execute(createTableSQL);
            System.out.println("用户表创建或已存在");
            
            // 检查是否已有初始用户，如果没有则添加
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM gobang.users");
            if (rs.next() && rs.getInt(1) == 0) {
                // 添加初始用户
                String insertUserSQL = "INSERT INTO gobang.users (username, password) VALUES ('1', '1'), ('2', '2')";
                stmt.execute(insertUserSQL);
                System.out.println("已创建初始用户");
            } else {
                System.out.println("初始用户已存在，无需创建");
            }
            
            return true;
        } catch (SQLException e) {
            System.out.println("初始化数据库失败：" + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            close(conn, stmt, null);
        }
    }
    
    /**
     * 用户注册
     */
    public static boolean register(String username, String password) {
        if (!driverLoaded) {
            System.out.println("驱动未加载，无法注册用户");
            return false; // 驱动未加载，直接返回false
        }
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = getConnection();
            if (conn == null) {
                System.out.println("无法连接到数据库，注册失败");
                return false;
            }
            
            String sql = "INSERT INTO gobang.users (username, password) VALUES (?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            int result = pstmt.executeUpdate();
            if (result > 0) {
                System.out.println("用户注册成功: " + username);
                return true;
            } else {
                System.out.println("用户注册失败: " + username);
                return false;
            }
        } catch (SQLException e) {
            System.out.println("注册用户失败：" + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            close(conn, pstmt, null);
        }
    }
    
    /**
     * 用户登录验证
     */
    public static boolean login(String username, String password) {
        if (!driverLoaded) {
            System.out.println("驱动未加载，无法验证登录");
            return false; // 驱动未加载，直接返回false
        }
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            if (conn == null) {
                System.out.println("无法连接到数据库，登录验证失败");
                return false;
            }
            
            String sql = "SELECT * FROM gobang.users WHERE username = ? AND password = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            rs = pstmt.executeQuery();
            boolean result = rs.next();
            if (result) {
                System.out.println("用户登录成功: " + username);
            } else {
                System.out.println("用户登录失败: " + username);
            }
            return result;
        } catch (SQLException e) {
            System.out.println("验证用户失败：" + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            close(conn, pstmt, rs);
        }
    }
    
    /**
     * 检查用户名是否存在
     */
    public static boolean isUsernameExists(String username) {
        if (!driverLoaded) {
            System.out.println("驱动未加载，无法检查用户名");
            return false; // 驱动未加载，直接返回false
        }
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            if (conn == null) {
                System.out.println("无法连接到数据库，检查用户名失败");
                return false;
            }
            
            String sql = "SELECT * FROM gobang.users WHERE username = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            rs = pstmt.executeQuery();
            return rs.next(); // 如果有记录返回true，否则返回false
        } catch (SQLException e) {
            System.out.println("检查用户名失败：" + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            close(conn, pstmt, rs);
        }
    }
} 