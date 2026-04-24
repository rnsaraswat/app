package com.example.fourinarow;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    int ROWS = 6, COLS = 7;
    int[][] board = new int[ROWS][COLS]; // 0 empty, 1 player, 2 AI
    int playerScore = 0, aiScore = 0;
    TextView scoreText;

    GridLayout gridLayout;
    ImageView[][] cells = new ImageView[ROWS][COLS];
    Spinner spinner;

    String difficulty = "MEDIUM"; // EASY / MEDIUM / HARD

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

            // 1. Sabse pehle layout set karein
            setContentView(R.layout.activity_main);

            // 2. Views ko ID se connect karein (Initialize)
            gridLayout = findViewById(R.id.grid);
            scoreText = findViewById(R.id.scoreText);
            spinner = findViewById(R.id.difficultySpinner);
            Button restartBtn = findViewById(R.id.restartBtn);

            // 3. UI logic set karein (Grid, Spinner, etc.)
            gridLayout.setRowCount(ROWS);
            gridLayout.setColumnCount(COLS);

            String[] levels = {"EASY", "MEDIUM", "HARD"};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_dropdown_item, levels);
            spinner.setAdapter(adapter);

            // 4. Listeners setup karein
            restartBtn.setOnClickListener(v -> resetGame());

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    difficulty = levels[position];
                    resetGame(); // Difficulty badalne par game reset karna sahi rehta hai
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });

            // 5. Last mein game board create karein
            createBoardUI();

    }

    // 🎮 UI Grid बनाना
    void createBoardUI() {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {

                ImageView cell = new ImageView(this);
                cell.setBackgroundResource(android.R.color.darker_gray);
                cell.setBackgroundResource(R.drawable.cell_bg);

                final int col = c;

                cell.setOnClickListener(v -> playerMove(col));

                cells[r][c] = cell;

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = 130;
                params.height = 130;
                params.setMargins(5, 5, 5, 5);

                gridLayout.addView(cell, params);
            }
        }
    }

    // 👤 Player Move
    void playerMove(int col) {

        int row = getAvailableRow(col);

        if (row == -1) return;

        board[row][col] = 1;
        updateUI(row, col, 1);

//        if (checkWin(1)) return;
        if (checkWin(1)) {
            playerScore++;
            updateScore();
            showWinner("You Win!");
            return;
        }

        // 🤖 AI Move delay
        gridLayout.postDelayed(() -> {
            int aiCol = getAIMove();
            int aiRow = getAvailableRow(aiCol);

            if (aiRow != -1) {
                board[aiRow][aiCol] = 2;
                updateUI(aiRow, aiCol, 2);

//                checkWin(2);
                if (checkWin(2)) {
                    aiScore++;
                    updateScore();
                    showWinner("AI Wins!");
                }
            }
        }, 500);
    }



    // 🎨 UI Update
//    void updateUI(int row, int col, int player) {
//        if (player == 1)
//            cells[row][col].setBackgroundColor(0xFFFF0000); // red
//        else
//            cells[row][col].setBackgroundColor(0xFFFFFF00); // yellow
//    }

    // Drop Animation
    void updateUI(int row, int col, int player) {

        ImageView cell = cells[row][col];

        if (player == 1)
            cell.setBackgroundResource(R.drawable.red_disc);
        else
            cell.setBackgroundResource(R.drawable.yellow_disc);

        // Animation (top से गिरना)
        cell.setTranslationY(-500);
        cell.animate()
                .translationY(0)
                .setDuration(300)
                .start();
    }

    // 🔽 Get Available Row
    int getAvailableRow(int col) {
        for (int r = ROWS - 1; r >= 0; r--) {
            if (board[r][col] == 0)
                return r;
        }
        return -1;
    }

    // 🏆 Win Check
    boolean checkWin(int player) {

        // Horizontal
        for (int r = 0; r < ROWS; r++)
            for (int c = 0; c < COLS - 3; c++)
                if (board[r][c] == player &&
                        board[r][c+1] == player &&
                        board[r][c+2] == player &&
                        board[r][c+3] == player)
                    return true;

        // Vertical
        for (int c = 0; c < COLS; c++)
            for (int r = 0; r < ROWS - 3; r++)
                if (board[r][c] == player &&
                        board[r+1][c] == player &&
                        board[r+2][c] == player &&
                        board[r+3][c] == player)
                    return true;

        // Diagonal
        for (int r = 0; r < ROWS - 3; r++)
            for (int c = 0; c < COLS - 3; c++)
                if (board[r][c] == player &&
                        board[r+1][c+1] == player &&
                        board[r+2][c+2] == player &&
                        board[r+3][c+3] == player)
                    return true;

        return false;
    }

    //Win Popup
    void showWinner(String msg) {
        new AlertDialog.Builder(this)
                .setTitle("Game Over")
                .setMessage(msg)
                .setPositiveButton("OK", null)
                .show();
    }

    // 🤖 AI Move Selector
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

    // 🎲 EASY
    int getRandomMove() {
        Random rand = new Random();
        int col;
        do {
            col = rand.nextInt(COLS);
        } while (getAvailableRow(col) == -1);
        return col;
    }

    // 🧠 MEDIUM
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

    // 🔥 HARD (Minimax simplified)
    int getBestMove() {
        int bestScore = -1000;
        int move = 0;

        for (int c = 0; c < COLS; c++) {
            int r = getAvailableRow(c);
            if (r != -1) {
                board[r][c] = 2;
                int score = minimax(4, false);
                board[r][c] = 0;

                if (score > bestScore) {
                    bestScore = score;
                    move = c;
                }
            }
        }
        return move;
    }

    int minimax(int depth, boolean isMax) {

        if (checkWin(2)) return 10;
        if (checkWin(1)) return -10;
        if (depth == 0) return 0;

        if (isMax) {
            int best = -1000;

            for (int c = 0; c < COLS; c++) {
                int r = getAvailableRow(c);
                if (r != -1) {
                    board[r][c] = 2;
                    best = Math.max(best, minimax(depth - 1, false));
                    board[r][c] = 0;
                }
            }
            return best;
        } else {
            int best = 1000;

            for (int c = 0; c < COLS; c++) {
                int r = getAvailableRow(c);
                if (r != -1) {
                    board[r][c] = 1;
                    best = Math.min(best, minimax(depth - 1, true));
                    board[r][c] = 0;
                }
            }
            return best;
        }
    }

    //Restart method
    void resetGame() {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                board[r][c] = 0;
                cells[r][c].setBackgroundColor(0x00000000);
            }
        }
    }

    //update Scoreboard
    void updateScore() {
        scoreText.setText("Player: " + playerScore + "  AI: " + aiScore);
    }

}