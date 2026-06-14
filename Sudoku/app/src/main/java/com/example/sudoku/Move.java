package com.example.sudoku;

public class Move {

    public int row;
    public int col;

    public String oldValue;
    public String newValue;

    public Move(int row,
                int col,
                String oldValue,
                String newValue) {

        this.row = row;
        this.col = col;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }
}
