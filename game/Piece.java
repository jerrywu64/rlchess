package game;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;


public abstract class Piece implements Cloneable {
    // May consider making some of these private
    private String symbol;
    private String name;
    protected int color; // 0 for white, 1 for black. Indexes into icons[].
    protected int rank; // 0 through 7 converts to 1 through 8
    protected int file; // 0 through 7 converts to a through h
    private boolean moved; // for castling, indicates whether it has moved yet

    public String getSymbol() { return symbol; }
    public String getName() { return name; }
    public int getColor() { return color; }
    public int getRank() { return rank; }
    public int getFile() { return file; }
    public boolean hasMoved() { return moved; }
    // private BufferedImage[] icons; (deprecated)
    // public BufferedImage getIcon() { return icons[color]; }

    // Returns a boolean array of whether the target square is a possible
    // move destination. Disregards whether it puts the king in check.
    public abstract boolean[][] getMoves(Piece[][] board);


    public String getLocation() {
        /*
        if (rank == -1 || file == -1) return null;
        return "" + ((char) file + 'a') + (rank + 1);
        */
        return Board.convToNot(rank, file);
    }

    public void setLocation(String loc) {
        int[] conv = Board.convFromNot(loc);
        setLocation(conv[0], conv[1]);
    }

    public void setLocation(int r, int f) {
        moved = rank != -1 && (moved || rank != r || file != f);
        rank = r;
        file = f;
    }

    public Piece(String name, String symbol, int color) {
        this.symbol = symbol;
        this.name = name;
        this.color = color;
        String ext = ".png";
        // icons = new BufferedImage[2];
        rank = -1;
        file = -1;
        moved = false;

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
        if (name.equals("king") || name.equals("k")) return new King(color);
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

    public static Piece getPiece(String str) {
        // Assumes the input string is valid.
        if (str.charAt(1) == 'P' && str.charAt(5) == '1') return Pawn.getPawn(str);
        Piece out = getPiece(str.charAt(0) == 'W'?0:1, "" + str.charAt(1), str.substring(2, 4));
        out.moved = str.charAt(4) == '1'?true:false;
        return out;
    }

    public Piece getClone() {
        try {
            return (Piece) super.clone();
        } catch (CloneNotSupportedException e) {
            System.out.println("WARNING: Clone attempt failed, returning self");
            return this;
        }
    }

    public String toString() {
        return (color == 0?"W":"B") + symbol + Board.convToNot(rank, file) + (moved?"1":"0") + "0";
    }






}
