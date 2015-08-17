package ai;

import game.*;

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


        

        

    // factory methods

}
