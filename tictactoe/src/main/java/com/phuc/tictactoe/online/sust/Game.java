package com.phuc.tictactoe.online.sust;

import com.phuc.tictactoe.online.sust.board.Board;
import com.phuc.tictactoe.online.sust.exception.GameQuitException;
import com.phuc.tictactoe.online.sust.player.Player;

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
        try {
            startGameLoop();
        } catch (GameQuitException e) {
            System.out.println(Constants.GAME_END);
        }
    }

    /**
     * Starts the game loop.
     */
    private void startGameLoop() throws GameQuitException {
        boolean isGameEnd = false;

        while (!isGameEnd) {
            currentPlayerMakeMove();

            if (board.checkWin()) {
                board.display();
                System.out.println("Player#" + currentPlayerId + " won!");
                isGameEnd = true;
            } else if (board.isFull()) {
                board.display();
                System.out.println(Constants.DRAW_ANNOUNCEMENT);
                isGameEnd = true;
            }

            switchCurrentPlayerId();
        }
    }

    /**
     * The current players make a move.
     */
    private void currentPlayerMakeMove() throws GameQuitException {
        Player currentPlayer = getCurrentPlayer();

        board.display();
        System.out.println("Player#" + currentPlayerId + "\'s turn");

        int move = currentPlayer.makeMove(board);
        if (move == -1) {
            throw new GameQuitException();
        }
        board.setCell(move, currentPlayerId);
    }

    /**
     * Gets the current player as an object.
     * 
     * @return the current player object
     */
    private Player getCurrentPlayer() {
        return currentPlayerId == 1 ? playerOne : playerTwo;
    }

    /**
     * Prints welcome message.
     */
    private void welcome() {
        System.out.println(Constants.INITIAL_MESSAGE);
    }

    /**
     * Switches the current player ID.
     */
    private void switchCurrentPlayerId() {
        currentPlayerId = currentPlayerId == 1 ? 2 : 1;
    }

}
