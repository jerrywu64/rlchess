import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;


public abstract class Piece {
    // May consider making some of these private
    public String symbol;
    public String name;
    public int color; // 0 for white, 1 for black. Indexes into icons[].
    public BufferedImage[] icons;
    public int rank; // 0 through 7 converts to 1 through 8
    public int file; // 0 through 7 converts to a through h
    public int prrank; // previous rank, mostly for en passant
    public int prfile; // previous file

    // Returns a boolean array of whether the target square is a possible
    // move destination.
    public abstract boolean[][] getMoves(Piece[][] board);

    public BufferedImage getIcon() {
        return icons[color];
    }

    public String getLocation() {
        /*
        if (rank == -1 || file == -1) return null;
        return "" + ((char) file + 'a') + (rank + 1);
        */
        return Board.convToNot(rank, file);
    }

    public void setLocation(String loc) {
        prrank = rank;
        prfile = file;
        /*
        if (loc == null) {
            rank = -1;
            file = -1;
        } else {
            rank = loc.charAt(1) - '1';
            file = loc.charAt(0) - 'a';
        }
        */
        int[] conv = Board.convFromNot(loc);
        setLocation(conv[0], conv[1]);
    }

    public void setLocation(int r, int f) {
        prrank = rank;
        prfile = file;
        rank = r;
        file = f;
    }

    public Piece(String name, String symbol, int color) {
        this.symbol = symbol;
        this.name = name;
        this.color = color;
        String ext = ".png";
        icons = new BufferedImage[2];
        rank = -1;
        file = -1;
        prrank = -1;
        prfile = -1;

        /* don't have images yet
        try {
            icons[0] = ImageIO.read(new File("White"+name+ext));
            icons[1] = ImageIO.read(new File("Black"+name+ext));
        } catch (IOException e) {
            System.out.println("Error: could not find icons for "+name);
        } */
    }

    // Piece Factories
    public static Piece getPiece(int color, String name) {
        name = name.toLowerCase();
        if (name.equals("king") || name.equals("j")) return new King(color);
        if (name.equals("queen") || name.equals("q")) return new Queen(color);
        if (name.equals("rook") || name.equals("r")) return new Rook(color);
        if (name.equals("bishop") || name.equals("b")) return new Bishop(color);
        if (name.equals("knight") || name.equals("n")) return new Knight(color);
        if (name.equals("pawn") || name.equals("p")) return new Pawn(color);
        return null;
    }

    public static Piece getPiece(int color, String name, String loc) {
        Piece out = getPiece(color, name);
        if (out != null) out.setLocation(loc);
        return out;
    }

    public static Piece getPiece(int color, String name, int r, int c) {
        Piece out = getPiece(color, name);
        if (out != null) out.setLocation(r, c);
        return out;
    }






}
