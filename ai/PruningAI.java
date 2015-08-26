package ai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import game.*;

public class PruningAI extends BasicAI {

    protected int select = 5; // number of moves to select for review


    public int[] getMove(int c, Piece[][] pieces, Board board) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            System.out.println("why you wake me up");
        }
        depth = 0;
        evals = 0;
        int[] best = recurse2(100000, c, pieces, board, 1);
        System.out.println("Best score found: "+best[5]);
        System.out.println("Move: "+Arrays.toString(best));
        System.out.println("Evaluations made: "+evals);

        return Arrays.copyOf(best, 4);


    }


    // A pruning version of recurse.
    public int[] recurse2(int levels, int c, Piece[][] pieces, Board board, int depth) {
        if (this.depth < depth) {
            this.depth = depth;
            System.out.println("Reached depth: "+depth);
            System.out.println("Levels remaining: "+levels);
        }

        ArrayList<int[]> moves = Game.getMoves(c, pieces);
        if (levels < moves.size()) {
            int[] out = new int[6];
            out[5] = evaluate(c, pieces, board);
            return out;
        }
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
                        int score = evaluate(c, sim2, board);
                        if (score <= minscore) minscore = score;
                        if (!promoted2) 
                            // we didn't actually promote, don't bother with
                            // the other possibilities
                            break;
                        // else System.out.println("Tested enemy promotion: "+s2);
                    }
                }
                int[] aug = Arrays.copyOf(move, move.length + 2);
                aug[move.length] = pr;
                aug[move.length + 1] = minscore;
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
        for (int i = 0; i < select; i++) {
            int[] move = movesarr[i];
            Piece[][] sim = Board.copyBoard(pieces);
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
            if (minscore >= best[5]) {
                move[5] = minscore;
                best = move;
            }
        }
        return best;




        /*
        while (levels > moves.size()) {
            // Lost of move sequences to be considered, each appended with the score.
            ArrayList<int[]> newmoves = new ArrayList<int[]>();
            for (int[] move : moves) {
                for (int pr = 0; pr < 4; pr++) {
                    Piece[][] sim = Board.copyBoard(pieces);
                    int[] aug = Arrays.copyOf(move, move.length + 1);
                    aug[move.length] = pr;
                    Piece p = sim[aug[aug.length - 5]][aug[aug.length - 4]];
                    AIUtils.simulateSequence(aug, sim, board);
                    if (Game.findKing(1-c, sim) == null) {
                        if (Game.findKing(c, sim) != null) {
                            int[] out = Arrays.copyOf(move, 5);
                            out[4] = 1000000;
                            return out;// win
                        }
                        else {
                            newmoves.add(Arrays.copyOf(aug, aug.length + 1) // draw, score = 0
                            break;
                        }
                    }
                    boolean promoted = (sim[aug[aug.length - 3]][aug[aug.length - 2]] != null && sim[aug[aug.length - 3]][aug[aug.length - 2]] != p);
                    int minscore = Integer.MAX_VALUE;
                    ArrayList<int[]> threats = Game.getMoves(1 - c, sim);
                    for (int[] threat : threats) {
                        for (String s2 : promarr) {
                            Piece[][] sim2 = Board.copyBoard(sim);
                            Piece p2 = sim2[threat[0]][threat[1]];
                            board.simulate(threat, sim2, s2);
                            if (Game.findKing(c, sim2) == null) {
                                if (Game.findKing(1-c, sim2) != null) minscore = -1000000; // loss
                                else {
                                    if (minscore > 0) minscore = 0; // draw
                                    break;
                                }
                            }
                            boolean promoted2 = (sim2[threat[2]][threat[3]] != null && sim2[threat[2]][threat[3]] != p2);
                            int score = evaluate(c, sim2, board);
                            if (score <= minscore) minscore = score;
                            if (!promoted2) 
                                // we didn't actually promote, don't bother with
                                // the other possibilities
                                break;
                            else System.out.println("Tested enemy promotion: "+s2);
                        }
                    }
                    int[] aug2 = Arrays.copyOf(aug, aug.length + 1);
                    aug2[aug.length] = minscore;
                    newmoves.add(aug2);
                    if (!promoted) 
                        break; 
                    else System.out.println("Tested promotion: "+pr);
                }
            }
            int[][] movesarr = newmoves.toArray();
            Arrays.sort(movesarr, new MoveComparator());
        }
        */
        // check diagonostics later /////



    }



}

class MoveComparator implements Comparator<int[]> {
    // Takes two arrays, compares their last element. Sorts descending.
    public int compare(int[] a, int[] b) {
        return b[b.length - 1] - a[a.length - 1];
    }
    
}
