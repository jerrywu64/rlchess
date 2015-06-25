public class Board {
    // Board orientation:
    // (0, 0) is bottom left
    // first coordinate is rank, second is file
    // so relative to arrays, the board is upside-down
    public Piece[][] board;

    
    public Board() {
        board = new Piece[8][8];
        resetBoard();
        


    }
    public void clearBoard() {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                board[i][j] = null;
            }
        }
    }
    public void resetBoard() {
        clearBoard();
        place(0, "Rook", "a1");
        place(0, "Knight", "b1");
        place(0, "Bishop", "c1");
        place(0, "Queen", "d1");
        place(0, "King", "e1");
        place(0, "Bishop", "f1");
        place(0, "Knight", "g1");
        place(0, "Rook", "h1");
        for (int i = 0; i < 8; i++) {
            place(0, "Pawn", 1, i);
            place(1, "Pawn", 6, i);
        }
        place(1, "Rook", "a8");
        place(1, "Knight", "b8");
        place(1, "Bishop", "c8");
        place(1, "Queen", "d8");
        place(1, "King", "e8");
        place(1, "Bishop", "f8");
        place(1, "Knight", "g8");
        place(1, "Rook", "h8");
    }
        
        

    public static int[] convFromNot(String loc) {
        int[] out = new int[2];
        if (loc == null) {
            out[0] = -1;
            out[1] = -1;
        } else {
            out[0] = loc.charAt(1) - '1';
            out[1] = loc.toLowerCase().charAt(0) - 'a';
        }
        return out;
    }

    public static String convToNot(int r, int c) {
        if (r < 0 || c < 0) return null;
        return "" + ((char) c + 'a') + (r + 1);
    }

    public void place(int color, String name, int r, int c) {
        board[r][c] = Piece.getPiece(color, name, r, c);
    }

    public void place(int color, String name, String loc) {
        int[] conv = convFromNot(loc);
        place(color, name, conv[0], conv[1]);
    }
}

