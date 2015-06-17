import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class King extends Piece {
    public King(int color) {
        this.color = color;
        symbol = "K";
        name = "King";
        String ext = ".png";
        icons = new BufferedImage[2];
        try {
            icons[0] = ImageIO.read(new File("White"+name+ext));
            icons[1] = ImageIO.read(new File("Black"+name+ext));
        } catch (IOException e) {
            System.out.println("Error: could not find icons for "+name);
        }
    }

    public King (int color, String location) {
        this(color);
        setLocation(location);
    }

    public King(int color, int rank, int file) {
        this(color);
        setLocation(rank, file);
    }

    public boolean[][] getMoves(Piece[][] board) {
        boolean[][] out = new boolean[board.length][board[0].length];
        for (int i = 0; i < out.length; i++) {
            for (int j = 0; j < out[0].length; i++) {
                if (Math.abs(j - file) > 1 || Math.abs(i - rank) > 1) {
                    out[i][j] = false;
                } else if (j == file && i == rank) {
                    out[i][j] = false;
                } else {
                    out[i][j] = true;
                }
            }
        }
        return out;
    }
}
