package com.phuc.tictactoe.basic.player;

import java.util.Scanner;

import com.phuc.tictactoe.basic.Constants;
import com.phuc.tictactoe.basic.board.Board;
import com.phuc.tictactoe.basic.exception.GameQuitException;

public class Human extends Player {

    private final Scanner scanner;

    public Human(String name, Scanner scanner) {
        super(name);
        this.scanner = scanner;
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
                System.out.println(Constants.CELL_INVALID);
                continue;
            }

            if (!board.isMoveInRange(cell)) {
                System.out.println(Constants.CELL_INVALID);
                continue;
            }

            if (!board.isCellEmpty(cell)) {
                System.out.println(Constants.CELL_OCCUPIED);
                continue;
            }

            isValidMove = true;
        }

        return cell;
    }

}
