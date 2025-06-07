package Gobang;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class TableSelectFrame extends JFrame {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String username;
    private JTextPane roomArea;
    private JLabel backgroundLabel;
    private JPanel centerPanel;

    public TableSelectFrame(Socket socket, BufferedReader in, PrintWriter out, String username) {
        this.socket = socket;
        this.in = in;
        this.out = out;
        this.username = username;

        setTitle("选桌对弈 - " + username);
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 加载背景图像
        ImageIcon backgroundImage = new ImageIcon("E:\\java项目\\Goban\\OOP\\src\\Gobang\\xuanzhuojiemian.jpg"); // 替换为您的背景图像路径
        backgroundLabel = new JLabel(backgroundImage);
        backgroundLabel.setLayout(new BorderLayout());

        roomArea = new JTextPane();
        roomArea.setEditable(false);
        roomArea.setOpaque(false); // 设置为透明
        // 设置居中样式
        StyledDocument doc = roomArea.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);

        // 创建包裹面板实现垂直居中
        centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(roomArea);
        centerPanel.add(Box.createVerticalGlue());
        // 不用JScrollPane，直接加centerPanel
        backgroundLabel.add(centerPanel, BorderLayout.CENTER);

        JButton refreshBtn = new JButton("刷新房间");
        JButton joinBtn = new JButton("加入房间");
        JTextField roomField = new JTextField(5);

        JPanel bottom = new JPanel();
        bottom.add(new JLabel("房间号:"));
        bottom.add(roomField);
        bottom.add(joinBtn);
        bottom.add(refreshBtn);
        backgroundLabel.add(bottom, BorderLayout.SOUTH);

        refreshBtn.addActionListener(e -> refreshRooms());
        joinBtn.addActionListener(e -> {
            String rid = roomField.getText();
            if (!rid.matches("\\d+")) return;
            out.println("JOIN_ROOM " + rid);
            try {
                String resp = in.readLine();
                if (resp.startsWith("JOINED")) {
                    // 弹出等待对话框
                    JDialog waitDialog = new JDialog(this, "等待对方加入", true);
                    waitDialog.setSize(300, 150);
                    waitDialog.setLocationRelativeTo(this);
                    waitDialog.setLayout(new BorderLayout());
                    JLabel waitLabel = new JLabel("等待对方加入...", SwingConstants.CENTER);
                    waitDialog.add(waitLabel, BorderLayout.CENTER);
                    JButton exitBtn = new JButton("退出房间");
                    waitDialog.add(exitBtn, BorderLayout.SOUTH);

                    exitBtn.addActionListener(ev -> {
                        out.println("EXIT");
                        out.println("CLEAR_ROOM " + rid);
                        waitDialog.dispose();
                        // 重新连接服务器并返回选桌界面
                        try {
                            Socket newSocket = new Socket("127.0.0.1", 8888);
                            PrintWriter newOut = new PrintWriter(newSocket.getOutputStream(), true);
                            BufferedReader newIn = new BufferedReader(new InputStreamReader(newSocket.getInputStream()));
                            SwingUtilities.invokeLater(() -> {
                                new TableSelectFrame(newSocket, newIn, newOut, username).setVisible(true);
                                // 关闭当前窗口，避免界面残留
                                Window current = SwingUtilities.getWindowAncestor(roomArea);
                                if (current != null) current.dispose();
                            });
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(null, "无法重新连接服务器！");
                        }
                    });

                    // 只用一个监听线程读取Socket流
                    Thread listenThread = new Thread(() -> {
                        try {
                            while (true) {
                                String msg = in.readLine();
                                if (msg == null) break;
                                if (msg.startsWith("START")) {
                                    int myColor = Integer.parseInt(msg.split(" ")[1]);
                                    // 先关闭等待对话框和TableSelectFrame，再用EDT创建棋盘窗口
                                    waitDialog.dispose();
                                    this.dispose();
                                    SwingUtilities.invokeLater(() -> {
                                        new GobangGameNet(socket, in, out, myColor == 1, username, rid).setVisible(true);
                                    });
                                    break;
                                } else if (msg.startsWith("EXITED")) {
                                    SwingUtilities.invokeLater(() -> {
                                        waitDialog.dispose();
                                        Window current = SwingUtilities.getWindowAncestor(roomArea);
                                        if (current != null) current.dispose();
                                        try {
                                            Socket newSocket = new Socket("127.0.0.1", 8888);
                                            PrintWriter newOut = new PrintWriter(newSocket.getOutputStream(), true);
                                            BufferedReader newIn = new BufferedReader(new InputStreamReader(newSocket.getInputStream()));
                                            new TableSelectFrame(newSocket, newIn, newOut, username).setVisible(true);
                                        } catch (Exception ex) {
                                            JOptionPane.showMessageDialog(null, "无法重新连接服务器！");
                                        }
                                    });
                                    break;
                                }
                            }
                        } catch (Exception ex) {
                            SwingUtilities.invokeLater(waitDialog::dispose);
                        }
                    });
                    listenThread.start();

                    waitDialog.setVisible(true);
                    // 回到选桌界面
                    refreshRooms();
                } else if (resp.startsWith("ROOM_FULL")) {
                    JOptionPane.showMessageDialog(this, "房间已满！");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "加入房间失败！");
            }
        });

        setContentPane(backgroundLabel);
        // 自动刷新房间列表，确保退出后房间状态及时变为"空，空"
        SwingUtilities.invokeLater(this::refreshRooms);
    }

    private void refreshRooms() {
        out.println("LIST_ROOMS");
        try {
            String resp = in.readLine();
            if (resp.startsWith("ROOMS")) {
                String[] rooms = resp.substring(6).split(";");
                StringBuilder sb = new StringBuilder();
                for (String r : rooms) {
                    if (r.trim().isEmpty()) continue;
                    String[] arr = r.split(":");
                    String[] users = arr[1].split(",");
                    sb.append("房间 ").append(arr[0]).append("：玩家1：").append(users[0])
                            .append("，玩家2：").append(users[1]).append("\n");
                }
                // 方法2：动态补空行实现竖直居中
                // 1. 先设置内容，计算内容高度
                roomArea.setText(sb.toString());
                StyledDocument doc = roomArea.getStyledDocument();
                SimpleAttributeSet center = new SimpleAttributeSet();
                StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
                doc.setParagraphAttributes(0, doc.getLength(), center, false);
                // 2. 计算显示区高度和内容高度
                int areaHeight = roomArea.getParent().getHeight();
                int lineHeight = roomArea.getFontMetrics(roomArea.getFont()).getHeight();
                int lineCount = roomArea.getText().split("\n").length;
                int contentHeight = lineCount * lineHeight;
                int blankLines = 0;
                if (areaHeight > contentHeight) {
                    blankLines = (areaHeight - contentHeight) / (2 * lineHeight);
                }
                // 3. 拼接空行
                StringBuilder finalText = new StringBuilder();
                for (int i = 0; i < blankLines; i++) finalText.append("\n");
                finalText.append(sb);
                roomArea.setText(finalText.toString());
                doc.setParagraphAttributes(0, doc.getLength(), center, false);
            }
        } catch (Exception ex) {
            roomArea.setText("获取房间信息失败！");
        }
    }
} 