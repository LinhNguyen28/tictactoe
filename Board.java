import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class Board {
    private char[][] board;
    private boolean XTurn;
    private int numOfEmptySpaces;

    public Board(String data) { //string should be of length 9, containing only X,O and -
        board = new char[3][3];
        if (data.length() != 9) {
            throw new IllegalArgumentException("String should be of length 9");
        }

        int countX = 0;
        int countO = 0;
        for (int i= 0; i < data.length(); i++) {
            char current = data.charAt(i);
            if (current != 'X' && current != 'O' && current != '-') {
                throw new IllegalArgumentException("String should only contain X,O or -");
            }
            if (current == 'X') countX++;
            else if (current == 'O') countO++;

            board[i/3][i%3] = current;

        }

        numOfEmptySpaces = 9- countO - countX;
        XTurn = (countX==countO);
    }

    public Board(Board other) {
        this(other.toShortString());
    }
    public Board() {
        board = new char[3][3];
        clear();
    }

    public void set (int row, int col, char c) {
        board[row][col] = c;
        numOfEmptySpaces--;
        XTurn = !XTurn;
    }

    public char get(int row, int col) {
        return board[row][col];
    }

    public int getNumOfEmptySpaces() {
        return numOfEmptySpaces;
    }

    public boolean getXTurn() {
        return XTurn;
    }

    public void clear() {
        for (int i=0; i<3; i++) {
            for (int j=0; j<3; j++) {
                board[i][j] = '-';
            }
        }

        XTurn = true;
        numOfEmptySpaces = 9;
    }
    @Override
    public int hashCode() {
        int val = 0;

        for (int i=0; i<board.length; i++) {
            for (int j=0; j<board[i].length; j++) {
                val = val + (int)Math.pow(11, (i*3+j))*board[i][j];
            }
        }

        return val;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Board)) {
            return false;
        }
        Board otherBoard = (Board) other;
        for (int i=0; i<board.length; i++) {
            for (int j=0; j<board.length; j++) {
                if (this.board[i][j] != otherBoard.board[i][j]) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public String toString() {
        String result="";

        for (char[] ca:board) {
            result = result + "| ";
            for (char c:ca) {
                if (c != '-') {
                    result = result + c + " | ";
                } else {
                    result = result + "  | ";
                }
            }
            result = result + "\n";
        }

        return result;
    }

    public String toShortString() {
        String r = "";
        for (char[] a: board) {
            for (char c:a) {
                r = r+c;
            }
        }

        return r;
    }
}
