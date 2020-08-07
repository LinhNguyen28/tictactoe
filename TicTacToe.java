public class TicTacToe {
    private Board mainBoard;
    private static final int SIZE = 3;
    private HashedDictionary<Board, Integer> bestMoves;

    public TicTacToe() {
        mainBoard = new Board();
        bestMoves = new HashedDictionary<>();

        generateValidBoards(mainBoard);
        bestMoves.add(new Board("---------"), findBestMove("---------"));
    }

    public boolean isXTurn() {
        return mainBoard.getXTurn();
    }

    public Board getBoard() {
        return mainBoard;
    }

    public boolean ended() {
        return getBestMove(mainBoard.toShortString()) < 0;
    }

    // returns -1 if X or O wins, returns -2 if draw
    public int getBestMove(String board) {
        return bestMoves.getValue(new Board(board));
    }

    private void generateValidBoards(Board board) {
        if (board.getNumOfEmptySpaces() == 0 || won(board) != '-') {
            return;
        }

        for (int i=0; i<3; i++) {
            for (int j=0; j<3; j++) {
                if (board.get(i,j) == '-') {
                    if (board.getXTurn()) {
                        board.set(i,j,'X');
                    } else {
                        board.set(i,j,'O');
                    }

                    bestMoves.add(new Board(board), findBestMove(board.toShortString()));
                    generateValidBoards(board);
                    board.set(i,j,'-');
                }
            }
        }
    }

    // returns -1 if X or O wins, returns -2 if draw
    private int findBestMove(String board) {
        Board currentBoard = new Board(board);
        if (won(currentBoard) != '-') {return -1;}
        else if (currentBoard.getNumOfEmptySpaces() == 0) { return -2;}

        if (currentBoard.equals(new Board("---------"))) {
            return 7;
        }

        // gets the winning possibilities for each cell
        int[][] winningPossibilities = possibleWinningCells(board);

        // if it's X's turn, try to maximize the winning possibilities of X
        // and minimize the winning possibilities of O
        if (currentBoard.getXTurn()) {
            if (winningPossibilities[0][0] != -1) { //if X is winning
                return winningPossibilities[0][0];
            } else if (winningPossibilities[1][0] != -1) { //if O is winning
                return winningPossibilities[1][0];
            } else { //if none if winning
                return examinePossibilities(winningPossibilities, board, true);
            }
        } else {
            if (winningPossibilities[1][0] != -1) { //if O is winning
                return winningPossibilities[1][0];
            } else if (winningPossibilities[0][0] != -1) { //if X is winning
                return winningPossibilities[0][0];
            } else { //if none is winning
                return examinePossibilities(winningPossibilities, board, false);
            }
        }
    }

    private int examinePossibilities(int[][] winningPossibilities, String originalBoard, boolean isXTurn) {
        int[] combination = new int[9];
        // index of the cell with largest combination of O and X possibilities
        int maxIndex = -1;
        int maxCount = 0;

        for (int i=0; i<9; i++) {
            combination[i] = winningPossibilities[2][i] + winningPossibilities[3][i];
            if (maxIndex == -1 || combination[i] > combination[maxIndex]) {
               maxIndex = i;
               maxCount =1;
            } else if (combination[i] == combination[maxIndex]) {
                maxCount++;
            }
        }

        // if there is only one position where there is maxIndex (largest advantage)
        if (maxCount == 1) {
            return maxIndex;
        } else {
            boolean OAdvantage = true;
            char c = 'O';
            if (isXTurn) {
                OAdvantage = false;
                c = 'X';
            }

            // else examine one future move
            // and choose the one in which X doesn't have
            // 2 in XMove (2 ways to win) and with biggest advantage of O
            int maxAdPoint = -1000;
            int maxAdPointIndex = -1;
            for (int i=0;i <combination.length; i++) {
                if (combination[i] >= 0) {
                    Board temp = new Board(originalBoard);
                    temp.set(i/3, i%3, c);
                    int[][] winningPoss = possibleWinningCells(temp.toShortString());
                    int[] simplePoint = simpleMovePoint(winningPoss, OAdvantage);

                    if (simplePoint[0] != 2) {
                        if (simplePoint[1] > maxAdPoint) {
                            maxAdPointIndex = i;
                            maxAdPoint = simplePoint[1];
                        }
                    }
                }
            }

            return maxAdPointIndex;
        }
    }

    private int[] simpleMovePoint(int[][] winningPoss, boolean OAdvantage) {
        int[] result = new int[2];

        if (OAdvantage) {
            int maxXMove = -1;
            int adPoint = 0;

            for (int i=0; i<9; i++) {
                adPoint = adPoint + winningPoss[3][i] - winningPoss[2][i];
                int XMove = winningPoss[2][i];
                if (XMove > maxXMove) {
                    maxXMove = XMove;
                }
            }

            result[1] = adPoint;
            if (winningPoss[0][0] != -1) {
                int XMove = winningPoss[0][0];
                result[0] = winningPoss[2][XMove];
            } else if (winningPoss[1][0] != -1) {
                int XMove = winningPoss[1][0];
                result[0] = winningPoss[2][XMove];
            } else {
                result[0] = maxXMove;
            }
        } else {
            int maxOMove = -1;
            int adPoint = 0;

            for (int i=0; i<9; i++) {
                adPoint = adPoint + winningPoss[2][i] - winningPoss[3][i];

                int OMove = winningPoss[3][i];
                if (OMove > maxOMove) {
                    maxOMove = OMove;
                }
            }

            result[1] = adPoint;
            if (winningPoss[1][0] != -1) {
                int XMove = winningPoss[1][0];
                result[0] = winningPoss[3][XMove];
            } else if (winningPoss[0][0] != -1) {
                int XMove = winningPoss[0][0];
                result[0] = winningPoss[3][XMove];
            } else {
                result[0] = maxOMove;
            }
        }
        return result;
    }

    public char won(Board cBoard) {
        char result = '-';
        for (int i=0; i<SIZE; i++) {
            int numRowX = 0, numRowE = 0;
            int numColX = 0, numColE = 0;
            for (int j=0; j<SIZE; j++) {
                char rCurrent = cBoard.get(i,j);
                if (rCurrent == 'X') {
                    numRowX++;
                } else if (rCurrent == '-') numRowE++;

                char cCurrent = cBoard.get(j,i);
                if (cCurrent == 'X') {
                    numColX++;
                } else if (cCurrent == '-') numColE++;
            }

            if ((numRowX == 3) || numColX == 3) {
                return 'X';
            } else if ((numRowX == 0 && numRowE == 0) || (numColX==0 && numColE == 0)) {
                return 'O';
            }
        }

        int numXLR=0, numXRL = 0;
        int numELR = 0, numERL = 0;
        for (int i=0; i<SIZE; i++) {
            if (cBoard.get(i,i) == 'X') numXLR++;
            else if (cBoard.get(i,i) == '-') numELR++;

            if (cBoard.get(i, 2-i) == 'X') numXRL++;
            else if (cBoard.get(i, 2-i) == '-') numERL++;
        }

        if ((numXLR==0 && numELR==0) || (numXRL == 0 && numERL==0)) {
            return 'O';
        } else if (numXLR == 3 || numXRL == 3) {
            return 'X';
        }

        return result;
    }

    /*
    Returns a 2D array with 4 arrays inside:
    [0] array: X's winning position, only the first index is filled
    [1] array: O's winning position, only the first index is filled
    [2] array: X's possible moves to win (the empty cells in a row/col in which there is 1X and 2 empty)
    each index will store the number of winning rows/cols this cell will fill
    [3] array: O's possible moves to win (the empty cells in a row/col in which there is 1O and 2 empty)
    each index will store the number of winning rows/cols this cell will fill
     */
    private int[][] possibleWinningCells(String board) {
        int[][] result = new int[4][9];
        Board mBoard = new Board(board);
        result[0][0] = -1;
        result[1][0] = -1;

        //check rows and cols
        for (int i=0; i<3; i++) {
            int numXRow = 0, numERow=0;
            int numXCol = 0, numECol=0;
            int posERow = -1;
            int posECol = -1;

            for (int j=0; j<3; j++) {
                if (mBoard.get(i,j) == 'X') {
                    result[2][i*3+j] = -1;
                    result[3][i*3+j] = -1;
                    numXRow ++;
                } else if (mBoard.get(i,j) == '-') {
                    posERow = i*3+j;
                    numERow++;
                } else {
                    result[2][i*3+j] = -1;
                    result[3][i*3+j] = -1;
                }

                if (mBoard.get(j,i) == 'X') {
                    numXCol ++;
                } else if (mBoard.get(j,i) == '-') {
                    posECol = j*3+i;
                    numECol++;
                }
            }

            if ((numXRow == 1 && numERow == 2) || (numXRow == 2 && numERow == 1)) {
                for (int j=0; j<3; j++) {
                    if (mBoard.get(i,j) == '-') {
                        result[2][i*3+j]++;
                    }
                }
            } else if (numXRow == 0 && numERow >0 && numERow < 3) {
                for (int j=0; j<3; j++) {
                    if (mBoard.get(i,j) == '-') {
                        result[3][i*3+j]++;
                    }
                }
            }

            if ((numXCol == 1 && numECol == 2) || (numXCol == 2 && numECol == 1)) {
                for (int j=0; j<3; j++) {
                    if (mBoard.get(j,i) == '-') {
                        result[2][j*3+i]++;
                    }
                }
            } else if (numXCol == 0 && numECol < 3 && numECol>0) {
                for (int j=0; j<3; j++) {
                    if (mBoard.get(j,i) == '-') {
                        result[3][j*3+i]++;
                    }
                }
            }

            if (numXRow == 2 && numERow == 1) {
                result[0][0] = posERow;
            } else if (numXRow == 0 && numERow == 1) {
                result[1][0] = posERow;
            }

            if (numXCol == 2 && numECol == 1) {
                result[0][0] = posECol;
            } else if (numXCol == 0 && numECol == 1) {
                result[1][0] = posECol;
            }
        }

        //check diagonals
        int numXLR = 0, numELR = 0;
        int numXRL = 0, numERL = 0;
        int posELR = -1, posERL = -1;
        for (int i=0; i<3; i++) {
            if (mBoard.get(i,i) == 'X') {
                numXLR++;
            } else if (mBoard.get(i,i) == '-') {
                numELR++;
                posELR = i*3+i;
            }

            if (mBoard.get(2-i,i) == 'X') {
                numXRL++;
            } else if (mBoard.get(2-i,i) == '-') {
                numERL++;
                posERL = (2-i)*3+i;
            }
        }

        if ((numXLR == 1 && numELR == 2) || (numXLR == 2 && numELR == 1)) {
            for (int i=0; i<3; i++) {
                if (mBoard.get(i,i) == '-') {
                    result[2][i*3+i]++;
                }
            }
        } else if (numXLR == 0 && numELR >0 && numELR < 3) {
            for (int i=0; i<3; i++) {
                if (mBoard.get(i,i) == '-') {
                    result[3][i*3+i]++;
                }
            }
        }

        if ((numXRL == 1 && numERL == 2) || (numXRL == 2 && numERL == 1)){
            for (int i=0; i<3; i++) {
                if (mBoard.get(2-i,i) == '-') {
                    result[2][(2-i)*3+i]++;
                }
            }
        } else if (numXRL == 0 && numERL >0 && numERL < 3) {
            for (int i=0; i<3; i++) {
                if (mBoard.get(2-i,i) == '-') {
                    result[3][(2-i)*3+i]++;
                }
            }
        }

        if (numXLR == 2 && numELR == 1) {
            result[0][0] = posELR;
        } else if (numXLR == 0 && numELR == 1) {
            result[1][0] = posELR;
        }

        if (numXRL == 2 && numERL == 1) {
            result[0][0] = posERL;
        } else if (numXRL == 0 && numERL == 1) {
            result[1][0] = posERL;
        }

        return result;
    }
}