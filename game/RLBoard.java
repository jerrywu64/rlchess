package game;

public class RLBoard extends Board {
    // Board for Rocket Launcher Chess:
    // When a piece captures, it stays in place,
    // and all pieces in a 3x3 around the target piece
    // will die.

    public RLBoard() {
        super();
    }

    public RLBoard(String str) {
        super(str);
    }

    protected void capture(Piece piece, Piece targ, Piece[][] board) {
        // Back up target location
        int targr = targ.rank;
        int targf = targ.file;
        if (piece.getName().equals("Pawn") && piece.getRank() == targ.getRank()) {
            // En passant; the explosion counts as being on the 6th (or 3rd) rank
            targr = targ.rank + targ.getColor() * 2 - 1;
        }
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
    }

}
