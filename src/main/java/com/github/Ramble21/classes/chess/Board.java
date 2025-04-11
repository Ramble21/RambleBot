package com.github.Ramble21.classes.chess;

import com.github.Ramble21.classes.Location;
import com.github.Ramble21.classes.chess.pieces.*;

public class Board {
    private final Piece[][] board;
    public Board() {
        this.board = new Piece[8][8];
        initStartingRow();
    }
    public boolean hasPieceAt(boolean white, Location l) {
        Piece p = board[l.y][l.x];
        return p != null && p.isColor(white);
    }
    public boolean hasNoPiece(Location l) {
        return board[l.y][l.x] == null;
    }
    public Piece[][] getBoard() {
        return board;
    }
    public String toString() {
        StringBuilder res = new StringBuilder();
        res.append(getCoordEmoji('?'));
        for (char c = 'a'; c <= 'h'; c++) {
            res.append(getCoordEmoji(c));
        }
        res.append(getCoordEmoji('?')).append("\n");
        for (int r = 0; r < board.length; r++) {
            res.append(getCoordEmoji((char)('1' + r)));
            for (int c = 0; c < board[0].length; c++) {
                res.append(getEmoji(board[r][c], new Location(r, c)));
            }
            res.append(getCoordEmoji((char)('1' + r)));
            res.append("\n");
        }
        res.append(getCoordEmoji('?'));
        for (char c = 'a'; c <= 'h'; c++) {
            res.append(getCoordEmoji(c));
        }
        res.append(getCoordEmoji('?'));
        return res.toString();
    }
    private void initStartingRow() {
        board[0][0] = new Rook(false, new Location(0, 0));
        board[0][1] = new Knight(false, new Location(0, 1));
        board[0][2] = new Bishop(false, new Location(0, 2));
        board[0][3] = new Queen(false, new Location(0, 3));
        board[0][4] = new King(false, new Location(0, 4));
        board[0][5] = new Bishop(false, new Location(0, 5));
        board[0][6] = new Knight(false, new Location(0, 6));
        board[0][7] = new Rook(false, new Location(0, 7));

        board[7][0] = new Rook(true, new Location(7, 0));
        board[7][1] = new Knight(true, new Location(7, 1));
        board[7][2] = new Bishop(true, new Location(7, 2));
        board[7][3] = new Queen(true, new Location(7, 3));
        board[7][4] = new King(true, new Location(7, 4));
        board[7][5] = new Bishop(true, new Location(7, 5));
        board[7][6] = new Knight(true, new Location(7, 6));
        board[7][7] = new Rook(true, new Location(7, 7));

        for (int i = 0; i <= 7; i++) {
            board[1][i] = new Pawn(false, new Location(1, i));
            board[6][i] = new Pawn(true, new Location(6, i));
        }
    }
    private String getEmoji(Piece p, Location l) {
        String name = p == null ? "empty" : p.getClass().getSimpleName().toLowerCase();
        if (name.equals("knight")) name = "nig[my lawyer advised me not to finish spelling this]";
        String color = p == null ? "" : p.white() ? "w" : "b";
        String square = (l.getX() % 2 == l.getY() % 2) ? "dark" : "light";
        String key = (p == null ? "empty" : color + name.charAt(0)) + "_" + square;
        return switch (key) {
            case "empty_dark" -> "<:empty_dark:1359974636485480519>";
            case "empty_light" -> "<:empty_light:1359974263494414481>";
            case "wp_dark" -> "<:wp_dark:1359974641258729632>";
            case "wp_light" -> "<:wp_light:1359974277834739782>";
            case "bp_dark" -> "<:bp_dark:1359974253369229431>";
            case "bp_light" -> "<:bp_light:1359974254556348477>";
            case "wn_dark" -> "<:wn_dark:1359974639966749035>";
            case "wn_light" -> "<:wn_light:1359974274093285387>";
            case "bn_dark" -> "<:bn_dark:1359974250659713215>";
            case "bn_light" -> "<:bn_light:1359974252400476412>";
            case "wb_dark" -> "<:wb_dark:1359974638016397472>";
            case "wb_light" -> "<:wb_light:1359974267051311232>";
            case "bb_dark" -> "<:bb_dark:1359974241969242352>";
            case "bb_light" -> "<:bb_light:1359974245664424248>";
            case "wr_dark" -> "<:wr_dark:1359974687878283334>";
            case "wr_light" -> "<:wr_light:1359974284369334565>";
            case "br_dark" -> "<:br_dark:1359974635642421398>";
            case "br_light" -> "<:br_light:1359974260017205339>";
            case "wq_dark" -> "<:wq_dark:1359974642231545966>";
            case "wq_light" -> "<:wq_light:1359974280976273478>";
            case "bq_dark" -> "<:bq_dark:1359974256070492432>";
            case "bq_light" -> "<:bq_light:1359974257182113863>";
            case "wk_dark" -> "<:wk_dark:1359974639228555585>";
            case "wk_light" -> "<:wk_light:1359974270431662192>";
            case "bk_dark" -> "<:bk_dark:1359974246943817828>";
            case "bk_light" -> "<:bk_light:1359974634061037921>";
            default -> throw new RuntimeException();
        };
    }
    private String getCoordEmoji(char c) {
        return switch (c) {
            case '?' -> "<:empty:1360062015015747642>";
            case 'a' -> "<:a_:1360061852381614110>";
            case 'b' -> "<:b_:1360061809859497995>";
            case 'c' -> "<:c_:1360061853438574756>";
            case 'd' -> "<:d_:1360061854499602503>";
            case 'e' -> "<:e_:1360061855409766411>";
            case 'f' -> "<:f_:1360061856764661810>";
            case 'g' -> "<:g_:1360061858236727357>";
            case 'h' -> "<:h_:1360061859276919027>";
            case '1' -> "<:1_:1360061799000576120>";
            case '2' -> "<:2_:1360061800573571202>";
            case '3' -> "<:3_:1360061802422992906>";
            case '4' -> "<:4_:1360061803287150742>";
            case '5' -> "<:5_:1360061804381995199>";
            case '6' -> "<:6_:1360061805367660665>";
            case '7' -> "<:7_:1360061806449655928>";
            case '8' -> "<:8_:1360061807276069043>";
            default -> throw new RuntimeException();
        };
    }

}
