package com.example.tictactoe;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.*;
import android.media.MediaPlayer;

public class MainActivity extends AppCompatActivity {

    //Multiple themes gradient
    //Theme class
    static class Theme {
        int bgColorStart;
        int bgColorEnd;
        int btnColor;
        int textColor;
        boolean isGradient;

        Theme(int bgStart, int bgEnd, int btn, int text, boolean gradient) {
            bgColorStart = bgStart;
            bgColorEnd = bgEnd;
            btnColor = btn;
            textColor = text;
            isGradient = gradient;
        }
    }

    //define Themes
    Theme lightTheme = new Theme(Color.WHITE, Color.WHITE, Color.LTGRAY, Color.BLACK, false);
    Theme darkTheme = new Theme(Color.BLACK, Color.BLACK, Color.DKGRAY, Color.WHITE, false);
    //Blue Gradient
    Theme blueTheme = new Theme(
            Color.parseColor("#0D47A1"),
            Color.parseColor("#42A5F5"),
            Color.parseColor("#1976D2"),
            Color.WHITE,
            true
    );
    //Orange Gradient
    Theme orangeTheme = new Theme(
            Color.parseColor("#E65100"),
            Color.parseColor("#FFB74D"),
            Color.parseColor("#FB8C00"),
            Color.BLACK,
            true
    );
    // Theme Array
    Theme[] themes = {lightTheme, darkTheme, blueTheme, orangeTheme};
    // theme names display in dropdown
    String[] themeNames = {"Light", "Dark", "Blue", "Orange"};
    //set Current theme index
    int currentThemeIndex = 0;

    //variables
    //theme local saved variable
    SharedPreferences prefs;
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
    TextView scoreText;
    //sound variables
    MediaPlayer tapSound, winSound, loseSound, drawSound;
    MediaPlayer bgMusic;
    boolean isMusicOn = false;
    //fireworks variable
    FireworksView fireworks;
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
        setContentView(R.layout.activity_main);
        //variable to save local
        prefs = getSharedPreferences("settings", MODE_PRIVATE);
        //score
        scoreText = findViewById(R.id.scoreText);
        //winline
        winLine = findViewById(R.id.winLine);
        //status
        statusText = findViewById(R.id.statusText);
        //level
        levelSpinner = findViewById(R.id.levelSpinner);

        //background music
        bgMusic = MediaPlayer.create(this, R.raw.bg_music);
        bgMusic.setLooping(true);
        //back ground music button
        findViewById(R.id.musicBtn).setOnClickListener(v -> toggleMusic());
        //sound variables
        tapSound = MediaPlayer.create(this, R.raw.tap);
        winSound = MediaPlayer.create(this, R.raw.win);
        loseSound = MediaPlayer.create(this, R.raw.lose);
        drawSound = MediaPlayer.create(this, R.raw.draw);

        //fireworks display
        fireworks = new FireworksView(this);
        addContentView(fireworks,
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT));

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
        // theme button click
        //Theme load
        currentThemeIndex = prefs.getInt("theme", 0);
        //theme apply
        applyTheme();

        //theme Spinner setup
        Spinner themeSpinner = findViewById(R.id.themeSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                themeNames
        );

        themeSpinner.setAdapter(adapter);
        // saved theme select करो
        themeSpinner.setSelection(currentThemeIndex);

        themeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentThemeIndex = position;

                applyTheme();
                // save theme safe call
                if (prefs != null) {
                    saveTheme();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        //restart button
        findViewById(R.id.restartBtn).setOnClickListener(v -> resetGame());
    }

    //save theme local Multiple
    void saveTheme() {
        prefs.edit().putInt("theme", currentThemeIndex).apply();
    }

    //applyTheme method Multiple
    void applyTheme() {
        Theme t = themes[currentThemeIndex];
        View root = getWindow().getDecorView();

        // change Background color
        if (t.isGradient) {
            GradientDrawable gradient = new GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    new int[]{t.bgColorStart, t.bgColorEnd}
            );
            root.setBackground(gradient);
        } else {
            root.setBackgroundColor(t.bgColorStart);
        }

        // change Buttons colors
        for (Button b : buttons) {
            b.setBackgroundColor(t.btnColor);
            b.setTextColor(t.textColor);
        }

        // change Text colors
        statusText.setTextColor(t.textColor);
        scoreText.setTextColor(t.textColor);
    }

    //play sound method
    void playSound(MediaPlayer mp) {
        if (mp != null) {
            mp.start();
        }
    }

    //background music on/off
    void toggleMusic() {
        if (isMusicOn) {
            bgMusic.pause();
        } else {
            bgMusic.start();
        }
        isMusicOn = !isMusicOn;
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
        playSound(tapSound);

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
            playSound(winSound);
            fireworks.setVisibility(View.VISIBLE);
            fireworks.start();
            disableAll();
            return;
        }

        //check for draw
        if (isDraw()) {
            statusText.setText("Draw!");
            playSound(drawSound);
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
            playSound(loseSound);
            disableAll();
            return;
        }

        if (isDraw()) {
            statusText.setText("Draw!");
            playSound(drawSound);
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
        applyTheme();
    }
}