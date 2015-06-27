import java.util.Scanner;
// Standard chess game.
public class Game {
    //TODO: fix pawn's EP (should capture the pawn)
    
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





}
