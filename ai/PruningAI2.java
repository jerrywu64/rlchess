package ai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import game.*;

public class PruningAI2 extends PruningAI {

    public int[] getMove(int c, Piece[][] pieces, Board board) {
        select = 4;
        recevals = new int[10];
        depth = 0;
        evals = 0;
        int[] best = recurse2(10000, c, pieces, board, 1);
        System.out.println("Best score found: "+best[5]);
        System.out.println("Move: "+Arrays.toString(best));
        System.out.println("Evaluations made: "+evals);
        System.out.println("Recurses: "+Arrays.toString(recevals));


        return Arrays.copyOf(best, 4);


    }

    // Returns best instead of truncating it. For usage by BookAI.
    public int[] getMove2(int c, Piece[][] pieces, Board board) {
        recevals = new int[10];
        depth = 0;
        evals = 0;
        int[] best = recurse2(10000, c, pieces, board, 1);
        System.out.println("Best score found: "+best[5]);
        System.out.println("Move: "+Arrays.toString(best));
        System.out.println("Evaluations made: "+evals);
        System.out.println("Recurses: "+Arrays.toString(recevals));

        return best;
    }





    protected int recurse3(int levels, int c, Piece[][] sim, Board board, int depth) {
        if (Game.getMoves(1 - c, sim).size() > levels / 5) return directeval(c, sim, board, depth); 
        else return -recurse2(levels / 5, 1 - c, sim, board, depth + 1)[5];
    }

    /*
    protected int directeval(int c, Piece[][] sim, Board board, int depth) {
        return evaluate(c, sim, board, depth);
    }*/







}
