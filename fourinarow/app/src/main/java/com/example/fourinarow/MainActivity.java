package com.example.fourinarow;

import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Stack;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

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
    String[] themeNames = {"Theme Light", "Theme Dark", "Theme Blue", "Theme Orange"};
    //set Current theme index
    int currentThemeIndex = 0;

    //variables
    //theme local saved variable
    SharedPreferences prefs;

    //define board ro and column
    int ROWS = 6, COLS = 7;
    //game board array 0 empty, 1 player, 2 AI
    int[][] board = new int[ROWS][COLS];
    //score variable
    int playerScore = 0, aiScore = 0;
    FrameLayout boardContainer;
    TextView scoreText;
    List<int[]> winningCells = new ArrayList<>();
//    WinLineView winLineView;
    WinLineView winLine;
    //status text view
    TextView statusText;

    GridLayout gridLayout;
    ImageView[][] cells = new ImageView[ROWS][COLS];
    //deop down for difficulty level EASY / MEDIUM / HARD
    Spinner modeSpinner, difficultySpinner;
    String difficulty = "MEDIUM";

    //pvp - pvc
    boolean isPvP = false;
    boolean playerTurn = true;

    //undo
    Button undoBtn;
    Stack<int[][]> boardHistory = new Stack<>();
    Stack<Boolean> playerTurnHistory = new Stack<>();
    int undoLimit = 3;

    DatabaseReference dbRef;
    //sound variables
    MediaPlayer tapSound, winSound, loseSound, drawSound, dropSound;
    MediaPlayer bgMusic;
    //variable to background music on off
    boolean isMusicOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Activit layout Sabse pehle
        setContentView(R.layout.activity_main);

        //Views/button sabhi ko ID se connect kare (Initialize)
        gridLayout = findViewById(R.id.grid);
        scoreText = findViewById(R.id.scoreText);
        boardContainer = findViewById(R.id.boardContainer);
        undoBtn = findViewById(R.id.undoBtn);
        //winLineView   = findViewById(R.id.winLine);
        winLine = findViewById(R.id.winLine);
        statusText = findViewById(R.id.statusText);
        difficultySpinner = findViewById(R.id.difficultySpinner);
        modeSpinner = findViewById(R.id.modeSpinner);
        Button restartBtn = findViewById(R.id.restartBtn);
//        Button leaderboardBtn = findViewById(R.id.leaderboardBtn);

        //background music
        bgMusic = MediaPlayer.create(this, R.raw.bg_music);
        bgMusic.setLooping(true);
        //back ground music button listener
//        findViewById(R.id.musicBtn).setOnClickListener(v -> toggleMusic());
        //sound variables
        tapSound = MediaPlayer.create(this, R.raw.tap);
        winSound = MediaPlayer.create(this, R.raw.win);
        loseSound = MediaPlayer.create(this, R.raw.lose);
        drawSound = MediaPlayer.create(this, R.raw.draw);
        dropSound = MediaPlayer.create(this, R.raw.drop);

        //drop down menu difficulty
        String[] levels = {"Level EASY", "Level MEDIUM", "Level HARD"};
        difficultySpinner.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, levels));
        //ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
        //      android.R.layout.simple_spinner_dropdown_item, levels);
        //spinner.setAdapter(adapter);
        ArrayAdapter<CharSequence> modeAdapter =
                ArrayAdapter.createFromResource(
                        this,
                        R.array.game_modes,
                        android.R.layout.simple_spinner_item);

        modeAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);

        modeSpinner.setAdapter(modeAdapter);

        //UI logic set kare (Grid, Spinner, etc.)
        gridLayout.setRowCount(ROWS);
        gridLayout.setColumnCount(COLS);
        //winLine = findViewById(R.id.winLine);
        boardContainer = findViewById(R.id.boardContainer);

        //change undolimit
//        String[] difficulty = {difficultySpinner.getSelectedItem().toString()};

        if (levels.equals("Easy")) {
            undoLimit = 5;
        } else if (levels.equals("Medium")) {
            undoLimit = 4;
        } else if (levels.equals("Hard")) {
            undoLimit = 3;
        }

        updateUndoButton();

        //undo
        undoBtn.setOnClickListener(v -> undoMove());

        //Restart button Listeners
        restartBtn.setOnClickListener(v -> resetGame());
