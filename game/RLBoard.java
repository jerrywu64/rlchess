package game;

public class RLBoard extends Board {
    // Board for Rocket Launcher Chess:
    // When a piece captures, it stays in place,
    // and all pieces in a 3x3 around the target piece
    // will die.

    public RLBoard() {
        super();
    }

    protected void capture(Piece piece, Piece targ) {
        // Back up target location
        int targr = targ.rank;
        int targf = targ.file;
        // Don't worry about en passant being a special case 
        // since everything will die anyway
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if (targr + i >= 0 && targr + i < board.length &&
                        targf + j >= 0 && targf + j < board[0].length &&
                        board[targr + i][targf + j] != null) {
                    board[targr + i][targf + j].setLocation(-1, -1);
                    board[targr + i][targf + j] = null;
                }
            }
        }
        turn = (turn + 1) % 2;
    }

}
