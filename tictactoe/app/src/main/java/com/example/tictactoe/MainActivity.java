package com.example.tictactoe;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.*;
import android.media.MediaPlayer;

public class MainActivity extends AppCompatActivity {


    //Multiple themes code
    // 👇 1. Theme class (अगर यहीं बना रहे हो)
    class Theme {
        int bgColor;
        int btnColor;
        int textColor;

        Theme(int bg, int btn, int text) {
            bgColor = bg;
            btnColor = btn;
            textColor = text;
        }
    }

    // 👇 2. Themes यहीं define करो
    Theme lightTheme = new Theme(Color.WHITE, Color.LTGRAY, Color.BLACK);
    Theme darkTheme = new Theme(Color.BLACK, Color.DKGRAY, Color.WHITE);
    Theme blueTheme = new Theme(Color.parseColor("#0D47A1"),
            Color.parseColor("#1976D2"),
            Color.WHITE);

    // 👇 3. Array
    Theme[] themes = {lightTheme, darkTheme, blueTheme};

    // 👇 4. Current theme index
    int currentThemeIndex = 0;

    // 👇 बाकी variables
    SharedPreferences prefs;
//    Button[] buttons = new Button[9];
//    TextView statusText, scoreText;

    Button[] buttons = new Button[9];
    TextView statusText;
    Spinner levelSpinner;

    char[] board = {' ',' ',' ',' ',' ',' ',' ',' ',' '};
    char player = 'X';
    char ai = 'O';
    View winLine;
    int playerScore = 0;
    int aiScore = 0;
    TextView scoreText;
    //old theme variable
    //boolean isDark = false;

    MediaPlayer tapSound, winSound, loseSound, drawSound;
    MediaPlayer bgMusic;
    boolean isMusicOn = false;
    FireworksView fireworks;
//    SharedPreferences prefs;

    int[][] winCombos = {
            {0,1,2},{3,4,5},{6,7,8},
            {0,3,6},{1,4,7},{2,5,8},
            {0,4,8},{2,4,6}
    };

    Random rand = new Random();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //score
        scoreText = findViewById(R.id.scoreText);

        //theme old
//        findViewById(R.id.themeBtn).setOnClickListener(v -> toggleTheme());
        prefs = getSharedPreferences("settings", MODE_PRIVATE);
        currentThemeIndex = prefs.getInt("theme", 0); // load saved
        applyTheme();

        winLine = findViewById(R.id.winLine);
        statusText = findViewById(R.id.statusText);
        levelSpinner = findViewById(R.id.levelSpinner);

        //background music
        bgMusic = MediaPlayer.create(this, R.raw.bg_music);
        bgMusic.setLooping(true);
        //back ground music button
        findViewById(R.id.musicBtn).setOnClickListener(v -> toggleMusic());

        //sound
        tapSound = MediaPlayer.create(this, R.raw.tap);
        winSound = MediaPlayer.create(this, R.raw.win);
        loseSound = MediaPlayer.create(this, R.raw.lose);
        drawSound = MediaPlayer.create(this, R.raw.draw);

        //fireworks
        fireworks = new FireworksView(this);
        addContentView(fireworks,
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT));

        //levels
        String[] levels = {"Easy", "Medium", "Hard"};
        levelSpinner.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, levels));

        //game buttons
        for (int i = 0; i < 9; i++) {
            int id = getResources().getIdentifier("b" + i, "id", getPackageName());
            buttons[i] = findViewById(id);

            int index = i;
            buttons[i].setOnClickListener(v -> playerMove(index));
        }
        // theme click
        findViewById(R.id.themeBtn).setOnClickListener(v -> {
            currentThemeIndex = (currentThemeIndex + 1) % themes.length;
            applyTheme();
            saveTheme();
        });

        //restart button
        findViewById(R.id.restartBtn).setOnClickListener(v -> resetGame());
    }

    //theme Change old theme
//    void toggleTheme() {
//////        if (isDark) {
//////            getWindow().getDecorView().setBackgroundColor(Color.WHITE);
//////        } else {
//////            getWindow().getDecorView().setBackgroundColor(Color.BLACK);
//////        }
//////        isDark = !isDark;
//
////        View root = getWindow().getDecorView();
////
////        root.animate()
////                .alpha(0f)
////                .setDuration(150)
////                .withEndAction(() -> {
////                    root.setBackgroundColor(isDark ? Color.WHITE : Color.BLACK);
////                    root.animate().alpha(1f).setDuration(150).start();
////                });
////
////        isDark = !isDark;
////    }
//
//    //Multiple themes code
//    class Theme {
//        int bgColor;
//        int btnColor;
//        int textColor;
//
//        Theme(int bg, int btn, int text) {
//            bgColor = bg;
//            btnColor = btn;
//            textColor = text;
//        }
//    }
//
//    Theme lightTheme = new Theme(Color.WHITE, Color.LTGRAY, Color.BLACK);
//    Theme darkTheme = new Theme(Color.BLACK, Color.DKGRAY, Color.WHITE);
//    Theme blueTheme = new Theme(Color.parseColor("#0D47A1"),
//            Color.parseColor("#1976D2"),
//            Color.WHITE);
//
//    Theme[] themes = {lightTheme, darkTheme, blueTheme};
//    int currentThemeIndex = 0;


    //save theme local Multiple
    void saveTheme() {
        prefs.edit().putInt("theme", currentThemeIndex).apply();
    }

    //applyTheme method Multiple
    void applyTheme() {

        Theme t = themes[currentThemeIndex];

        // Background
        getWindow().getDecorView().setBackgroundColor(t.bgColor);

        // Buttons
        for (Button b : buttons) {
            b.setBackgroundColor(t.btnColor);
            b.setTextColor(t.textColor);
        }

        // Text
        statusText.setTextColor(t.textColor);
        scoreText.setTextColor(t.textColor);
    }

    //play sound
    void playSound(MediaPlayer mp) {
        if (mp != null) {
            mp.start();
        }
    }

    //background music
    void toggleMusic() {
        if (isMusicOn) {
            bgMusic.pause();
        } else {
            bgMusic.start();
        }
        isMusicOn = !isMusicOn;
    }

    void drawWinLine(int[] combo) {

//        winLine.setVisibility(View.VISIBLE);
//
//        winLine.setScaleX(0f);
//
//        winLine.animate()
//                .scaleX(1f)
//                .setDuration(500)
//                .start();

        winLine.setVisibility(View.VISIBLE);
        winLine.setScaleX(0f);

        winLine.animate()
                .scaleX(1f)
                .setDuration(600)
                .setInterpolator(new android.view.animation.DecelerateInterpolator())
                .start();
    }

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

        if (isDraw()) {
            statusText.setText("Draw!");
            playSound(drawSound);
            return;
        }

//        aiMove();
        new android.os.Handler().postDelayed(() -> {
            disableAll();
            aiMove();
            enableAll();
        }, 400);
    }

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

    // 🎮 LEVEL LOGIC
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

    // 🧠 MINIMAX
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

    // ✨ Highlight Winning Line
//    void highlightWin(char p) {
//        for (int[] combo : winCombos) {
//            if (board[combo[0]] == p &&
//                    board[combo[1]] == p &&
//                    board[combo[2]] == p) {
//
//                buttons[combo[0]].setBackgroundColor(Color.GREEN);
//                buttons[combo[1]].setBackgroundColor(Color.GREEN);
//                buttons[combo[2]].setBackgroundColor(Color.GREEN);
//                drawWinLine(combo);
//            }
//        }
//    }

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