package com.example.slitherlink;

import java.util.Random;

public class PuzzleGenerator {

    public static PuzzleData getEasyPuzzle() {

        int[][] clues = {
                {2,1,1,1,2},
                {1,-1,-1,-1,1},
                {1,-1,-1,-1,1},
                {1,-1,-1,-1,1},
                {2,1,1,1,2}
        };

        boolean[][] h = {
                {true,true,true,true,true},
                {false,false,false,false,false},
                {false,false,false,false,false},
                {false,false,false,false,false},
                {false,false,false,false,false},
                {true,true,true,true,true}
        };

        boolean[][] v = {
                {true,false,false,false,false,true},
                {true,false,false,false,false,true},
                {true,false,false,false,false,true},
                {true,false,false,false,false,true},
                {true,false,false,false,false,true}
        };

        return new PuzzleData(clues,h,v);
    }

    public static PuzzleData getMediumPuzzle() {

        int size = 8;

        int[][] clues = new int[size][size];

        for(int r=0;r<size;r++) {

            for(int c=0;c<size;c++) {

                if((r==0 || r==size-1) &&
                        (c==0 || c==size-1))
                    clues[r][c] = 2;

                else if(r==0 || r==size-1 ||
                        c==0 || c==size-1)
                    clues[r][c] = 1;

                else
                    clues[r][c] = -1;
            }
        }

        boolean[][] h =
                new boolean[size+1][size];

        boolean[][] v =
                new boolean[size][size+1];

        for(int c=0;c<size;c++) {

            h[0][c] = true;
            h[size][c] = true;
        }

        for(int r=0;r<size;r++) {

            v[r][0] = true;
            v[r][size] = true;
        }

        return new PuzzleData(clues,h,v);
    }

    public static PuzzleData getHardPuzzle() {

        int size = 12;

        int[][] clues = new int[size][size];

        for(int r=0;r<size;r++) {

            for(int c=0;c<size;c++) {

                if((r==0 || r==size-1) &&
                        (c==0 || c==size-1))
                    clues[r][c] = 2;

                else if(r==0 || r==size-1 ||
                        c==0 || c==size-1)
                    clues[r][c] = 1;

                else
                    clues[r][c] = -1;
            }
        }

        boolean[][] h =
                new boolean[size+1][size];

        boolean[][] v =
                new boolean[size][size+1];

        for(int c=0;c<size;c++) {

            h[0][c] = true;
            h[size][c] = true;
        }

        for(int r=0;r<size;r++) {

            v[r][0] = true;
            v[r][size] = true;
        }

        return new PuzzleData(clues,h,v);
    }

    public static PuzzleData getPuzzle(
            int difficulty){

        switch(difficulty){

            case 0:
                return getEasyPuzzle();

            case 1:
                return getMediumPuzzle();

            case 2:
                return getHardPuzzle();
        }

        return getEasyPuzzle();
    }

//
//    public static int[][] getEasyPuzzle() {
//
//        return new int[][]{
//                {3,-1,2,1,0},
//                {2,1,-1,2,1},
//                {-1,3,2,-1,2},
//                {1,2,1,3,-1},
//                {0,1,2,2,3}
//        };
//    }
//
//    public static int[][] generate(int size) {
//
//        Random random = new Random();
//
//        int[][] puzzle = new int[size][size];
//
//        for (int r = 0; r < size; r++) {
//            for (int c = 0; c < size; c++) {
//
//                if (random.nextBoolean()) {
//                    puzzle[r][c] = random.nextInt(4);
//                } else {
//                    puzzle[r][c] = -1;
//                }
//            }
//        }
//
//        return puzzle;
//    }

//    public static PuzzleData getEasyPuzzle() {
//
//        int[][] clues = {
//                {2,1,1,1,2},
//                {1,-1,-1,-1,1},
//                {1,-1,-1,-1,1},
//                {1,-1,-1,-1,1},
//                {2,1,1,1,2}
//        };
//
//        boolean[][] h = {
//                {true,true,true,true,true},
//                {false,false,false,false,false},
//                {false,false,false,false,false},
//                {false,false,false,false,false},
//                {false,false,false,false,false},
//                {true,true,true,true,true}
//        };
//
//        boolean[][] v = {
//                {true,false,false,false,false,true},
//                {true,false,false,false,false,true},
//                {true,false,false,false,false,true},
//                {true,false,false,false,false,true},
//                {true,false,false,false,false,true}
//        };
//
//        return new PuzzleData(
//                clues,
//                h,
//                v
//        );
//    }
}
