package com.example.watersort;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.Collections;

public class LevelGenerator {

//    private static final int[] COLORS = {
//
//            Color.RED,
//            Color.BLUE,
//            Color.GREEN,
//            Color.YELLOW,
//            Color.MAGENTA,
//            Color.CYAN,
//            Color.GRAY,
//            Color.BLACK
//    };

//    public static ArrayList<Tube>
//    generate(int level) {
//
//        ArrayList<Tube> tubes =
//                new ArrayList<>();
//
////        int colorCount =
////                Math.min(
////                        3 + (level / 5),
////                        8);
//
//        int filledTubes = 12;
//        int emptyTubes = 2;
//
//        int colorCount = filledTubes;
//
//        ArrayList<Integer> pool =
//                new ArrayList<>();
//
//        for (int c = 0;
//             c < colorCount;
//             c++) {
//
//            for (int i = 0;
//                 i < 4;
//                 i++) {
//
//                pool.add(COLORS[c]);
//            }
//        }
//
//        Collections.shuffle(pool);
//
//        while (!pool.isEmpty()) {
//
//            Tube tube =
//                    new Tube();
//
//            for (int i = 0;
//                 i < 4 &&
//                         !pool.isEmpty();
//                 i++) {
//
//                tube.push(
//                        pool.remove(0));
//            }
//
//            tubes.add(tube);
//        }
//
//        // 2 Empty Tubes
//
//        tubes.add(new Tube());
//        tubes.add(new Tube());
//
//        return tubes;
//    }

    public static ArrayList<Tube> generate(int level) {

        ArrayList<Tube> tubes =
                new ArrayList<>();

        int filledTubes = 12;
        int emptyTubes = 2;

        ArrayList<Integer> pool =
                new ArrayList<>();

        int[] colors = {

                Color.RED,
                Color.BLUE,
                Color.GREEN,
                Color.YELLOW,
                Color.MAGENTA,
                Color.CYAN,
                Color.GRAY,
                Color.BLACK,
                Color.rgb(255,128,0),
                Color.rgb(128,0,255),
                Color.rgb(0,128,255),
                Color.rgb(255,0,128)
        };

        // 12 colors × 4 layers
        for (int c = 0;
             c < filledTubes;
             c++) {

            for (int i = 0;
                 i < 4;
                 i++) {

                pool.add(colors[c]);
            }
        }

        Collections.shuffle(pool);

        for (int t = 0;
             t < filledTubes;
             t++) {

            Tube tube = new Tube();

            for (int i = 0;
                 i < 4;
                 i++) {

                tube.push(
                        pool.remove(0));
            }

            tubes.add(tube);
        }

        // 2 Empty Tubes

        tubes.add(new Tube());
        tubes.add(new Tube());

        return tubes;
    }
}