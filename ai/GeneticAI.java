package ai;
import game.*;

public class GeneticAI extends PruningAI2 {

    protected int[] params;
    /*
     * Parameters:
     * 0.  getMaterial multiplier
     * 1.  friendly threat multiplier
     * 2.  enemy threat multiplier
     * 3.  king weight
     * 4.  queen weight
     * 5.  rook weight
     * 6.  bishop weight
     * 7.  knight weight
     * 8.  pawn weight
     * 9.  threatfunc numerator
     * 10. threatfunc denominator constant
     * 11. threatfunc denominator multiplier
     * 12. threatfunc denominator base
     * 13. threatfunc denominator exponent divisor
     * 14. threatfunc constant
    */

    public GeneticAI(int[] p) {
        params = p;
    }

    protected int evaluate(int c, Piece[][] pieces, Board board) {
        evals++;
        return params[0] * getMaterial(c, pieces) + params[1] * checkThreats(c, pieces, board) - params[2] * checkThreats(1-c, pieces, board);
    }

    protected int getValue(Piece p) {
        if (p.getSymbol().equals("K")) return params[3];
        if (p.getSymbol().equals("Q")) return params[4];
        if (p.getSymbol().equals("R")) return params[5];
        if (p.getSymbol().equals("B")) return params[6];
        if (p.getSymbol().equals("N")) return params[7];
        return params[8];
    }

    // Transforms material from a threat into a more useful form.
    protected int threatFunc(int in) {
        // Modified logistic curve
        return (int) ((double) params[9]/(params[10] + params[11] * Math.pow(params[12], -in/(double) params[13]))) + params[14];
    }


}

