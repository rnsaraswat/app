package com.example.fiveinarow;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Stack;

public class GameActivity extends AppCompatActivity {

    BoardView boardView;
    int mode, difficulty;
    boolean gameOver = false;
    TextView statusText;
    Button restartBtn;
    //undo variales
    Button undoBtn;
    Stack<int[]> moveHistory = new Stack<>();
    int undoLimit;
    int undoLeft;

    int currentPlayer = 1; // 1 = Player1, 2 = Player2/AI
    AIPlayer ai;

    //sound variables
    MediaPlayer tapSound, winSound, loseSound, drawSound;
    MediaPlayer bgMusic;
    //variable to background music on off
    boolean isMusicOn = false;
    SharedPreferences prefs;
    FireworkView fireworkView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);


        statusText = findViewById(R.id.statusText);
        restartBtn = findViewById(R.id.restartBtn);
        boardView = findViewById(R.id.boardView);
        fireworkView = findViewById(R.id.fireworkView);

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

//        mode = getIntent().getIntExtra("mode", 0);
//        difficulty = getIntent().getIntExtra("difficulty", 0);
        if (boardView == null) {
            Toast.makeText(this, "BoardView NULL!", Toast.LENGTH_LONG).show();
        }

//        mode = getIntent().getIntExtra("mode", 0);
//        difficulty = getIntent().getIntExtra("difficulty", 0);

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
                //auto save after each move
//                saveGame();
                playSound(tapSound);
                if (checkWin(row, col)) {
                    if (boardView.checkWin(row, col)) {
                        gameOver = true;
                        playSound(winSound);
//                        fireworkView.startFireworks();
                        fireworkView.startFireworksWithDelay();
                        statusText.setText("Player " + currentPlayer + " Wins!");
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

            gameOver = false;
            statusText.setText("Player " + currentPlayer + " Turn");
        });

        //restart method
        restartBtn.setOnClickListener(v -> {
            boardView.resetBoard();
            moveHistory.clear();

            //clear saved game on restart
            prefs.edit().clear().apply();
            fireworkView.stopFireworks();

            undoLeft = undoLimit;
            updateUndoText();
            gameOver = false;
            currentPlayer = 1;

            statusText.setText("Player 1 Turn");
            restartBtn.setVisibility(View.GONE);
        });

        //load previously saved game
        //loadGame();
    }

    //undo method
    private void updateUndoText() {
        undoBtn.setText("Undo (" + undoLeft + ")");
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
