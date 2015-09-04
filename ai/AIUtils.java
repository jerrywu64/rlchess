package ai;

import game.*;
import java.util.ArrayList;

public class AIUtils {

    /*
    public static Piece[][] shallowCopyBoard(Piece[][] board) {
        Piece[][] out = new Piece[board.length][board[0].length];
        for (int i = 0; i < out.length; i++) {
            for (int j = 0; j < out[0].length; j++) {
                out[i][j] = board[i][j];
            }
        }
        return 
    */

    // The backup is an independent copy of the board.
    // The template is where we're going to get pieces from, and
    // states of its pieces are checked against the backup.
    // "to" is the board to be modified.
    public static void restoreBoard(Piece[][] backup, Piece[][] template, Piece[][] to) {
        for (int i = 0; i < to.length; i++) {
            for (int j = 0; j < to[0].length; j++) {
                to[i][j] = template[i][j];
                if (to[i][j] == null) continue;
                to[i][j].setLocation(i, j);
                if (!to[i][j].equals(backup[i][j])) {
                    // System.out.println("Cloning: "+to[i][j].toString());
                    to[i][j] = backup[i][j].getClone();
                }
            }
        }
    }

    // Sequence is an array of length 5n, the last one being the promotion piece,
    // indexing into {Q, R, B, N}.
    public static void simulateSequence(int[] sequence, Piece[][] pieces, Board board) {
        String[] promarr = {"Q", "R", "B", "N"};
        for (int i = 0; i < sequence.length / 5; i++) {
            int[] move = new int[4];
            for (int j = 0; j < 4; j++) {
                move[j] = sequence[5 * i + j];
            }
            board.simulate(move, pieces, promarr[sequence[5 * i + 4]]);
        }
    }

    public static boolean isKingKillableRL(int c, Piece[][] board) {
        // Detects if the king of color c can be killed by the enemy in
        // RLChess without the enemy king dying
        int[] k = Game.findKing(c, board);
        int[] ek = Game.findKing(1-c, board);
        if (ek == null) return false;
        if (k == null) return true;
        ArrayList<int[]> targSquares = new ArrayList<int[]>();
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if (k[0] + i >= 0 && k[0] + i < 8 &&
                        k[1] + j >= 0 && k[1] + j < 8 &&
                        (Math.abs(k[0] + i - ek[0]) > 1 ||
                                  Math.abs(k[1] + j - ek[1]) > 1) &&
                        board[k[0] + i][k[1] + j] != null &&
                        board[k[0] + i][k[1] + j].getColor() == c) {
                    int[] sq = new int[2];
                    sq[0] = k[0] + i;
                    sq[1] = k[1] + j;
                    targSquares.add(sq);
                }
            }
        }
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == null || board[i][j].getColor() == c) continue;
                boolean[][] moves = board[i][j].getMoves(board);
                for (int[] sq : targSquares) if (moves[sq[0]][sq[1]]) return true;
            }
        }
        return false;
    }





        

        

    // factory methods

}
