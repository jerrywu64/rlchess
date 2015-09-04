package ai;

import game.*;
import java.util.Arrays;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class GeneticEvolution {

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
     * 15. score penalty to king moves
    */

    public static int[] params;
    public static boolean done = false;
    public static int cycles = 0;


    public static void main(String[] args) throws FileNotFoundException {
        params = new int[16];
        Scanner sc = new Scanner(new File("ai/geneticparams.txt"));
        for (int i = 0; i < 16; i++) {
            params[i] = sc.nextInt();
        }
        sc.close();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                if (!done) {
                    System.out.println("Keyboard interrupt detected.");
                    System.out.println("Cycles completed: "+cycles);
                    System.out.println("Last winner: "+Arrays.toString(params));
                    System.out.println("Outputting to file.");
                    try {
                        PrintWriter p = new PrintWriter("ai/geneticparams.txt");
                        for (int i = 0; i < 16; i++) {
                            p.println(params[i]);
                        }
                        p.close();
                    } catch (FileNotFoundException e) {
                        System.out.println("ERROR: Unable to write to file. Exiting.");
                    }
                }
            }
        });
        for (cycles = 0; cycles < 20; cycles++) {
            System.out.println("=====================");
            System.out.println("Starting new cycle: "+Arrays.toString(params));
            System.out.println("=====================");
            params = evolve(params, 5, (20 - cycles) / 5. + 1);
        }
        System.out.println("Final winner: "+Arrays.toString(params));
        System.out.println("Outputting to file.");
        PrintWriter p = new PrintWriter("ai/geneticparams.txt");
        for (int i = 0; i < 16; i++) {
            p.println(params[i]);
        }
        p.close();
        done = true;



    }

    // Generates num GeneticAIs off of the seed parameters, 
    // randomly multiplying some parameters by something in the range
    // (1/vol, vol) and potentially adding +/- 1.
    // One of the competing AIs will directly use the seed.
    // Returns the winning ai's parameter array.
    public static int[] evolve(int[] seed, int num, double vol) {
        GeneticAI[] ais = new GeneticAI[num];
        System.out.println("Starting AI: "+Arrays.toString(seed));
        ais[0] = new GeneticAI(seed);
        for (int i = 1; i < num; i++) {
            int[] param = new int[seed.length];
            for (int j = 0; j < seed.length; j++) {
                if (Math.random() < 0.4) {
                    if (Math.random() < 0.5) {
                        param[j] = (int) (seed[j] * (Math.random() * (vol - 1) + 1));
                    } else {
                        param[j] = (int) (seed[j] / (Math.random() * (vol - 1) + 1));
                    }
                    param[j] = param[j] + (int) (Math.random() * 3) - 1;
                    if (param[j] < 1) param[j] = 1; // Prevent div0 errors
                } else param[j] = seed[j];
            }
            ais[i] = new GeneticAI(param);
            System.out.println("Generated AI: " + Arrays.toString(param));
        }
        double[] points = new double[num];
        for (int i = 0; i < num; i++) {
            for (int j = 0; j < num; j++) {
                if (i == j) continue;
                System.out.println("==New Match==");
                System.out.println("White: "+Arrays.toString(ais[i].params));
                System.out.println("Black: "+Arrays.toString(ais[j].params));
                Game game = new Game(new RLBoard());
                for (int turn = 0; turn < 120; turn++) {
                    Piece[][] pieces = game.board.getBoardCopy();
                    System.out.println();
                    System.out.println("Turn: " + turn / 2);
                    System.out.println((turn % 2 == 0?"White":"Black") + " to move");
                    for (int r = 7; r >= 0; r--) {
                        for (int f = 0; f < 8; f++) {
                            if (pieces[r][f] == null) System.out.print("..");
                            else System.out.print(pieces[r][f].getColor() + pieces[r][f].getSymbol());
                        }
                        System.out.println();
                    }

                    int[] move;
                    if (turn % 2 == 0) move = ais[i].getMove(0, pieces, game.board);
                    else move = ais[j].getMove(1, pieces, game.board);
                    game.board.move(move[0], move[1], move[2], move[3]);

                    // Promotions?
                    // Check checkmate, draw?
                    if (game.checkCheckmate(1 - turn % 2)) break;
                    if (game.findKing(0, game.board.getBoardCopy()) == null) break;
                    if (game.getMoves(1 - turn % 2, game.board.getBoardCopy()).size() == 0) break;
                }
                if (game.checkCheckmate(0)) {
                    points[j]++;
                    System.out.println("Black wins.");
                }
                else if (game.checkCheckmate(1)) {
                    points[i]++;
                    System.out.println("White wins");
                }
                else {
                    // Compute material ratio using original numbers, the two AIs get 0.5 points
                    // between them

                    int whitemat = 0;
                    int blackmat = 0;
                    Piece[][] pieces = game.board.getBoardCopy();
                    for (int r = 0; r < 8; r++) {
                        for (int f = 0; f < 8; f++) {
                            if (pieces[r][f] != null) {
                                if (pieces[r][f].getColor() == 0) whitemat += getValue(pieces[r][f]);
                                else blackmat += getValue(pieces[r][f]);
                            }
                        }
                    }
                    if (blackmat == 0 && whitemat == 0) {
                        points[i] += 0.25;
                        points[j] += 0.25;
                        System.out.println("Board cleared. 0.25 points given to each.");
                    } else {
                        points[i] += whitemat / 2. / (whitemat + blackmat);
                        points[j] += blackmat / 2. / (whitemat + blackmat);
                        System.out.println("No winner. Points given: " 
                                + (whitemat / 2. / (whitemat + blackmat)) + ", " 
                                + (blackmat / 2. / (whitemat + blackmat)));
                    }
                }
            }
        }
        System.out.println("Evolution complete. Points:");
        System.out.println(Arrays.toString(points));
        double min = -99999999;
        int[] best = null;
        for (int i = 0; i < num; i++) {
            if (points[i] > min) {
                min = points[i];
                best = ais[i].params;
            }
        }
        return best;
    }

    public static int getValue(Piece p) {
        if (p.getSymbol().equals("K")) return 5;
        if (p.getSymbol().equals("Q")) return 50;
        if (p.getSymbol().equals("R")) return 10;
        if (p.getSymbol().equals("B")) return 5;
        if (p.getSymbol().equals("N")) return 8;
        return 1;
    }

                



                
                





}
