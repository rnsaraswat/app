package com.example.slitherlink;

public class PuzzleData {

    public int[][] clues;
    public boolean[][] solutionHorizontal;
    public boolean[][] solutionVertical;

    public PuzzleData(
            int[][] clues,
            boolean[][] h,
            boolean[][] v) {

        this.clues = clues;
        this.solutionHorizontal = h;
        this.solutionVertical = v;
    }
}