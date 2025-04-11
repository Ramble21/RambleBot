package com.github.Ramble21.classes.chess.pieces;

import com.github.Ramble21.classes.Direction;
import com.github.Ramble21.classes.Location;
import com.github.Ramble21.classes.chess.Board;
import com.github.Ramble21.classes.chess.Piece;

import java.util.HashSet;

public class Knight extends Piece {
    public Knight(boolean white, Location startPos) {
        super(3, white, startPos);
    }
    public HashSet<Location> getLegalMoves(Board b) {
        HashSet<Location> result = new HashSet<>();
        Location current = getCurrentPosition();
        int[][] howDoesTheHorseyMove = new int[][]{{1, 2}, {2, 1}, {1, -2}, {-1, 2}, {2, -1}, {-2, 1}};
        for (int[] delta : howDoesTheHorseyMove) {
            Location potentialMove = new Location(current.x + delta[0], current.y + delta[1]);
            if (potentialMove.isOnGrid(b.getBoard()) && (b.hasNoPiece(potentialMove) || b.hasPieceAt(!white(), potentialMove))) {
                result.add(potentialMove);
            }
        }
        return result;
    }
}
