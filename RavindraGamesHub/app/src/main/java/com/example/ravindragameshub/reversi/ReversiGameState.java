package com.example.ravindragameshub.reversi;

public class ReversiGameState {
    public int[][] board;
    public boolean blackTurn;

    public ReversiGameState(int[][] b, boolean turn) {

        board = new int[8][8];

        for(int i=0;i<8;i++) {
            for(int j=0;j<8;j++) {
                board[i][j] = b[i][j];
            }
        }

        blackTurn = turn;
    }
}
