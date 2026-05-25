package com.example.ravindragameshub;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import com.example.ravindragameshub.common.ThemeManager;

import java.util.*;

public class TicTacToeActivity extends BaseGameActivity {

    //tic tac toe game buttons (3x3=9)
    Button[] buttons = new Button[9];
    //status text view
    TextView statusText;
    //level drop down button
    Spinner levelSpinner;
    //board variable Spaces(empty) in initial
    char[] board = {' ',' ',' ',' ',' ',' ',' ',' ',' '};
    //player / ai variable
    char player = 'X';
    char ai = 'O';
    //winline variable
    View winLine;
    //player / ai score variable
    int playerScore = 0;
    int aiScore = 0;
    //score text view
    TextView scoreText, txtTitle;
    Button btnHome;
    LinearLayout rootLayout;
    //sound variables
//    MediaPlayer tapSound, winSound, loseSound, drawSound;
//    MediaPlayer bgMusic;
//    boolean isMusicOn = false;
    //fireworks variable
//    FireworksView fireworks;
    //define winning combination
    int[][] winCombos = {
            {0,1,2},{3,4,5},{6,7,8},
            {0,3,6},{1,4,7},{2,5,8},
            {0,4,8},{2,4,6}
    };
    //random move
    Random rand = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tictactoe);





        //variable to save local
//        prefs = getSharedPreferences("settings", MODE_PRIVATE);
        //score
        scoreText = findViewById(R.id.scoreText);
        //winline
        winLine = findViewById(R.id.winLine);
        //status
        statusText = findViewById(R.id.statusText);
        txtTitle = findViewById(R.id.txtTitle);
        //level
        levelSpinner = findViewById(R.id.levelSpinner);
        //home button
        btnHome = findViewById(R.id.btnHome);
        //theme root layout
//        rootLayout = findViewById(R.id.rootLayout);
        // 🌈 Theme
        LinearLayout rootLayout = findViewById(R.id.rootLayout);

        //background music
//        bgMusic = MediaPlayer.create(this, R.raw.bg_music);
//        bgMusic.setLooping(true);
//        //back ground music button
//        findViewById(R.id.musicBtn).setOnClickListener(v -> toggleMusic());
//        //sound variables
//        tapSound = MediaPlayer.create(this, R.raw.tap);
//        winSound = MediaPlayer.create(this, R.raw.win);
//        loseSound = MediaPlayer.create(this, R.raw.lose);
//        drawSound = MediaPlayer.create(this, R.raw.draw);

        //fireworks display
//        fireworks = new FireworksView(this);
//        addContentView(fireworks,
//                new FrameLayout.LayoutParams(
//                        FrameLayout.LayoutParams.MATCH_PARENT,
//                        FrameLayout.LayoutParams.MATCH_PARENT));

        //change & set levels
        String[] levels = {"Easy", "Medium", "Hard"};
        levelSpinner.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, levels));

        //game buttons click
        for (int i = 0; i < 9; i++) {
            int id = getResources().getIdentifier("b" + i, "id", getPackageName());
            buttons[i] = findViewById(id);

            int index = i;
            buttons[i].setOnClickListener(v -> playerMove(index));
        }

        //theme apply
        //theme Spinner setup
