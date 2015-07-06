import java.util.Scanner;
// Standard chess game.
public class Game {
    //TODO: promotion, check+checkmate detection, castling
    
    public Board board;

    public Game() {
        board = new Board();


    }

    public static void main(String[] args) {
        Game g = new Game();
        g.runConsole();


    }

    // Playing Chess in console
    public void runConsole() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Checkmate detection not implemented. Exit with ctrl+C.");
        while (true) {
            while (true) { // yes I'm lazy
                System.out.println("Current board:");
                printBoard(null);
                System.out.println("Select a piece.");
                String sq = sc.nextLine();
                Piece selected = null;
                try {
                    int[] rf = Board.convFromNot(sq);
                    selected = board.board[rf[0]][rf[1]];
                    if (selected == null) {
                        System.out.println("That square is empty.");
                        continue;
                    }
                    if (selected.color != board.turn) {
                        System.out.println("That piece is the wrong color.");
                        continue;
                    }
                    printBoard(selected);
                } catch (Exception e) {
                    System.out.println("Invalid piece selection.");
                    continue;
                }
                System.out.println("Select a destination, or select an invalid one to deselect.");
                String dest = sc.nextLine();
                try {
                    int[] rf = Board.convFromNot(sq);
                    if (!board.move(sq, dest)) {
                        System.out.println("Deselected.");
                        continue;
                    }
                    break;


                } catch (Exception e) {
                    System.out.println("Deselected.");
                    continue;
                }
            }
            checkPromotions();

        }
            






    }

    public void printBoard(Piece selected) {
        System.out.println("Turn: "+board.turn);
        boolean[][] moves;
        if (selected == null) {
            moves = new boolean[board.board.length][board.board[0].length];
        } else {
            moves = selected.getMoves(board.board);
        }
        for (int i = board.board.length - 1; i >= 0; i--) {
            for (int j =0; j < board.board[i].length; j++) {
                String mchar = moves[i][j]?"x":"."; // except not actually a char                    
                if (selected != null && selected.rank == i && selected.file == j) mchar = "!";
                if (board.board[i][j] == null) {
                    System.out.print(mchar + ".." + mchar);
                } else {
                    System.out.print(mchar+board.board[i][j].color+board.board[i][j].symbol+mchar);
                }
                System.out.print("  ");
            }

            System.out.println();
        }

    }

    public void checkPromotions() {
        for (int i = 0; i < board.board[0].length; i++) {
            if (board.board[0][i] != null && board.board[0][i].name.equals("Pawn")) promote(0, i);
            if (board.board[7][i] != null && board.board[7][i].name.equals("Pawn")) promote(7, i);
        }
    }
    public void promote(int rank, int file) {
        System.out.println("Choose piece to promote to.");
        int c = board.board[rank][file].color;
        Scanner sc = new Scanner(System.in);
        String cin = sc.nextLine();
        Piece piece = Piece.getPiece(c, cin, rank, file);
        while (piece == null || piece.symbol.equals("K") || piece.symbol.equals("P")) {
            if (piece == null) {
                System.out.println("Invalid input.");
            } else {
                System.out.println("Can't promote to that.");
            }
            cin = sc.nextLine();
            piece = Piece.getPiece(c, cin, rank, file);
        }
        board.board[rank][file] = piece;
    }

    public int[] findKing(int c) { // Finds a king of the given color
        // or returns null if none exists.
        for (int i = 0; i < board.board.length; i++) {
            for (int j = 0; j < board.board[i].length; j++) {
                if (board.board[i][j] != null && 
                        board.board[i][j].symbol.equals("K") && 
                        board.board[i][j].color == c) {
                    int[] out = new int[2];
                    out[0] = i;
                    out[1] = j;
                    return out;
                }
            }
        }
        return null;
    }

    public boolean checkCheck(int c) { // returns whether the king
        // of the specifie color is in check. Returns false if no
        // such king exists. Returns this for the bottom (and then left)
        // king if multiple kings exit.
        int[] loc = findKing(c);
        for (int i = 0; i < board.board.length; i++) {
            for (int j = 0; j < board.board[i].length; j++) {
                if (board.board[i][j] == null || 
                        board.board[i][j].color == c) 
                    continue;
                Piece p = board.board[i][j];
                if (p.getMoves(board.board)[loc[0]][loc[1]]) return true;
            }
        }
        return false;
    }





}
