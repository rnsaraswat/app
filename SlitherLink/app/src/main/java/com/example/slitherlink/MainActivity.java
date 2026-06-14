package com.example.slitherlink;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    private SlitherlinkView gameView;
    private Button btnHint;
    private Button btnUndo;
    private Button btnRedo;
    private Button btnNew;
    private Button btnCheck;
    private Spinner spDifficulty;
    private Switch switchDark;
    private TextView txtScore, txtTitle, txtStatus;

    private TextView txtTimer;

    private android.os.Handler timerHandler =
            new android.os.Handler();

    private int score = 0;

    private int seconds = 0;

    private long startTime = 0;

    private boolean timerRunning = false;

    private Runnable timerRunnable;

    private Button btnPause;

    private long pausedElapsedTime = 0;

    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs =
                getSharedPreferences(
                        "theme",
                        MODE_PRIVATE);

        prefs.edit()
                .putBoolean(
                        "dark",
                        true)
                .apply();

        gameView = findViewById(R.id.gameView);
        txtStatus = findViewById(R.id.txtStatus);
        txtScore = findViewById(R.id.txtScore);
        txtTitle = findViewById(R.id.txtTitle);

        btnCheck = findViewById(R.id.btnCheck);
        btnHint = findViewById(R.id.btnHint);
        btnUndo = findViewById(R.id.btnUndo);
        btnRedo = findViewById(R.id.btnRedo);
        btnNew = findViewById(R.id.btnNew);
        spDifficulty = findViewById(R.id.spDifficulty);
        switchDark = findViewById(R.id.switchDark);
        txtTimer = findViewById(R.id.txtTimer);
        btnPause = findViewById(R.id.btnPause);

        String[] difficulties = {
                "Easy (5x5)",
                "Medium (8x8)",
                "Hard (12x12)"
        };

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        difficulties);

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);

        spDifficulty.setAdapter(adapter);


        btnNew.setOnClickListener(v -> {

            gameView.startNewGame(
                    spDifficulty.getSelectedItemPosition());

            score = 0;

            txtScore.setText("Score: 0");

            txtStatus.setText("Solve the Puzzle");

            btnHint.setEnabled(true);
            btnUndo.setEnabled(true);
            btnRedo.setEnabled(true);
            btnCheck.setEnabled(true);
            btnPause.setEnabled(true);

            startTimer();
        });

        btnHint.setOnClickListener(v -> {

            gameView.giveHint();
            score -= 10;

            if(score < 0)
                score = 0;

            txtScore.setText(
                    "Score: " + score);

        });

        btnUndo.setOnClickListener(v -> {

            gameView.undo();

        });

        btnRedo.setOnClickListener(v -> {

            gameView.redo();

        });

        switchDark.setOnCheckedChangeListener(
                (buttonView, checked) -> {

                    gameView.setDarkMode(checked);

                });

        btnPause.setOnClickListener(v -> {

            pauseGame();
        });

        btnCheck.setOnClickListener(v -> {

            if(gameView.checkPuzzle()) {

                stopTimer();

                score += 100;

                int finalScore = Math.max(1000 - seconds, 100);

                score = finalScore;

                txtScore.setText("Score: " + score);

                prefs = getSharedPreferences(
                                "scores",
                                MODE_PRIVATE);

                int best =
                        prefs.getInt(
                                "bestScore",
                                0);

                if(score > best){

                    prefs.edit()
                            .putInt(
                                    "bestScore",
                                    score)
                            .apply();
                }

                android.util.Log.d(
                        "SLITHER",
                        "GAME FINISHED");

                txtStatus.setText("🎉 Puzzle Solved!");

            } else {

                txtStatus.setText("❌ Not Solved Yet");
            }
        });

        gameView.setOnSolvedListener(() -> {

            timerHandler.removeCallbacks(timerRunnable);

            int finalScore =
                    Math.max(1000 - seconds, 100);

            txtScore.setText(
                    "Score: " + finalScore);

            txtStatus.setText(
                    "🎉 Puzzle Solved!");

            btnHint.setEnabled(false);
            btnUndo.setEnabled(false);
            btnRedo.setEnabled(false);
            btnCheck.setEnabled(false);
            btnPause.setEnabled(false);
        });

        if(savedInstanceState != null){

            gameView.restoreState(
                    (boolean[][]) savedInstanceState.getSerializable("horizontal"),
                    (boolean[][]) savedInstanceState.getSerializable("vertical")
            );
        }

        startTimer();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(
                "horizontal",
                gameView.getHorizontal());

        outState.putSerializable(
                "vertical",
                gameView.getVertical());
    }

    @Override
    protected void onPause() {
        super.onPause();

        timerHandler.removeCallbacks(
                timerRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(timerRunnable != null)
            timerHandler.post(timerRunnable);
    }

    private void startTimer() {

        stopTimer();

        startTime = System.currentTimeMillis();

        timerRunning = true;

        timerRunnable = new Runnable() {

            @Override
            public void run() {

                if (!timerRunning)
                    return;

                long elapsed =
                        System.currentTimeMillis()
                                - startTime;

                int totalSeconds =
                        (int)(elapsed / 1000);

                int hours =
                        totalSeconds / 3600;

                int minutes =
                        (totalSeconds % 3600) / 60;

                int seconds =
                        totalSeconds % 60;

                txtTimer.setText(
                        String.format(
                                "%02d:%02d:%02d",
                                hours,
                                minutes,
                                seconds));

                timerHandler.postDelayed(
                        this,
                        1000);
            }
        };

        timerHandler.post(timerRunnable);
    }

    private void stopTimer() {

        timerRunning = false;

        if (timerRunnable != null) {

            timerHandler.removeCallbacks(
                    timerRunnable);
        }
    }

    private void resetTimer() {

        stopTimer();

        txtTimer.setText("00:00");
    }

    private void pauseGame() {

        pausedElapsedTime =
                System.currentTimeMillis()
                        - startTime;

        stopTimer();

        showPauseDialog();
    }

    private void resumeGame() {

        startTime =
                System.currentTimeMillis()
                        - pausedElapsedTime;

        timerRunning = true;

        timerHandler.post(timerRunnable);
    }

    private void showPauseDialog() {

        Dialog dialog =
                new Dialog(this);

        dialog.setCancelable(false);

        dialog.setContentView(
                R.layout.dialog_pause);

        Window window =
                dialog.getWindow();

        if(window != null){

            window.setLayout(
                    (int)(getResources()
                            .getDisplayMetrics()
                            .widthPixels * 0.95),

                    (int)(getResources()
                            .getDisplayMetrics()
                            .heightPixels * 0.95));
        }

        Button btnResume =
                dialog.findViewById(
                        R.id.btnResume);

        btnResume.setOnClickListener(v -> {

            dialog.dismiss();

            resumeGame();
        });

        dialog.show();
    }
}