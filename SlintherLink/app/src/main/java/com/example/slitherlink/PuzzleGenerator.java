package com.example.slitherlink;

public class PuzzleGenerator {

    public static int[][] getEasyPuzzle() {

        return new int[][]{
                {3,-1,2,1,0},
                {2,1,-1,2,1},
                {-1,3,2,-1,2},
                {1,2,1,3,-1},
                {0,1,2,2,3}
        };
    }
}