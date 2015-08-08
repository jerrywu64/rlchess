package ai;

import game.Piece;

public interface AI {

    public int[] getMove(int c, Piece[][] board);

}
