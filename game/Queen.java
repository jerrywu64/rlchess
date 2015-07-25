package game;

public class Queen extends Piece {
    public Queen(int color) {
        super("Queen", "Q", color);
    }

    public Queen(int color, String location) {
        this(color);
        setLocation(location);
    }

    public Queen(int color, int rank, int file) {
        this(color);
        setLocation(rank, file);
    }

    public boolean[][] getMoves(Piece[][] board) {
        boolean[][] out = new boolean[board.length][board[0].length];
        /* for (int i = 0; i < out.length; i++) {
            for (int j = 0; j < out[0].length; i++) {
                out[i][j] = false;
            }
        } */
        boolean[] finished = new boolean[8];
        int[] ranksweep = new int[8];
        int[] filesweep = new int[8];
        int remaining = 8;
        for (int i = 0; i < 8; i++) {
            ranksweep[i] = rank;
            filesweep[i] = file;
        }
        while (remaining > 0) {
            // where (0, 0) is bottom left,
            // for 0 through 7, the direction we move starts up
            // and and alternates left and right, moving down
            // so 0 is straight up, 7 is straight down, 3 is left,
            // 4 is right.
            for (int i = 0; i < 8; i++) {
                if (i < 3) ranksweep[i]++;
                if (i > 4) ranksweep[i]--;
                if (i % 7 != 0) {
                    if (i % 2 == 0) filesweep[i]++;
                    if (i % 2 == 1) filesweep[i]--;
                }
                if (!finished[i]) {
                    if (ranksweep[i] < 0 ||
                            ranksweep[i] >= board.length ||
                            filesweep[i] < 0 ||
                            filesweep[i] >= board.length) {
                        finished[i] = true;
                        remaining--;
                    } else {
                        if (board[ranksweep[i]][filesweep[i]] == null) {
                            out[ranksweep[i]][filesweep[i]] = true;
                        } else {
                            out[ranksweep[i]][filesweep[i]] =
                                color != board[ranksweep[i]][filesweep[i]].color;
                            finished[i] = true;
                            remaining--;
                        }
                    }
                }
            }
        }




        /*
        for (int i = rank + 1; i < board.length; i++) {
            if (board[i][file] == null) {
                out[i][file] = true;
            } else {
                out[i][fi;e] = (color != board[i][file].color);
                break;
            }
        }
        for (int i = rank - 1; i > -1; i--) {
            if (board[i][file] == null) {
                out[i][file] = true;
            } else {
                out[i][fi;e] = (color != board[i][file].color);
                break;
            }
        }
        for (int j = file + 1; j < board[0].length; j++) {
            if (board[rank][j] == null) {
                out[rank][j] = true;
            } else {
                out[rank][j] = (color != board[rank][j].color);
                break;
            }
        }
        for (int j = file - 1; j > 0; j--) {
            if (board[rank][j] == null) {
                out[rank][j] = true;
            } else {
                out[rank][j] = (color != board[rank][j].color);
                break;
            }
        }*/

        return out;
    }
}
