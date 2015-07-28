package game;

import java.util.Scanner;
// Standard chess game.
public class Game {
    //TODO: promotion, check+checkmate detection, castling
    
    public Board board;

    public Game() {
        board = new Board();
    }
    public Game(Board b) {
        board = b;
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
            String promot = null;
            while (true) { // yes I'm lazy
                System.out.println("Current board:");
                printBoard(null);
                /*
                System.out.println("Select a piece.");
                String sq = sc.nextLine();
                Piece selected = null;
                try {
                    int[] rf = Board.convFromNot(sq);
                    selected = board.getBoardCopy()[rf[0]][rf[1]];
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
                }*/
                String from = null;
                String move = null;
                while (from == null) {
                    System.out.println("Input move.");
                    move = sc.nextLine();
                    from = processMove(move, board.turn);
                    if (from == null) System.out.println("Invalid.");
                    else if (from.equals("ambiguous")) {
                        System.out.println("Ambiguous.");
                        from = null;
                    }
                }
                if (move.charAt(move.length() - 2) == '=') promot = ""+move.charAt(move.length() - 1);
                String dest = from.substring(2);
                from = from.substring(0, 2);
                if (!board.move(from, dest)) System.out.println("Move is apparently invalid but checker failed.");
                else break;

            }
            checkPromotions(promot);
            if (checkCheck(board.turn)) System.out.println("Check!");

        }
    }

    public void printBoard(Piece selected) {
        System.out.println("Turn: "+board.turn);
        boolean[][] moves;
        if (selected == null) {
            moves = new boolean[board.getBoardCopy().length][board.getBoardCopy()[0].length];
        } else {
            moves = selected.getMoves(board.getBoardCopy());
        }
        for (int i = board.getBoardCopy().length - 1; i >= 0; i--) {
            for (int j =0; j < board.getBoardCopy()[i].length; j++) {
                String mchar = moves[i][j]?"x":"."; // except not actually a char                    
                if (selected != null && selected.rank == i && selected.file == j) mchar = "!";
                if (board.getBoardCopy()[i][j] == null) {
                    System.out.print(mchar + ".." + mchar);
                } else {
                    System.out.print(mchar+board.getBoardCopy()[i][j].color+board.getBoardCopy()[i][j].getSymbol()+mchar);
                }
                System.out.print("  ");
            }

            System.out.println();
        }

    }

    public void checkPromotions(String in) {
        for (int i = 0; i < board.getBoardCopy()[0].length; i++) {
            if (board.getBoardCopy()[0][i] != null && board.getBoardCopy()[0][i].getName().equals("Pawn")) promote(0, i, in);
            if (board.getBoardCopy()[7][i] != null && board.getBoardCopy()[7][i].getName().equals("Pawn")) promote(7, i, in);
        }
    }
    public void promote(int rank, int file, String in) {
        Scanner sc = new Scanner(System.in);
        String cin = in;
        if (in == null) {
            System.out.println("Choose piece to promote to.");
            cin = sc.nextLine();
        }
        int c = board.getBoardCopy()[rank][file].color;
        // This should be cleaned up.
        Piece piece = Piece.getPiece(c, cin, rank, file);
        while (piece == null || piece.getSymbol().equals("K") || piece.getSymbol().equals("P")) {
            if (piece == null) {
                System.out.println("Invalid input.");
            } else {
                System.out.println("Can't promote to that.");
            }
            cin = sc.nextLine();
            piece = Piece.getPiece(c, cin, rank, file);
        }
        board.promote(rank, file, cin);
    }

    public int[] findKing(int c) { // Finds a king of the given color
        // or returns null if none exists.
        for (int i = 0; i < board.getBoardCopy().length; i++) {
            for (int j = 0; j < board.getBoardCopy()[i].length; j++) {
                if (board.getBoardCopy()[i][j] != null && 
                        board.getBoardCopy()[i][j].getSymbol().equals("K") && 
                        board.getBoardCopy()[i][j].color == c) {
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
        if (loc == null) return false;
        return ((King) board.getBoardCopy()[loc[0]][loc[1]]).isAttacked(loc[0], loc[1], board.getBoardCopy());
    }

    // Inputs a move in standard notation, and returns 
    // the square of the piece making the move as a String,
    // concatenated with the destination square as a String.
    // returns "ambiguous" if ambiguous, or returns null if
    // the move is impossible.
    public String processMove(String move, int c) {
        int pawn_offset = 2 * c - 1;
        String symbol;
        int file = -1;
        int rank = -1;
        int fromrank = -1;
        int fromfile = -1;
        if (move.matches("[a-h][2-7]") || move.matches("[a-h][18]=[NBRQ]")) { // Regular pawn move (promotion doesn't affect which piece it is)
            file = move.charAt(0) - 'a';
            rank = move.charAt(1) - '1';
            // rewrite this
            symbol = "P";
            if (move.length() == 4 && rank == (c==0?0:7)) return null;        
            fromfile = file;
        } else if (move.matches("[a-h]x?[a-h][2-7]?") || move.matches("[a-h]x?[a-h][18]?=[NBRQ]")) { // pawn capture
            fromfile = move.charAt(0) - 'a';
            int offset = (move.charAt(1) == 'x'?1:0);
            file = move.charAt(1 + offset) - 'a';
            if (move.length() >= 3 + offset && move.charAt(2 + offset) != '=') {
                rank = move.charAt(2 + offset) - '1';
                fromrank = rank + pawn_offset;
            }
            if (move.length() >= 4 + offset) {
                int newfromrank = c==0?6:1;
                if (fromrank != -1 && fromrank != newfromrank) return null;
                fromrank = newfromrank;
            }
            symbol = "P";
            if (fromfile == file) return null;               
        } else if (move.matches("[NBRQK][a-h]?[1-8]?[a-h][1-8]")) { // piece movement
            symbol = "" + move.charAt(0);
            int offset = move.length() - 3;
            if (move.length() == 4) {
                if (Character.isDigit(move.charAt(1))) fromrank = move.charAt(1) - '1';
                else fromfile = move.charAt(1) - 'a';
            } else if (move.length() == 5) {
                fromfile = move.charAt(1) - 'a';
                fromrank = move.charAt(2) - '1';
            }
            file = move.charAt(1 + offset) - 'a';
            rank = move.charAt(2 + offset) - '1';
        } else if (move.matches("[NBRQK][a-h]?[1-8]?x[a-h][1-8]")) { // piece capture
            symbol = "" + move.charAt(0);
            int offset = move.length() - 3;
            if (move.length() == 5) {
                if (Character.isDigit(move.charAt(1))) fromrank = move.charAt(1) - '1';
                else fromfile = move.charAt(1) - 'a';
            } else if (move.length() == 6) {
                fromfile = move.charAt(1) - 'a';
                fromrank = move.charAt(2) - '1';
            }
            file = move.charAt(1 + offset) - 'a';
            rank = move.charAt(2 + offset) - '1';
            if (board.getBoardCopy()[rank][file] == null) return null;
        } else if (move.equals("0-0") || move.equals("0-0-0")) { // castle
            rank = c * 7;
            fromrank = c * 7;
            fromfile = 4;
            file = move.equals("0-0")?6:2;
            symbol = "K";


        } else {
            return null;
        }
        String ret = null;
        // System.out.println("Decoded:");
        // System.out.println("Rank File Fromrank Fromfile Symbol: " + rank + " " + file + " " + fromrank + " " + fromfile + " " + symbol);
        for (int i = 0; i < board.getBoardCopy().length; i++) {
            if (fromrank != -1 && i != fromrank) continue;
            for (int j = 0; j < board.getBoardCopy()[i].length; j++) {
                if (fromfile != -1 && j != fromfile) continue;
                Piece p = board.getBoardCopy()[i][j];
                if (p == null || p.color != c || !p.getSymbol().equals(symbol)) continue;
                if (rank == -1) {
                    for (int k = 0; k < board.getBoardCopy().length; k++) {
                        if (p.getMoves(board.getBoardCopy())[k][file]) {
                            if (ret != null) return "ambiguous";
                            ret = Board.convToNot(i, j) + Board.convToNot(k, file);
                        }
                    }
                } else {
                    if (p.getMoves(board.getBoardCopy())[rank][file]) {
                        if (ret != null) return "ambiguous";
                        ret = Board.convToNot(i, j) + Board.convToNot(rank, file);
                    }
                }
            }
        }
        return ret;

    }




}
