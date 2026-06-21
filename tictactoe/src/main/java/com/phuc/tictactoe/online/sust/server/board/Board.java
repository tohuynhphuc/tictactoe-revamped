package com.phuc.tictactoe.online.sust.server.board;

import java.io.PrintWriter;

public class Board {

    /** Size of the board. Hardcoded to 3. */
    private static final int SIZE = 3;

    /** Total number of cells in the board. Hardcoded to a square board. */
    private static final int NUM_CELLS = SIZE * SIZE;

    /** Array of integers for values in the board. */
    private int[] boardData;

    private final PrintWriter output;

    /**
     * Constructor for Board.
     */
    public Board(PrintWriter printWriter) {
        this.output = printWriter;
        setup();
    }

    /**
     * Initializes the board with all 0s.
     */
    private void setup() {
        boardData = new int[NUM_CELLS];
        for (int i = 0; i < NUM_CELLS; i++) {
            boardData[i] = 0;
        }
    }

    /**
     * Validates the move. The move must be in range of the board and is empty.
     * 
     * @param move the move (1-9)
     * @return whether the move is valid
     */
    public boolean isValidMove(int move) {
        return isMoveInRange(move) && isCellEmpty(move);
    }

    /**
     * Checks if the move is in the board.
     * 
     * @param move the move (1-9)
     * @return whether the move is in the board
     */
    public boolean isMoveInRange(int move) {
        return move >= 1 && move <= 9;
    }

    /**
     * Checks if the cell is empty.
     * 
     * @param move the move (1-9)
     * @return whether the cell is empty
     */
    public boolean isCellEmpty(int move) {
        return getCell(move) == 0;
    }

    /**
     * Checks if there is a win on the board.
     * 
     * @return whether there is a win
     */
    public boolean checkWin() {
        return checkRowWin() || checkColumnWin() || checkDiagonalWin();
    }

    /**
     * Checks if the board is full.
     * 
     * @return whether the board is full
     */
    public boolean isFull() {
        for (int cell : boardData) {
            if (cell == 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if any row has a win.
     * 
     * @return whether there is a win on any row
     */
    private boolean checkRowWin() {
        for (int i = 0; i < SIZE; i++) {
            int firstCell = boardData[toId(i, 0)];
            if (firstCell == 0) {
                continue;
            }

            boolean isThisRowWin = true;

            for (int j = 1; j < SIZE; j++) {
                if (boardData[toId(i, j)] != firstCell) {
                    isThisRowWin = false;
                    break;
                }
            }
            if (isThisRowWin) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if any column has a win.
     * 
     * @return whether there is a win on any column
     */
    private boolean checkColumnWin() {
        for (int j = 0; j < SIZE; j++) {
            int firstCell = boardData[toId(0, j)];
            if (firstCell == 0) {
                continue;
            }

            boolean isThisColWin = true;

            for (int i = 1; i < SIZE; i++) {
                if (boardData[toId(i, j)] != firstCell) {
                    isThisColWin = false;
                    break;
                }
            }
            if (isThisColWin) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the two diagonals has a win.
     * 
     * @return whether there is a win on the two diagonals
     */
    private boolean checkDiagonalWin() {
        // Main diagonal: top-left -> bottom-right
        int firstCell = boardData[toId(0, 0)];
        if (firstCell != 0) {
            boolean isDiagonalWin = true;

            for (int i = 1; i < SIZE; i++) {
                if (boardData[toId(i, i)] != firstCell) {
                    isDiagonalWin = false;
                    break;
                }
            }

            if (isDiagonalWin) {
                return true;
            }
        }

        // Anti-diagonal: top-right -> bottom-left
        firstCell = boardData[toId(0, SIZE - 1)];
        if (firstCell != 0) {
            boolean isDiagonalWin = true;

            for (int i = 1; i < SIZE; i++) {
                if (boardData[toId(i, SIZE - 1 - i)] != firstCell) {
                    isDiagonalWin = false;
                    break;
                }
            }

            if (isDiagonalWin) {
                return true;
            }
        }

        return false;
    }

    /**
     * Gets the current cell.
     * 
     * @param cell the cell (1-9)
     * @return the value on the cell
     */
    private int getCell(int cell) {
        return boardData[toId(cell)];
    }

    /**
     * Sets the current cell to the value.
     * 
     * @param cell     the cell
     * @param playerId the value
     */
    public void setCell(int cell, int playerId) {
        boardData[toId(cell)] = playerId;
    }

    /**
     * Converts a move to the index in the board data.
     * 
     * @param move the move
     * @return the index
     */
    private int toId(int move) {
        return move - 1;
    }

    /**
     * Converts a row-col to the index in the board data.
     * 
     * @param row the row
     * @param col the col
     * @return the index
     */
    private int toId(int row, int col) {
        return row * SIZE + col;
    }

    /**
     * Displays the board.
     */
    public void display() {
        for (int i = 0; i < SIZE; i++) {
            output.print("|");
            for (int j = 0; j < SIZE; j++) {
                output.print(" " + boardData[i * 3 + j] + " ");
                output.print("|");
            }
            output.println();
        }
    }

    public String oneLiner() {
        StringBuilder sb = new StringBuilder();

        sb.append("{");
        for (int i = 0; i < NUM_CELLS; i++) {
            sb.append(boardData[i]).append(i == NUM_CELLS - 1 ? "" : ",");
        }
        sb.append("}");

        return sb.toString();
    }

    public static boolean isOneLiner(String oneLiner) {
        if (oneLiner == null) {
            return false;
        }

        String trimmed = oneLiner.trim();

        if (!trimmed.startsWith("{") || !trimmed.endsWith("}")) {
            return false;
        }

        String contents = trimmed.substring(1, trimmed.length() - 1);
        String[] cellValues = contents.split(",", -1);

        if (cellValues.length != NUM_CELLS) {
            return false;
        }

        for (String cellValue : cellValues) {
            String valueText = cellValue.trim();

            if (valueText.isEmpty()) {
                return false;
            }

            int value;
            try {
                value = Integer.parseInt(valueText);
            } catch (NumberFormatException e) {
                return false;
            }

            if (value < 0 || value > 2) {
                return false;
            }
        }
        return true;
    }

    /**
     * Creates a Board from its one-line representation and assigns an output
     * destination for display().
     *
     * @param oneLiner the serialized board
     * @param output   output used by display()
     * @return the parsed board
     * @throws IllegalArgumentException if the format is invalid
     */
    public static Board fromOneLiner(String oneLiner, PrintWriter output) {
        if (!isOneLiner(oneLiner)) {
            throw new IllegalArgumentException("Invalid board representation: " + oneLiner);
        }

        String trimmed = oneLiner.trim();
        String contents = trimmed.substring(1, trimmed.length() - 1);
        String[] cellValues = contents.split(",", -1);

        Board board = new Board(output);
        for (int i = 0; i < cellValues.length; i++) {
            String valueText = cellValues[i].trim();
            board.boardData[i] = Integer.parseInt(valueText);
        }

        return board;
    }

    public int getNumCells() {
        return NUM_CELLS;
    }

}
