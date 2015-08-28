package ai;

import java.util.ArrayList;
import java.util.Arrays;
import game.*;

public class BasicAI implements AI {

    protected int depth = 0;
    protected int evals = 0;
    protected final String[] promarr = {"Q", "R", "N", "B"};

    public int[] getMove(int c, Piece[][] pieces, Board board) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            System.out.println("why you wake me up");
        }
        ArrayList<int[]> moves = Game.getMoves(c, pieces);
        depth = 0;
        evals = 0;
        int bestscore = Integer.MIN_VALUE;
        int[] bestmove = null;
        for (int[] move : moves) {
            for (String s : promarr) {
                Piece[][] sim = Board.copyBoard(pieces);
                Piece p = sim[move[0]][move[1]];
                board.simulate(move, sim, s);
                if (Game.findKing(1-c, sim) == null) {
                    if (Game.findKing(c, sim) != null) {
                        System.out.println("Win detected.");
                        return move;// win
                    }
                    else {
                        if (bestscore < 0) {
                            bestscore = 0; // draw
                            bestmove = move;
                        }
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
                            if (Game.findKing(1-c, sim2) != null) minscore = -1000000; // loss
                            else {
                                if (minscore > 0) minscore = 0; // draw
                                break;
                            }
                        }
                        boolean promoted2 = (sim2[threat[2]][threat[3]] != null && sim2[threat[2]][threat[3]] != p2);
                        int score = recurse(100000 / moves.size() / threats.size(), c, sim2, board, 1);
                        if (score <= minscore) minscore = score;
                        if (!promoted2) 
                            // we didn't actually promote, don't bother with
                            // the other possibilities
                            break;
                        else System.out.println("Tested enemy promotion: "+s2);
                    }
                }
                if (minscore >= bestscore) {
                    bestscore = minscore;
                    bestmove = move;
                }
                if (!promoted) 
                    break; 
                else System.out.println("Tested promotion: "+s);
            }
        }
        System.out.println("Best score found: "+bestscore);
        System.out.println("Move: "+Arrays.toString(bestmove));
        System.out.println("Evaluations made: "+evals);

        return bestmove;
    }

    // Recurses, heuristically explores approximately --levels-- possibilities
    protected int recurse(int levels, int c, Piece[][] pieces, Board board, int depth) {
        if (this.depth < depth) {
            this.depth = depth;
            System.out.println("Reached depth: "+depth);
            System.out.println("Levels remaining: "+levels);
        }
        // restructuring is needed
        ArrayList<int[]> moves = Game.getMoves(c, pieces);
        if (moves.size() > levels) 
            // The heuristic here will be off by however many moves
            // the opponent can make, but whatever. The starting value
            // can be adjusted.
            return evaluate(c, pieces, board);
        int bestscore = Integer.MIN_VALUE;
        for (int[] move : moves) {
            for (String s : promarr) {
                Piece[][] sim = Board.copyBoard(pieces);
                Piece p = sim[move[0]][move[1]];
                board.simulate(move, sim, s);
                if (Game.findKing(1-c, sim) == null) {
                    if (Game.findKing(c, sim) != null) return 1000000 - depth; // win
                    else {
                        if (bestscore < 0) bestscore = 0; // draw
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
                        int score = recurse(levels / moves.size() / threats.size(), c, sim2, board, depth + 1);
                        if (score <= minscore) minscore = score;
                        if (!promoted2) 
                            // we didn't actually promote, don't bother with
                            // the other possibilities
                            break;
                        // else System.out.println("Tested enemy promotion: "+s2);
                    }
                }
                if (minscore >= bestscore) 
                    bestscore = minscore;
                if (!promoted) 
                    break; 
                // else System.out.println("Tested promotion: "+s);
            }
        }
        return bestscore;
    }

    // Make this nonpublic later
    protected int evaluate(int c, Piece[][] pieces, Board board) {
        evals++;
        return getMaterial(c, pieces) + 7 * checkThreats(c, pieces, board) - 4 * checkThreats(1-c, pieces, board);
        //

    }

    protected int getMaterial(int c, Piece[][] pieces) {
        int material = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (pieces[i][j] != null) {
                    if (pieces[i][j].getColor() == c) material += getValue(pieces[i][j]);
                    else material -= getValue(pieces[i][j]);
                }
            }
        }
        return material;
    }


    protected int getValue(Piece p) {
        if (p.getSymbol().equals("K")) return 250;
        if (p.getSymbol().equals("Q")) return 50;
        if (p.getSymbol().equals("R")) return 10;
        if (p.getSymbol().equals("B")) return 5;
        if (p.getSymbol().equals("N")) return 8;
        return 1;
    }

    protected int checkThreats(int c, Piece[][] pieces, Board board) {
        int out = 0;
        int mat = getMaterial(c, pieces);
        ArrayList<int[]> moves = Game.getMoves(c, pieces);
        for (int[] move : moves) {
            out += threatFunc(mat + checkRLMaterialDelta(move, pieces));
            // System.out.println(Arrays.toString(move) + " " + threatFunc(mat + checkRLMaterialDelta(move, pieces)));
        }
        // System.out.println(out);
        // System.out.println("--");
        return out;
    }

    // Figures out how the material balance changes for a move in
    // RLChess specifically. 
    protected int checkRLMaterialDelta(int[] move, Piece[][] pieces) {
        int out = 0;
        int c = pieces[move[0]][move[1]].getColor();
        if (pieces[move[2]][move[3]] == null) {
            if (pieces[move[0]][move[1]].getSymbol().equals("P") &&
                    move[2] == 7 - 7 * c) return getValue(Piece.getPiece(0, "Q"));
            else return 0;
        } else {
            for (int i = -1; i < 2; i++) {
                for (int j = -1; j < 2; j++) {
                    if (move[2] + i >= 0 &&
                            move[2] + i < pieces.length &&
                            move[3] + j >= 0 &&
                            move[3] + j < pieces.length) {
                        if (pieces[move[2] + i][move[3] + j] == null) continue;
                        else {
                            out += (c == pieces[move[2] + i][move[3] + j].getColor()?
                                -1:1) * getValue(pieces[move[2] + i][move[3] + j]);
                            // System.out.println((c == pieces[move[2] + i][move[3] + j].getColor()?
                            //    -1:1) * getValue(pieces[move[2] + i][move[3] + j]));

                        }
                    }
                }
            }
        }
        // System.out.println(out);
        // System.out.println();
        return out;
    }


    // Transforms material from a threat into a more useful form.
    protected int threatFunc(int in) {
        // Modified logistic curve
        return (int) (100./(1+100 * Math.pow(2, -in/5.))) + 1;
    }


            


}
