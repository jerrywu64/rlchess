package ui;

import game.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import ai.AI;
import ai.BasicAI;
import java.util.ArrayList;


public class GUI extends JFrame implements MouseListener {

    private JButton black;
    private AI blackai = null;
    private JButton white;
    private AI whiteai = null;
    private Square[][] board;
    private Game game; // unless we want to set this up as an instance
    private int squaresize = 50;
    private Square selected; // selected square

    private static String start = null;

    public GUI(String name) {
        super(name);


    }

    public void createGUI() {

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel backpanel = new JPanel(new BorderLayout());
        backpanel.setBorder(BorderFactory.createEmptyBorder(10, squaresize/2, 10, squaresize/2));

        black = new JButton("Black");
        white = new JButton("White");
        black.setBackground(Color.white);
        black.setOpaque(true);
        white.setBackground(new Color(255, 255, 128));
        white.setOpaque(true);
        black.setPreferredSize(new Dimension(8 * squaresize, squaresize));
        white.setPreferredSize(new Dimension(8 * squaresize, squaresize));

        AI temporaryAI = new AI() {
            public int[] getMove(int c, Piece[][] board, Board b) {
                ArrayList<int[]> moves = game.getMoves(c, board);
                System.out.println("Choosing random move.");
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    System.out.println("Stupid alarm clocks.");
                }
                return moves.get((int) (Math.random() * moves.size()));
            }};

        black.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (blackai == null) {
                    black.setText("blackai");
                    blackai = new BasicAI();
                    updateBoard();
                } else {
                    blackai = null;
                    black.setText("Black");
                }
            }});
        white.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (whiteai == null) {
                    white.setText("whiteai");
                    whiteai = new BasicAI();
                    updateBoard();
                } else {
                    whiteai = null;
                    white.setText("White");
                }
            }});


        backpanel.add(black, BorderLayout.NORTH);
        backpanel.add(white, BorderLayout.SOUTH);

        JPanel boardpanel = new JPanel(new GridLayout(9, 9));
        board = new Square[8][8];

        game = new Game(new RLBoard());
        if (start != null) game = new Game(new RLBoard(start));

        for (int i = 7; i >= 0; i--) {
            boardpanel.add(new JLabel(""+(1+i), SwingConstants.CENTER));
            for (int j = 0; j < 8; j++) {
                board[i][j] = new Square(getIcon(game.board.getBoardCopy()[i][j]), i, j);
                board[i][j].setPreferredSize(new Dimension(squaresize, squaresize));
                board[i][j].setOpaque(true);
                board[i][j].setFont(new Font(Font.SANS_SERIF, Font.PLAIN, squaresize - 10));
                board[i][j].addMouseListener(this);
                if ((i + j)% 2 == 1) board[i][j].setBackground(Color.gray);
                else board[i][j].setBackground(Color.white);
                boardpanel.add(board[i][j]);
            }
        }
        // because obviously we use the unicode snowman for a reset button
        JButton reset = new JButton("" + (char) 9731);
        reset.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, (squaresize - 15)));
        reset.setMargin(new Insets(0, 0, 0, 0));
        reset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                game.board.resetBoard();
                updateBoard();
            }});
        boardpanel.add(reset);
        for (int i = 0; i < 8; i++) {
            boardpanel.add(new JLabel(""+(char) ('a'+i), SwingConstants.CENTER));
        }

        backpanel.add(boardpanel, BorderLayout.CENTER);

        JButton undoButton = new JButton("Undo");
        undoButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                game.board.undo();
                updateBoard();
            }});
        backpanel.add(undoButton, BorderLayout.EAST);

        updateBoard();

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
        if (args.length > 0) start = args[0];
    }

    public String getIcon(Piece p) {
        if (p == null) return "";
        if (p.getSymbol().equals("K")) return "" + (char) (9812 + p.getColor() * 6);
        else if (p.getSymbol().equals("Q")) return "" + (char) (9813 + p.getColor() * 6);
        else if (p.getSymbol().equals("R")) return "" + (char) (9814 + p.getColor() * 6);
        else if (p.getSymbol().equals("B")) return "" + (char) (9815 + p.getColor() * 6);
        else if (p.getSymbol().equals("N")) return "" + (char) (9816 + p.getColor() * 6);
        else if (p.getSymbol().equals("P")) return "" + (char) (9817 + p.getColor() * 6);
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
            Piece p = game.board.getBoardCopy()[selected.getRank()][selected.getFile()];
            boolean[][] moves;
            if (p == null || p.getColor() != game.board.turn) {
                selected = null;
                moves = new boolean[8][8];
            } else {
                moves = p.getMoves(game.board.getBoardCopy());
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
        checkPromotions();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j].setText(getIcon(game.board.getBoardCopy()[i][j]));
            }
        }
        if (game.checkCheckmate(game.board.turn)) JOptionPane.showMessageDialog(this, "Checkmate");
        if (game.board.turn == 0) {
            white.setBackground(new Color(255, 255, 128));
            black.setBackground(Color.white);
            if (whiteai != null && !game.checkCheckmate(game.board.turn)) {
                SwingUtilities.invokeLater( new Runnable() {
                    public void run() {
                        int[] move= whiteai.getMove(0, game.board.getBoardCopy(), game.board);
                        game.board.move(move[0], move[1], move[2], move[3]);
                        updateBoard();
                    }});
            }
        } else {
            white.setBackground(Color.white);
            black.setBackground(new Color(255, 255, 128));
            if (blackai != null && !game.checkCheckmate(game.board.turn)) {
                SwingUtilities.invokeLater( new Runnable() {
                    public void run() {
                        int[] move= blackai.getMove(1, game.board.getBoardCopy(), game.board);
                        game.board.move(move[0], move[1], move[2], move[3]);
                        updateBoard();
                    }});
            }

        }
    }

    private void checkPromotions() {
        for (int i = 0; i < 8; i++) {
            if (game.board.getBoardCopy()[0][i] != null && game.board.getBoardCopy()[0][i].getName().equals("Pawn")) promote(0, i);
            if (game.board.getBoardCopy()[7][i] != null && game.board.getBoardCopy()[7][i].getName().equals("Pawn")) promote(7, i);
        }
    }

    // Promotes the pice on (rank, file). Assumes it's a pawn and promotion is possible.
    private void promote(int rank, int file) {
        String[] options = {"Q", "R", "B", "N"};
        try {
            game.board.promote(rank, file, (String) JOptionPane.showInputDialog(
                        this, 
                        "Choose piece to promote to.",
                        "Promotion",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        options,
                        "Q"));
        } catch (NullPointerException e) {
            System.out.println("Promotion piece not selected. Defaulting to queen.");
            game.board.promote(rank, file, "Q");
        }


    }

    public void mouseClicked(MouseEvent e) {
        Square square = (Square) e.getSource();
        Point click = e.getPoint();
        if (selected == null) { // Selecting new square
            selected = square;
        } else {
            Piece p = game.board.getBoardCopy()[selected.getRank()][selected.getFile()];
            if (p.getMoves(game.board.getBoardCopy())[square.getRank()][square.getFile()]) {
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
