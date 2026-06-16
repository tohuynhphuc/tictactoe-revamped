package com.phuc.tictactoe.basic.board;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BoardTest {

    private Board board;

    @BeforeEach
    public void setUp() {
        board = new Board();
    }

    @Test
    public void shouldReturnNineCells() {
        assertEquals(9, board.getNumCells(), "Board should have 9 cells");
    }

    @Test
    public void shouldInitializeAllCellsAsEmpty() {
        for (int cell = 1; cell <= board.getNumCells(); cell++) {
            assertTrue(board.isCellEmpty(cell), "Cell " + cell + " should be empty initially");
        }
    }

    @Test
    public void shouldReturnTrueForValidMoveRange() {
        for (int move = 1; move <= 9; move++) {
            assertTrue(board.isMoveInRange(move), "Move " + move + " should be in range");
        }
    }

    @Test
    public void shouldReturnFalseForOutOfRangeMoves() {
        assertFalse(board.isMoveInRange(0), "Move 0 should be out of range");
        assertFalse(board.isMoveInRange(-1), "Move -1 should be out of range");
        assertFalse(board.isMoveInRange(-10), "Move -10 should be out of range");

        assertFalse(board.isMoveInRange(10), "Move 10 should be out of range");
        assertFalse(board.isMoveInRange(11), "Move 11 should be out of range");
        assertFalse(board.isMoveInRange(100), "Move 100 should be out of range");
    }

    @Test
    public void shouldReturnTrueForEmptyCell() {
        assertTrue(board.isCellEmpty(5), "Cell 5 should be empty");
        board.setCell(5, 1);
        assertFalse(board.isCellEmpty(5), "Cell 5 should not be empty after setting");
    }

    @Test
    public void shouldAllowValidMovesAndBlockInvalid() {
        assertTrue(board.isValidMove(1), "Move on empty cell should be valid");
        board.setCell(1, 1);
        assertFalse(board.isValidMove(1), "Move on occupied cell should be invalid");
        assertFalse(board.isValidMove(0), "Move out of range should be invalid");
        assertFalse(board.isValidMove(10), "Move out of range should be invalid");
    }

    @Test
    public void shouldSetCellValueCorrectly() {
        board.setCell(1, 1);
        assertFalse(board.isCellEmpty(1), "Cell should be occupied after setCell");
        board.setCell(5, 2);
        board.setCell(9, 1);
        assertFalse(board.isCellEmpty(5), "Cell 5 should be occupied");
        assertFalse(board.isCellEmpty(9), "Cell 9 should be occupied");
        assertTrue(board.isCellEmpty(2), "Cell 2 should be empty");
    }

    @Test
    public void shouldDetectWinOnTopRow() {
        board.setCell(1, 1);
        board.setCell(2, 1);
        board.setCell(3, 1);
        assertTrue(board.checkWin(), "Player 1 should win with top row");
    }

    @Test
    public void shouldDetectWinOnMiddleRow() {
        board.setCell(4, 1);
        board.setCell(5, 1);
        board.setCell(6, 1);
        assertTrue(board.checkWin(), "Player 1 should win with middle row");
    }

    @Test
    public void shouldDetectWinOnBottomRow() {
        board.setCell(7, 1);
        board.setCell(8, 1);
        board.setCell(9, 1);
        assertTrue(board.checkWin(), "Player 1 should win with bottom row");
    }

    @Test
    public void shouldDetectWinWithPlayer2OnRow() {
        board.setCell(1, 2);
        board.setCell(2, 2);
        board.setCell(3, 2);
        assertTrue(board.checkWin(), "Player 2 should win with row");
    }

    @Test
    public void shouldDetectWinOnLeftColumn() {
        board.setCell(1, 1);
        board.setCell(4, 1);
        board.setCell(7, 1);
        assertTrue(board.checkWin(), "Player 1 should win with left column");
    }

    @Test
    public void shouldDetectWinOnMiddleColumn() {
        board.setCell(2, 1);
        board.setCell(5, 1);
        board.setCell(8, 1);
        assertTrue(board.checkWin(), "Player 1 should win with middle column");
    }

    @Test
    public void shouldDetectWinOnRightColumn() {
        board.setCell(3, 1);
        board.setCell(6, 1);
        board.setCell(9, 1);
        assertTrue(board.checkWin(), "Player 1 should win with right column");
    }

    @Test
    public void shouldDetectWinWithPlayer2OnColumn() {
        board.setCell(1, 2);
        board.setCell(4, 2);
        board.setCell(7, 2);
        assertTrue(board.checkWin(), "Player 2 should win with column");
    }

    // ===== Diagonal Win Tests =====
    @Test
    public void shouldDetectWinOnMainDiagonal() {
        board.setCell(1, 1);
        board.setCell(5, 1);
        board.setCell(9, 1);
        assertTrue(board.checkWin(), "Player 1 should win with main diagonal");
    }

    @Test
    public void shouldDetectWinOnAntiDiagonal() {
        board.setCell(3, 1);
        board.setCell(5, 1);
        board.setCell(7, 1);
        assertTrue(board.checkWin(), "Player 1 should win with anti-diagonal");
    }

    @Test
    public void shouldDetectWinWithPlayer2OnMainDiagonal() {
        board.setCell(1, 2);
        board.setCell(5, 2);
        board.setCell(9, 2);
        assertTrue(board.checkWin(), "Player 2 should win with main diagonal");
    }

    @Test
    public void shouldDetectWinWithPlayer2OnAntiDiagonal() {
        board.setCell(3, 2);
        board.setCell(5, 2);
        board.setCell(7, 2);
        assertTrue(board.checkWin(), "Player 2 should win with anti-diagonal");
    }

    @Test
    public void shouldReturnFalseForEmptyBoard() {
        assertFalse(board.checkWin(), "Empty board should have no win");
    }

    @Test
    public void shouldReturnFalseWithMixedPlayers() {
        board.setCell(1, 1);
        board.setCell(2, 2);
        board.setCell(3, 1);
        board.setCell(4, 2);
        board.setCell(5, 2);
        board.setCell(6, 1);
        board.setCell(7, 1);
        board.setCell(8, 1);
        board.setCell(9, 2);
        // 1 2 1
        // 2 2 1
        // 1 1 2

        assertFalse(board.checkWin(), "Should be a draw");
    }

    @Test
    public void shouldReturnFalseWhenBoardIsEmpty() {
        assertFalse(board.isFull(), "Empty board should not be full");
    }

    @Test
    public void shouldReturnFalseWhenBoardIsPartiallyFilled() {
        board.setCell(1, 1);
        board.setCell(2, 2);
        board.setCell(3, 1);
        assertFalse(board.isFull(), "Partially filled board should not be full");
    }

    @Test
    public void shouldReturnTrueWhenAllCellsOccupied() {
        board.setCell(1, 1);
        board.setCell(2, 1);
        board.setCell(3, 1);
        board.setCell(4, 2);
        board.setCell(5, 2);
        board.setCell(6, 2);
        board.setCell(7, 1);
        board.setCell(8, 2);
        board.setCell(9, 1);
        assertTrue(board.isFull(), "Completely filled board should be full");
    }

    // ===== Edge Case Tests =====
    @Test
    public void shouldDisplayBoardWithoutError() {
        board.setCell(1, 1);
        board.setCell(5, 2);
        assertDoesNotThrow(() -> board.display(), "Display should not throw exception");
    }

}
