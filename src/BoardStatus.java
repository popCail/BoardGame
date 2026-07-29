import java.util.ArrayDeque;
import java.util.Deque;

/**
 * 棋盘状态：棋子布局、轮次与胜负判定。
 */
public class BoardStatus {

    private static final BoardStatus INSTANCE = new BoardStatus();

    /** 四个方向：横、竖、主对角、副对角 */
    private static final int[][] DIRECTIONS = {
            {1, 0},
            {0, 1},
            {1, 1},
            {1, -1}
    };

    /** 0 无棋，1 黑棋，-1 白棋 */
    public int[][] chess = new int[Value.ROW_NUM + 1][Value.ROW_NUM + 1];

    /** 下一手颜色：1 黑，-1 白 */
    public int nextChess = 1;

    /** 落子历史（栈），用于悔棋 */
    public Deque<int[]> playChessQueue = new ArrayDeque<>();

    public boolean canPlayNext = false;

    private BoardStatus() {
    }

    public static BoardStatus getInstance() {
        return INSTANCE;
    }

    /**
     * 取出当前应落子颜色，并切换下一手。
     */
    public int getNextChessType() {
        int res = nextChess;
        nextChess = -nextChess;
        return res;
    }

    /**
     * 清空棋盘并开始新局。
     */
    public void clearBoard() {
        canPlayNext = true;
        playChessQueue = new ArrayDeque<>();
        nextChess = 1;
        chess = new int[Value.ROW_NUM + 1][Value.ROW_NUM + 1];
    }

    /**
     * 悔棋：撤销最近一手。
     */
    public void withdraw() {
        canPlayNext = true;
        if (!playChessQueue.isEmpty()) {
            int[] res = playChessQueue.pop();
            nextChess = -nextChess;
            chess[res[0]][res[1]] = 0;
        }
    }

    /**
     * 在指定位置落子。
     *
     * @return -2 不能落子；0 未分出胜负；1 黑胜；-1 白胜
     */
    public int addChess(int row, int col) {
        if (!canPlayNext || !isOnBoard(row, col) || chess[row][col] != 0) {
            return -2;
        }
        int chessType = getNextChessType();
        playChessQueue.push(new int[]{row, col});
        chess[row][col] = chessType;
        int winTest = winCheck(row, col, chessType);
        if (winTest != 0) {
            canPlayNext = false;
        }
        return winTest;
    }

    /**
     * 判断是否已满盘（和棋）。
     */
    public boolean isBoardFull() {
        int capacity = (Value.ROW_NUM + 1) * (Value.ROW_NUM + 1);
        return playChessQueue.size() >= capacity;
    }

    private boolean isOnBoard(int row, int col) {
        return row >= 0 && row <= Value.ROW_NUM && col >= 0 && col <= Value.ROW_NUM;
    }

    /**
     * @return 0 未赢；否则返回获胜方颜色
     */
    private int winCheck(int row, int col, int type) {
        for (int[] dir : DIRECTIONS) {
            int count = 1
                    + countInDirection(row, col, type, dir[0], dir[1])
                    + countInDirection(row, col, type, -dir[0], -dir[1]);
            if (count >= Value.WIN_NUMBER) {
                return type;
            }
        }
        return 0;
    }

    /**
     * 沿 (dRow, dCol) 方向统计连续同色棋子数（不含落点本身）。
     */
    private int countInDirection(int row, int col, int type, int dRow, int dCol) {
        int count = 0;
        int r = row + dRow;
        int c = col + dCol;
        int limit = Value.WIN_NUMBER - 1;
        while (count < limit && isOnBoard(r, c) && chess[r][c] == type) {
            count++;
            r += dRow;
            c += dCol;
        }
        return count;
    }
}
