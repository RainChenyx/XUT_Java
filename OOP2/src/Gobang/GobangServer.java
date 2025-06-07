package Gobang;

import java.io.*;
import java.net.*;
import java.util.*;

public class GobangServer {
    // 默认端口和备选端口范围
    private static final int DEFAULT_PORT = 8888;
    private static final int MAX_PORT_ATTEMPTS = 10; // 最多尝试10个端口
    
    // 用户信息存储在内存中（作为备用）
    private static Map<String, String> users = new HashMap<>();
    private static List<Room> rooms = new ArrayList<>();
    private static List<ClientHandler> clients = new ArrayList<>();
    // 数据库是否可用标志
    private static boolean dbAvailable = false;
    // 当前使用的端口
    private static int currentPort = DEFAULT_PORT;

    public static void main(String[] args) throws IOException {
        // 添加lib目录到类路径（尝试动态加载）
        try {
            File libDir = new File("OOP/lib");
            if (!libDir.exists() || !libDir.isDirectory()) {
                // 尝试相对于当前工作目录的路径
                libDir = new File("lib");
            }
            
            if (libDir.exists() && libDir.isDirectory()) {
                for (File file : libDir.listFiles()) {
                    if (file.getName().endsWith(".jar")) {
                        System.out.println("尝试加载JAR文件: " + file.getAbsolutePath());
                        try {
                            // 这只是输出信息，实际加载仍依赖于启动时的类路径
                            // 纯粹为了调试目的
                        } catch (Exception e) {
                            System.out.println("无法加载JAR: " + e.getMessage());
                        }
                    }
                }
            } else {
                System.out.println("找不到lib目录，请确保路径正确");
            }
        } catch (Exception e) {
            System.out.println("尝试加载lib目录时出错: " + e.getMessage());
        }
        
        // 打印类路径信息，用于调试
        DBUtil.printClasspathInfo();
        
        // 初始化内存用户和房间
        users.put("1", "1");
        users.put("2", "2");
        for (int i = 0; i < 5; i++) rooms.add(new Room(i + 1));
        
        // 初始化数据库
        try {
            System.out.println("正在检查MySQL数据库连接...");
            if (DBUtil.isConnected()) {
                System.out.println("MySQL服务器连接成功，正在初始化数据库...");
                
                // 初始化数据库和表结构
                if (DBUtil.initDatabase()) {
                    dbAvailable = true;
                    System.out.println("数据库初始化成功，将使用数据库模式");
                } else {
                    dbAvailable = false;
                    System.out.println("数据库初始化失败，将使用内存模式");
                }
            } else {
                dbAvailable = false;
                System.out.println("无法连接到MySQL服务器，将使用内存模式");
            }
        } catch (Exception e) {
            dbAvailable = false;
            System.out.println("数据库初始化过程中发生异常：" + e.getMessage());
            e.printStackTrace();
            System.out.println("将使用内存模式继续运行");
        }

        // 启动服务器，尝试多个端口
        ServerSocket serverSocket = null;
        boolean started = false;
        
        for (int portAttempt = 0; portAttempt < MAX_PORT_ATTEMPTS && !started; portAttempt++) {
            currentPort = DEFAULT_PORT + portAttempt;
            try {
                serverSocket = new ServerSocket(currentPort);
                started = true;
                System.out.println("Gobang服务器已启动，端口：" + currentPort);
                System.out.println("使用" + (dbAvailable ? "数据库" : "内存") + "模式存储用户信息");
            } catch (BindException e) {
                System.out.println("端口 " + currentPort + " 已被占用，尝试下一个端口...");
            } catch (IOException e) {
                System.out.println("在端口 " + currentPort + " 启动服务器时出错: " + e.getMessage());
            }
        }
        
        if (!started) {
            System.out.println("无法找到可用端口，服务器启动失败");
            System.out.println("请检查网络配置或等待一段时间后再试");
            throw new IOException("所有端口尝试均失败");
        }
        
        // 服务器主循环
        try {
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    System.out.println("新客户端连接: " + socket.getInetAddress().getHostAddress());
                    
                    ClientHandler handler = new ClientHandler(socket);
                    clients.add(handler);
                    new Thread(handler).start();
                } catch (IOException e) {
                    System.out.println("接受客户端连接时发生错误: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println("服务器运行时发生错误: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (serverSocket != null && !serverSocket.isClosed()) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    // 忽略关闭时的错误
                }
            }
        }
    }

    // 获取当前端口号（供客户端调用）
    public static int getCurrentPort() {
        return currentPort;
    }

    static class Room {
        int id;
        ClientHandler player1, player2;
        int[][] board = new int[15][15];
        boolean isBlackTurn = true;
        boolean gameOver = false;

        Room(int id) { this.id = id; }

        // 判定五子连珠
        boolean checkWin(int y, int x, int color) {
            int[][] board = this.board;
            int[] dx = {1, 0, 1, 1};
            int[] dy = {0, 1, 1, -1};
            for (int d = 0; d < 4; d++) {
                int cnt = 1;
                for (int k = 1; k < 5; k++) {
                    int ny = y + dy[d] * k, nx = x + dx[d] * k;
                    if (ny < 0 || ny >= 15 || nx < 0 || nx >= 15 || board[ny][nx] != color) break;
                    cnt++;
                }
                for (int k = 1; k < 5; k++) {
                    int ny = y - dy[d] * k, nx = x - dx[d] * k;
                    if (ny < 0 || ny >= 15 || nx < 0 || nx >= 15 || board[ny][nx] != color) break;
                    cnt++;
                }
                if (cnt >= 5) return true;
            }
            return false;
        }
    }

    static class ClientHandler implements Runnable {
        Socket socket;
        PrintWriter out;
        BufferedReader in;
        String username;
        Room room;

        ClientHandler(Socket socket) throws IOException {
            this.socket = socket;
            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }

        public void run() {
            try {
                System.out.println("客户端处理线程启动: " + socket.getInetAddress().getHostAddress());
                
                while (true) {
                    String line = in.readLine();
                    if (line == null) break;
                    
                    System.out.println("收到客户端消息: " + line);
                    
                    String[] parts = line.split(" ", 2);
                    String cmd = parts[0];
                    String data = parts.length > 1 ? parts[1] : "";

                    if ("LOGIN".equals(cmd)) {
                        String[] arr = data.split(" ");
                        boolean loginSuccess = false;
                        
                        if (arr.length != 2) {
                            System.out.println("登录格式错误: " + data);
                            out.println("LOGIN_FAIL");
                            continue;
                        }
                        
                        System.out.println("尝试登录用户: " + arr[0]);
                        
                        // 先尝试数据库验证
                        if (dbAvailable) {
                            try {
                                loginSuccess = DBUtil.login(arr[0], arr[1]);
                                if (loginSuccess) {
                                    System.out.println("数据库验证用户成功: " + arr[0]);
                                } else {
                                    System.out.println("数据库验证用户失败: " + arr[0]);
                                }
                            } catch (Exception e) {
                                System.out.println("数据库登录异常：" + e.getMessage());
                            }
                        } else {
                            System.out.println("数据库不可用，使用内存验证");
                        }
                        
                        // 如果数据库验证失败，尝试内存验证
                        if (!loginSuccess) {
                            loginSuccess = users.containsKey(arr[0]) && users.get(arr[0]).equals(arr[1]);
                            if (loginSuccess) {
                                System.out.println("内存验证用户成功: " + arr[0]);
                            } else {
                                System.out.println("内存验证用户失败: " + arr[0]);
                            }
                        }
                        
                        if (loginSuccess) {
                            username = arr[0];
                            out.println("LOGIN_OK");
                            System.out.println("用户 " + username + " 登录成功");
                        } else {
                            out.println("LOGIN_FAIL");
                            System.out.println("用户 " + arr[0] + " 登录失败");
                        }
                    } else if ("LIST_ROOMS".equals(cmd)) {
                        StringBuilder sb = new StringBuilder();
                        for (Room r : rooms) {
                            sb.append(r.id).append(":");
                            sb.append(r.player1 == null ? "空" : r.player1.username);
                            sb.append(",");
                            sb.append(r.player2 == null ? "空" : r.player2.username);
                            sb.append(";");
                        }
                        out.println("ROOMS " + sb.toString());
                        System.out.println("发送房间列表: " + sb.toString());
                    } else if ("JOIN_ROOM".equals(cmd)) {
                        try {
                            int rid = Integer.parseInt(data);
                            Room r = rooms.get(rid - 1);
                            if (r.player1 == null) {
                                r.player1 = this;
                                room = r;
                                out.println("JOINED 1");
                                System.out.println("用户 " + username + " 加入房间 " + rid + " 作为玩家1");
                            } else if (r.player2 == null) {
                                r.player2 = this;
                                room = r;
                                out.println("JOINED 2");
                                System.out.println("用户 " + username + " 加入房间 " + rid + " 作为玩家2");
                                // 只通知player1可以开始
                                r.player1.out.println("START 1");
                                // player2等待player1的READY
                            } else {
                                out.println("ROOM_FULL");
                                System.out.println("用户 " + username + " 尝试加入已满房间 " + rid);
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("房间ID格式错误: " + data);
                            out.println("INVALID_ROOM");
                        } catch (IndexOutOfBoundsException e) {
                            System.out.println("房间ID超出范围: " + data);
                            out.println("INVALID_ROOM");
                        }
                    } else if ("MOVE".equals(cmd)) {
                        if (room == null) {
                            System.out.println("用户 " + username + " 未在房间中，无法移动");
                            continue;
                        }
                        
                        try {
                            String[] arr = data.split(" ");
                            int y = Integer.parseInt(arr[0]);
                            int x = Integer.parseInt(arr[1]);
                            
                            if (y < 0 || y >= 15 || x < 0 || x >= 15) {
                                System.out.println("无效的移动坐标: " + y + "," + x);
                                continue;
                            }
                            
                            int color = room.isBlackTurn ? 1 : 2;
                            room.board[y][x] = color;
                            room.isBlackTurn = !room.isBlackTurn;
                            
                            System.out.println("用户 " + username + " 在位置 " + y + "," + x + " 放置棋子，颜色: " + (color == 1 ? "黑" : "白"));
                            
                            for (ClientHandler ch : Arrays.asList(room.player1, room.player2)) {
                                if (ch != null && ch != this) {
                                    ch.out.println("MOVE " + data);
                                }
                            }
                            
                            if (room.checkWin(y, x, color)) {
                                System.out.println("玩家 " + username + " 获胜！");
                                // 广播胜负
                                for (ClientHandler ch : Arrays.asList(room.player1, room.player2)) {
                                    if (ch != null) ch.out.println("WINNER " + color);
                                }
                                room.board = new int[15][15];
                                room.isBlackTurn = true;
                                room.gameOver = false;
                            }
                        } catch (Exception e) {
                            System.out.println("处理移动时出错: " + e.getMessage());
                        }
                    } else if ("SURRENDER".equals(cmd)) {
                        if (room != null) {
                            System.out.println("用户 " + username + " 投降");
                            // 通知对方认输
                            for (ClientHandler ch : Arrays.asList(room.player1, room.player2)) {
                                if (ch != null && ch != this) ch.out.println("SURRENDERED");
                            }
                        }
                    } else if ("EXIT".equals(cmd)) {
                        System.out.println("用户 " + username + " 退出");
                        if (room != null) {
                            boolean wasPlayer1 = room.player1 == this;
                            boolean wasPlayer2 = room.player2 == this;
                            if (wasPlayer1) {
                                room.player1 = null;
                                System.out.println("玩家1退出房间 " + room.id);
                            }
                            if (wasPlayer2) {
                                room.player2 = null;
                                System.out.println("玩家2退出房间 " + room.id);
                            }
                            // 通知对方退出
                            for (ClientHandler ch : Arrays.asList(room.player1, room.player2)) {
                                if (ch != null && ch != this) ch.out.println("EXITED");
                            }
                            // 如果房间没人了，彻底清空房间
                            if (room.player1 == null && room.player2 == null) {
                                room.board = new int[15][15];
                                room.isBlackTurn = true;
                                room.gameOver = false;
                                System.out.println("房间 " + room.id + " 已重置");
                            }
                            // 广播房间列表更新
                            updateRoomList();
                        }
                        out.println("EXITED");
                        break;
                    } else if ("CLEAR_ROOM".equals(cmd)) {
                        try {
                            int rid = Integer.parseInt(data);
                            Room r = rooms.get(rid - 1);
                            System.out.println("清空房间 " + rid);
                            // 通知房间内所有玩家退出
                            if (r.player1 != null) r.player1.out.println("EXITED");
                            if (r.player2 != null) r.player2.out.println("EXITED");
                            r.player1 = null;
                            r.player2 = null;
                            r.board = new int[15][15];
                            r.isBlackTurn = true;
                            r.gameOver = false;
                            // 广播房间列表更新
                            updateRoomList();
                        } catch (NumberFormatException e) {
                            System.out.println("房间ID格式错误: " + data);
                        } catch (IndexOutOfBoundsException e) {
                            System.out.println("房间ID超出范围: " + data);
                        }
                    } else if ("REGISTER".equals(cmd)) {
                        String[] arr = data.split(" ");
                        boolean registerSuccess = false;
                        
                        if (arr.length != 2) {
                            System.out.println("注册格式错误: " + data);
                            out.println("REGISTER_FAIL");
                            continue;
                        }
                        
                        System.out.println("尝试注册用户: " + arr[0]);
                        
                        // 检查用户名是否已存在
                        boolean userExists = false;
                        
                        // 先检查数据库
                        if (dbAvailable) {
                            try {
                                userExists = DBUtil.isUsernameExists(arr[0]);
                                if (userExists) {
                                    System.out.println("数据库中用户已存在: " + arr[0]);
                                }
                            } catch (Exception e) {
                                System.out.println("数据库检查用户异常：" + e.getMessage());
                            }
                        } else {
                            System.out.println("数据库不可用，使用内存检查用户");
                        }
                        
                        // 再检查内存
                        if (!userExists) {
                            userExists = users.containsKey(arr[0]);
                            if (userExists) {
                                System.out.println("内存中用户已存在: " + arr[0]);
                            }
                        }
                        
                        if (!userExists) {
                            // 尝试数据库注册
                            if (dbAvailable) {
                                try {
                                    registerSuccess = DBUtil.register(arr[0], arr[1]);
                                    if (registerSuccess) {
                                        System.out.println("数据库注册用户成功: " + arr[0]);
                                    } else {
                                        System.out.println("数据库注册用户失败: " + arr[0]);
                                    }
                                } catch (Exception e) {
                                    System.out.println("数据库注册异常：" + e.getMessage());
                                }
                            } else {
                                System.out.println("数据库不可用，使用内存注册");
                            }
                            
                            // 如果数据库注册失败，尝试内存注册
                            if (!registerSuccess) {
                                users.put(arr[0], arr[1]);
                                registerSuccess = true;
                                System.out.println("内存注册用户成功: " + arr[0]);
                            }
                            
                            if (registerSuccess) {
                                out.println("REGISTER_OK");
                                System.out.println("用户 " + arr[0] + " 注册成功");
                            } else {
                                out.println("REGISTER_FAIL");
                                System.out.println("用户 " + arr[0] + " 注册失败");
                            }
                        } else {
                            out.println("REGISTER_FAIL");
                            System.out.println("用户 " + arr[0] + " 注册失败，用户名已存在");
                        }
                    } else if ("READY".equals(cmd)) {
                        // 只有player1会发READY
                        if (room != null && room.player1 == this && room.player2 != null) {
                            System.out.println("玩家1准备就绪，通知玩家2开始");
                            try {
                                Thread.sleep(1000); // 延迟1秒
                            } catch (InterruptedException ignored) {}
                            room.player2.out.println("START 2");
                        }
                    } else {
                        System.out.println("未知命令: " + cmd);
                    }
                }
            } catch (SocketException e) {
                System.out.println("客户端连接断开: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("处理客户端消息时发生错误: " + e.getMessage());
                e.printStackTrace();
            } finally {
                try {
                    System.out.println("客户端断开连接: " + (username != null ? username : "未登录用户"));
                    
                    // 从房间中移除玩家
                    if (room != null) {
                        if (room.player1 == this) room.player1 = null;
                        if (room.player2 == this) room.player2 = null;
                        
                        // 如果房间为空，重置房间状态
                        if (room.player1 == null && room.player2 == null) {
                            room.board = new int[15][15];
                            room.isBlackTurn = true;
                            room.gameOver = false;
                        }
                        
                        // 更新房间列表
                        updateRoomList();
                    }
                    
                    // 从客户端列表中移除
                    clients.remove(this);
                    
                    // 关闭连接
                    socket.close();
                } catch (IOException ignored) {}
            }
        }
        
        /**
         * 向所有客户端广播房间列表更新
         */
        private void updateRoomList() {
            StringBuilder sb = new StringBuilder();
            for (Room r : rooms) {
                sb.append(r.id).append(":");
                sb.append(r.player1 == null ? "空" : r.player1.username);
                sb.append(",");
                sb.append(r.player2 == null ? "空" : r.player2.username);
                sb.append(";");
            }
            
            String roomsMsg = "ROOMS " + sb.toString();
            System.out.println("更新房间列表: " + roomsMsg);
            
            for (ClientHandler ch : clients) {
                if (ch != null) {
                    ch.out.println(roomsMsg);
                }
            }
        }
    }
} 