//        difficulties = {"Easy", "Medium", "Hard"};

        difficultySpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> parent,
                                               View view,
                                               int position,
                                               long id) {

                        difficulty = levels[position];

                        resetGame();

                        if ("Easy".equals(difficulty)) {
                            undoLimit = 5;
                        } else if ("Medium".equals(difficulty)) {
                            undoLimit = 4;
                        } else if ("Hard".equals(difficulty)) {
                            undoLimit = 3;
                        }

//                        undoLeft = undoLimit;
                        updateUndoButton();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

        modeSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id) {

                        isPvP = position == 1;
                        resetGame();
                        updateScore();

                        if (isPvP) {
                            statusText.setText("Player 1 Turn");
                        } else {
                            statusText.setText("Your Turn");
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

        //create game board
        createBoardUI();
    }

    void saveBoardState() {

        int[][] copy = new int[ROWS][COLS];

        for (int r = 0; r < ROWS; r++) {

            for (int c = 0; c < COLS; c++) {

                copy[r][c] = board[r][c];
            }
        }

        boardHistory.push(copy);

        // 🔥 SAVE TURN ALSO
        turnHistory.push(playerTurn);
    }

    void undoMove() {

        if (undoLimit <= 0) {

            Toast.makeText(this,
                    "No Undo Left",
                    Toast.LENGTH_SHORT).show();

            return;
        }

        if (boardHistory.empty()) {

            Toast.makeText(this,
                    "Nothing to Undo",
                    Toast.LENGTH_SHORT).show();

            return;
        }

        // 🔥 Restore previous board
        int[][] prevBoard = boardHistory.pop();

        for (int r = 0; r < ROWS; r++) {

            for (int c = 0; c < COLS; c++) {

                board[r][c] = prevBoard[r][c];
            }
        }

        // 🔥 FULL UI REFRESH
        refreshBoardUI();

        undoLimit--;

        updateUndoButton();

        enableBoard();
    }

    void updateUndoButton() {
        undoBtn.setText("Undo (" + undoLimit + ")");
    }

    void refreshBoardUI() {

        for (int r = 0; r < ROWS; r++) {

            for (int c = 0; c < COLS; c++) {

                if (board[r][c] == 1) {

                    cells[r][c].setImageResource(
                            R.drawable.red_disc);

                } else if (board[r][c] == 2) {

                    cells[r][c].setImageResource(
                            R.drawable.yellow_disc);

                } else {

                    cells[r][c].setImageDrawable(null);
                }
            }
        }
    }

    //play sound method
    void playSound(MediaPlayer mp) {
        if (mp != null) {
            mp.start();
        }
    }

    //create Gameboard Grid
    void createBoardUI() {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {

                FrameLayout cell = new FrameLayout(this);

                ImageView bg = new ImageView(this);
                bg.setBackgroundResource(R.drawable.board_cell);

                ImageView disc = new ImageView(this);

                cell.addView(bg);
                cell.addView(disc);

                final int col = c;
                cell.setOnClickListener(v -> playerMove(col));

                cells[r][c] = disc;

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                int size = getResources().getDisplayMetrics().widthPixels / 8;
                params.width = size;
                params.height = size;
                params.setMargins(5, 5, 5, 5);

                gridLayout.addView(cell, params);
            }
        }
    }

    //Player Move method
    void playerMove(int col) {

        int row = getAvailableRow(col);

        if (row == -1) return;

        // 🎯 PvP Mode
        if (isPvP) {

            int disc = playerTurn ? 1 : 2;

            saveBoardState();
            board[row][col] = disc;

            updateUI(row, col, disc);

            playSound(tapSound);

            // WIN CHECK
            if (checkWin(disc)) {

                highlightWinningCells();

                drawWinningLine();

                showWinner(
                        playerTurn
                                ? "Player 1 Wins!"
                                : "Player 2 Wins!");

                disableBoard();

                return;
            }

            // DRAW
            if (isBoardFull()) {

                showWinner("Draw 🤝");

                statusText.setText("Draw!");

                disableBoard();

                return;
            }

            // TURN CHANGE
            playerTurn = !playerTurn;

            statusText.setText(
                    playerTurn
                            ? "Player 1 Turn"
                            : "Player 2 Turn");

            return;
        }

        // 🎯 PLAYER vs AI MODE
        saveBoardState();
        board[row][col] = 1;

        updateUI(row, col, 1);

        playSound(tapSound);

        if (checkWin(1)) {

            playerScore++;

            updateScore();

            playSound(winSound);

            highlightWinningCells();

            showNameDialog(playerScore);

            showWinner("You Win!");

            statusText.setText("You Win!");

            disableBoard();

            return;
        }

        // DRAW
        if (isBoardFull()) {

            showWinner("Draw 🤝");

            statusText.setText("Draw!");

            playSound(drawSound);

            disableBoard();

            return;
        }

        // 🎯 AI MOVE
        gridLayout.postDelayed(() -> {

            int aiCol = getAIMove();

            int aiRow = getAvailableRow(aiCol);

            if (aiRow != -1) {

                board[aiRow][aiCol] = 2;

                updateUI(aiRow, aiCol, 2);

                if (checkWin(2)) {

                    aiScore++;

                    updateScore();

                    highlightWinningCells();

                    drawWinningLine();

                    playSound(loseSound);

                    showWinner("Computer Wins!");

                    statusText.setText("Computer Wins!");

                    disableBoard();

                    return;
                }

                // DRAW AFTER AI
                if (isBoardFull()) {

                    showWinner("Draw 🤝");

                    statusText.setText("Draw!");

                    disableBoard();
                }
            }

        }, 500);
    }

    // update UI and Drop Animation
    void updateUI(int row, int col, int player) {

        ImageView cell = cells[row][col];
//        ImageView cell = animateDrop(cells[row][col], row, col, player);

        //UI Update
        if (player == 1)
            cell.setBackgroundResource(R.drawable.red_disc);
        else
            cell.setBackgroundResource(R.drawable.yellow_disc);

        // Animation (drop from top)
        cell.setTranslationY(-800);
        cell.animate()
                .translationY(0)
                .setDuration(400)
                .setInterpolator(new android.view.animation.BounceInterpolator())
                .start();
    }

    //disc drop animation
    ImageView animateDrop(ImageView disc, int row, int col, int player) {

        // disc image set करो
        if (player == 1)
            disc.setImageResource(R.drawable.red_disc);
        else
            disc.setImageResource(R.drawable.yellow_disc);

        // top of screen in start
        disc.setTranslationY(-1000f);

        // drop animate
        disc.animate()
                .translationY(0f)
                .setDuration(300)
                .setInterpolator(new AccelerateInterpolator())
                .withEndAction(() -> {
                    playSound(dropSound);
//                    vibrate();
                })
                .start();
        return disc;
    }

    //Get Available Row
    int getAvailableRow(int col) {
        for (int r = ROWS - 1; r >= 0; r--) {
            if (board[r][col] == 0)
                return r;
        }
        return -1;
    }

    //check Win
    boolean checkWin(int player) {

        winningCells.clear();

        // Horizontal
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS - 3; c++) {
                if (board[r][c] == player &&
                        board[r][c+1] == player &&
                        board[r][c+2] == player &&
                        board[r][c+3] == player) {

                    winningCells.add(new int[]{r,c});
                    winningCells.add(new int[]{r,c+1});
                    winningCells.add(new int[]{r,c+2});
                    winningCells.add(new int[]{r,c+3});
                    return true;
                }
            }
        }

        // Vertical
        for (int c = 0; c < COLS; c++) {
            for (int r = 0; r < ROWS - 3; r++) {
                if (board[r][c] == player &&
                        board[r+1][c] == player &&
                        board[r+2][c] == player &&
                        board[r+3][c] == player) {

                    winningCells.add(new int[]{r,c});
                    winningCells.add(new int[]{r+1,c});
                    winningCells.add(new int[]{r+2,c});
                    winningCells.add(new int[]{r+3,c});
                    return true;
                }
            }
        }

        // Diagonal ➘ top down right
        for (int r = 0; r < ROWS - 3; r++) {
            for (int c = 0; c < COLS - 3; c++) {
                if (board[r][c] == player &&
                        board[r+1][c+1] == player &&
                        board[r+2][c+2] == player &&
                        board[r+3][c+3] == player) {

                    winningCells.add(new int[]{r,c});
                    winningCells.add(new int[]{r+1,c+1});
                    winningCells.add(new int[]{r+2,c+2});
                    winningCells.add(new int[]{r+3,c+3});
                    return true;
                }
            }
        }

        // Diagonal ➙
        for (int r = 3; r < ROWS; r++) {
            for (int c = 0; c < COLS - 3; c++) {
                if (board[r][c] == player &&
                        board[r-1][c+1] == player &&
                        board[r-2][c+2] == player &&
                        board[r-3][c+3] == player) {

                    winningCells.add(new int[]{r,c});
                    winningCells.add(new int[]{r-1,c+1});
                    winningCells.add(new int[]{r-2,c+2});
                    winningCells.add(new int[]{r-3,c+3});
                    return true;
                }
            }
        }
        return false;
    }

    //highligh winning cell
    void highlightWinningCells() {

        for (int[] pos : winningCells) {

            int r = pos[0];
            int c = pos[1];

            ImageView disc = cells[r][c];

            //Scale + Blink animation
            disc.animate()
                    .scaleX(1.3f)
                    .scaleY(1.3f)
                    .setDuration(2000)
                    .withEndAction(() -> {
                        disc.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(2000)
                                .start();
                    })
                    .start();

            //Glow effect
            disc.setAlpha(0.7f);
            disc.animate().alpha(1f).setDuration(3000).start();
        }
    }

    //check draw game method
    boolean isBoardFull() {
        for (int c = 0; c < COLS; c++) {
            if (board[0][c] == 0) return false; // top row खाली है
        }
        return true;
    }

    //Win Popup box
    void showWinner(String msg) {
        new AlertDialog.Builder(this)
                .setTitle("Game Over")
                .setMessage(msg)
                .setPositiveButton("OK", null)
                .show();
    }

    //AI Move
    int getAIMove() {

        String difficulty =
                difficultySpinner.getSelectedItem().toString();

        // 🎯 EASY = RANDOM
        if (difficulty.equals("Easy")) {

            ArrayList<Integer> moves = new ArrayList<>();

            for (int c = 0; c < COLS; c++) {

                if (getAvailableRow(c) != -1) {
                    moves.add(c);
                }
            }

            return moves.get(
                    new Random().nextInt(moves.size()));
        }

        // 🎯 MEDIUM
        // Try win
        // Try block
        // else random

        if (difficulty.equals("Medium")) {

            // WIN MOVE
            for (int c = 0; c < COLS; c++) {

                int r = getAvailableRow(c);

                if (r != -1) {

                    board[r][c] = 2;

                    if (checkWin(2)) {
                        board[r][c] = 0;
                        return c;
                    }

                    board[r][c] = 0;
                }
            }

            // BLOCK PLAYER
            for (int c = 0; c < COLS; c++) {

                int r = getAvailableRow(c);

                if (r != -1) {

                    board[r][c] = 1;

                    if (checkWin(1)) {
                        board[r][c] = 0;
                        return c;
                    }

                    board[r][c] = 0;
                }
            }

            // RANDOM
            ArrayList<Integer> moves = new ArrayList<>();

            for (int c = 0; c < COLS; c++) {

                if (getAvailableRow(c) != -1) {
                    moves.add(c);
                }
            }

            return moves.get(
                    new Random().nextInt(moves.size()));
        }

        // 🎯 HARD = UNBEATABLE

        return getBestMove();
    }

    //EASY
    int getRandomMove() {
        Random rand = new Random();
        int col;
        do {
            col = rand.nextInt(COLS);
        } while (getAvailableRow(col) == -1);
        return col;
    }

    //MEDIUM
    int getMediumMove() {

        // AI win
        for (int c = 0; c < COLS; c++) {
            int r = getAvailableRow(c);
            if (r != -1) {
                board[r][c] = 2;
                if (checkWin(2)) {
                    board[r][c] = 0;
                    return c;
                }
                board[r][c] = 0;
            }
        }

        // Block player
        for (int c = 0; c < COLS; c++) {
            int r = getAvailableRow(c);
            if (r != -1) {
                board[r][c] = 1;
                if (checkWin(1)) {
                    board[r][c] = 0;
                    return c;
                }
                board[r][c] = 0;
            }
        }

        return getRandomMove();
    }

    //HARD AI
    int getBestMove() {

        int bestScore = Integer.MIN_VALUE;

        int bestCol = 0;

        for (int c = 0; c < COLS; c++) {

            int r = getAvailableRow(c);

            if (r != -1) {

                board[r][c] = 2;

                int score = minimax(4, false);

                board[r][c] = 0;

                if (score > bestScore) {

                    bestScore = score;

                    bestCol = c;
                }
            }
        }

        return bestCol;
    }

    int minimax(int depth, boolean maximizing) {

        if (checkWin(2)) return 100;

        if (checkWin(1)) return -100;

        if (depth == 0 || isBoardFull()) return 0;

        if (maximizing) {

            int best = Integer.MIN_VALUE;

            for (int c = 0; c < COLS; c++) {

                int r = getAvailableRow(c);

                if (r != -1) {

                    board[r][c] = 2;

                    best = Math.max(
                            best,
                            minimax(depth - 1, false));

                    board[r][c] = 0;
                }
            }

            return best;

        } else {

            int best = Integer.MAX_VALUE;

            for (int c = 0; c < COLS; c++) {

                int r = getAvailableRow(c);

                if (r != -1) {

                    board[r][c] = 1;

                    best = Math.min(
                            best,
                            minimax(depth - 1, true));

                    board[r][c] = 0;
                }
            }

            return best;
        }
    }

    //Restart method
    void resetGame() {
        // board reset
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                board[r][c] = 0;
                cells[r][c].setBackground(null); // disc हटाओ
            }
        }
        boardHistory.clear();
        playerTurnHistory.clear();
        playerTurn = true;
        //enable Grid again
        gridLayout.setEnabled(true);
        updateScore();

        //enable each cell
        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            gridLayout.getChildAt(i).setEnabled(true);
        }
        statusText.setText("Your Turn");
