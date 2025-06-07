package Gobang;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GobangGame extends JFrame {
    private static final int BOARD_SIZE = 15;
    private static final int CELL_SIZE = 40;
    private static final int MARGIN = 20;
    private static final int PIECE_SIZE = 34;
    private static final int CLICK_THRESHOLD = CELL_SIZE / 3; // 点击判定阈值

    private int[][] board;
    private boolean isBlackTurn = true;
    private boolean gameOver = false;
    private int blackScore = 0;
    private int whiteScore = 0;
    private boolean isAIMode = true;

    private JLabel statusLabel;
    private JLabel blackScoreLabel;
    private JLabel whiteScoreLabel;
    private JButton restartButton;
    private JButton toggleAIButton;

    public GobangGame() {
        setTitle("五子棋");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // 初始化棋盘
        board = new int[BOARD_SIZE][BOARD_SIZE];

        // 创建主面板
        JPanel mainPanel = new JPanel(new BorderLayout());

        // 创建棋盘面板
        BoardPanel boardPanel = new BoardPanel();
        mainPanel.add(boardPanel, BorderLayout.CENTER);

        // 创建控制面板
        JPanel controlPanel = new JPanel();
        statusLabel = new JLabel("黑方回合");
        blackScoreLabel = new JLabel("黑方得分: 0");
        whiteScoreLabel = new JLabel("白方得分: 0");
        restartButton = new JButton("重新开始");
        toggleAIButton = new JButton("切换为用户对战");

        restartButton.addActionListener(e -> resetGame());
        toggleAIButton.addActionListener(e -> {
            isAIMode = !isAIMode;
            toggleAIButton.setText(isAIMode ? "切换为用户对战" : "切换为AI对战");
            resetGame();
        });

        controlPanel.add(statusLabel);
        controlPanel.add(blackScoreLabel);
        controlPanel.add(whiteScoreLabel);
        controlPanel.add(restartButton);
        controlPanel.add(toggleAIButton);

        mainPanel.add(controlPanel, BorderLayout.SOUTH);

        add(mainPanel);
        pack();
        setLocationRelativeTo(null);
    }

    private void resetGame() {
        board = new int[BOARD_SIZE][BOARD_SIZE];
        isBlackTurn = true;
        gameOver = false;
        statusLabel.setText("黑方回合");
        repaint();
    }

    private class BoardPanel extends JPanel {
        public BoardPanel() {
            setPreferredSize(new Dimension(
                    BOARD_SIZE * CELL_SIZE + 2 * MARGIN,
                    BOARD_SIZE * CELL_SIZE + 2 * MARGIN
            ));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (gameOver) return;

                    // 获取鼠标点击位置
                    int mouseX = e.getX();
                    int mouseY = e.getY();

                    // 找到最近的交叉点
                    int[] nearestPoint = findNearestIntersection(mouseX, mouseY);
                    if (nearestPoint != null) {
                        int x = nearestPoint[0];
                        int y = nearestPoint[1];

                        if (board[y][x] == 0) {
                            makeMove(x, y);

                            // 如果是AI模式且游戏未结束，让AI下棋
                            if (isAIMode && !gameOver && !isBlackTurn) {
                                Timer timer = new Timer(500, evt -> {
                                    makeAIMove();
                                    ((Timer)evt.getSource()).stop();
                                });
                                timer.setRepeats(false);
                                timer.start();
                            }
                        }
                    }
                }
            });
        }

        private int[] findNearestIntersection(int mouseX, int mouseY) {
            // 遍历所有交叉点
            for (int i = 0; i < BOARD_SIZE; i++) {
                for (int j = 0; j < BOARD_SIZE; j++) {
                    int crossX = MARGIN + j * CELL_SIZE;
                    int crossY = MARGIN + i * CELL_SIZE;

                    // 计算点击位置到交叉点的水平和垂直距离
                    int dx = mouseX - crossX;
                    int dy = mouseY - crossY;

                    // 判断是否在十字交叉点周围的有效范围内
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
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // 绘制棋盘背景
            g2d.setColor(new Color(222, 184, 135));
            g2d.fillRect(0, 0, getWidth(), getHeight());

            // 绘制网格线
            g2d.setColor(Color.BLACK);
            for (int i = 0; i < BOARD_SIZE; i++) {
                g2d.drawLine(MARGIN, MARGIN + i * CELL_SIZE,
                        MARGIN + (BOARD_SIZE - 1) * CELL_SIZE, MARGIN + i * CELL_SIZE);
                g2d.drawLine(MARGIN + i * CELL_SIZE, MARGIN,
                        MARGIN + i * CELL_SIZE, MARGIN + (BOARD_SIZE - 1) * CELL_SIZE);
            }

            // 绘制天元和星位
            int[] starPoints = {3, 7, 11};
            for (int i : starPoints) {
                for (int j : starPoints) {
                    g2d.fillOval(MARGIN + i * CELL_SIZE - 4, MARGIN + j * CELL_SIZE - 4, 8, 8);
                }
            }

            // 绘制棋子
            for (int i = 0; i < BOARD_SIZE; i++) {
                for (int j = 0; j < BOARD_SIZE; j++) {
                    if (board[i][j] != 0) {
                        int x = MARGIN + j * CELL_SIZE;
                        int y = MARGIN + i * CELL_SIZE;

                        if (board[i][j] == 1) { // 黑棋
                            g2d.setColor(Color.BLACK);
                        } else { // 白棋
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

    private void makeMove(int x, int y) {
        board[y][x] = isBlackTurn ? 1 : 2;

        if (checkWin(x, y)) {
            gameOver = true;
            if (isBlackTurn) {
                blackScore++;
                blackScoreLabel.setText("黑方得分: " + blackScore);
                statusLabel.setText("黑方胜利！");
            } else {
                whiteScore++;
                whiteScoreLabel.setText("白方得分: " + whiteScore);
                statusLabel.setText("白方胜利！");
            }
            // 延迟1秒后清空棋盘
            Timer timer = new Timer(1000, evt -> {
                resetGame();
                ((Timer)evt.getSource()).stop();
            });
            timer.setRepeats(false);
            timer.start();
        } else {
            isBlackTurn = !isBlackTurn;
            statusLabel.setText(isBlackTurn ? "黑方回合" : "白方回合");
        }

        repaint();
    }

    private void makeAIMove() {
        int[] move = GobangAI.findBestMove(board, !isBlackTurn);
        if (move != null) {
            makeMove(move[1], move[0]);
        }
    }

    private boolean checkWin(int x, int y) {
        int currentPlayer = board[y][x];

        // 检查水平方向
        int count = 1;
        for (int i = x - 1; i >= 0 && board[y][i] == currentPlayer; i--) count++;
        for (int i = x + 1; i < BOARD_SIZE && board[y][i] == currentPlayer; i++) count++;
        if (count >= 5) return true;

        // 检查垂直方向
        count = 1;
        for (int i = y - 1; i >= 0 && board[i][x] == currentPlayer; i--) count++;
        for (int i = y + 1; i < BOARD_SIZE && board[i][x] == currentPlayer; i++) count++;
        if (count >= 5) return true;

        // 检查对角线方向
        count = 1;
        for (int i = x - 1, j = y - 1; i >= 0 && j >= 0 && board[j][i] == currentPlayer; i--, j--) count++;
        for (int i = x + 1, j = y + 1; i < BOARD_SIZE && j < BOARD_SIZE && board[j][i] == currentPlayer; i++, j++) count++;
        if (count >= 5) return true;

        // 检查反对角线方向
        count = 1;
        for (int i = x - 1, j = y + 1; i >= 0 && j < BOARD_SIZE && board[j][i] == currentPlayer; i--, j++) count++;
        for (int i = x + 1, j = y - 1; i < BOARD_SIZE && j >= 0 && board[j][i] == currentPlayer; i++, j--) count++;
        if (count >= 5) return true;

        return false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GobangGame().setVisible(true);
        });
    }
} 