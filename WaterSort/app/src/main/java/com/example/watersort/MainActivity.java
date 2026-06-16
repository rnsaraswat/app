package com.example.watersort;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    private GridLayout grid;

    private TextView tvMoves;

    private Button btnUndo;
    private Button btnRestart;
    private Button btnHint;
    private Button btnTheme;

    private ArrayList<Tube> tubes =
            new ArrayList<>();

    private ArrayList<TubeView> tubeViews =
            new ArrayList<>();

    private Stack<Move> undoStack =
            new Stack<>();

    private Tube selectedTube = null;

    private TubeView selectedView = null;

    private int moves = 0;
    private MediaPlayer bgPlayer, pourPlayer, winPlayer;
    private int currentLevel = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if (PreferenceManager
                .isDarkMode(this)) {

            setTheme(
                    R.style.Theme_App_Dark);
        }
        else {

            setTheme(
                    R.style.Theme_App_Light);
        }

        grid = findViewById(R.id.grid);

        tvMoves = findViewById(R.id.tvMoves);

        btnUndo = findViewById(R.id.btnUndo);
        btnRestart = findViewById(R.id.btnRestart);
        btnHint = findViewById(R.id.btnHint);
        btnTheme = findViewById(R.id.btnTheme);

        bgPlayer =
                MediaPlayer.create(
                        this,
                        R.raw.bg_music);

        bgPlayer.setLooping(true);

        bgPlayer.start();

        pourPlayer =
                MediaPlayer.create(
                        this,
                        R.raw.pour);

        winPlayer =
                MediaPlayer.create(
                        this,
                        R.raw.win);

        btnTheme.setOnClickListener(v -> {

            boolean dark =
                    !PreferenceManager
                            .isDarkMode(this);

            PreferenceManager
                    .saveDarkMode(
                            this,
                            dark);

            recreate();
        });

        btnUndo.setOnClickListener(v -> undoMove());

        btnRestart.setOnClickListener(v -> loadLevel());

        btnHint.setOnClickListener(v -> showHint());

        currentLevel =
                PreferenceManager
                        .getLevel(this);

        loadLevel();
    }

    private void playSound() {

        if (pourPlayer != null)
            pourPlayer.start();
    }

    private void loadLevel() {

        moves = 0;

        undoStack.clear();

        tvMoves.setText("Moves : 0");

        tubes.clear();

        grid.removeAllViews();

        tubeViews.clear();

        selectedTube = null;

        selectedView = null;

//        createDemoLevel();
        tubes =
                LevelGenerator.generate(
                        currentLevel);

        createTubeViews();
    }