//        Spinner themeSpinner = findViewById(R.id.themeSpinner);
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(
//                this,
//                android.R.layout.simple_spinner_dropdown_item,
//                themeNames
//        );
//
//        themeSpinner.setAdapter(adapter);
//        // saved theme select करो
//        themeSpinner.setSelection(currentThemeIndex);
//
//        themeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                currentThemeIndex = position;
//
//                applyTheme();
//                // save theme safe call
//                if (prefs != null) {
//                    saveTheme();
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {}
//        });

        btnHome.setOnClickListener(v -> {

            Toast.makeText(this, "Home clicked", Toast.LENGTH_SHORT).show();

            AlertDialog.Builder builder =
                    new AlertDialog.Builder(this);

            builder.setTitle("Exit Game");

            builder.setMessage(
                    "Your game will be lost.\nDo you want to go Home?"
            );

            // ✅ YES
            builder.setPositiveButton("Yes",
                    (dialog, which) -> {

                        Intent intent =
                                new Intent(
                                        TicTacToeActivity.this,
                                        MainActivity.class
                                );

                        startActivity(intent);

                        finish();
                    });

            // ❌ NO
            builder.setNegativeButton("No",
                    (dialog, which) -> {

                        dialog.dismiss();
                    });

            builder.show();
        });

        //restart button
        findViewById(R.id.restartBtn).setOnClickListener(v -> resetGame());
    }

    //draw win line
    void drawWinLine(int[] combo) {
        winLine.setVisibility(View.VISIBLE);
        winLine.setScaleX(0f);

        winLine.animate()
                .scaleX(1f)
                .setDuration(600)
                .setInterpolator(new android.view.animation.DecelerateInterpolator())
                .start();
    }

    //player move
    void playerMove(int i) {
        if (board[i] != ' ') return;

        board[i] = player;
        buttons[i].setText("X");
//        playSound(tapSound);

        //Button Click Animation (Smooth animation)
        buttons[i].animate()
                .scaleX(0.8f)
                .scaleY(0.8f)
                .setDuration(100)
                .withEndAction(() -> {
                    buttons[i].setScaleX(1f);
                    buttons[i].setScaleY(1f);
                });

        //check win
        if (checkWin(player)) {
            statusText.setText("You Win!");
            playerScore++;
            updateScore();
            highlightWin(player);
//            playSound(winSound);
//            fireworks.setVisibility(View.VISIBLE);
//            fireworks.start();
            disableAll();
            return;
        }

        //check for draw
        if (isDraw()) {
            statusText.setText("Draw!");
//            playSound(drawSound);
            return;
        }

        //disable all button during before computer move & enable after
        new android.os.Handler().postDelayed(() -> {
            disableAll();
            aiMove();
            enableAll();
        }, 400);
    }

    //computer turn
    void aiMove() {
        int move = getBestMove();
        board[move] = ai;
        buttons[move].setText("O");

        if (checkWin(ai)) {
            statusText.setText("Computer Wins!");
            aiScore++;
            updateScore();
            highlightWin(ai);
//            playSound(loseSound);
            disableAll();
            return;
        }

        if (isDraw()) {
            statusText.setText("Draw!");
//            playSound(drawSound);
        }
    }

    //update score
    void updateScore() {
        scoreText.setText("Player: " + playerScore + "  AI: " + aiScore);
    }

    //LEVEL LOGIC
    int getBestMove() {
        String level = levelSpinner.getSelectedItem().toString();

        if (level.equals("Easy")) {
            return getRandomMove();
        } else if (level.equals("Medium")) {
            return rand.nextBoolean() ? getRandomMove() : minimaxMove();
        } else {
            return minimaxMove(); // Hard
        }
    }

    int getRandomMove() {
        List<Integer> empty = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            if (board[i] == ' ') empty.add(i);
        }
        return empty.get(rand.nextInt(empty.size()));
    }

    //MINIMAX
    int minimaxMove() {
        int bestScore = Integer.MIN_VALUE;
        int move = 0;

        for (int i = 0; i < 9; i++) {
            if (board[i] == ' ') {
                board[i] = ai;
                int score = minimax(0, false);
                board[i] = ' ';

                if (score > bestScore) {
                    bestScore = score;
                    move = i;
                }
            }
        }
        return move;
    }

    int minimax(int depth, boolean isMax) {
        if (checkWin(ai)) return 10 - depth;
        if (checkWin(player)) return depth - 10;
        if (isDraw()) return 0;

        if (isMax) {
            int best = Integer.MIN_VALUE;
            for (int i = 0; i < 9; i++) {
                if (board[i] == ' ') {
                    board[i] = ai;
                    best = Math.max(best, minimax(depth + 1, false));
                    board[i] = ' ';
                }
            }
            return best;
        } else {
            int best = Integer.MAX_VALUE;
            for (int i = 0; i < 9; i++) {
                if (board[i] == ' ') {
                    board[i] = player;
                    best = Math.min(best, minimax(depth + 1, true));
                    board[i] = ' ';
                }
            }
            return best;
        }
    }

    //enable all buttons
    void enableAll() {
        for (int i = 0; i < 9; i++) {
            if (board[i] == ' ') buttons[i].setEnabled(true);
        }
    }

    boolean checkWin(char p) {
        for (int[] combo : winCombos) {
            if (board[combo[0]] == p &&
                    board[combo[1]] == p &&
                    board[combo[2]] == p) {
                return true;
            }
        }
        return false;
    }

    boolean isDraw() {
        for (char c : board) if (c == ' ') return false;
        return true;
    }

    void highlightWin(char p) {
        for (int[] combo : winCombos) {

            if (board[combo[0]] == p &&
                    board[combo[1]] == p &&
                    board[combo[2]] == p) {

                for (int i : combo) {

                    buttons[i].setBackgroundColor(Color.GREEN);

                    buttons[i].animate()
                            .scaleX(1.2f)
                            .scaleY(1.2f)
                            .setDuration(200)
                            .withEndAction(() -> {
                                buttons[i].setScaleX(1f);
                                buttons[i].setScaleY(1f);
                            });
                }

                drawWinLine(combo); // अगर line भी use कर रहे हो
            }
        }
    }

    void disableAll() {
        for (Button b : buttons) b.setEnabled(false);
    }

    void resetGame() {
        for (int i = 0; i < 9; i++) {
            board[i] = ' ';
            buttons[i].setText("");
            buttons[i].setEnabled(true);
            buttons[i].setBackgroundColor(Color.LTGRAY);
        }
        statusText.setText("Your Turn");
//        applyTheme();
    }

}
