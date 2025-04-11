package com.github.Ramble21.classes.chess;

import com.github.Ramble21.classes.Location;
import net.dv8tion.jda.api.entities.User;

public class Game {
    private final Board board;
    private final User white;
    private final User black;
    private boolean whiteToMove;
    public Game(User white, User black) {
        board = new Board();
        this.white = white;
        this.black = black;
        whiteToMove = true;
    }
    /* return codes:
    0 - successful
    1 - bad input
    2 - illegal move
    3 - pawn promotion
    4 - checkmate
     */
    public int processTurn(String input) {
        String[] squares = input.split("\\s+");
        Location start, end;
        try {
            start = squareToLoc(squares[0]);
            end = squareToLoc(squares[1]);
        } catch (IllegalArgumentException e) {
            return 1;
        }
        return 2;
    }
    public Location squareToLoc(String square) {
        if (square.length() != 2) throw new IllegalArgumentException();
        char file = square.charAt(0);
        char rank = square.charAt(1);
        int x = 8 - (rank - '0');
        int y = file - 'a';
        if (x < 0 || x > 7 || y < 0 || y > 7) throw new IllegalArgumentException();
        return new Location(x, y);
    }

}
