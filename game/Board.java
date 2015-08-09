package game;

import java.util.ArrayList;

//Standard chessboard.
public class Board {
    // Board orientation:
    // (0, 0) is bottom left
    // first coordinate is rank, second is file
    // so relative to arrays, the board is upside-down
    protected Piece[][] board;
    public int turn; // whose turn it is, not the move number. Corresponds to color.
    protected int movenum; // Number of moves so far, where it counts a half-move
                           // in standard notation as a single move, so e.g. if black
                           // and white both move then this counter is incremented twice.
                           // Indexes into boardhist.
                           // Kind of useless for now (since it's basically boardhist.size() - 1)
                           // but can conceivably be useful once redoes are allowed.
    protected ArrayList<Piece[][]> boardhist; // Board history. Last entry should be the
                                              // current board as returned by getBoardCopy().
    // TODO protected ArrayList<String> movehist; // Move history, in standard notation.
    
    public Board() {
        board = new Piece[8][8];
        resetBoard();
        turn = 0;
    }

    public Board(String str) {
        board = new Piece[8][8];
        turn = str.charAt(0) - '0';
        str = str.substring(1);
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (str.charAt(0) == 'x') str = str.substring(1);
                else {
                    board[i][j] = Piece.getPiece(str.substring(0, 6));
                    str = str.substring(6);
                }
            }
        }
        movenum = 0;
        boardhist = new ArrayList<Piece[][]>();
        boardhist.add(getBoardCopy());
    }

    public static Piece[][] copyBoard(Piece[][] board) {
        Piece[][] out = new Piece[board.length][board[0].length];
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (board[i][j] != null) out[i][j] = board[i][j].getClone();
            }
        }
        return out;
    }
    public Piece[][] getBoardCopy() {
        return copyBoard(board);
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
        movenum = 0;
        turn = 0;
        boardhist = new ArrayList<Piece[][]>();
        boardhist.add(getBoardCopy());
    }
        
    // Returns true if the move was valid (and executed)
    // and false if invalid.
    private boolean move(Piece piece, String dest) {
        int[] destarr = convFromNot(dest);
        if (!piece.getMoves(board)[destarr[0]][destarr[1]]) return false; // invalid move attempt
        simulate(piece, destarr[0], destarr[1], board, null);
        movenum++;
        turn = (turn + 1) % 2;
        boardhist.add(getBoardCopy());
        System.out.println(this.toString());
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
        

    // Uses the given piece to capture the targeted piece on the given board.
    protected void capture(Piece piece, Piece targ, Piece[][] board) {
        board[piece.rank][piece.file] = null;
        if (piece.getName().equals("Pawn") && piece.rank == targ.rank) {
            int newrank = targ.rank + 1 - 2 * piece.color;
            board[newrank][targ.file] = piece;
            board[targ.rank][targ.file] = null;
            piece.setLocation(newrank, targ.file);
        } else {
            board[targ.rank][targ.file] = piece;
            piece.setLocation(targ.rank, targ.file);
        }
        targ.setLocation(-1, -1);
    }

    public boolean promote(int rank, int file, String symbol, Piece[][] board) {
        // Attempts to promote the piece on the specified
        // rank and file, rejects if it's not a valid promotion.
        // Returns false if rejected, true otherwise.
        // Operates on the specified board.
        Piece p = board[rank][file];
        if (p == null || !p.getSymbol().equals("P") || 
                p.getColor() * 7 != 7 - rank || 
                !symbol.matches("[QRBN]")) return false;
        board[rank][file] = Piece.getPiece(p.color, symbol, rank, file);
        return true;
    }

    public boolean promote(int rank, int file, String symbol) {
        // Same as previous, but operates on this.board.
        if (promote(rank, file, symbol, board)) {
            // Update boardhist
            boardhist.set(movenum, getBoardCopy());
            return true;
        } else return false;
    }

    public void simulate(Piece piece, int torank, int tofile, Piece[][] board, String promote) {
        // Makes the move on the board and attempts to promote the moved piece.
        // The move is assumed to be valid.
        // This isn't static to allow for polymorphism.
        if (board[torank][tofile] == null) {
            if (piece.getName().equals("Pawn") && piece.file != tofile) {
                // En Passant
                capture(piece, board[piece.rank][tofile], board);
            } else {
                if (piece.getSymbol().equals("K") && Math.abs(piece.file - tofile) == 2) {
                    // Castling, move the rook
                    if (piece.file < tofile) { // Kingside
                        board[piece.rank][5] = board[piece.rank][7];
                        board[piece.rank][7] = null;
                        board[piece.rank][5].setLocation(piece.rank, 5);
                    } else { // Queenside
                        board[piece.rank][3] = board[piece.rank][0];
                        board[piece.rank][0] = null;
                        board[piece.rank][3].setLocation(piece.rank, 3);
                    }
                } 
                board[torank][tofile] = piece;
                board[piece.rank][piece.file] = null;
                piece.setLocation(torank, tofile);
            }
        } else {
            capture(piece, board[torank][tofile], board);
        }
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                if (board[i][j] != null && board[i][j] != piece) 
                    board[i][j].setLocation(i, j);
            }
        }
        if (promote != null) promote(torank, tofile, promote, board);
    }

    // Alternate input method, assumes the move is a valid quadruple.
    public void simulate(int[] move, Piece[][] board, String promote) {
        simulate(board[move[0]][move[1]], move[2], move[3], board, promote);
    }
        


    // Undoes the last move. Returns true if this is possible, false if
    // we're at the beginning of the game.
    public boolean undo() {
        if (movenum == 0) return false;
        boardhist.remove(movenum);
        movenum--;
        board = boardhist.get(movenum);
        boardhist.set(movenum, getBoardCopy());
        turn = (turn + 1) % 2;
        return true;
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

    private void place(int color, String name, int r, int c) {
        board[r][c] = Piece.getPiece(color, name, r, c);
    }

    private void place(int color, String name, String loc) {
        int[] conv = convFromNot(loc);
        place(color, name, conv[0], conv[1]);
    }

    public String toString() {
        String out = "" + turn;
        for (Piece[] row : board) {
            for (Piece p : row) {
                if (p == null) out += "x";
                else out += p.toString();
            }
        }
        return out;
    }
}