//    private void createDemoLevel() {
//
//        Tube t1 = new Tube();
//
//        t1.push(Color.RED);
//        t1.push(Color.BLUE);
//        t1.push(Color.GREEN);
//        t1.push(Color.RED);
//
//        Tube t2 = new Tube();
//
//        t2.push(Color.GREEN);
//        t2.push(Color.BLUE);
//        t2.push(Color.RED);
//        t2.push(Color.GREEN);
//
//        Tube t3 = new Tube();
//
//        t3.push(Color.BLUE);
//        t3.push(Color.RED);
//        t3.push(Color.GREEN);
//        t3.push(Color.BLUE);
//
//        Tube empty1 = new Tube();
//
//        Tube empty2 = new Tube();
//
//        tubes.add(t1);
//        tubes.add(t2);
//        tubes.add(t3);
//        tubes.add(empty1);
//        tubes.add(empty2);
//    }

    private void createTubeViews() {

        for (Tube tube : tubes) {

            TubeView tubeView =
                    new TubeView(this);

            tubeView.setTube(tube);

            GridLayout.LayoutParams params =
                    new GridLayout.LayoutParams();

//            params.width = 220;
//            params.height = 420;

//            params.width =
//                    dm.widthPixels / 4 - 20;
//
//            params.height =
//                    dm.heightPixels / 4;

            DisplayMetrics dm =
                    getResources()
                            .getDisplayMetrics();

            params.width =
                    dm.widthPixels / 4 - 65;

            params.height =
                    dm.heightPixels / 5;

            params.setMargins(5, 15, 4, 15);

            tubeView.setLayoutParams(params);

            tubeView.setOnClickListener(v ->
                    onTubeClicked(
                            tube,
                            tubeView));

            tubeViews.add(tubeView);

            grid.addView(tubeView);
        }
    }

    private void onTubeClicked(
            Tube tube,
            TubeView tubeView) {

        // First Selection

        if (selectedTube == null) {

            if (tube.isEmpty())
                return;

            selectedTube = tube;

            selectedView = tubeView;

            tubeView.setSelectedTube(true);

            return;
        }

        // Same Tube

        if (selectedTube == tube) {

            selectedView.setSelectedTube(false);

            selectedTube = null;

            selectedView = null;

            return;
        }

        // Pour

        if (selectedTube.canPourTo(tube)) {

            int sourceIndex =
                    tubes.indexOf(selectedTube);

            int targetIndex =
                    tubes.indexOf(tube);

//            int color =
//                    selectedTube.pop();
//
//            tube.push(color);
//
//            undoStack.push(
//                    new Move(
//                            sourceIndex,
//                            targetIndex,
//                            color));

            pourTube(
                    selectedTube,
                    tube,
                    sourceIndex,
                    targetIndex);

            moves++;

            tvMoves.setText(
                    "Moves : " + moves);

            refreshViews();

            checkWin();
        }

        selectedView.setSelectedTube(false);

        selectedTube = null;

        selectedView = null;
    }

    private void refreshViews() {

        for (int i = 0;
             i < tubeViews.size();
             i++) {

            tubeViews.get(i)
                    .setTube(
                            tubes.get(i));
        }
    }

    private void undoMove() {

        if (undoStack.empty())
            return;

        Move move =
                undoStack.pop();

        Tube source =
                tubes.get(move.from);

        Tube target =
                tubes.get(move.to);

        for (int i = 0;
             i < move.count;
             i++) {

            source.push(
                    target.pop());
        }

        moves--;

        if (moves < 0)
            moves = 0;

        tvMoves.setText(
                "Moves : " + moves);

        refreshViews();
    }

    private void showHint() {

        for (int i = 0; i < tubes.size(); i++) {

            for (int j = 0; j < tubes.size(); j++) {

                if (i == j)
                    continue;

                Tube source =
                        tubes.get(i);

                Tube target =
                        tubes.get(j);

                if (source.canPourTo(target)) {

                    tubeViews.get(i)
                            .setSelectedTube(true);

                    tubeViews.get(j)
                            .setSelectedTube(true);

                    return;
                }
            }
        }
    }

    private Move findBestMove() {

        Move bestMove = null;

        int bestScore = -1;

        for (int i = 0;
             i < tubes.size();
             i++) {

            for (int j = 0;
                 j < tubes.size();
                 j++) {

                if (i == j)
                    continue;

                Tube source =
                        tubes.get(i);

                Tube target =
                        tubes.get(j);

                if (!source.canPourTo(target))
                    continue;

                int score = 0;

                if (target.isEmpty())
                    score += 1;

                if (!target.isEmpty())
                    score += 5;

                if (score > bestScore) {

                    bestScore = score;

                    bestMove =
                            new Move(
                                    i,
                                    j,
                                    1);
                }
            }
        }

        return bestMove;
    }

    private void showSmartHint() {

        Move move =
                findBestMove();

        if(move == null)
            return;

        tubeViews.get(move.from)
                .setSelectedTube(true);

        tubeViews.get(move.to)
                .setSelectedTube(true);
    }

    private void checkWin() {

        for (Tube tube : tubes) {

            if (!tube.isEmpty()
                    && !tube.isCompleted()) {

                return;
            }
        }

        if (winPlayer != null)
            winPlayer.start();

        FireworkView fw =
                new FireworkView(this);

//        addContentView(
//                fw,
//                new ViewGroup.LayoutParams(
//                        MATCH_PARENT,
//                        MATCH_PARENT));

        addContentView(
                fw,
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                )
        );

        new AlertDialog.Builder(this)
                .setTitle("Congratulations!")
                .setMessage(
                        "Level " +
                                currentLevel +
                                "\n\nMoves : " +
                                moves +
                                "\n\n" +
                                starText()
                )
                .setPositiveButton(
                        "Next Level",
                        (d,w)->{

                            currentLevel++;

                            PreferenceManager
                                    .saveLevel(
                                            this,
                                            currentLevel);

                            loadLevel();
                        })
                .show();



    }

    private void pourTube(
            Tube source,
            Tube target,
            int sourceIndex,
            int targetIndex) {

        int color =
                source.topColor();

        int sameCount =
                source.countTopSameColor();

        int freeSpace =
                target.emptySlots();

        int transferCount =
                Math.min(
                        sameCount,
                        freeSpace);

        for (int i = 0;
             i < transferCount;
             i++) {

            int movedColor =
                    source.pop();

            target.push(movedColor);

            undoStack.push(
                    new Move(
                            sourceIndex,
                            targetIndex,
                            transferCount));
        }

        moves++;

        tvMoves.setText(
                "Moves : " + moves);

        playPourAnimation(
                tubeViews.get(sourceIndex));

        tubeViews.get(sourceIndex)
                .startPour(color);

        tubeViews.get(sourceIndex)
                .startWobble();

        tubeViews.get(targetIndex)
                .startWobble();

        tubeViews.get(targetIndex)
                .animateLevel();

        tubeViews.get(targetIndex)
                .splash();

        playSound();

        refreshViews();

        checkWin();
    }

    //simple tilt animation
