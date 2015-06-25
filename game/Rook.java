public class Rook extends Piece {
    public Rook(int color) {
        super("Rook", "R", color);
    }

    public Rook(int color, String location) {
        this(color);
        setLocation(location);
    }

    public Rook(int color, int rank, int file) {
        this(color);
        setLocation(rank, file);
    }

    // Modified from Queen's getMoves.
    public boolean[][] getMoves(Piece[][] board) {
        boolean[][] out = new boolean[board.length][board[0].length];
        boolean[] finished = new boolean[8];
        finished[1] = true;
        finished[2] = true;
        finished[5] = true;
        finished[6] = true;
        int[] ranksweep = new int[8];
        int[] filesweep = new int[8];
        int remaining = 4;
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

        return out;
    }
}
