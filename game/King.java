package game;

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
        if (!moved && !isAttacked(rank, file, board)) { // castling
            int r = color * 7;
            if (canCastleKingside(board)) out[r][6] = true;
            if (canCastleQueenside(board)) out[r][2] = true;
        }
        return out;
    }

    public boolean isAttacked(int r, int c, Piece[][] board) {
        // Detects whether the specified square is attacked by
        // an enemy piece. Used for check detection and castling.
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] == null || 
                        board[i][j].color == color) 
                    continue;
                Piece p = board[i][j];
                if (p.symbol.equals("K")) {
                    if (Math.abs(p.rank - r) < 2 && Math.abs(p.file - c) < 2) return true;
                } else if (p.getMoves(board)[r][c]) return true;
            }
        }
        return false;
    }

    // Assumes the king is not in check and has not moved.
    public boolean canCastleKingside(Piece[][] board) {
        int r = color * 7;
        return board[r][5] == null && !isAttacked(r, 5, board) &&
            board[r][6] == null && !isAttacked(r, 6, board) &&
            board[r][7] != null && board[r][7].symbol.equals("R") &&
            board[r][7].color == color && !board[r][7].moved;
    }
    public boolean canCastleQueenside(Piece[][] board) {
        int r = color * 7;
        return board[r][3] == null && !isAttacked(r, 3, board) &&
            board[r][2] == null && !isAttacked(r, 2, board) &&
            board[r][1] == null &&
            board[r][0] != null && board[r][0].symbol.equals("R") &&
            board[r][0].color == color && !board[r][0].moved;
    }
       
}
