package com.phuc.tictactoe.basic;

import com.phuc.tictactoe.basic.board.Board;
import com.phuc.tictactoe.basic.player.Player;

/**
 * The game instance.
 */
public class Game {

    private final Board board;

    private int currentPlayerId;
    private final Player playerOne;
    private final Player playerTwo;

    /**
     * Initializes the game with the first player.
     * 
     * @param firstPlayer the first player (1-2)
     */
    public Game(int firstPlayer, Player playerOne, Player playerTwo) {
        board = new Board();

        currentPlayerId = firstPlayer;
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
    }

    /**
     * Starts the game.
     */
    public void start() {
        welcome();
        startGameLoop();

        System.out.println("Game ends!");
    }

    /**
     * Starts the game loop.
     */
    private void startGameLoop() {
        boolean isGameEnd = false;

        while (!isGameEnd) {
            currentPlayerMakeMove();

            if (board.checkWin()) {
                System.out.println("Player " + currentPlayerId + " wins!");
                isGameEnd = true;
            } else if (board.isFull()) {
                System.out.println("It's a tie!");
                isGameEnd = true;
            }

            switchCurrentPlayerId();
        }
    }

    /**
     * The current players make a move.
     */
    private void currentPlayerMakeMove() {
        Player currentPlayer = currentPlayerId == 1 ? playerOne : playerTwo;

        board.display();
        System.out.println(currentPlayer.getName() + "\s turn: ");

        int move = currentPlayer.makeMove(board);
        board.setCell(move, currentPlayerId);
    }

    /**
     * Prints welcome message.
     */
    private void welcome() {
        System.out.println("Welcome!");
    }

    /**
     * Switches the current player ID.
     */
    private void switchCurrentPlayerId() {
        currentPlayerId = currentPlayerId == 1 ? 2 : 1;
    }

}
