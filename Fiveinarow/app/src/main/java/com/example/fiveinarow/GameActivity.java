package com.example.fiveinarow;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Stack;

public class GameActivity extends AppCompatActivity {

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
    String[] themeNames = {"Theme Light", "Theme Dark", "Theme Blue", "Theme Orange"};
    //set Current theme index
    int currentThemeIndex = 0;

    BoardView boardView;
    int mode, difficulty;
    boolean gameOver = false;
    boolean isGamePaused = false;
    TextView statusText;
    Button restartBtn;
    //undo variales
    Button undoBtn;
    Stack<int[]> moveHistory = new Stack<>();
    int undoLimit;
    int undoLeft;

    int currentPlayer = 1; // 1 = Player1, 2 = Player2/AI
    AIPlayer ai;

    //timer variables
//    GameTimer gameTimer;
//    Handler handler;
    TextView tvTime;
    Button btnPause;

    GameTimer gameTimer;
    Handler handler = new Handler();
    Runnable timerRunnable;

//    boolean isGamePaused = false;
    boolean isGameOver = false;

    //moves
    int moveCount = 0;
    TextView tvMoves;

    //sound variables
    MediaPlayer tapSound, winSound, loseSound, drawSound;
    MediaPlayer bgMusic;
    //variable to background music on off
    boolean isMusicOn = false;
    SharedPreferences prefs;
    FireworkView fireworkView;

    //score display
    FrameLayout resultOverlay;
    TextView tvResultTitle, tvResultMoves, tvResultTime, tvResultScore;
    LinearLayout resultBox;
    Button btnOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //variable to save local device
        prefs = getSharedPreferences("settings", MODE_PRIVATE);

        statusText = findViewById(R.id.statusText);
        restartBtn = findViewById(R.id.restartBtn);
        boardView = findViewById(R.id.boardView);
        fireworkView = findViewById(R.id.fireworkView);
        tvTime = findViewById(R.id.tvTime);
        btnPause = findViewById(R.id.btnPause);
        tvMoves = findViewById(R.id.tvMoves);

        //score display
        resultBox = findViewById(R.id.resultBox);
        btnOk = findViewById(R.id.btnOk);
        resultOverlay = findViewById(R.id.resultOverlay);
        tvResultTitle = findViewById(R.id.tvResultTitle);
        tvResultMoves = findViewById(R.id.tvResultMoves);
        tvResultTime = findViewById(R.id.tvResultTime);
        tvResultScore = findViewById(R.id.tvResultScore);
        //Neon Glow
        tvResultTitle.setShadowLayer(20, 0, 0, Color.CYAN);



        if (fireworkView == null) {
            Log.d("FW", "FireworkView NULL");
        }

        //background music
        bgMusic = MediaPlayer.create(this, R.raw.bg_music);
        bgMusic.setLooping(true);
        //back ground music button listener
        findViewById(R.id.musicBtn).setOnClickListener(v -> toggleMusic());
        //initialize sound variables
        tapSound = MediaPlayer.create(this, R.raw.tap);
        winSound = MediaPlayer.create(this, R.raw.win);
        loseSound = MediaPlayer.create(this, R.raw.lose);
        drawSound = MediaPlayer.create(this, R.raw.draw);

        //undo
        switch (difficulty) {
            case 0: undoLimit = 5; break; // Easy
            case 1: undoLimit = 4; break; // Medium
            case 2: undoLimit = 3; break; // Hard
        }
        undoLeft = undoLimit;
        undoBtn = findViewById(R.id.undoBtn);
        updateUndoText();

        if (boardView == null) {
            Toast.makeText(this, "BoardView NULL!", Toast.LENGTH_LONG).show();
        }

        // ✅ यहीं लिखो
        Intent intent = getIntent();

        if (intent != null) {
            difficulty = intent.getIntExtra("difficulty", 0);
            mode = intent.getIntExtra("mode", 0);
        }

        ai = new AIPlayer(difficulty);

