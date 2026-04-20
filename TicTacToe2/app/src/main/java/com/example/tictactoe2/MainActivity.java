package com.example.tictactoe2;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
        import androidx.appcompat.app.AppCompatActivity;
import java.util.*;

public class MainActivity extends AppCompatActivity {

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
    boolean isDark = false;

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

        scoreText = findViewById(R.id.scoreText);
        findViewById(R.id.themeBtn).setOnClickListener(v -> toggleTheme());
        winLine = findViewById(R.id.winLine);
        statusText = findViewById(R.id.statusText);
        levelSpinner = findViewById(R.id.levelSpinner);

        String[] levels = {"Easy", "Medium", "Hard"};
        levelSpinner.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, levels));

        for (int i = 0; i < 9; i++) {
            int id = getResources().getIdentifier("b" + i, "id", getPackageName());
            buttons[i] = findViewById(id);

            int index = i;
            buttons[i].setOnClickListener(v -> playerMove(index));
        }

        findViewById(R.id.restartBtn).setOnClickListener(v -> resetGame());
    }

    //theme Change
    void toggleTheme() {
        if (isDark) {
            getWindow().getDecorView().setBackgroundColor(Color.WHITE);
        } else {
            getWindow().getDecorView().setBackgroundColor(Color.BLACK);
        }
        isDark = !isDark;
    }

    void drawWinLine(int[] combo) {

        winLine.setVisibility(View.VISIBLE);

        winLine.setScaleX(0f);

        winLine.animate()
                .scaleX(1f)
                .setDuration(500)
                .start();
    }

    void playerMove(int i) {
        if (board[i] != ' ') return;

        board[i] = player;
        buttons[i].setText("X");

        if (checkWin(player)) {
            statusText.setText("You Win!");
            playerScore++;
            updateScore();
            highlightWin(player);
            disableAll();
            return;
        }

        if (isDraw()) {
            statusText.setText("Draw!");
            return;
        }

        aiMove();
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
            disableAll();
            return;
        }

        if (isDraw()) {
            statusText.setText("Draw!");
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
    void highlightWin(char p) {
        for (int[] combo : winCombos) {
            if (board[combo[0]] == p &&
                    board[combo[1]] == p &&
                    board[combo[2]] == p) {

                buttons[combo[0]].setBackgroundColor(Color.GREEN);
                buttons[combo[1]].setBackgroundColor(Color.GREEN);
                buttons[combo[2]].setBackgroundColor(Color.GREEN);
                drawWinLine(combo);
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
    }
}