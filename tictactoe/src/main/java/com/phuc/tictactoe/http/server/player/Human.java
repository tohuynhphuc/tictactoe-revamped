package com.phuc.tictactoe.http.server.player;

import java.io.PrintWriter;
import java.util.Scanner;

import com.phuc.tictactoe.http.board.Board;
import com.phuc.tictactoe.http.exception.GameQuitException;
import com.phuc.tictactoe.http.util.Constants;

public class Human extends Player {

    private final Scanner scanner;
    private final PrintWriter output;

    public Human(String name, Scanner scanner, PrintWriter printWriter) {
        super(name);
        this.scanner = scanner;
        this.output = printWriter;
    }

    @Override
    public int makeMove(Board board) throws GameQuitException {
        int cell = -1;

        boolean isValidMove = false;
        while (!isValidMove) {
            String playersInput = scanner.nextLine();

            if (playersInput.equalsIgnoreCase("q")) {
                throw new GameQuitException();
            }

            try {
                cell = Integer.parseInt(playersInput);
            } catch (NumberFormatException e) {
                output.println(Constants.CELL_INVALID);
                continue;
            }

            if (!board.isMoveInRange(cell)) {
                output.println(Constants.CELL_INVALID);
                continue;
            }

            if (!board.isCellEmpty(cell)) {
                output.println(Constants.CELL_OCCUPIED);
                continue;
            }

            isValidMove = true;
        }

        return cell;
    }

}
