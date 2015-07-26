package ui;

import game.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;


public class GUI extends JFrame implements MouseListener {

    private JLabel black;
    private JLabel white;
    private Square[][] board;
    private Game game; // unless we want to set this up as an instance
    private int squaresize = 50;
    private Square selected; // selected square

    public GUI(String name) {
        super(name);


    }

    public void createGUI() {

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel backpanel = new JPanel(new BorderLayout());
        backpanel.setBorder(BorderFactory.createEmptyBorder(10, squaresize/2, 10, squaresize/2));

        black = new JLabel("Black", SwingConstants.CENTER);
        white = new JLabel("White", SwingConstants.CENTER);
        black.setBackground(Color.white);
        black.setOpaque(true);
        white.setBackground(new Color(255, 255, 128));
        white.setOpaque(true);
        black.setPreferredSize(new Dimension(8 * squaresize, squaresize));
        white.setPreferredSize(new Dimension(8 * squaresize, squaresize));

        backpanel.add(black, BorderLayout.NORTH);
        backpanel.add(white, BorderLayout.SOUTH);

        JPanel boardpanel = new JPanel(new GridLayout(8, 8));
        boardpanel.addMouseListener(this);
        board = new Square[8][8];

        game = new Game(new RLBoard());

        for (int i = 7; i >= 0; i--) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = new Square(getIcon(game.board.board[i][j]), i, j);
                board[i][j].setPreferredSize(new Dimension(squaresize, squaresize));
                board[i][j].setOpaque(true);
                board[i][j].setFont(new Font(Font.SANS_SERIF, Font.PLAIN, squaresize - 10));
                // board[i][j].addMouseListener(this);
                if ((i + j)% 2 == 1) board[i][j].setBackground(Color.gray);
                else board[i][j].setBackground(Color.white);
                boardpanel.add(board[i][j]);
            }
        }

        backpanel.add(boardpanel, BorderLayout.CENTER);

        this.setContentPane(backpanel);
        this.pack();
        this.setVisible(true);
    }

    public static void main(String[] args) {
        GUI gui = new GUI("RLChess");
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                gui.createGUI();
            }
        });
    }

    public String getIcon(Piece p) {
        if (p == null) return "";
        if (p.symbol.equals("K")) return "" + (char) (9812 + p.color * 6);
        else if (p.symbol.equals("Q")) return "" + (char) (9813 + p.color * 6);
        else if (p.symbol.equals("R")) return "" + (char) (9814 + p.color * 6);
        else if (p.symbol.equals("B")) return "" + (char) (9815 + p.color * 6);
        else if (p.symbol.equals("N")) return "" + (char) (9816 + p.color * 6);
        else if (p.symbol.equals("P")) return "" + (char) (9817 + p.color * 6);
        else return null;
    }

    public void updateBoard() {
        if (selected == null) {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    board[i][j].setSelection(0);
                }
            }
        } else {
            Piece p = game.board.board[selected.getRank()][selected.getFile()];
            boolean[][] moves;
            if (p == null || p.color != game.board.turn) {
                selected = null;
                moves = new boolean[8][8];
            } else {
                moves = p.getMoves(game.board.board);
            }
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (moves[i][j]) 
                        board[i][j].setSelection(1);
                    else if (selected == board[i][j])
                        board[i][j].setSelection(2);
                    else
                        board[i][j].setSelection(0);
                }
            }
        }
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j].setText(getIcon(game.board.board[i][j]));
            }
        }
        if (game.board.turn == 0) {
            white.setBackground(new Color(255, 255, 128));
            black.setBackground(Color.white);
        } else {
            white.setBackground(Color.white);
            black.setBackground(new Color(255, 255, 128));
        }
    }

    public void mouseClicked(MouseEvent e) {
        // System.out.println("Clickmouse!");
        Square square = null;
        Point click = e.getPoint();
        // System.out.println(click.x + " " + click.y);
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Point sq = board[i][j].getLocation();
                // sq.translate(-board[0][0].getLocation().x, -board[0][0].getLocation().y);
                Dimension dim = board[i][j].getSize();
                if (click.x - sq.x >= 0 && click.x - sq.x < dim.width && 
                        click.y - sq.y >= 0 && click.y - sq.y < dim.height) {
                    /* if (board[i][j].getBackground().equals(Color.yellow))
                        board[i][j].setBackground((i+j)%2==0?Color.white:Color.gray);
                    else board[i][j].setBackground(Color.yellow);*/
                    square = board[i][j];
                }
            }
        }
        if (selected == null) { // Selecting new square
            selected = square;
        } else {
            Piece p = game.board.board[selected.getRank()][selected.getFile()];
            if (p.getMoves(game.board.board)[square.getRank()][square.getFile()]) {
                game.board.move(selected.getRank(), selected.getFile(), square.getRank(), square.getFile());
            }
            selected = null;

        }
        updateBoard();
    }

    public void mousePressed(MouseEvent e) {
        // System.out.println("Mouse!");
    }
    public void mouseReleased(MouseEvent e) {
        // System.out.println("Unmouse!");
    }
    public void mouseEntered(MouseEvent e) {
        // System.out.println("Inmouse");
    }
    public void mouseExited(MouseEvent e) {
        // System.out.println("Nomouse!");
    }

}

class Square extends JLabel {
    private int selection; // 0 for no selection, 1 for indirect selection, 2 for direct selection
    private int rank;
    private int file;
    public Square(String text, int rank, int file) {
        super(text, SwingConstants.CENTER);
        this.rank = rank;
        this.file = file;
    }
    public void setSelection(int s) {
        if (s == 0) {
            selection = s;
            this.setBackground((rank + file) % 2 == 0?Color.white:Color.gray);
        } else if (s == 1) {
            selection = s;
            this.setBackground((rank + file) % 2 == 0?(new Color(255, 255, 128)):(new Color(128, 128, 64)));
        } else if (s == 2) {
            selection = s;
            this.setBackground((rank + file) % 2 == 0?(new Color(255, 255, 64)):(new Color(192, 192, 32)));
        } else {
            System.out.println("Invalid selection set attempt: "+s);
        }
    }
    public int getRank() {
        return rank;
    }
    public int getFile() {
        return file;
    }
}
