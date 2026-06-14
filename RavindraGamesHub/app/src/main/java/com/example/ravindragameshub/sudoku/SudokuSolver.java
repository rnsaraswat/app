package com.example.ravindragameshub.sudoku;

public class SudokuSolver {

    public boolean solve(int[][] board) {

        for (int row = 0; row < 9; row++) {

            for (int col = 0; col < 9; col++) {

                if (board[row][col] == 0) {

                    for (int num = 1; num <= 9; num++) {

                        if (isValid(board, row, col, num)) {

                            board[row][col] = num;

                            if (solve(board))
                                return true;

                            board[row][col] = 0;
                        }
                    }

                    return false;
                }
            }
        }

        return true;
    }

    public boolean isValid(int[][] board,
                           int row,
                           int col,
                           int num) {

        for (int i = 0; i < 9; i++) {

            if (board[row][i] == num)
                return false;

            if (board[i][col] == num)
                return false;
        }

        int startRow = row - row % 3;
        int startCol = col - col % 3;

        for (int r = startRow; r < startRow + 3; r++) {

            for (int c = startCol; c < startCol + 3; c++) {

                if (board[r][c] == num)
                    return false;
            }
        }

        return true;
    }
}
