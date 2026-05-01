package com.example.fiveinarow;

import java.util.*;
//import java.util.Random;
//
//public class AIPlayer {
//        int difficulty;
//        Random rand = new Random();
//
//        public AIPlayer(int difficulty) {
//            this.difficulty = difficulty;
//        }
//
//        public int[] getMove(int[][] board) {
//
//            if (difficulty == 0) return randomMove(board);      // Easy
//            if (difficulty == 1) return mediumMove(board);      // Medium
//            return hardMove(board);                             // Hard
//        }
//
//        // EASY → Random move
//        private int[] randomMove(int[][] board) {
//            int r, c;
//            do {
//                r = rand.nextInt(15);
//                c = rand.nextInt(15);
//            } while (board[r][c] != 0);
//            return new int[]{r,c};
//        }
//
//        // MEDIUM → block opponent
//        private int[] mediumMove(int[][] board) {
//            // Try to block player
//            for(int r=0;r<15;r++){
//                for(int c=0;c<15;c++){
//                    if(board[r][c]==0){
//                        board[r][c]=1;
//                        if(checkWin(board,r,c)){
//                            board[r][c]=0;
//                            return new int[]{r,c};
//                        }
//                        board[r][c]=0;
//                    }
//                }
//            }
//            return randomMove(board);
//        }
//
//        // HARD → win + block
//        private int[] hardMove(int[][] board) {
//
//            // Try win
//            for(int r=0;r<15;r++){
//                for(int c=0;c<15;c++){
//                    if(board[r][c]==0){
//                        board[r][c]=2;
//                        if(checkWin(board,r,c)){
//                            board[r][c]=0;
//                            return new int[]{r,c};
//                        }
//                        board[r][c]=0;
//                    }
//                }
//            }
//
//            // Block
//            return mediumMove(board);
//        }
//
//        private boolean checkWin(int[][] b, int r, int c){
//            int p = b[r][c];
//
//            return count(b,r,c,1,0,p)+count(b,r,c,-1,0,p)>=4 ||
//                    count(b,r,c,0,1,p)+count(b,r,c,0,-1,p)>=4 ||
//                    count(b,r,c,1,1,p)+count(b,r,c,-1,-1,p)>=4 ||
//                    count(b,r,c,1,-1,p)+count(b,r,c,-1,1,p)>=4;
//        }
//
//        private int count(int[][] b,int r,int c,int dr,int dc,int p){
//            int cnt=0;
//            for(int i=1;i<5;i++){
//                int nr=r+dr*i,nc=c+dc*i;
//                if(nr<0||nc<0||nr>=15||nc>=15||b[nr][nc]!=p) break;
//                cnt++;
//            }
//            return cnt;
//        }
//}



public class AIPlayer {
    int difficulty;
    int size = 15;
    Random rand = new Random();

    public AIPlayer(int difficulty) {
        this.difficulty = difficulty;
    }

    // 🎯 MAIN METHOD
    public int[] getMove(int[][] board) {

        if (difficulty == 0) return randomMove(board);
        if (difficulty == 1) return mixedMove(board);

        return smartMove(board);
    }

    // 🔹 Easy
    private int[] randomMove(int[][] board) {
        int r, c;
        do {
            r = rand.nextInt(size);
            c = rand.nextInt(size);
        } while (board[r][c] != 0);
        return new int[]{r, c};
    }

    // 🔹 Medium
    private int[] mixedMove(int[][] board) {
        if (rand.nextBoolean()) return randomMove(board);
        return smartMove(board);
    }

    // 🔹 Hard
    private int[] smartMove(int[][] board) {

        int bestScore = Integer.MIN_VALUE;
        int[] bestMove = null;

        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {

                if (board[r][c] == 0) {

                    int score = evaluatePosition(board, r, c);

                    if (score > bestScore) {
                        bestScore = score;
                        bestMove = new int[]{r, c};
                    }
                }
            }
        }

        return bestMove;
    }

    // 🔴 IMPORTANT: ये class के अंदर होना चाहिए
    private int evaluatePosition(int[][] board, int r, int c) {

        int score = 0;

        score += evaluateDirection(board, r, c, 2) * 2; // attack
        score += evaluateDirection(board, r, c, 1);     // defense

        int center = size / 2;
        score += (size - (Math.abs(r - center) + Math.abs(c - center)));

        return score;
    }

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


