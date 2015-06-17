import java.awt.image.BufferedImage;

public abstract class Piece {
    // May consider making some of these private
    public String symbol;
    public String name;
    public int color; // 0 for white, 1 for black. Indexes into icons[].
    public BufferedImage[] icons;
    public int rank; // 0 through 7 converts to 1 through 8
    public int file; // 0 through 7 converts to a through h

    // Returns a boolean array of whether the target square is a possible
    // move destination.
    public abstract boolean[][] getMoves(Piece[][] board);

    public BufferedImage getIcon() {
        return icons[color];
    }

    public String getLocation() {
        if (rank == -1 || file == -1) return null;
        return "" + ((char) file + 'a') + (rank + 1);
    }

    public void setLocation(String loc) {
        if (loc == null) {
            rank = -1;
            file = -1;
        } else {
            rank = loc.charAt(1) - '1';
            file = loc.charAt(0) - 'a';
        }
    }

    public void setLocation(int r, int f) {
        rank = r;
        file = f;
    }





}
