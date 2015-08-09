package ai;

import game.Piece;
import game.Board;

public interface AI {

    public int[] getMove(int c, Piece[][] pieces, Board board);

}
