package Gobang;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class GobangGameNet extends JFrame {
    private static final int BOARD_SIZE = 15;
    private static final int CELL_SIZE = 40;
    private static final int MARGIN = 20;
    private static final int PIECE_SIZE = 34;
    private static final int CLICK_THRESHOLD = CELL_SIZE / 3;

    private int[][] board = new int[BOARD_SIZE][BOARD_SIZE];
    private boolean isBlackTurn = true;
    private boolean gameOver = false;
    private boolean myTurn;
    private String username;
    private String roomId;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private JLabel statusLabel;
    private int blackScore = 0;
    private int whiteScore = 0;
    private JLabel blackScoreLabel;
    private JLabel whiteScoreLabel;
    private JButton surrenderButton;
    private JButton exitButton;
    private int myColor; // 1=黑, 2=白
    private boolean hasExited = false;

    public GobangGameNet(Socket socket, BufferedReader in, PrintWriter out, boolean isBlack, String username, String roomId) {
        System.out.println("GobangGameNet构造方法被调用，isBlack=" + isBlack);
        this.socket = socket;
        this.in = in;
        this.out = out;
        this.myTurn = isBlack;
        this.isBlackTurn = true;
        this.username = username;
        this.roomId = roomId;
        this.myColor = isBlack ? 1 : 2;

        setTitle("五子棋对弈 - 房间" + roomId + " - " + username + (isBlack ? "（黑方）" : "（白方）"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        BoardPanel boardPanel = new BoardPanel();
        mainPanel.add(boardPanel, BorderLayout.CENTER);

        // 比分和操作按钮面板
        JPanel topPanel = new JPanel();
        blackScoreLabel = new JLabel("黑方得分: 0");
        whiteScoreLabel = new JLabel("白方得分: 0");
        surrenderButton = new JButton("认输");
        exitButton = new JButton("退出");
        topPanel.add(blackScoreLabel);
        topPanel.add(whiteScoreLabel);
        topPanel.add(surrenderButton);
        topPanel.add(exitButton);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        statusLabel = new JLabel(myTurn ? "你的回合" : "等待对方落子");
        mainPanel.add(statusLabel, BorderLayout.SOUTH);

        add(mainPanel);
        System.out.println("UI已添加");
        pack();
        setLocationRelativeTo(null);

        // 认输按钮事件
        surrenderButton.addActionListener(e -> {
            if (!gameOver) {
                out.println("SURRENDER");
                handleSurrender(true);
            }
        });
        // 退出按钮事件
        exitButton.addActionListener(e -> {
            if (!hasExited) {
                hasExited = true;
                new Thread(() -> {
                    out.println("CLEAR_ROOM " + roomId);
                }).start();
                this.dispose();
                // 重新连接服务器
                try {
                    Socket newSocket = new Socket("127.0.0.1", 8888);
                    PrintWriter newOut = new PrintWriter(newSocket.getOutputStream(), true);
                    BufferedReader newIn = new BufferedReader(new InputStreamReader(newSocket.getInputStream()));
                    SwingUtilities.invokeLater(() -> {
                        new TableSelectFrame(newSocket, newIn, newOut, username).setVisible(true);
                    });
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "无法重新连接服务器！");
                }
            }
        });

        // 启动接收线程
        new Thread(this::receiveLoop).start();

        // 如果我是黑方（player1），加载完UI后通知服务器我已准备好
        if (myTurn) { // 黑方
            new Thread(() -> {
                try {
                    out.println("READY");
                } catch (Exception ignored) {}
            }).start();
        }

        // 强制刷新和显示窗口，确保paintComponent被调用
        this.invalidate();
        this.validate();
        this.repaint();
        this.setVisible(true);
        playStartSound();
    }

    private void handleSurrender(boolean isSelf) {
        gameOver = true;
        if (isSelf) {
            // 自己认输，对方加分
            if (isBlackTurn) {
                whiteScore++;
                whiteScoreLabel.setText("白方得分: " + whiteScore);
            } else {
                blackScore++;
                blackScoreLabel.setText("黑方得分: " + blackScore);
            }
            statusLabel.setText("你认输了，对方得分+1，自动重新开始");
        } else {
            // 对方认输，自己加分
            if (isBlackTurn) {
                blackScore++;
                blackScoreLabel.setText("黑方得分: " + blackScore);
            } else {
                whiteScore++;
                whiteScoreLabel.setText("白方得分: " + whiteScore);
            }
            statusLabel.setText("对方认输，你得分+1，自动重新开始");
            JOptionPane.showMessageDialog(this, "对方认输！");
        }
        // 自动重新开始
        resetGame();
    }

    private void resetGame() {
        board = new int[BOARD_SIZE][BOARD_SIZE];
        isBlackTurn = true;
        gameOver = false;
        statusLabel.setText(isBlackTurn ? "你的回合" : "等待对方落子");
        repaint(); // 请求重绘
        validate(); // 强制立即重绘
        invalidate(); // 强制立即重绘
    }

    private void handleExit() {
        if (!hasExited) {
            hasExited = true;
            JOptionPane.showMessageDialog(this, "对手退出，房间解散");
            this.dispose();
            // 重新连接服务器
            try {
                Socket newSocket = new Socket("127.0.0.1", 8888);
                PrintWriter newOut = new PrintWriter(newSocket.getOutputStream(), true);
                BufferedReader newIn = new BufferedReader(new InputStreamReader(newSocket.getInputStream()));
                SwingUtilities.invokeLater(() -> {
                    new TableSelectFrame(newSocket, newIn, newOut, username).setVisible(true);
                });
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "无法重新连接服务器！");
            }
        }
    }

    private void receiveLoop() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                if (line.startsWith("MOVE")) {
                    String[] arr = line.split(" ");
                    int y = Integer.parseInt(arr[1]);
                    int x = Integer.parseInt(arr[2]);
                    board[y][x] = 3 - myColor; // 对方下棋用3-myColor
                    isBlackTurn = !isBlackTurn;
                    myTurn = true;
                    statusLabel.setText("你的回合");
                    repaint();
                } else if (line.startsWith("SURRENDERED")) {
                    handleSurrender(false);
                } else if (line.startsWith("EXITED")) {
                    handleExit();
                    break;
                } else if (line.startsWith("WINNER")) {
                    int winner = Integer.parseInt(line.split(" ")[1]);
                    handleGameOver(winner);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "连接断开！");
            System.exit(0);
        }
    }

    private void handleGameOver(int winnerColor) {
        if (winnerColor == myColor) {
            JOptionPane.showMessageDialog(this, "你赢了！");
        } else {
            JOptionPane.showMessageDialog(this, "你输了！");
        }
        if (winnerColor == 1) {
            blackScore++;
            blackScoreLabel.setText("黑方得分: " + blackScore);
        } else {
            whiteScore++;
            whiteScoreLabel.setText("白方得分: " + whiteScore);
        }
        resetGame();
    }

    private class BoardPanel extends JPanel {
        public BoardPanel() {
            System.out.println("BoardPanel构造方法被调用");
            setPreferredSize(new Dimension(
                    BOARD_SIZE * CELL_SIZE + 2 * MARGIN,
                    BOARD_SIZE * CELL_SIZE + 2 * MARGIN
            ));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (gameOver || !myTurn) return;
                    int mouseX = e.getX();
                    int mouseY = e.getY();
                    int[] nearestPoint = findNearestIntersection(mouseX, mouseY);
                    if (nearestPoint != null) {
                        int x = nearestPoint[0];
                        int y = nearestPoint[1];
                        if (board[y][x] == 0) {
                            board[y][x] = myColor; // 自己下棋用myColor
                            out.println("MOVE " + y + " " + x);
                            if (checkWin(x, y)) {
                                gameOver = true;
                                statusLabel.setText("你赢了！");
                            } else {
                                isBlackTurn = !isBlackTurn;
                                myTurn = false;
                                statusLabel.setText("等待对方落子");
                            }
                            repaint();
                        }
                    }
                }
            });
        }

        private int[] findNearestIntersection(int mouseX, int mouseY) {
            for (int i = 0; i < BOARD_SIZE; i++) {
                for (int j = 0; j < BOARD_SIZE; j++) {
                    int crossX = MARGIN + j * CELL_SIZE;
                    int crossY = MARGIN + i * CELL_SIZE;
                    int dx = mouseX - crossX;
                    int dy = mouseY - crossY;
                    if (Math.abs(dx) <= CLICK_THRESHOLD && Math.abs(dy) <= CLICK_THRESHOLD) {
                        return new int[]{j, i};
                    }
                }
            }
            return null;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            System.out.println("BoardPanel paintComponent被调用");
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2d.setColor(new Color(222, 184, 135));
            g2d.fillRect(0, 0, getWidth(), getHeight());

            g2d.setColor(Color.BLACK);
            for (int i = 0; i < BOARD_SIZE; i++) {
                g2d.drawLine(MARGIN, MARGIN + i * CELL_SIZE,
                        MARGIN + (BOARD_SIZE - 1) * CELL_SIZE, MARGIN + i * CELL_SIZE);
                g2d.drawLine(MARGIN + i * CELL_SIZE, MARGIN,
                        MARGIN + i * CELL_SIZE, MARGIN + (BOARD_SIZE - 1) * CELL_SIZE);
            }

            int[] starPoints = {3, 7, 11};
            for (int i : starPoints) {
                for (int j : starPoints) {
                    g2d.fillOval(MARGIN + i * CELL_SIZE - 4, MARGIN + j * CELL_SIZE - 4, 8, 8);
                }
            }

            for (int i = 0; i < BOARD_SIZE; i++) {
                for (int j = 0; j < BOARD_SIZE; j++) {
                    if (board[i][j] != 0) {
                        int x = MARGIN + j * CELL_SIZE;
                        int y = MARGIN + i * CELL_SIZE;
                        if (board[i][j] == 1) {
                            g2d.setColor(Color.BLACK);
                        } else {
                            g2d.setColor(Color.WHITE);
                        }
                        g2d.fillOval(x - PIECE_SIZE/2, y - PIECE_SIZE/2, PIECE_SIZE, PIECE_SIZE);
                        g2d.setColor(Color.BLACK);
                        g2d.drawOval(x - PIECE_SIZE/2, y - PIECE_SIZE/2, PIECE_SIZE, PIECE_SIZE);
                    }
                }
            }
        }
    }

    private boolean checkWin(int x, int y) {
        int currentPlayer = board[y][x];
        int count = 1;
        for (int i = x - 1; i >= 0 && board[y][i] == currentPlayer; i--) count++;
        for (int i = x + 1; i < BOARD_SIZE && board[y][i] == currentPlayer; i++) count++;
        if (count >= 5) {
            gameOver = true;
            try {
                SwingUtilities.invokeAndWait(() -> {
                    statusLabel.setText("你赢了！");
                    if (currentPlayer == 1) {
                        blackScore++;
                        blackScoreLabel.setText("黑方得分: " + blackScore);
                    } else {
                        whiteScore++;
                        whiteScoreLabel.setText("白方得分: " + whiteScore);
                    }
                    JOptionPane.showMessageDialog(this, "你赢了！");
                    board = new int[BOARD_SIZE][BOARD_SIZE];
                    isBlackTurn = true;
                    gameOver = false;
                    statusLabel.setText(isBlackTurn ? "你的回合" : "等待对方落子");
                    repaint();
                    validate();
                    invalidate();
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
        count = 1;
        for (int i = y - 1; i >= 0 && board[i][x] == currentPlayer; i--) count++;
        for (int i = y + 1; i < BOARD_SIZE && board[i][x] == currentPlayer; i++) count++;
        if (count >= 5) return true;
        count = 1;
        for (int i = x - 1, j = y - 1; i >= 0 && j >= 0 && board[j][i] == currentPlayer; i--, j--) count++;
        for (int i = x + 1, j = y + 1; i < BOARD_SIZE && j < BOARD_SIZE && board[j][i] == currentPlayer; i++, j++) count++;
        if (count >= 5) return true;
        count = 1;
        for (int i = x - 1, j = y + 1; i >= 0 && j < BOARD_SIZE && board[j][i] == currentPlayer; i--, j++) count++;
        for (int i = x + 1, j = y - 1; i < BOARD_SIZE && j >= 0 && board[j][i] == currentPlayer; i++, j--) count++;
        if (count >= 5) return true;
        return false;
    }

    private void playStartSound() {
        try {
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(getClass().getResource("start.wav"));
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 