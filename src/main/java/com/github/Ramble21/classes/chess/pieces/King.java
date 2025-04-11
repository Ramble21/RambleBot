package com.github.Ramble21.classes.chess.pieces;

import com.github.Ramble21.classes.Direction;
import com.github.Ramble21.classes.Location;
import com.github.Ramble21.classes.chess.Board;
import com.github.Ramble21.classes.chess.Piece;

import java.util.HashSet;

public class King extends Piece {
    public King(boolean white, Location startPos) {
        super(Integer.MAX_VALUE, white, startPos);
    }
    public HashSet<Location> getLegalMoves(Board b) {
        HashSet<Location> result = new HashSet<>();
        Direction[] dirs = new Direction[]{Direction.UP, Direction.RIGHT, Direction.LEFT, Direction.DOWN,
                Direction.UPLEFT, Direction.UPRIGHT, Direction.DOWNLEFT, Direction.DOWNRIGHT};
        for (Direction dir : dirs) {
            Location nextOut = getCurrentPosition().getDirectionalLoc(dir);
            if (b.hasNoPiece(nextOut)) {
                result.add(nextOut);
                nextOut = nextOut.getDirectionalLoc(dir);
            }
            else if (b.hasPieceAt(!white(), nextOut)) {
                result.add(nextOut);
            }
        }
        return result;
    }
}
