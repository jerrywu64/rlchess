package ai;

import game.*;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Arrays;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;


public class BookAI extends GeneticAI {
    // It's called BookAI, though it actually builds a game tree,
    // not an opening book. Based on GeneticAI, attempts to solve.
    // It does build a cache, though, which I guess is like a temporary
    // opening book.
    // Static, so that we can run multiple BoardAIs which collaboratively
    // build the tree.
    //
    protected static HashMap<String, int[]> gametree;
    private boolean updatingTree; // We might not want to actually build on
                                 // the tree to avoid concurrent modification issues.
                                 // False by default.
    protected HashMap<String, int[]> cache; // Caches moves for speed. 5th entry is the probability of making the move.
    public static final int[] LOSE = {-1, -1, -1, -1, 0, -699999}; // Represents a lost game in the game tree.

    public BookAI(int[] p) {
        super(p);
        gametree = new HashMap<String, int[]>();
        cache = new HashMap<String, int[]>();
        updatingTree = false;
        try {
            Scanner sc = new Scanner(new File("ai/gametree.txt"));
            while (sc.hasNext()) {
                String nextboard = sc.next();
                int[] move = new int[6];
                for (int i = 0; i < 6; i++) {
                    move[i] = sc.nextInt();
                }                    
                if (!gametree.containsKey(nextboard)) gametree.put(nextboard, move);
                if (move[0] >= 0) cache.put(nextboard, move);
            }
            System.out.println("Game tree loaded.");
            System.out.println("Solving status: "+ checkGameSolved());
        } catch (FileNotFoundException e) {
            System.out.println("Error: couldn't read gametree.txt!");
        }
        // printGameTree();
    }
    public BookAI() {
        this(GeneticEvolution.getGeneticParams());
    }
    public BookAI(boolean update) {
        this();
        updatingTree = update;
    }
    public BookAI(boolean update, int sel) {
        this(update);
        select = sel;
    }

    // Save the gametree after each move and cache, but otherwise proceed as usual.
    public int[] getMove(int c, Piece[][] pieces, Board board) {
        int[] cachepull = cache.get(convToString(c, pieces));
        if (cachepull != null && Math.random() * 1000 < cachepull[4]) {
            System.out.println("Returning cached move...");
            Piece[][] sim = Board.copyBoard(pieces);
            board.simulate(cachepull, sim, "Q");
            directeval(c, sim, board, 1); // Should help build the game tree a bit, though unnecessary for 
                                          // computing the output.
            directeval(1-c, pieces, board, 0);
            return Arrays.copyOf(cache.get(convToString(c, pieces)), 4);
        }
        if (retrieve(c, pieces) != null && retrieve(c, pieces)[0] < 0) {
            System.out.println("Detected forced loss. Reducing move probabilities in cache, returning without computing.");
            for (String key : cache.keySet()) {
                cache.get(key)[4] = (int) (cache.get(key)[4] * 0.99);
            }
            directeval(1-c, pieces, board, 0); // Again, for building game tree
            return Game.getMoves(c, pieces).get(0);
        }
        int[] out = getMove2(c, pieces, board);
        out[4] = 990; // Overriding promotion item with cache-use probability.
        saveGameTree();
        if (out[5] < -600000) {
            // lost, so flush cache
            System.out.println("Loss detected. Reducing move probabilities in cache.");
            for (String key : cache.keySet()) {
                cache.get(key)[4] = (int) (cache.get(key)[4] * 0.99);
            }
        } else {
            cache.put(convToString(c, pieces), out);
        }
        return Arrays.copyOf(out, 4);
    }

    // Checks if the board is in the game tree, and returns appropriately if so.
    // Otherwise, it evaluates as per GeneticAI.
    protected int evaluate(int c, Piece[][] pieces, Board board) {
        int[] b = retrieve(c, pieces);
        if (b == null) return super.evaluate(c, pieces, board);
        else if (b[0] >= 0) return 700000;
        else return -700000;
    }

    // We're not changing the evaluation, but we'll add a detector
    // which allows us to expand the game tree.
    protected int directeval(int c, Piece[][] sim, Board board, int depth) {
        int[] out = super.directeval2(c, sim, board, depth);
        if (out == null) return Integer.MAX_VALUE; // keeping with original behavior, says enemy is out of moves
        // Note that when we call directeval for c, that's after c just moved.
        if (-out[5] > 690000) updateTree(1-c, sim, LOSE); // Win!, so loss for the other player
        if (-out[5] < -690000) {
            updateTree(1-c, sim, out); // Loss :(
            Piece[][] pieces = Board.copyBoard(sim);
            board.simulate(out, pieces, "Q");
            updateTree(c, pieces, LOSE); // And mark the result as a lost position
        }
        return -out[5];
    }



    protected int[] retrieve(int c, Piece[][] pieces) {
        // Retrieves the specified board from the game tree.
        // Returns true if it's a winning board, false if it's
        // a losing board, null if neither.
        return gametree.get(convToString(c, pieces));
    }

    // Converts a board (with current turn) to a String in a manner
    // consistent with board.toString().
    public String convToString(int c, Piece[][] pieces) {
        String out = "" + c;
        for (Piece[] row : pieces) {
            for (Piece p : row) {
                if (p == null) out += "x";
                else out += p.toString();
            }
        }
        return out;
    }


    // Updates the gametree if we're updating the tree, does nothing
    // otherwise.
    protected void updateTree(int c, Piece[][] pieces, int[] move) {
        if (updatingTree) gametree.put(convToString(c, pieces), move);
    }

    public static void printGameTree() {
        System.out.println("Printing gametree:");
        for (String key : gametree.keySet()) {
            System.out.println(key + " " + Arrays.toString(gametree.get(key))+":");
            Board b = new Board(key);
            System.out.println((b.turn == 0?"White":"Black") + " to move");
            Piece[][] pieces = b.getBoardCopy();
            for (int i = 7; i >= 0; i--) {
                for (int j = 0; j < 8; j++) {
                    if (pieces[i][j] == null) {
                        System.out.print("  ");
                    } else {
                        System.out.print(pieces[i][j].getColor()+pieces[i][j].getSymbol());
                    }
                }
                System.out.println();
            }
        }
        System.out.println("End of gametree.");
    }

    private static void saveGameTree() {
        System.out.println("Saving gametree. Size: "+gametree.size());
        try {
            PrintWriter fout = new PrintWriter("ai/gametree.txt");
            for (String key : gametree.keySet()) {
                fout.print(key + " ");
                for (int i = 0; i < 6; i++) {
                    fout.print(gametree.get(key)[i] + " ");
                }
                fout.println();
            }
            fout.close();
            System.out.println("Successfully saved gametree.");
        } catch (Exception e) {
            System.out.println("Error saving gametree; aborting.");
        }
    }

    // Returns null if not solved, or True or False in Boolean form if solved.
    public static String checkGameSolved() {
        String start = "0WRa100WNb100WBc100WQd100WKe100WBf100WNg110WRh100WPa200WPb200WPc200WPd200WPe200WPf200WPg200WPh200xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxBPa700BPb700BPc700BPd700BPe700BPf700BPg700BPh700BRa800BNb800BBc800BQd800BKe800BBf800BNg810BRh800";
        return Arrays.toString(gametree.get(start));
    }




}