        boardView.setOnCellTouchListener((row, col) -> {

            if (gameOver) return;
            if (boardView.placeMove(row, col, currentPlayer)) {
                //history for undo
                moveHistory.push(new int[]{row, col});
                //moves
                moveCount++;
                updateMoveUI();
                //auto save after each move
//                saveGame();
                playSound(tapSound);
                if (checkWin(row, col)) {
                    if (boardView.checkWin(row, col)) {
                        gameOver = true;
                        isGameOver = true;
                        handler.removeCallbacks(timerRunnable); // stop UI
                        gameTimer.pause(); // freeze time
                        //score
                        long finalTime = gameTimer.getElapsedTime();
                        int baseScore = calculateEfficiencyScore(moveCount, finalTime);
                        int finalScore = applyDifficulty(baseScore, String.valueOf(difficulty));
                        // score add Bonus
                        finalScore = applyBonus(finalScore, moveCount, finalTime);
                        //score save Best
                        saveBestScore(finalScore);
                        //Score display in toast
                        showResultDialog("Player " + currentPlayer + " Wins!", finalScore, moveCount, finalTime);
//                        showCenterResult("Player " + currentPlayer + " Wins!", moveCount, finalTime, finalScore);

                        playSound(winSound);
//                        fireworkView.startFireworks();
                        fireworkView.startFireworksWithDelay();
                        //display winning time in status text
                        int sec = (int)(finalTime / 1000);
                        int min = sec / 60;
                        int hrs = sec / 3600;
                        sec = sec % 60;
                        String result = String.format("%02d:%02d:%02d", hrs, min, sec);
                        statusText.setText("Player " + currentPlayer + " Wins in " + result +" Score:" + finalScore);
                        restartBtn.setVisibility(View.VISIBLE);
                        return;
                    }
                    Toast.makeText(this, "Player " + currentPlayer + " wins!", Toast.LENGTH_SHORT).show();
//                    winningCells.clear();
//                    winningCells.add(new int[]{r, c});
                    return;
                }

                if (boardView.isBoardFull()) {
                    gameOver = true;
                    isGameOver = true;
                    handler.removeCallbacks(timerRunnable); // stop UI
                    gameTimer.pause(); // freeze time
                    statusText.setText("Draw!");
                    playSound(drawSound);

                    restartBtn.setVisibility(View.VISIBLE);
                    return;
                }

                currentPlayer = 3 - currentPlayer;

                // AI move
                if (mode == 1 && currentPlayer == 2) {
                    int[] move = ai.getMove(boardView.getBoard());
                    boardView.placeMove(move[0], move[1], 2);
                    //history for undo
                    moveHistory.push(new int[]{move[0], move[1]});

                    // 👉 AI move के बाद भी win check करो
                    if (boardView.checkWin(move[0], move[1])) {
                        gameOver = true;
                        isGameOver = true;
                        handler.removeCallbacks(timerRunnable); // stop UI
                        gameTimer.pause(); // freeze time

                        playSound(loseSound);
                        statusText.setText("AI Wins!");

                        restartBtn.setVisibility(View.VISIBLE);
                        return;
                    }

//                    currentPlayer = 1;
                    currentPlayer = 3 - currentPlayer;

                    statusText.setText("Player " + currentPlayer + " Turn");
                }
            }
        });

        //undo button listener
        undoBtn.setOnClickListener(v -> {

            if (undoLeft <= 0 || moveHistory.isEmpty() || gameOver) return;

            // 👉 AI mode में 2 moves हटाओ
            int removeCount = (mode == 1) ? 2 : 1;

            for (int i = 0; i < removeCount; i++) {
                if (!moveHistory.isEmpty()) {
                    int[] last = moveHistory.pop();
                    boardView.removeMove(last[0], last[1]);
                }
            }

            undoLeft--;
            updateUndoText();

            if (moveCount > 0) {
                moveCount--;
                updateMoveUI();
            }

            gameOver = false;
            statusText.setText("Player " + currentPlayer + " Turn");
        });

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
        //select saved theme
        themeSpinner.setSelection(currentThemeIndex);
        //theme listener
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
        //Restart button Listeners

        restartBtn.setOnClickListener(v -> resetGame());

        isGameOver = false;

        startTimerUI();

        //define timer
        tvTime = findViewById(R.id.tvTime);

