package game;

//Standard chessboard.
public class Board {
    // Board orientation:
    // (0, 0) is bottom left
    // first coordinate is rank, second is file
    // so relative to arrays, the board is upside-down
    public Piece[][] board;
    public int turn; // corresponds to color

    
    public Board() {
        board = new Piece[8][8];
        resetBoard();
        turn = 0;
        


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
        
    // Returns true if the move was valid (and executed)
    // and false if invalid.
    private boolean move(Piece piece, String dest) {
        int[] destarr = convFromNot(dest);
        if (!piece.getMoves(board)[destarr[0]][destarr[1]]) return false; // invalid move attempt
        if (board[destarr[0]][destarr[1]] == null) {
            // System.out.println("Moving.");
            if (piece.name.equals("Pawn") && piece.file != destarr[1]) {
                // En Passant
                // System.out.println("EP!");
                capture(piece, board[piece.rank][destarr[1]]);
            } else {
                if (piece.symbol.equals("K") && Math.abs(piece.file - destarr[1]) == 2) {
                    // Castling, move the rook
                    if (piece.file < destarr[1]) { // Kingside
                        board[piece.rank][5] = board[piece.rank][7];
                        board[piece.rank][7] = null;
                        board[piece.rank][5].setLocation(piece.rank, 5);
                    } else { // Queenside
                        board[piece.rank][3] = board[piece.rank][0];
                        board[piece.rank][0] = null;
                        board[piece.rank][3].setLocation(piece.rank, 3);
                    }
                } 
                board[destarr[0]][destarr[1]] = piece;
                board[piece.rank][piece.file] = null;
                piece.setLocation(dest);
                turn = (turn + 1) % 2;
            }
        } else {
            capture(piece, board[destarr[0]][destarr[1]]);
        }
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                if (board[i][j] != null && board[i][j] != piece) 
                    board[i][j].setLocation(i, j);
            }
        }
        return true;
    }

    // Same return behavior.
    public boolean move(String from, String to) {
        int[] fromarr = convFromNot(from);
        if (board[fromarr[0]][fromarr[1]] == null) {
            return false;
        }
        Piece piece = board[fromarr[0]][fromarr[1]];
        if (piece.color != turn) {
            return false;
        }
        return move(piece, to);
    }   

    public boolean move(int fromrank, int fromfile, int torank, int tofile) {
        if (board[fromrank][fromfile] == null) {
            return false;
        }
        Piece piece = board[fromrank][fromfile];
        if (piece.color != turn) {
            return false;
        }
        return move(piece, convToNot(torank, tofile));
    }
        

    // Uses the given peice to capture the target piece.
    private void capture(Piece piece, Piece targ) {
        board[piece.rank][piece.file] = null;
        if (piece.name.equals("Pawn") && piece.rank == targ.rank) {
            int newrank = targ.rank + 1 - 2 * piece.color;
            board[newrank][targ.file] = piece;
            board[targ.rank][targ.file] = null;
            piece.setLocation(newrank, targ.file);
            

        } else {
            board[targ.rank][targ.file] = piece;
            piece.setLocation(targ.rank, targ.file);
        }
        targ.setLocation(-1, -1);
        turn = (turn + 1) % 2;

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
        return "" + ((char) (c + 'a')) + (r + 1);
    }

    public void place(int color, String name, int r, int c) {
        board[r][c] = Piece.getPiece(color, name, r, c);
    }

    public void place(int color, String name, String loc) {
        int[] conv = convFromNot(loc);
        place(color, name, conv[0], conv[1]);
    }
}

