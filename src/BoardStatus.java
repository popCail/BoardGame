import java.util.LinkedList;

public class BoardStatus {

    //单例模式，只有一个棋盘
    private static BoardStatus boardStatus = new BoardStatus();

    //返回棋盘对象
    public static BoardStatus getInstance() {
        return boardStatus;
    }

    //16 * 16 , 0 表示没有棋子，1表示有黑棋， -1表示有白棋
    public int[][] chess = new int[Value.ROW_NUM + 1][Value.ROW_NUM + 1];

    //下一个可以增加的棋子的颜色,1表示黑棋， -1表示白棋
    public int nextChess = 1;
    //下棋队列，记录下棋的顺序，用于撤回功能
    public LinkedList<int[]> playChessQueue = new LinkedList<>();

    //用于标记现在是否能下棋
    public boolean canPlayNext = false;

    /**
     * 用于下入棋子的类型
     *
     * @return 返回下入棋子的类型
     */
    public int getNextChessType() {
        int res = nextChess;
        nextChess = -nextChess;
        return res;
    }


    /**
     * 清空棋盘上的棋子，并重新设置相关初始值,开始新游戏
     */
    public void clearBoard() {
        canPlayNext = true;
        playChessQueue = new LinkedList<>();
        nextChess = 1;
        chess = new int[Value.ROW_NUM + 1][Value.ROW_NUM + 1];
    }

    /**
     * 用于悔棋
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
     * 返回 -2 表示不能下棋, 返回0表示不能判断输赢，返回1表示黑棋赢，返回-1表示白棋赢，
     *
     * @param row
     * @param col
     * @return
     */
    public int addChess(int row, int col) {
        if (!canPlayNext) {
            return -2;
        }
        boolean canAdd = chess[row][col] == 0;
        if (!canAdd) {
            return -2;
        }
        int getAddChessType = getNextChessType();
        playChessQueue.push(new int[]{row, col});
        chess[row][col] = getAddChessType;
        int winTest = winCheck(row, col, getAddChessType);
        if (winTest != 0) {
            canPlayNext = false;
        }
        return winTest;
    }

    /**
     * 返回0表示不能判断输赢，返回1表示黑棋赢，返回-1表示白棋赢
     *
     * @param row  新增棋子的横坐标
     * @param col  新增棋子的纵坐标
     * @param type 新增棋子的类型
     * @return 返回输赢状态
     */
    private int winCheck(int row, int col, int type) {
        int r = winCheckRow(row, col, type);
        int c = winCheckCol(row, col, type);
        int x = winCheckX(row, col, type);
        int y = winCheckY(row, col, type);
        if (r != 0 || c != 0 || x != 0 || y != 0) {
            return type;
        }
        return 0;
    }

    /**
     * 判断斜线是否有赢,左下到右上
     *
     * @param row  新增棋子的横坐标
     * @param col  新增棋子的纵坐标
     * @param type 新增棋子的类型
     * @return 返回输赢状态
     */
    private int winCheckY(int row, int col, int type) {
        int n = 1;
        for (int i = row + 1, j = col - 1; i < Value.ROW_NUM + 1 && i <= row + (Value.WIN_NUMBER - 1) && j >= 0 && j >= col - (Value.WIN_NUMBER - 1); i++, j--) {
            if (chess[i][j] == type) {
                ++n;
            } else {
                break;
            }
            if (n == Value.WIN_NUMBER) {
                return type;
            }
        }
        for (int i = row - 1, j = col + 1; i >= 0 && i >= row - (Value.WIN_NUMBER - 1) && j < Value.ROW_NUM + 1 && j <= col + (Value.WIN_NUMBER - 1); i--, j++) {
            if (chess[i][j] == type) {
                ++n;
            } else {
                break;
            }
            if (n == Value.WIN_NUMBER) {
                return type;
            }
        }

        return 0;
    }

    /**
     * 判断斜线是否有赢,左上到右下
     *
     * @param row  新增棋子的横坐标
     * @param col  新增棋子的纵坐标
     * @param type 新增棋子的类型
     * @return 返回输赢状态
     */
    private int winCheckX(int row, int col, int type) {
        int n = 1;
        for (int i = row - 1, j = col - 1; i >= 0 && j >= 0 && i >= row - (Value.WIN_NUMBER - 1) && j >= col - (Value.WIN_NUMBER - 1); i--, j--) {
            if (chess[i][j] == type) {
                ++n;
            } else {
                break;
            }
            if (n == Value.WIN_NUMBER) {
                return type;
            }
        }
        for (int i = row + 1, j = col + 1; i < Value.ROW_NUM + 1 && i <= row + (Value.WIN_NUMBER - 1) && j < Value.ROW_NUM + 1 && j <= col + (Value.WIN_NUMBER - 1); i++, j++) {
            if (chess[i][j] == type) {
                ++n;
            } else {
                break;
            }
            if (n == Value.WIN_NUMBER) {
                return type;
            }
        }

        return 0;
    }

    /**
     * 判断竖线是否有赢
     *
     * @param row  新增棋子的横坐标
     * @param col  新增棋子的纵坐标
     * @param type 新增棋子的类型
     * @return 返回输赢状态
     */
    private int winCheckCol(int row, int col, int type) {
        int n = 1;
        for (int i = col - 1; i > 0 && i >= col - (Value.WIN_NUMBER - 1); i--) {
            if (chess[row][i] == type) {
                ++n;
            } else {
                break;
            }
            if (n == Value.WIN_NUMBER) {
                return type;
            }
        }
        for (int i = col + 1; i < Value.ROW_NUM + 1 && i <= col + (Value.WIN_NUMBER - 1); i++) {
            if (chess[row][i] == type) {
                ++n;
            } else {
                break;
            }
            if (n == Value.WIN_NUMBER) {
                return type;
            }
        }

        return 0;
    }

    /**
     * 判断横线是否有赢
     *
     * @param row  新增棋子的横坐标
     * @param col  新增棋子的纵坐标
     * @param type 新增棋子的类型
     * @return 返回输赢状态
     */
    private int winCheckRow(int row, int col, int type) {
        int n = 1;
        for (int i = row - 1; i > 0 && i >= row - (Value.WIN_NUMBER - 1); i--) {
            if (chess[i][col] == type) {
                ++n;
            } else {
                break;
            }
            if (n == Value.WIN_NUMBER) {
                return type;
            }
        }
        for (int i = row + 1; i < Value.ROW_NUM + 1 && i <= row + (Value.WIN_NUMBER - 1); i++) {
            if (chess[i][col] == type) {
                ++n;
            } else {
                break;
            }
            if (n == Value.WIN_NUMBER) {
                return type;
            }
        }
        return 0;
    }
}