        //Timer Runnable method old
        handler.post(new Runnable() {
            @Override
            public void run() {
                long millis = gameTimer.getElapsedTime();

                int totalSeconds = (int) (millis / 1000);

                int hours = totalSeconds / 3600;
                int minutes = (totalSeconds % 3600) / 60;
                int seconds = totalSeconds % 60;

                // 🔥 HH:MM:SS format
                tvTime.setText(String.format(" Time: %02d:%02d:%02d", hours, minutes, seconds));

                handler.postDelayed(this, 1000);
            }
        });

        //pause button listener
        btnPause.setOnClickListener(v -> {
            isGamePaused = true;
            gameTimer.pause();
            showPauseDialog();
        });

        gameTimer = new GameTimer();
        gameTimer.start();
    }

    //score display method
    private void showCenterResult(String playerName, int moves, long timeMillis, int score) {

        int totalSec = (int)(timeMillis / 1000);
        int hours = totalSec / 3600;
        int minutes = (totalSec % 3600) / 60;
        int seconds = totalSec % 60;

        String timeStr = String.format("%02d:%02d:%02d", hours, minutes, seconds);

        tvResultTitle.setText(playerName + " Wins");
        tvResultMoves.setText("Moves: " + moves);
        tvResultTime.setText("Time: " + timeStr);
        tvResultScore.setText("Score: " + score);

        resultOverlay.setVisibility(View.VISIBLE);

        // 🔥 Auto hide after 3 seconds
        new Handler().postDelayed(() -> {
            resultOverlay.setVisibility(View.GONE);
        }, 3000);


    }

    //score bonus appy method
    private int applyBonus(int score, int moves, long timeMillis) {
        int timeSec = (int)(timeMillis / 1000);
        //Fast bonus (less time)
        if (timeSec < 60) score += 50;

        //Smart play bonus (less move)
        if (moves < 20) score += 50;

        //Perfect game bonus (less time & bonus)
        if (moves < 15 && timeSec < 40) {
            score += 100;
        }
        return score;
    }

    //save best score
    private void saveBestScore(int finalScore) {

        SharedPreferences prefs = getSharedPreferences("GameData", MODE_PRIVATE);

        int best = prefs.getInt("bestScore", 0);

        if (finalScore > best) {
            prefs.edit().putInt("bestScore", finalScore).apply();
        }
    }

    //show score popup
    private void showResultDialog(String player, int moves, long time, long score ) {

        int totalSec = (int)(time / 1000);
        int hrs = totalSec / 3600;
        int min = (totalSec % 3600) / 60;
        int sec = totalSec % 60;

        String t = String.format("%02d:%02d:%02d", hrs, min, sec);

        ((TextView)findViewById(R.id.tvResultTitle)).setText(player + " Wins");
        ((TextView)findViewById(R.id.tvResultMoves)).setText("Moves: " + moves);
        ((TextView)findViewById(R.id.tvResultTime)).setText("Time: " + t);
        ((TextView)findViewById(R.id.tvResultScore)).setText("Score: " + score);

        resultOverlay.setVisibility(View.VISIBLE);

        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        resultBox.startAnimation(fadeIn);

        btnOk.setOnClickListener(v -> {

            Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);

            resultBox.startAnimation(fadeOut);

            resultBox.postDelayed(() -> {
                resultOverlay.setVisibility(View.GONE);
            }, 400);
        });
    }

    //moves update method
    private void updateMoveUI() {
        tvMoves.setText("Moves: " + moveCount + " ");
    }

    //Timer Runnable method
    private void startTimerUI() {

        timerRunnable = new Runnable() {
            @Override
            public void run() {

                if (!isGamePaused && !isGameOver) {

                    long millis = gameTimer.getElapsedTime();

                    int totalSeconds = (int) (millis / 1000);
                    int hours = totalSeconds / 3600;
                    int minutes = (totalSeconds % 3600) / 60;
                    int seconds = totalSeconds % 60;

                    tvTime.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
                }

                handler.postDelayed(this, 1000);
            }
        };

        handler.post(timerRunnable);
    }

    //stop timer method
    private void stopTimer() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    // 🔥 Timer stop जब app background में जाए
    @Override
    protected void onPause() {
        super.onPause();

        if (gameTimer != null && !isGameOver) {
            gameTimer.pause();
        }

        if (handler != null && timerRunnable != null) {
            handler.removeCallbacks(timerRunnable);
        }
    }

    // timer Resume when app in forground
    @Override
    protected void onResume() {
        super.onResume();

        if (gameTimer != null && !isGameOver) {
            gameTimer.resume();
            startTimerUI();   // UI भी restart करो
        }
    }

    //timer stop when game (activity) closed
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 🔥 Timer stop
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    //pause dialog box
    private void showPauseDialog() {

        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_pause);

        dialog.setCancelable(false);

        // cover 95% screen size
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(dialog.getWindow().getAttributes());
        params.width = (int)(getResources().getDisplayMetrics().widthPixels * 0.95);
        params.height = (int)(getResources().getDisplayMetrics().heightPixels * 0.95);

        dialog.getWindow().setAttributes(params);

        Button btnResume = dialog.findViewById(R.id.btnResume);

        btnResume.setOnClickListener(v -> {

            Log.d("DEBUG", "Resume clicked");  // 👈 logcat में देखो
            // 🔥 Resume logic
            isGamePaused = false;

            if (gameTimer != null) {
                gameTimer.resume();
            }

            startTimerUI();   // UI timer फिर start

            dialog.dismiss(); // popup बंद
        });

        //display popup
        dialog.show();
    }

    //save Multiple theme local device
    void saveTheme() {
        prefs.edit().putInt("theme", currentThemeIndex).apply();
    }

    //Multiple Theme apply
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

        // change Text colors
        statusText.setTextColor(t.textColor);
