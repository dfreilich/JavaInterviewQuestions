package interview3;

import java.util.Random;

public class MinesweeperBoard implements Board<MinesweeperPiece> {
    private MinesweeperPiece[][] board;
    private boolean lost;
    private int row;
    private int col;

    public MinesweeperBoard(int row, int col, int numMines){
        lost = false;
        board = new MinesweeperPiece[row][col];

        if(!minesFitInBoard(row, col, numMines)) {
            throw new IllegalArgumentException();
        }

        Random random = new Random();
        for(int i = 0; i < numMines; i++) {
            int currRow = random.nextInt(row);
            int currCol = random.nextInt(col);
            if(board[currRow][currCol] != null) {
                i--;
            } else {
                board[currRow][currCol] = new MinesweeperPiece(MinesweeperPiece.BOMB);
            }
        }

        for(int i = 0; i < row; i++) {
            for(int j = 0; j < col; j++) {
                if(!board[i][j].isBomb()) {
                    MinesweeperPiece[] adj = getAdjacentPieces(i, j);
                    int numBombs = 0;
                    for(MinesweeperPiece piece: adj) {
                        if(piece.isBomb()) numBombs++;
                    }
                    if(numBombs > 0) {
                        board[i][j] = new MinesweeperPiece(numBombs);
                    } else {
                        board[i][j] = new MinesweeperPiece(MinesweeperPiece.EMPTY);
                    }
                }
            }
        }
    }

    public MinesweeperPiece[] getAdjacentPieces(int i, int j) {
        return null;
    }

    public int[][] getAdjacentCoordinates(int i, int j) {
        return null;
    }

    public boolean minesFitInBoard(int row, int col, int numMines) {
        return row*col < numMines;
    }


    /*
    3 cases:
    1. If the piece is a bomb: flip piece and say we've lost
    2. If the piece is a number: flip it;
    3. If the piece is Empty: recursive it
     */

    @Override
    public void change(int i, int j) throws Exception {
        if(i < 0 || i > row || j < 0 || j > col) return;
        MinesweeperPiece piece = board[i][j];
        if(piece.isBomb()) {
            throw new Exception();
        } else if(piece.getValue() != MinesweeperPiece.EMPTY) {
            piece.setExposed();
        } else {
            if(!piece.isExposed()) {
                piece.setExposed();
                for(int[] coords: getAdjacentCoordinates(i, j)) {
                    change(coords[0], coords[1]);
                }
            }
        }
    }

    @Override
    public void print() {

    }

    @Override
    public boolean hasWon() {
        return false;
    }
}
