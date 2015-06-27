public class Pawn extends Piece {
    public boolean doublemoved;
    public Pawn(int color) {
        super("Pawn", "P", color);
    }

    public Pawn(int color, String location) {
        this(color);
        setLocation(location);
    }

    public Pawn(int color, int rank, int file) {
        this(color);
        setLocation(rank, file);
    }
    public void setLocation(String loc) {
        super.setLocation(loc);
        doublemoved = (rank - prrank == 2 - 4 * color);
    }
    public void setLocation(int r, int f) {
        super.setLocation(r, f);
        doublemoved = (rank - prrank == 2 - 4 * color);
    }


    public boolean[][] getMoves(Piece[][] board) {
        boolean[][] out = new boolean[board.length][board[0].length];
        int inc = 1 - 2 * color;
        if (inc + rank < 0 || inc + rank >= board.length) {
            // not sure why this would happen since pawns should promote
            // at this point, but whatever
            return out;
        }
        out[rank + inc][file] = (board[rank + inc][file] == null);
        if (file < board.length - 1) 
            out[rank + inc][file + 1] = (board[rank + inc][file + 1] != null &&
                board[rank + inc][file + 1].color != color);
        if (file > 0) 
            out[rank + inc][file - 1] = (board[rank + inc][file - 1] != null &&
                board[rank + inc][file - 1].color != color);

        // yay doublemoving
        if ((color == 0 && rank == 1) || (color == 1 && rank == 6)) 
            out[rank + 2 * inc][file] = out[rank + inc][file] && 
                board[rank + inc][file] == null;

        // yay en passant
        if ((color == 0 && rank == 4) || (color == 1 && rank == 3)) {
            if (file > 0 && board[rank][file - 1] != null && board[rank][file - 1].name.equals("Pawn")) {
               out[rank + inc][file - 1] = ((Pawn) board[rank][file - 1]).doublemoved;
            }
            if (file < board[0].length - 1 && board[rank][file + 1] != null && board[rank][file + 1].name.equals("Pawn")) {
               out[rank + inc][file + 1] = ((Pawn) board[rank][file + 1]).doublemoved;
            }

        }



        return out;
    }
}