//        scoreText.setTextColor(t.textColor);
    }
    //undo method
    private void updateUndoText() {
        undoBtn.setText("Undo (" + undoLeft + ")");
    }

    //Efficiency Score (Moves + Time)
    private int calculateEfficiencyScore(int moves, long timeMillis) {

        int timeSec = (int) (timeMillis / 1000);

        if (moves == 0 || timeSec == 0) return 0;

        int score = 10000 / (moves * timeSec);

        return Math.max(score, 1); // minimum 1
    }

    private int applyDifficulty(int baseScore, String difficulty) {

        int multiplier = 1;

        switch (difficulty) {
            case "MEDIUM":
                multiplier = 2;
                break;
            case "HARD":
                multiplier = 3;
                break;
        }

        return baseScore * multiplier;
    }

    //play sound method
    void playSound(MediaPlayer mp) {
        if (mp != null) {
            mp.start();
        }
    }

    //background music on/off method
    void toggleMusic() {
        if (isMusicOn) {
            bgMusic.pause();
        } else {
            bgMusic.start();
        }
        isMusicOn = !isMusicOn;
    }

    private boolean checkWin(int r, int c) {
        return boardView.checkWin(r, c);
    }

    //Restart method
    void resetGame() {
        boardView.resetBoard();
        moveHistory.clear();

        //clear saved game on restart
        prefs.edit().clear().apply();
        fireworkView.stopFireworks();

        //undo
        moveCount = 0;
        updateMoveUI();

        undoLeft = undoLimit;
        updateUndoText();
        gameOver = false;
        currentPlayer = 1;

        //start timer
        handler.removeCallbacks(timerRunnable);

        gameTimer = new GameTimer();
        gameTimer.start();

        isGamePaused = false;
        isGameOver = false;

        startTimerUI();

        statusText.setText("Player 1 Turn");
        restartBtn.setVisibility(View.GONE);
        applyTheme();
    }

    //Save game method local
    private void saveGame() {

        SharedPreferences prefs = getSharedPreferences("game", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putInt("currentPlayer", currentPlayer);

        // board save करो
        int[][] board = boardView.getBoard();

        for (int r = 0; r < 15; r++) {
            for (int c = 0; c < 15; c++) {
                editor.putInt("cell_" + r + "_" + c, board[r][c]);
            }
        }

        editor.apply();
    }

    //Load local saved game method
    private void loadGame() {

        SharedPreferences prefs = getSharedPreferences("game", MODE_PRIVATE);

        currentPlayer = prefs.getInt("currentPlayer", 1);

        int[][] board = boardView.getBoard();

        for (int r = 0; r < 15; r++) {
            for (int c = 0; c < 15; c++) {
                board[r][c] = prefs.getInt("cell_" + r + "_" + c, 0);
            }
        }

        boardView.invalidate();
    }
}
