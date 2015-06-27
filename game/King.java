public class King extends Piece {
    public King(int color) {
        super("King", "K", color);
    }

    public King(int color, String location) {
        this(color);
        setLocation(location);
    }

    public King(int color, int rank, int file) {
        this(color);
        setLocation(rank, file);
    }

    public boolean[][] getMoves(Piece[][] board) {
        boolean[][] out = new boolean[board.length][board[0].length];
        for (int i = 0; i < out.length; i++) {
            for (int j = 0; j < out[0].length; j++) {
                if (Math.abs(j - file) > 1 || Math.abs(i - rank) > 1) {
                    out[i][j] = false;
                } else if (j == file && i == rank) {
                    out[i][j] = false;
                } else {
                    out[i][j] = true;
                }
                if (board[i][j] != null && board[i][j].color == color) {
                    out[i][j] = false;
                }
            }
        }
        return out;
    }
}
