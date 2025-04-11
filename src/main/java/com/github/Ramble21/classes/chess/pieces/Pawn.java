package com.github.Ramble21.classes.chess.pieces;

import com.github.Ramble21.classes.Direction;
import com.github.Ramble21.classes.Location;
import com.github.Ramble21.classes.chess.Board;
import com.github.Ramble21.classes.chess.Piece;

import java.util.HashSet;

public class Pawn extends Piece {
    public Pawn(boolean white, Location startPos) {
        super(1, white, startPos);
    }
    public HashSet<Location> getLegalMoves(Board b) {
        HashSet<Location> result = new HashSet<>();
        Location forward = getCurrentPosition().getDirectionalLoc(Direction.UP);
        if (b.hasNoPiece(forward)) {
            result.add(forward);
            Location twoForward = forward.getDirectionalLoc(Direction.UP);
            if (getCurrentPosition().x == (white() ? 6 : 1) && b.hasNoPiece(twoForward)) {
                result.add(twoForward);
            }
        }
        Location captureA = getCurrentPosition().getDirectionalLoc(white() ? Direction.UPLEFT : Direction.DOWNLEFT);
        Location captureB = getCurrentPosition().getDirectionalLoc(white() ? Direction.UPRIGHT : Direction.DOWNRIGHT);
        if (captureA.isOnGrid(b.getBoard()) && b.hasPieceAt(!white(), captureA)) {
            result.add(captureA);
        }
        if (captureB.isOnGrid(b.getBoard()) && b.hasPieceAt(!white(), captureB)) {
            result.add(captureB);
        }
        return result;
    }
}
