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
    Spinner spinner;
    String difficulty = "MEDIUM";

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

        //variable to save local device
        prefs = getSharedPreferences("settings", MODE_PRIVATE);
        // ye code firbase main connection or data create and save test karne ke liyte hai
        //DatabaseReference dbRef;
        //dbRef = FirebaseDatabase.getInstance().getReference("leaderboard");
        //dbRef = FirebaseDatabase
        //        .getInstance("https://fourinarow-31ccf-default-rtdb.asia-southeast1.firebasedatabase.app/")
        //        .getReference("leaderboard");
        //Toast.makeText(this, "Firebase Connected", Toast.LENGTH_SHORT).show();
        //testSave();

        //leaderboard pahale se dikhane ke liye
        //startActivity(new Intent(this, LeaderboardActivity.class));

        //Views/button sabhi ko ID se connect kare (Initialize)
        gridLayout = findViewById(R.id.grid);
        scoreText = findViewById(R.id.scoreText);
        boardContainer = findViewById(R.id.boardContainer);
        //winLineView   = findViewById(R.id.winLine);
        winLine = findViewById(R.id.winLine);
        statusText = findViewById(R.id.statusText);
        spinner = findViewById(R.id.difficultySpinner);
        Button restartBtn = findViewById(R.id.restartBtn);
        Button leaderboardBtn = findViewById(R.id.leaderboardBtn);

        //background music
        bgMusic = MediaPlayer.create(this, R.raw.bg_music);
        bgMusic.setLooping(true);
        //back ground music button listener
        findViewById(R.id.musicBtn).setOnClickListener(v -> toggleMusic());
        //sound variables
        tapSound = MediaPlayer.create(this, R.raw.tap);
        winSound = MediaPlayer.create(this, R.raw.win);
        loseSound = MediaPlayer.create(this, R.raw.lose);
        drawSound = MediaPlayer.create(this, R.raw.draw);
        dropSound = MediaPlayer.create(this, R.raw.drop);

        //drop down menu difficulty
        String[] levels = {"Level EASY", "Level MEDIUM", "Level HARD"};
        spinner.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, levels));
        //ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
        //      android.R.layout.simple_spinner_dropdown_item, levels);
        //spinner.setAdapter(adapter);

        //UI logic set kare (Grid, Spinner, etc.)
        gridLayout.setRowCount(ROWS);
        gridLayout.setColumnCount(COLS);
        //winLine = findViewById(R.id.winLine);
        boardContainer = findViewById(R.id.boardContainer);

        //leaderboard listener
        leaderboardBtn.setOnClickListener(v -> {
            showLeaderboardDialog();
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
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                difficulty = levels[position];
                //restart new game when Difficulty changed
                resetGame();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        //create game board
        createBoardUI();
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
        scoreText.setTextColor(t.textColor);
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

    //vibration effect method
    void vibrate() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            v.vibrate(150);
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

        board[row][col] = 1;
        updateUI(row, col, 1);
        playSound(tapSound);

        if (checkWin(1)) {
            playerScore++;
            updateScore();
            playSound(winSound);
            vibrate();
            //winLineView.post(() -> drawWinningLine());
            //drawWinningLine();
            highlightWinningCells();
            showNameDialog(playerScore);
            showWinner("You Win!");
            statusText.setText("You Win!");
            disableBoard();
            return;
        }

        //check for draw
        if (isBoardFull()) {
            showWinner("Draw 🤝");
            statusText.setText("Draw!");
            playSound(drawSound);
            disableBoard();
        }

        //AI Move delay
        gridLayout.postDelayed(() -> {
            int aiCol = getAIMove();
            int aiRow = getAvailableRow(aiCol);

            if (aiRow != -1) {
                board[aiRow][aiCol] = 2;
                updateUI(aiRow, aiCol, 2);

                if (checkWin(2)) {
                    aiScore++;
                    updateScore();
                    //winLineView.post(() -> drawWinningLine());
                    //drawWinningLine();
                    highlightWinningCells();
                    drawWinningLine();
                    playSound(loseSound);
                    showWinner("Computer Wins!");
                    statusText.setText("Computer Wins!");
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
                    vibrate();
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
        switch (difficulty) {
            case "EASY":
                return getRandomMove();
            case "MEDIUM":
                return getMediumMove();
            case "HARD":
                return getBestMove();
        }
        return 0;
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

    //HARD (Minimax simplified)
//    int getBestMove() {
//        int bestScore = -1000;
//        int move = 0;

//        for (int c = 0; c < COLS; c++) {
//            int r = getAvailableRow(c);
//            if (r != -1) {
//                board[r][c] = 2;
//                int score = minimax(4, false);
//                board[r][c] = 0;
//
//                if (score > bestScore) {
//                    bestScore = score;
//                    move = c;
//                }
//            }
//        }
//        return move;
//    }

    int getBestMove() {

        int bestScore = -1000;
        int move = 0;

        for (int c = 0; c < COLS; c++) {
            int r = getAvailableRow(c);

            if (r != -1) {
                board[r][c] = 2;

//                minimax(4, -1000, 1000, false);
                int score = minimax(5, -1000, 1000, false);

                board[r][c] = 0;

                if (score > bestScore) {
                    bestScore = score;
                    move = c;
                }
            }
        }

        return move;
    }

    int minimax(int depth, int alpha, int beta, boolean isMax) {

        if (checkWin(2)) return 100;
        if (checkWin(1)) return -100;

        if (depth == 0) return evaluateBoard();

        if (isMax) {
            int maxEval = -1000;

            for (int c = 0; c < COLS; c++) {
                int r = getAvailableRow(c);
                if (r != -1) {
                    board[r][c] = 2;

                    int eval = minimax(depth - 1, alpha, beta, false);

                    board[r][c] = 0;

                    maxEval = Math.max(maxEval, eval);
                    alpha = Math.max(alpha, eval);

                    if (beta <= alpha) break; // pruning
                }
            }
            return maxEval;

        } else {
            int minEval = 1000;

            for (int c = 0; c < COLS; c++) {
                int r = getAvailableRow(c);
                if (r != -1) {
                    board[r][c] = 1;

                    int eval = minimax(depth - 1, alpha, beta, true);

                    board[r][c] = 0;

                    minEval = Math.min(minEval, eval);
                    beta = Math.min(beta, eval);

                    if (beta <= alpha) break; // pruning
                }
            }
            return minEval;
        }
    }

    int evaluateBoard() {
        int score = 0;

        // center column preference
        for (int r = 0; r < ROWS; r++) {
            if (board[r][COLS/2] == 2) score += 3;
        }

        return score;
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

        //enable Grid again
        gridLayout.setEnabled(true);

        //enable each cell
        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            gridLayout.getChildAt(i).setEnabled(true);
        }
        statusText.setText("Your Turn");
        applyTheme();
    }

    //disable game board method
    void disableBoard() {
        gridLayout.setEnabled(false);

        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            gridLayout.getChildAt(i).setEnabled(false);
        }
    }

    //update Scoreboard
    void updateScore() {
        scoreText.setText("Player: " + playerScore + "  AI: " + aiScore);
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
//    leaderboard test save
//    void testSave() {
//        String id = dbRef.push().getKey();
//        Map<String, Object> data = new HashMap<>();
//        data.put("name", "TestUser");
//        data.put("score", new Random().nextInt(100));
//        Toast.makeText(this, "testSave!", Toast.LENGTH_SHORT).show();
//        dbRef.child(id).setValue(data)
//                .addOnSuccessListener(aVoid -> {
//                    Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
//                })
//                .addOnFailureListener(e -> {
//                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
//                });
//        dbRef.child(id).setValue(data)
//                .addOnSuccessListener(aVoid ->
//                        Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show()
//                )
//                .addOnFailureListener(e ->
//                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show()
//                );
//    }

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

                    saveScore(name, score); // Firebase save
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

    //save score to leaderboard
    void saveScore(String name, int score) {

        DatabaseReference dbRef = FirebaseDatabase
                .getInstance("https://fourinarow-31ccf-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("leaderboard");

        String id = dbRef.push().getKey();

        if (id == null) return;

        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("score", score);

        dbRef.child(id).setValue(data)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Score Saved!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    //show leaderboard in pupup box
    void showLeaderboardDialog() {

        View view = getLayoutInflater().inflate(R.layout.dialog_leaderboard, null);

        RecyclerView recycler = view.findViewById(R.id.dialogRecycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        List<Player> list = new ArrayList<>();
        LeaderboardAdapter adapter = new LeaderboardAdapter(list);
        recycler.setAdapter(adapter);

        DatabaseReference dbRef = FirebaseDatabase
                .getInstance("https://fourinarow-31ccf-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("leaderboard");

        dbRef.orderByChild("score").limitToLast(20)
                .addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        list.clear();

                        for (DataSnapshot data : snapshot.getChildren()) {
                            Player p = data.getValue(Player.class);
                            if (p != null) list.add(p);
                        }

                        Collections.reverse(list);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {}
                });

        //big alert dialog box
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();

        dialog.show();

        // Transparent background
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        //increase popup window height
        dialog.getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                1200 // height px
        );
    }
}