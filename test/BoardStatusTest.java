import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BoardStatusTest {

    private BoardStatus board;

    @BeforeEach
    void setUp() {
        board = BoardStatus.getInstance();
        board.clearBoard();
    }

    @Test
    void clearBoard_resetsStateAndAllowsPlay() {
        assertTrue(board.canPlayNext);
        assertEquals(1, board.nextChess);
        assertTrue(board.playChessQueue.isEmpty());
        assertEquals(0, board.chess[0][0]);
    }

    @Test
    void addChess_placesBlackThenWhiteAlternating() {
        assertEquals(0, board.addChess(7, 7));
        assertEquals(1, board.chess[7][7]);
        assertEquals(-1, board.nextChess);

        assertEquals(0, board.addChess(7, 8));
        assertEquals(-1, board.chess[7][8]);
        assertEquals(1, board.nextChess);
        assertEquals(2, board.playChessQueue.size());
    }

    @Test
    void addChess_rejectsOccupiedOrOutOfBoundsOrWhenNotAllowed() {
        board.canPlayNext = false;
        assertEquals(-2, board.addChess(0, 0));

        board.clearBoard();
        assertEquals(0, board.addChess(0, 0));
        assertEquals(-2, board.addChess(0, 0));
        assertEquals(-2, board.addChess(-1, 0));
        assertEquals(-2, board.addChess(0, Value.ROW_NUM + 1));
    }

    @Test
    void withdraw_undoesLastMoveAndRestoresTurn() {
        board.addChess(3, 3);
        board.addChess(4, 4);
        assertEquals(2, board.playChessQueue.size());

        board.withdraw();
        assertEquals(0, board.chess[4][4]);
        assertEquals(1, board.playChessQueue.size());
        assertEquals(-1, board.nextChess);
        assertTrue(board.canPlayNext);

        board.withdraw();
        assertEquals(0, board.chess[3][3]);
        assertTrue(board.playChessQueue.isEmpty());
        assertEquals(1, board.nextChess);
    }

    @Test
    void withdraw_onEmptyBoard_isSafe() {
        board.withdraw();
        assertTrue(board.playChessQueue.isEmpty());
        assertTrue(board.canPlayNext);
    }

    @Test
    void winCheck_detectsHorizontalWinIncludingBoardEdge() {
        // Place four black stones with whites in between turns, then fifth on edge
        // Black: (0,0)(1,0)(2,0)(3,0)(4,0) — includes index 0 which old code skipped
        placeWithoutWin(0, 0); // B
        placeWithoutWin(0, 1); // W
        placeWithoutWin(1, 0); // B
        placeWithoutWin(1, 1); // W
        placeWithoutWin(2, 0); // B
        placeWithoutWin(2, 1); // W
        placeWithoutWin(3, 0); // B
        placeWithoutWin(3, 1); // W
        assertEquals(1, board.addChess(4, 0)); // B wins horizontally
        assertFalse(board.canPlayNext);
    }

    @Test
    void winCheck_detectsVerticalWin() {
        placeWithoutWin(5, 1); // B
        placeWithoutWin(0, 0); // W
        placeWithoutWin(5, 2); // B
        placeWithoutWin(0, 1); // W
        placeWithoutWin(5, 3); // B
        placeWithoutWin(0, 2); // W
        placeWithoutWin(5, 4); // B
        placeWithoutWin(0, 3); // W
        assertEquals(1, board.addChess(5, 5)); // B wins vertically
    }

    @Test
    void winCheck_detectsMainDiagonalWin() {
        placeWithoutWin(1, 1); // B
        placeWithoutWin(0, 1); // W
        placeWithoutWin(2, 2); // B
        placeWithoutWin(0, 2); // W
        placeWithoutWin(3, 3); // B
        placeWithoutWin(0, 3); // W
        placeWithoutWin(4, 4); // B
        placeWithoutWin(0, 4); // W
        assertEquals(1, board.addChess(5, 5)); // B wins on main diagonal
    }

    @Test
    void winCheck_detectsAntiDiagonalWin() {
        placeWithoutWin(1, 5); // B
        placeWithoutWin(0, 0); // W
        placeWithoutWin(2, 4); // B
        placeWithoutWin(0, 1); // W
        placeWithoutWin(3, 3); // B
        placeWithoutWin(0, 2); // W
        placeWithoutWin(4, 2); // B
        placeWithoutWin(0, 3); // W
        assertEquals(1, board.addChess(5, 1)); // B wins on anti-diagonal
    }

    @Test
    void winCheck_detectsWhiteWin() {
        placeWithoutWin(8, 8); // B
        placeWithoutWin(0, 0); // W
        placeWithoutWin(9, 9); // B
        placeWithoutWin(1, 0); // W
        placeWithoutWin(10, 10); // B
        placeWithoutWin(2, 0); // W
        placeWithoutWin(11, 11); // B
        placeWithoutWin(3, 0); // W
        placeWithoutWin(6, 8); // B (off the diagonal, no black win)
        assertEquals(-1, board.addChess(4, 0)); // W wins horizontally
    }

    @Test
    void addChess_afterWin_isRejected() {
        placeWithoutWin(0, 0);
        placeWithoutWin(0, 1);
        placeWithoutWin(1, 0);
        placeWithoutWin(1, 1);
        placeWithoutWin(2, 0);
        placeWithoutWin(2, 1);
        placeWithoutWin(3, 0);
        placeWithoutWin(3, 1);
        assertEquals(1, board.addChess(4, 0));
        assertEquals(-2, board.addChess(5, 5));
    }

    @Test
    void isBoardFull_falseWhenEmpty() {
        assertFalse(board.isBoardFull());
    }

    private void placeWithoutWin(int row, int col) {
        int result = board.addChess(row, col);
        assertEquals(0, result, "expected unfinished game at (" + row + "," + col + ")");
    }
}
