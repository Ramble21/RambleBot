package com.github.Ramble21.classes.chess.pieces;

import com.github.Ramble21.classes.Direction;
import com.github.Ramble21.classes.Location;
import com.github.Ramble21.classes.chess.Board;
import com.github.Ramble21.classes.chess.Piece;

import java.util.HashSet;

public class Rook extends Piece {
    public Rook(boolean white, Location startPos) {
        super(5, white, startPos);
    }
    public HashSet<Location> getLegalMoves(Board b) {
        HashSet<Location> result = new HashSet<>();
        Direction[] dirs = new Direction[]{Direction.UP, Direction.RIGHT, Direction.LEFT, Direction.DOWN};
        for (Direction dir : dirs) {
            Location nextOut = getCurrentPosition().getDirectionalLoc(dir);
            while (nextOut.isOnGrid(b.getBoard())) {
                if (b.hasNoPiece(nextOut)) {
                    result.add(nextOut);
                    nextOut = nextOut.getDirectionalLoc(dir);
                }
                else if (b.hasPieceAt(!white(), nextOut)) {
                    result.add(nextOut);
                    break;
                }
                else {
                    break;
                }
            }
        }
        return result;
    }
}
