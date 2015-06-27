public class Knight extends Piece {
    public Knight(int color) {
        super("Knight", "N", color);
    }

    public Knight(int color, String location) {
        this(color);
        setLocation(location);
    }

    public Knight(int color, int rank, int file) {
        this(color);
        setLocation(rank, file);
    }

    public boolean[][] getMoves(Piece[][] board) {
        boolean[][] out = new boolean[board.length][board[0].length];
        for (int i = 0; i < out.length; i++) {
            for (int j = 0; j < out[0].length; j++) {
                if (Math.abs((j - file) * (i - rank)) == 2) {
                    out[i][j] = board[i][j] == null?true:board[i][j].color != color;
                } else {
                    out[i][j] = false;
                }
            }
        }
        return out;
    }
}
