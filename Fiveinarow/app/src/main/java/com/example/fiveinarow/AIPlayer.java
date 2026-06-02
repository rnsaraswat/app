package com.example.fiveinarow;

import android.util.Log;
import android.widget.Toast;

import java.util.*;

//class AI Player
public class AIPlayer {
    //variables
    int difficulty;
    int size = 15;
    //random number variable
    Random rand = new Random();

    //set difficulty
    public AIPlayer(int difficulty) {
        this.difficulty = difficulty;
    }

    // 🎯 MAIN METHOD
    public int[] getMove(int[][] board) {

//        difficulty = 2;
        Log.d("AI", "Difficulty = " + difficulty);
        //easy
        if (difficulty == 0) return randomMove(board);
        //medium
        if (difficulty == 1) return mixedMove(board);
        //hard
        return smartMove(board);
    }

    //Easy (choose random number)
    private int[] randomMove(int[][] board) {
        int r, c;
        //loop to choose random row / col until empty cell
        do {
            //choose random row / col
            r = rand.nextInt(size);
            c = rand.nextInt(size);
        } while (board[r][c] != 0);
        return new int[]{r, c};
    }

    //Medium (choose random or samart move)
    private int[] mixedMove(int[][] board) {
        //choose random move
        if (rand.nextBoolean()) return randomMove(board);
        //choose smart move
        return smartMove(board);
    }

    //Hard (Choose smart move)
    private int[] smartMove(int[][] board) {

        int bestScore = Integer.MIN_VALUE;
        int[] bestMove = null;

        //loop to playe for each cell of board
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {

                //if cell is empty
                if (board[r][c] == 0) {

                    //evaluate score for current cell move
                    int score = evaluatePosition(board, r, c);

                    //if score > baset score store it in best move
                    if (score > bestScore) {
                        //store score to best score and best move
                        bestScore = score;
                        bestMove = new int[]{r, c};
                    }
                }
            }
        }
        //return best move for ai
        return bestMove;
    }

    //evaluation method for AI
    private int evaluatePosition(int[][] board, int r, int c) {

        int score = 0;

        //evaluate for direction for attack
        score += evaluateDirection(board, r, c, 2) * 2;
        //evaluate for direction for defence
        score += evaluateDirection(board, r, c, 1);

        //play mostely in center
        int center = size / 2;
        score += (size - (Math.abs(r - center) + Math.abs(c - center)));

        return score;
    }

    // evaluation (direction) method
    private int evaluateDirection(int[][] board, int r, int c, int player) {

        int total = 0;

        int[][] dirs = {{1,0},{0,1},{1,1},{1,-1}};

        for (int[] d : dirs) {

            int count = 1;

            count += countInDir(board, r, c, d[0], d[1], player);
            count += countInDir(board, r, c, -d[0], -d[1], player);

            total += getScore(count);
        }

        return total;
    }

    private int countInDir(int[][] board, int r, int c, int dr, int dc, int player) {

        int count = 0;

        for (int i = 1; i < 5; i++) {

            int nr = r + dr * i;
            int nc = c + dc * i;

            if (nr < 0 || nc < 0 || nr >= size || nc >= size) break;

            if (board[nr][nc] == player) count++;
            else break;
        }

        return count;
    }

    //get score
    private int getScore(int count) {

        switch (count) {
            case 5: return 100000;
            case 4: return 10000;
            case 3: return 1000;
            case 2: return 100;
            default: return 10;
        }
    }
}


