package com.phuc.tictactoe.basic.player;

import java.util.Scanner;

import com.phuc.tictactoe.basic.board.Board;

public class Human extends Player {

    private final Scanner scanner;

    public Human(String name, Scanner scanner) {
        super(name);
        this.scanner = scanner;
    }

    @Override
    public int makeMove(Board board) {
        int cell = -1;

        boolean isValidMove = false;
        while (!isValidMove) {
            try {
                cell = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid move");
                continue;
            }

            isValidMove = board.isValidMove(cell);
            if (!isValidMove) {
                System.out.println("Invalid move.");
            }
        }

        return cell;
    }

}
