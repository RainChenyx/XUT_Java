package Gobang;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class RegisterFrame extends JFrame {
    private JTextField userField;
    private JPasswordField passField;
    private JButton registerButton;
    private JLabel statusLabel;

    public RegisterFrame() {
        setTitle("用户注册");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(180, 200, 255));

        JLabel avatarLabel = new JLabel(new ImageIcon("avatar.png"));
        avatarLabel.setBounds(150, 30, 100, 100);
        panel.add(avatarLabel);

        JLabel userLabel = new JLabel("账号：");
        userLabel.setBounds(60, 150, 50, 30);
        panel.add(userLabel);

        userField = new JTextField();
        userField.setBounds(120, 150, 200, 30);
        panel.add(userField);

        JLabel passLabel = new JLabel("密码：");
        passLabel.setBounds(60, 190, 50, 30);
        panel.add(passLabel);

        passField = new JPasswordField();
        passField.setBounds(120, 190, 200, 30);
        panel.add(passField);

        registerButton = new JButton("注册");
        registerButton.setBounds(120, 240, 200, 35);
        panel.add(registerButton);

        statusLabel = new JLabel("");
        statusLabel.setBounds(120, 280, 200, 20);
        statusLabel.setForeground(Color.RED);
        panel.add(statusLabel);

        registerButton.addActionListener(e -> doRegister());

        setContentPane(panel);
    }

    private void doRegister() {
        String username = userField.getText();
        String password = new String(passField.getPassword());
        
        // 首先检查输入是否有效
        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("账号或密码不能为空！");
            return;
        }
        
        // 检查用户名是否已存在（使用数据库）
        try {
            if (DBUtil.isUsernameExists(username)) {
                statusLabel.setText("账号已存在，请更换账号！");
                return;
            }
            
            // 尝试直接注册到数据库
            if (DBUtil.register(username, password)) {
                JOptionPane.showMessageDialog(this, "注册成功，请登录！");
                this.dispose();
                return;
            } else {
                statusLabel.setText("注册失败，请稍后再试！");
            }
        } catch (Exception ex) {
            // 如果数据库连接失败，尝试连接服务器
            System.out.println("数据库连接失败，尝试连接服务器...");
        }
        
        // 尝试连接服务器注册
        try {
            Socket socket = new Socket("127.0.0.1", 8888);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out.println("REGISTER " + username + " " + password);
            String resp = in.readLine();
            if ("REGISTER_OK".equals(resp)) {
                JOptionPane.showMessageDialog(this, "注册成功，请登录！");
                this.dispose();
            } else {
                statusLabel.setText("注册失败，账号已存在！");
                socket.close();
            }
        } catch (Exception ex) {
            statusLabel.setText("无法连接服务器！");
        }
    }
} 