//        applyTheme();
    }

    void disableBoard() {

        for (int r = 0; r < ROWS; r++) {

            for (int c = 0; c < COLS; c++) {

                cells[r][c].setEnabled(false);
            }
        }
    }

    void enableBoard() {

        for (int r = 0; r < ROWS; r++) {

            for (int c = 0; c < COLS; c++) {

                cells[r][c].setEnabled(true);
            }
        }
    }

    //update Scoreboard
    void updateScore() {

        if (isPvP) {

            scoreText.setText(
                    "Player 1: " + playerScore +
                            "   Player 2: " + aiScore);

        } else {

            scoreText.setText(
                    "Player: " + playerScore +
                            "   AI: " + aiScore);
        }
    }

    //get cell center for draw winline
    float[] getCellCenter(int row, int col) {
        View cellView = (View) cells[row][col];

        // Cell position relative to parent (FrameLayout)
        float cx = cellView.getX() + cellView.getWidth() / 2f;
        float cy = cellView.getY() + cellView.getHeight() / 2f;
        return new float[]{cx, cy};
    }
    //draw wining line
    void drawWinningLine() {

        if (winningCells.size() < 4) return;
        int[] first = winningCells.get(0);
        int[] last = winningCells.get(3);
        float[] start = getCellCenter(first[0], first[1]);
        float[] end = getCellCenter(last[0], last[1]);
        winLine.setLine(start[0], start[1], end[0], end[1]);
        // 🎬 animation
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(400);
        animator.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            winLine.setProgress(value);
        });
        animator.start();
    }

    //name dialog box display method
    void showNameDialog(int score) {

        View view = getLayoutInflater().inflate(R.layout.dialog_name, null);
        EditText input = view.findViewById(R.id.nameInput);
        // show saved name
        input.setText(getPlayerName());

        new AlertDialog.Builder(this)
                .setTitle("Enter Your Name")
                .setView(view)
                .setPositiveButton("Save", (dialog, which) -> {

                    String name = input.getText().toString().trim();
                    if (!name.isEmpty()) {
                        savePlayerName(name);
                        Toast.makeText(this, "Saved: " + name, Toast.LENGTH_SHORT).show();
                    } else {
                        name = "Player";
                        Toast.makeText(this, "Enter valid name", Toast.LENGTH_SHORT).show();
                    }

//                    saveScore("Player", playerScore);
//                    showNameDialog(playerScore);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    //Save name to local device
    void savePlayerName(String name) {
        getSharedPreferences("game_prefs", MODE_PRIVATE)
                .edit()
                .putString("player_name", name)
                .apply();
    }

    //Load name from local device
    String getPlayerName() {
        return getSharedPreferences("game_prefs", MODE_PRIVATE)
                .getString("player_name", "");
    }
}