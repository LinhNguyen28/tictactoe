import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class TicTacToeInteraction {
    //Graphics
    private static Cell[][] cells;
    private static JFrame mainFrame;
    private static TicTacToe game;
    private static JLabel result;

    public static void main(String[] args) {
        game = new TicTacToe();
        Board mainBoard = game.getBoard();
        mainBoard.clear();
        //Graphics component
        cells = new Cell[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                cells[i][j] = new Cell(mainBoard.get(i, j), (short) (i * 3 + j));
            }
        }

        display();
    }

    private static void move() {
        Board mainBoard = game.getBoard();
        char c = 'O';
        if (game.isXTurn()) {
            c = 'X';
        }
        if (!checkEnded()) {
            int num = game.getBestMove(mainBoard.toShortString());
            cells[num / 3][num % 3].setStatus(c);
            mainBoard.set(num / 3, num % 3, c);
        }
        checkEnded();
    }

    private static boolean checkEnded() {
        if (game.ended()) {
            for (Cell[] a:cells) {
                for (Cell i:a) {
                    i.setEnabled(false);
                }
            }

            String rText = "X WON!";
            if (game.won(game.getBoard()) == 'O') {
                rText = "O WON!";
            } else if (game.won(game.getBoard()) == '-') {
                rText = "DRAW";
            }

            result.setText(rText);
            return true;
        }

        return false;
    }

    private static void display() {
        mainFrame = new JFrame("TicTacToe");
        JPanel mainPanel = new JPanel();

        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(new Dimension(300, 400));
        mainFrame.setResizable(false);
        mainFrame.setLocationRelativeTo(null);

        setupCtrlPanel(mainPanel);
        setupCellPanel(mainPanel);
        mainFrame.add(mainPanel);

        mainFrame.setVisible(true);
    }

    private static void setupCtrlPanel(JPanel mainPanel) {
        JPanel control = new JPanel();
        control.setPreferredSize(new Dimension(300,60));
        control.setLayout(new GridLayout(0,2));

        JPanel subControl = new JPanel();
        subControl.setLayout(new GridLayout(2,0));
        //resetButton
        JButton reset = new JButton("RESET");
        reset.setBackground(new Color(225,198,153));
        reset.addActionListener(actionEvent -> {
            game.getBoard().clear();
            for (Cell[] a:cells) {
                for (Cell i: a) {
                    i.setStatus('-');
                }
            }
            result.setText("--------");
        });
        subControl.add(reset);

        //Status/Result
        result = new JLabel("--------");
        result.setSize(new Dimension(50,20));
        result.setBackground(new Color(225,198,153));
        result.setHorizontalAlignment(SwingConstants.CENTER);
        subControl.add(result);

        control.add(subControl);

        //computer first button
        JButton computerTurn = new JButton("Computer First");
        computerTurn.setBackground(new Color(225,198,153));
        computerTurn.addActionListener(actionEvent -> {
            if (game.getBoard().getNumOfEmptySpaces() == 9) {
                move();
            }
        });
        control.add(computerTurn);

        mainPanel.add(control);
    }

    private static void setupCellPanel(JPanel mainPanel) {
        JPanel cellPanel = new JPanel();

        cellPanel.setPreferredSize(new Dimension(300,300));
        cellPanel.setLayout(new GridLayout(3, 3));
        Board mainBoard = game.getBoard();

        for (int i=0; i<3; i++) {
            for (int j=0; j<3; j++) {
                Cell c = cells[i][j];
                c.addMouseListener(new MouseListener() {
                    @Override
                    public void mouseClicked(MouseEvent mouseEvent) {
                        if (c.status == '-' && c.isEnabled()) {
                            if (game.isXTurn()) {
                                c.setStatus('X');
                                mainBoard.set(c.num / 3, c.num % 3, 'X');
                            } else {
                                c.setStatus('O');
                                mainBoard.set(c.num / 3, c.num % 3, 'O');
                            }
                            move();
                        }
                    }

                    @Override
                    public void mousePressed(MouseEvent mouseEvent) {}

                    @Override
                    public void mouseReleased(MouseEvent mouseEvent) {}

                    @Override
                    public void mouseEntered(MouseEvent mouseEvent) {}

                    @Override
                    public void mouseExited(MouseEvent mouseEvent) {}
                });

                cellPanel.add(c);
            }
        }
        mainPanel.add(cellPanel);
    }

    private static class Cell extends JButton {
        private char status;
        private short num;

        private Cell(char c, short n) {
            super();
            status = c;
            num = n;
        }
        public Cell(short n) {
            this('-', n);
        }

        public void setStatus(char c) {
            if (c == '-') {
                setEnabled(true);
            } else {
                setEnabled(false);
            }
            status = c;
            repaint();
        }
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            this.setBackground(new Color(97, 110, 119));

            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(Color.ORANGE);
            g2.setStroke(new BasicStroke(5));
            if (status == 'X') {
                g2.drawLine(25, 25, this.getWidth() - 25, this.getHeight() - 25);
                g2.drawLine(this.getWidth() - 25, 25, 25, this.getHeight() - 25);
            } else if (status == 'O') {
                g2.drawOval(25, 25, 50, 50);
            }
        }
    }
}
