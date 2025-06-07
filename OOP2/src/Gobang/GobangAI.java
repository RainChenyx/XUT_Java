package Gobang;

public class GobangAI {
    private static final int BOARD_SIZE = 15;
    private static final int EMPTY = 0;
    private static final int BLACK = 1;
    private static final int WHITE = 2;
    
    // 棋型分数
    private static final int FIVE = 100000;    // 连五
    private static final int OPEN_FOUR = 10000; // 活四
    private static final int FOUR = 1000;      // 冲四
    private static final int OPEN_THREE = 1000; // 活三
    private static final int THREE = 100;      // 冲三
    private static final int OPEN_TWO = 100;   // 活二
    private static final int TWO = 10;         // 冲二
    
    public static int[] findBestMove(int[][] board, boolean isBlack) {
        int bestScore = Integer.MIN_VALUE;
        int[] bestMove = new int[2];
        
        // 遍历所有可能的落子位置
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] == EMPTY) {
                    // 评估这个位置的分数
                    int score = evaluatePosition(board, i, j, isBlack);
                    
                    // 如果是进攻位置，增加分数
                    if (isBlack) {
                        score += evaluateAttack(board, i, j, BLACK);
                    } else {
                        score += evaluateAttack(board, i, j, WHITE);
                    }
                    
                    // 如果是防守位置，增加分数
                    if (isBlack) {
                        score += evaluateDefense(board, i, j, WHITE);
                    } else {
                        score += evaluateDefense(board, i, j, BLACK);
                    }
                    
                    if (score > bestScore) {
                        bestScore = score;
                        bestMove[0] = i;
                        bestMove[1] = j;
                    }
                }
            }
        }
        
        return bestMove;
    }
    
    private static int evaluatePosition(int[][] board, int row, int col, boolean isBlack) {
        int score = 0;
        int player = isBlack ? BLACK : WHITE;
        
        // 检查周围8个方向
        int[][] directions = {
            {-1, -1}, {-1, 0}, {-1, 1},
            {0, -1},           {0, 1},
            {1, -1},  {1, 0},  {1, 1}
        };
        
        for (int[] dir : directions) {
            int count = 1;
            int space = 0;
            boolean blocked = false;
            
            // 正向检查
            for (int i = 1; i <= 4; i++) {
                int newRow = row + dir[0] * i;
                int newCol = col + dir[1] * i;
                
                if (newRow < 0 || newRow >= BOARD_SIZE || newCol < 0 || newCol >= BOARD_SIZE) {
                    blocked = true;
                    break;
                }
                
                if (board[newRow][newCol] == player) {
                    count++;
                } else if (board[newRow][newCol] == EMPTY) {
                    space++;
                    break;
                } else {
                    blocked = true;
                    break;
                }
            }
            
            // 反向检查
            for (int i = 1; i <= 4; i++) {
                int newRow = row - dir[0] * i;
                int newCol = col - dir[1] * i;
                
                if (newRow < 0 || newRow >= BOARD_SIZE || newCol < 0 || newCol >= BOARD_SIZE) {
                    blocked = true;
                    break;
                }
                
                if (board[newRow][newCol] == player) {
                    count++;
                } else if (board[newRow][newCol] == EMPTY) {
                    space++;
                    break;
                } else {
                    blocked = true;
                    break;
                }
            }
            
            // 根据棋型评分
            if (count >= 5) {
                score += FIVE;
            } else if (count == 4) {
                if (!blocked) {
                    score += OPEN_FOUR;
                } else if (space > 0) {
                    score += FOUR;
                }
            } else if (count == 3) {
                if (!blocked) {
                    score += OPEN_THREE;
                } else if (space > 0) {
                    score += THREE;
                }
            } else if (count == 2) {
                if (!blocked) {
                    score += OPEN_TWO;
                } else if (space > 0) {
                    score += TWO;
                }
            }
        }
        
        return score;
    }
    
    private static int evaluateAttack(int[][] board, int row, int col, int player) {
        return evaluatePosition(board, row, col, player == BLACK);
    }
    
    private static int evaluateDefense(int[][] board, int row, int col, int opponent) {
        return evaluatePosition(board, row, col, opponent == WHITE);
    }
} 