package Gobang;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LoginFrame extends JFrame {
    private JTextField userField;
    private JPasswordField passField;
    private JButton loginButton;
    private JLabel statusLabel;
    private JLabel titleImageLabel; // 标题图片
    private JLabel backgroundLabel; // 背景标签
    private JLabel descriptionLabel; // 新增：描述文字标签
    private JButton registerBtn; // 修改：使用JButton代替 JLabel
    private JButton offlineBtn; // 添加离线模式按钮
    
    // 默认端口及备选端口
    private static final int DEFAULT_PORT = 8888;
    private static final int MAX_PORT_ATTEMPTS = 10;

    public LoginFrame() {
        setTitle("用户登录");
        setSize(1000, 600); // 设置窗口大小
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        try {
            // 加载背景图（denglubeijing.jpg）
            BufferedImage backgroundImage = ImageIO.read(new File("E:\\java项目\\Goban\\OOP\\src\\Gobang\\denglubeijing.jpg"));
            Image scaledBackground = scaleImageToFill(backgroundImage, getWidth(), getHeight()); // 铺满窗口
            backgroundLabel = new JLabel(new ImageIcon(scaledBackground));
            backgroundLabel.setLayout(null); // 使用绝对布局
            setContentPane(backgroundLabel);

            // 加载标题图片（wuziqi.png）并放在左上角
            BufferedImage titleImage = ImageIO.read(new File("E:\\java项目\\Goban\\OOP\\src\\Gobang\\wuziqi.png"));
            Image scaledTitle = scaleImage(titleImage, 250, 150); // 标题图片尺寸
            titleImageLabel = new JLabel(new ImageIcon(scaledTitle));
            titleImageLabel.setBounds(120, 50, 250, (int) scaledTitle.getHeight(null)); // 左上角位置

            // 创建透明登录表单面板（对齐到标题下方）
            JPanel formPanel = new JPanel(null);
            formPanel.setOpaque(false); // 面板透明
            formPanel.setBounds(50, 150, 350, 250); // 对齐标题下方

            // 账号输入框
            JLabel userLabel = new JLabel("账号：");
            userLabel.setFont(new Font("微软雅黑", Font.PLAIN, 18));
            userLabel.setForeground(Color.WHITE);
            userLabel.setBounds(30, 30, 80, 30);
            formPanel.add(userLabel);

            userField = new JTextField();
            userField.setFont(new Font("微软雅黑", Font.PLAIN, 16));
            userField.setBounds(110, 30, 200, 30);
            formPanel.add(userField);

            // 密码输入框
            JLabel passLabel = new JLabel("密码：");
            passLabel.setFont(new Font("微软雅黑", Font.PLAIN, 18));
            passLabel.setForeground(Color.WHITE);
            passLabel.setBounds(30, 80, 80, 30);
            formPanel.add(passLabel);

            passField = new JPasswordField();
            passField.setFont(new Font("微软雅黑", Font.PLAIN, 16));
            passField.setBounds(110, 80, 200, 30);
            formPanel.add(passField);

            // 登录按钮
            loginButton = new JButton("登录");
            loginButton.setFont(new Font("微软雅黑", Font.BOLD, 18));
            loginButton.setBounds(110, 140, 120, 30); // 调整按钮高度为30
            loginButton.addActionListener(e -> doLogin());
            formPanel.add(loginButton);

            // 状态标签
            statusLabel = new JLabel("");
            statusLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
            statusLabel.setForeground(Color.RED);
            statusLabel.setBounds(110, 190, 200, 20);
            formPanel.add(statusLabel);

            // 修改：注册按钮改为JButton样式
            registerBtn = new JButton("注册");
            registerBtn.setFont(new Font("微软雅黑", Font.PLAIN, 14));
            registerBtn.setForeground(Color.CYAN);
            registerBtn.setOpaque(false); // 透明背景
            registerBtn.setContentAreaFilled(false); // 取消填充
            registerBtn.setBorderPainted(false); // 取消边框
            registerBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            registerBtn.addActionListener(e -> new RegisterFrame().setVisible(true));
            registerBtn.setBounds(30, 220, 80, 30); // 位置与离线按钮对齐
            formPanel.add(registerBtn);

            // 离线模式按钮
            offlineBtn = new JButton("离线模式");
            offlineBtn.setFont(new Font("微软雅黑", Font.PLAIN, 14));
            offlineBtn.setBounds(200, 220, 100, 30);
            offlineBtn.addActionListener(e -> {
                new GobangGame().setVisible(true);
                dispose();
            });
            formPanel.add(offlineBtn);

            // 新增：描述文字标签（上移10像素）
            String description = "五子棋起源于中国上古时代的传统黑白棋种之一，史书中\"日月如合璧，五星如连珠\"。主要流行于华人和汉字文化圈，是世界上最古老的棋。通常双方分别使用黑白两色的棋子，下在棋盘直线与横线的交叉点上，先形成5子连线者获胜。";
            descriptionLabel = new JLabel("<html><body style='text-align: center;'>" + description + "</body></html>");
            descriptionLabel.setFont(new Font("华文行楷", Font.PLAIN, 14));
            descriptionLabel.setForeground(Color.WHITE);
            int labelWidth = 800;
            int labelHeight = 60;
            // 上移10像素（原y坐标为getHeight() - labelHeight - 20，现改为 -30）
            descriptionLabel.setBounds((getWidth() - labelWidth) / 2, getHeight() - labelHeight - 30, labelWidth, labelHeight);
            backgroundLabel.add(descriptionLabel);

            // 将标题和表单添加到背景标签
            backgroundLabel.add(titleImageLabel);
            backgroundLabel.add(formPanel);

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "图片加载失败！");
        }

        setVisible(true);
    }

    // 图片缩放方法（保持宽高比）
    private Image scaleImage(BufferedImage original, int maxWidth, int maxHeight) {
        int width = original.getWidth();
        int height = original.getHeight();
        double ratio = (double) width / height;
        if (width > maxWidth) {
            width = maxWidth;
            height = (int) (width / ratio);
        }
        if (height > maxHeight) {
            height = maxHeight;
            width = (int) (height * ratio);
        }
        return original.getScaledInstance(width, height, Image.SCALE_SMOOTH);
    }

    // 图片缩放方法（铺满窗口，可能裁剪）
    private Image scaleImageToFill(BufferedImage original, int targetWidth, int targetHeight) {
        int width = original.getWidth();
        int height = original.getHeight();
        double imgRatio = (double) width / height;
        double targetRatio = (double) targetWidth / targetHeight;

        // 根据比例决定如何缩放
        if (imgRatio > targetRatio) {
            // 图片更宽，以高度为基准缩放
            height = targetHeight;
            width = (int) (height * imgRatio);
        } else {
            // 图片更高，以宽度为基准缩放
            width = targetWidth;
            height = (int) (width / imgRatio);
        }

        return original.getScaledInstance(width, height, Image.SCALE_SMOOTH);
    }

    private void doLogin() {
        String username = userField.getText();
        String password = new String(passField.getPassword());
        
        // 首先检查登录信息是否有效
        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("账号或密码不能为空！");
            return;
        }
        
        try {
            // 尝试直接使用DBUtil进行登录验证（离线模式）
            if (DBUtil.login(username, password)) {
                // 离线登录成功，转到游戏界面
                new GobangGame().setVisible(true);
                dispose();
                return;
            }
        } catch (Exception ex) {
            // 数据库连接异常，继续尝试连接服务器
            System.out.println("数据库连接失败，尝试连接服务器...");
        }
        
        // 显示正在连接服务器的状态
        statusLabel.setText("正在连接服务器...");
        loginButton.setEnabled(false);
        
        // 创建一个新线程来尝试连接服务器，避免UI冻结
        new Thread(() -> {
            boolean connected = false;
            String failReason = "无法连接服务器！";
            
            // 尝试多个端口
            for (int portAttempt = 0; portAttempt < MAX_PORT_ATTEMPTS && !connected; portAttempt++) {
                int port = DEFAULT_PORT + portAttempt;
                try {
                    final Socket socket = new Socket();
                    // 设置连接超时为2秒
                    socket.connect(new InetSocketAddress("127.0.0.1", port), 2000);
                    connected = true;
                    System.out.println("成功连接到服务器端口: " + port);
                    
                    // 实际登录逻辑
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    out.println("LOGIN " + username + " " + password);
                    String resp = in.readLine();
                    
                    // 在UI线程中更新界面
                    if ("LOGIN_OK".equals(resp)) {
                        final Socket finalSocket = socket;
                        final BufferedReader finalIn = in;
                        final PrintWriter finalOut = out;
                        SwingUtilities.invokeLater(() -> {
                            TableSelectFrame tsf = new TableSelectFrame(finalSocket, finalIn, finalOut, username);
                            tsf.setVisible(true);
                            dispose();
                        });
                        return; // 登录成功，直接返回
                    } else {
                        failReason = "账号或密码错误！";
                        // 登录失败，关闭连接
                        try {
                            socket.close();
                        } catch (IOException e) {
                            // 忽略关闭时的错误
                        }
                        break; // 如果是认证失败，不再尝试其他端口
                    }
                } catch (ConnectException e) {
                    System.out.println("无法连接到端口 " + port + "，尝试下一个...");
                    failReason = "服务器可能未启动，请确认服务器状态！";
                } catch (Exception e) {
                    e.printStackTrace();
                    failReason = "连接错误: " + e.getMessage();
                    break;
                }
            }
            
            // 所有端口尝试均失败，或登录失败
            final String finalReason = failReason;
            SwingUtilities.invokeLater(() -> {
                statusLabel.setText(finalReason);
                loginButton.setEnabled(true);
            });
        }).start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginFrame::new);
    }
}