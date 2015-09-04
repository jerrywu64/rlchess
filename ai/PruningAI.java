package ai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import game.*;

public class PruningAI extends BasicAI {

    protected int select = 5; // number of moves to select for review
    protected int[] recevals = new int[10];
    protected int kingpen = 300; // Score penalty to king moves


    public int[] getMove(int c, Piece[][] pieces, Board board) {
        recevals = new int[10];
        depth = 0;
        evals = 0;
        int[] best = recurse2(100000, c, pieces, board, 1);
        System.out.println("Best score found: "+best[5]);
        System.out.println("Move: "+Arrays.toString(best));
        System.out.println("Evaluations made: "+evals);
        System.out.println("Recurses: "+Arrays.toString(recevals));

        return Arrays.copyOf(best, 4);


    }


    // A pruning version of recurse. 
    protected int[] recurse2(int levels, int c, Piece[][] pieces, Board board, int depth) {
        ArrayList<int[]> moves = Game.getMoves(c, pieces);
        if (levels < moves.size()) {
            int[] out = new int[6];
            out[5] = evaluate(c, pieces, board, depth);
            return out;
        }
        if (this.depth < depth) {
            this.depth = depth;
            System.out.println("Reached depth: "+depth);
            System.out.println("Levels remaining: "+levels);
        }

        if (Game.findKing(c, pieces) == null) {
            int[] out = new int[6];
            out[5] = -1000000 + depth;
            return out;
        }
        if (moves.size() == 0) return new int[6]; // Draw




        ArrayList<int[]> newmoves = new ArrayList<int[]>();
        for (int[] move : moves) {
            for (int pr = 0; pr < 4; pr++) {
                String s = promarr[pr];
                Piece[][] sim = Board.copyBoard(pieces);
                Piece p = sim[move[0]][move[1]];
                board.simulate(move, sim, s);
                if (Game.findKing(1-c, sim) == null) {
                    if (Game.findKing(c, sim) != null) {
                        int[] out = Arrays.copyOf(move, 6);
                        out[5] = 1000000 - depth;
                        return out;// win
                    } else {
                        newmoves.add(Arrays.copyOf(move, move.length + 2)); // draw, score = 0
                        break;
                    }
                }
                boolean promoted = (sim[move[2]][move[3]] != null && sim[move[2]][move[3]] != p);
                int score = directeval(c, sim, board, depth);
                if (p.getSymbol().equals("K")) score =- kingpen;
                int[] aug = Arrays.copyOf(move, move.length + 2);
                aug[move.length] = pr;
                aug[move.length + 1] = score;
                newmoves.add(aug);
                if (!promoted) 
                    break; 
                // else System.out.println("Tested promotion: "+s);
            }
        }
        int[][] movesarr = newmoves.toArray(new int[1][1]);
        Arrays.sort(movesarr, new MoveComparator());
        int[] best = new int[6];
        best[5] = Integer.MIN_VALUE;
        /*
        for (int i = 7; i >= 0; i--) {
            for (int j = 0; j < 8; j++) {
                if (pieces[i][j] == null) System.out.print("..");
                else System.out.print(pieces[i][j].getColor() + pieces[i][j].getSymbol());
            }
            System.out.println();
        }
        System.out.println(Arrays.deepToString(Arrays.copyOf(movesarr, 5)));
        */
        for (int i = 0; i < Math.min(select, movesarr.length); i++) {
            int[] move = movesarr[i];
            Piece[][] sim = Board.copyBoard(pieces);
            String s = sim[move[0]][move[1]].getSymbol();
            board.simulate(Arrays.copyOf(move, 4), sim, promarr[move[4]]);
            if (Game.findKing(1-c, sim) == null) {
                if (Game.findKing(c, sim) != null) {
                    int[] out = Arrays.copyOf(move, 6);
                    out[5] = 1000000 - depth;
                    return out;// win
                } else {
                    if (best[5] < 0) best = move;  // draw, score = 0
                    continue;
                }
            }
            int minscore = recurse3(levels, c, sim, board, depth);
            if (s.equals("K")) minscore -= kingpen;
            if (minscore >= best[5]) {
                move[5] = minscore;
                best = move;
            }
        }
        return best;
    }

    // Helper for recurse2, handles enemy moves in the recursion phase.
    protected int recurse3(int levels, int c, Piece[][] sim, Board board, int depth) {
        int minscore = Integer.MAX_VALUE;
        ArrayList<int[]> threats = Game.getMoves(1 - c, sim);
        for (int[] threat : threats) {
            for (String s2 : promarr) {
                Piece[][] sim2 = Board.copyBoard(sim);
                Piece p2 = sim2[threat[0]][threat[1]];
                board.simulate(threat, sim2, s2);
                if (Game.findKing(c, sim2) == null) {
                    if (Game.findKing(1-c, sim2) != null) minscore = -1000000 + depth; // loss
                    else {
                        if (minscore > 0) minscore = 0; // draw
                        break;
                    }
                }
                boolean promoted2 = (sim2[threat[2]][threat[3]] != null && sim2[threat[2]][threat[3]] != p2);
                int score = recurse2(levels / 5 / threats.size(), c, sim2, board, depth + 1)[5];
                if (score <= minscore) minscore = score;
                if (!promoted2) 
                    // we didn't actually promote, don't bother with
                    // the other possibilities
                    break;
                // else System.out.println("Tested enemy promotion: "+s2);
            }
        }
        return minscore;
    }

    // Version of evaluate with better diagnostics
    protected int evaluate(int c, Piece[][] pieces, Board board, int depth) {
        recevals[depth]++;
        return evaluate(c, pieces, board);
    }

    // Evaluates a move without recursing more deeply. This version makes the
    // enemy move first before calling evaluate().
    protected int directeval(int c, Piece[][] sim, Board board, int depth) {
        int minscore = Integer.MAX_VALUE;
        ArrayList<int[]> threats = Game.getMoves(1 - c, sim);
        for (int[] threat : threats) {
            for (String s2 : promarr) {
                Piece[][] sim2 = Board.copyBoard(sim);
                Piece p2 = sim2[threat[0]][threat[1]];
                board.simulate(threat, sim2, s2);
                if (Game.findKing(c, sim2) == null) {
                    if (Game.findKing(1-c, sim2) != null) minscore = -1000000 + depth; // loss
                    else {
                        if (minscore > 0) minscore = 0; // draw
                        break;
                    }
                }
                boolean promoted2 = (sim2[threat[2]][threat[3]] != null && sim2[threat[2]][threat[3]] != p2);
                int score = evaluate(c, sim2, board, depth);
                if (score <= minscore) minscore = score;
                if (!promoted2) 
                    // we didn't actually promote, don't bother with
                    // the other possibilities
                    break;
                // else System.out.println("Tested enemy promotion: "+s2);
            }
        }
        return minscore;
    }


}

class MoveComparator implements Comparator<int[]> {
    // Takes two arrays, compares their last element. Sorts descending.
    public int compare(int[] a, int[] b) {
        return b[b.length - 1] - a[a.length - 1];
    }
    
}