//    private void playPourAnimation(
//            TubeView tubeView) {
//
//        ObjectAnimator tilt =
//                ObjectAnimator.ofFloat(
//                        tubeView,
//                        "rotation",
//                        0f,
//                        25f,
//                        0f);
//
//        tilt.setDuration(300);
//
//        tilt.start();
//    }

    private void playPourAnimation(
            TubeView tubeView) {

        ObjectAnimator tilt =
                ObjectAnimator.ofFloat(
                        tubeView,
                        "rotation",
                        0f,
                        35f,
                        20f,
                        35f,
                        0f);

        tilt.setDuration(500);

        tilt.start();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (pourPlayer != null)
            pourPlayer.release();

        if (winPlayer != null)
            winPlayer.release();

        if (bgPlayer != null) {

            bgPlayer.release();

            bgPlayer = null;
        }
    }

    protected void onPause() {

        super.onPause();

        PreferenceManager
                .saveLevel(
                        this,
                        currentLevel);
        if (bgPlayer != null)
            bgPlayer.pause();
    }

    @Override
    protected void onResume() {

        super.onResume();

        if (bgPlayer != null)
            bgPlayer.start();
    }

    private int getStars() {

        if (moves <= 15)
            return 3;

        if (moves <= 30)
            return 2;

        return 1;
    }

    private String starText() {

        switch (getStars()) {

            case 3:
                return "⭐⭐⭐";

            case 2:
                return "⭐⭐";

            default:
                return "⭐";
        }
    }

    private void addExtraTube() {

        Tube tube =
                new Tube();

        tubes.add(tube);

        createSingleTubeView(
                tube);

        refreshViews();
    }

    private void createSingleTubeView(Tube tube) {

        TubeView tubeView =
                new TubeView(this);

        tubeView.setTube(tube);

        GridLayout.LayoutParams params =
                new GridLayout.LayoutParams();

        params.width = 220;
        params.height = 420;

        tubeView.setLayoutParams(params);

        tubeView.setOnClickListener(v ->
                onTubeClicked(
                        tube,
                        tubeView));

        tubeViews.add(tubeView);

        grid.addView(tubeView);
    }

}