package com.phuc.tictactoe.online.sust;

import java.util.Scanner;

import com.phuc.tictactoe.online.sust.player.Computer;
import com.phuc.tictactoe.online.sust.player.Human;
import com.phuc.tictactoe.online.sust.player.Player;

/**
 * Tic Tac Toe App!
 */
public class App {

    public static void main(String[] args) {
        if (!isValidArguments(args)) {
            System.out.println(Constants.INVALID_ARGUMENTS);
            return;
        }

        Scanner scanner = new Scanner(System.in);

        Player playerOne = new Human("1", scanner);
        Player playerTwo = new Computer("2");

        Game game = new Game(processArguments(args), playerOne, playerTwo);

        Thread gameThread = new Thread(() -> {
            game.start();
        });

        gameThread.start();
    }

    /**
     * Processes the command line arguments. There should be only one argument,
     * which is either a 1 or a 2.
     * 
     * @param args the command line arguments
     * @return the processes argument (1 or 2)
     */
    private static int processArguments(String[] args) {
        if (null == args[0]) {
            return -1;
        } else {
            return switch (args[0]) {
                case "1" -> 1;
                case "2" -> 2;
                default -> -1;
            };
        }
    }

    /**
     * Checks if the command line arguments are valid. There should be only one
     * argument, which is either a 1 or a 2.
     * 
     * @param args the command line arguments
     * @return whether the arguments are valid
     */
    private static boolean isValidArguments(String[] args) {
        if (args.length != 1)
            return false;
        return "1".equals(args[0]) || "2".equals(args[0]);
    }

}
