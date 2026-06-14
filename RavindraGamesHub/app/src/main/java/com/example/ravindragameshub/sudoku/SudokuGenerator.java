package com.example.ravindragameshub.sudoku;

import static com.example.ravindragameshub.sudoku.SudokuDifficulty.EASY;
import static com.example.ravindragameshub.sudoku.SudokuDifficulty.MEDIUM;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class SudokuGenerator {

    private final Random random = new Random();

    public int[][] generateSolution() {

        int[][] board = new int[9][9];

        fillBoard(board);

        return board;
    }

    private boolean fillBoard(int[][] board) {

        for (int row = 0; row < 9; row++) {

            for (int col = 0; col < 9; col++) {

                if (board[row][col] == 0) {

                    ArrayList<Integer> numbers =
                            new ArrayList<>();

                    for (int i = 1; i <= 9; i++) {
                        numbers.add(i);
                    }

                    Collections.shuffle(numbers);

                    for (int num : numbers) {

                        if (isValid(board,row,col,num)) {

                            board[row][col] = num;

                            if (fillBoard(board))
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

    public int[][] createPuzzle(int[][] solution,
                                SudokuDifficulty difficulty) {

        int[][] puzzle = copyBoard(solution);

        int cellsToRemove;

        switch (difficulty) {

            case EASY:
                cellsToRemove = 35;
                break;

            case MEDIUM:
                cellsToRemove = 45;
                break;

            default:
                cellsToRemove = 55;
                break;
        }

        while (cellsToRemove > 0) {

            int row = random.nextInt(9);
            int col = random.nextInt(9);

            if (puzzle[row][col] != 0) {

                puzzle[row][col] = 0;

                cellsToRemove--;
            }
        }

        return puzzle;
    }

    private boolean isValid(int[][] board,
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

    private int[][] copyBoard(int[][] board) {

        int[][] copy = new int[9][9];

        for (int i = 0; i < 9; i++) {

            System.arraycopy(board[i],
                    0,
                    copy[i],
                    0,
                    9);
        }

        return copy;
    }
}
