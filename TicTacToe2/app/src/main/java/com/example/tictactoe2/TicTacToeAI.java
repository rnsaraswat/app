package com.example.tictactoe2;

import java.util.*;

public class TicTacToeAI {

    public char[][] board = {
            {' ', ' ', ' '},
            {' ', ' ', ' '},
            {' ', ' ', ' '}
    };

    public char HUMAN = 'X';
    public char AI = 'O';
    Random rand = new Random();

    public List<int[]> getEmptyCells() {
        List<int[]> list = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == ' ') {
                    list.add(new int[]{i, j});
                }
            }
        }
        return list;
    }

    public boolean checkWinner(char p) {
        for (int i = 0; i < 3; i++) {
            if ((board[i][0] == p && board[i][1] == p && board[i][2] == p) ||
                    (board[0][i] == p && board[1][i] == p && board[2][i] == p)) {
                return true;
            }
        }

        if ((board[0][0] == p && board[1][1] == p && board[2][2] == p) ||
                (board[0][2] == p && board[1][1] == p && board[2][0] == p)) {
            return true;
        }

        return false;
    }

    // EASY
    public int[] easyMove() {
        List<int[]> empty = getEmptyCells();
        return empty.get(rand.nextInt(empty.size()));
    }

    // MEDIUM
    public int[] mediumMove() {
        if (rand.nextBoolean()) return easyMove();
        return bestMove();
    }

    // HARD
    public int[] hardMove() {
        return bestMove();
    }

    public int[] bestMove() {
        int bestScore = Integer.MIN_VALUE;
        int[] move = new int[2];

        for (int[] cell : getEmptyCells()) {
            board[cell[0]][cell[1]] = AI;
            int score = minimax(0, false);
            board[cell[0]][cell[1]] = ' ';

            if (score > bestScore) {
                bestScore = score;
                move = cell;
            }
        }
        return move;
    }

    public int minimax(int depth, boolean isMax) {
        if (checkWinner(AI)) return 10 - depth;
        if (checkWinner(HUMAN)) return depth - 10;
        if (getEmptyCells().isEmpty()) return 0;

        if (isMax) {
            int best = Integer.MIN_VALUE;
            for (int[] cell : getEmptyCells()) {
                board[cell[0]][cell[1]] = AI;
                best = Math.max(best, minimax(depth + 1, false));
                board[cell[0]][cell[1]] = ' ';
            }
            return best;
        } else {
            int best = Integer.MAX_VALUE;
            for (int[] cell : getEmptyCells()) {
                board[cell[0]][cell[1]] = HUMAN;
                best = Math.min(best, minimax(depth + 1, true));
                board[cell[0]][cell[1]] = ' ';
            }
            return best;
        }
    }

    public void resetBoard() {
        for (int i = 0; i < 3; i++)
            Arrays.fill(board[i], ' ');
    }
}

