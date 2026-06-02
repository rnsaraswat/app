package com.example.ravindragameshub.connect4;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.animation.ObjectAnimator;
import android.animation.AnimatorSet;
import android.view.animation.BounceInterpolator;

import java.util.ArrayList;
import java.util.Random;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import com.example.ravindragameshub.common.ThemeManager;
import com.example.ravindragameshub.common.SoundManager;
import com.example.ravindragameshub.R;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Stack;

public class Connect4Activity extends AppCompatActivity {

    class Move {

        int row;
        int col;
        int player;

        Move(int row, int col, int player) {

            this.row = row;
            this.col = col;
            this.player = player;
        }
    }

    LinearLayout rootLayout;
    TextView statusText, scoreText;

    Spinner modeSpinner, levelSpinner;

    int mode, level;
    GridLayout gameBoard;

    Button restartBtn, undoBtn;

    final int ROWS = 6;
    final int COLS = 7;

    int[][] board = new int[ROWS][COLS];

    ImageView[][] cells = new ImageView[ROWS][COLS];

    //Winning Line Animation
    private final ArrayList<int[]> winningCells =
            new ArrayList<>();
    int currentPlayer = 1;

    int player1Score = 0;
    int player2Score = 0;

    boolean gameOver = false;

    int maxUndo = 3;
    int remainingUndo = 3;

    Stack<Move> moveHistory = new Stack<>();

    //AI Variables
    Random random = new Random();

    boolean aiThinking = false;


//    private WinningLineView winningLineView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect4);

        //apply theme
        rootLayout = findViewById(R.id.rootLayout);

        // Theme Apply
        ThemeManager.applyTheme(
                this,
                rootLayout
        );

        statusText = findViewById(R.id.statusText);
        scoreText = findViewById(R.id.scoreText);

        modeSpinner = findViewById(R.id.modeSpinner);
        levelSpinner = findViewById(R.id.levelSpinner);

        gameBoard = findViewById(R.id.gameBoard);

        restartBtn = findViewById(R.id.restartBtn);
        undoBtn = findViewById(R.id.undoBtn);
//        winningLineView =
//                findViewById(
//                        R.id.winningLineView);

        Button btnHome =
                findViewById(R.id.btnHome);

        Button btnShare =
                findViewById(R.id.btnShare);

        btnHome.setOnClickListener(v -> {

            finish();

        });

        btnShare.setOnClickListener(v -> {
            SoundManager.playClick();
            Intent shareIntent =
                    new Intent(Intent.ACTION_SEND);

            shareIntent.setType("text/plain");

            shareIntent.putExtra(
                    Intent.EXTRA_TEXT,
                    "Play Ravindra Games Hub!"
            );

            SoundManager.playClick();
            startActivity(
                    Intent.createChooser(
                            shareIntent,
                            "Share App"
                    )
            );
        });


        setupSpinners();

        createBoard();

        updateUndoButton();

        restartBtn.setOnClickListener(v -> restartGame());

        undoBtn.setOnClickListener(v -> undoMove());
    }

    // theme update on Screen reopen
    @Override
    protected void onResume() {

        super.onResume();

        ThemeManager.applyTheme(
                this,
                rootLayout
        );
    }

    private void setupSpinners() {

        String[] modes = {
                "Player vs Player",
                "Player vs AI"
        };

        ArrayAdapter<String> modeAdapter =
                new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_dropdown_item,
                        modes);

        modeSpinner.setAdapter(modeAdapter);

        modeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mode = position;
                SoundManager.playClick();
//                restart game
                restartGame();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        String[] levels = {
                "Easy",
                "Medium",
                "Hard"
        };

        ArrayAdapter<String> levelAdapter =
                new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_dropdown_item,
                        levels);

        levelSpinner.setAdapter(levelAdapter);

        levelSpinner.setSelection(0);

        levelSpinner.setOnItemSelectedListener(
                new android.widget.AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            android.widget.AdapterView<?> parent,
                            View view,
                            int position,
                            long id) {

                        SoundManager.playClick();
                        level = position;
                        if (level == 0) {
                            maxUndo = 3;
                        }
                        else if (level == 1) {
                            maxUndo = 4;
                        }
                        else {
                            maxUndo = 5;
                        }

                        remainingUndo = maxUndo;

                        restartGame();
                        updateUndoButton();
                    }

                    @Override
                    public void onNothingSelected(
                            android.widget.AdapterView<?> parent) {

                    }
                });
    }

    private void createBoard() {

        gameBoard.removeAllViews();

        for (int r = 0; r < ROWS; r++) {

            for (int c = 0; c < COLS; c++) {

                ImageView cell = new ImageView(this);

                GridLayout.LayoutParams params =
                        new GridLayout.LayoutParams();

                params.width = 115;
                params.height = 115;

                params.setMargins(6,6,6,6);

                cell.setLayoutParams(params);

                // 3D Board Hole
                GradientDrawable shape = new GradientDrawable();

                shape.setShape(GradientDrawable.OVAL);

                shape.setColor(Color.parseColor("#EEEEEE"));

                shape.setStroke(5,
                        Color.parseColor("#0D47A1"));

                cell.setBackground(shape);

                int col = c;

                cell.setOnClickListener(v -> {

                    if (aiThinking) return;

                    makeMove(col);
                });

                cells[r][c] = cell;

                gameBoard.addView(cell);
            }
        }
    }

    private void makeMove(int col) {

        if (gameOver) return;

        for (int row = ROWS - 1; row >= 0; row--) {

            if (board[row][col] == 0) {

                board[row][col] = currentPlayer;

                moveHistory.push(
                        new Move(row, col, currentPlayer));

                animateDiscDrop(row, col);
                SoundManager.playBallTap();

                if (checkWinner(row, col)) {

                    gameOver = true;

                    if (currentPlayer == 1) {

                        player1Score++;
                        SoundManager.playWin();

                        statusText.setText("Player 1 Wins!");
                        animateWinningLine();
//                        drawWinningLine();
                    }
                    else {

                        player2Score++;
                        SoundManager.playLose();
                        if (mode == 0) {
                            statusText.setText("Player 2 Wins!");
                            animateWinningLine();
//                            drawWinningLine();
                        }
                        else {
                            statusText.setText("AI Wins!");

                        }
                    }

                    updateScore();

                    return;
                }

                currentPlayer =
                        (currentPlayer == 1) ? 2 : 1;

                updateTurnText();

                if (isBoardFull()) {
                    gameOver = true;
                    statusText.setText("Draw Game!");
                    SoundManager.playDraw();
                    return;
                }

                // AI Move
                if (!gameOver &&
                        mode == 1 &&
                        currentPlayer == 2) {

                    aiThinking = true;

                    gameBoard.postDelayed(() -> {

                        aiMove();

                        aiThinking = false;

                    }, 500);
                }

                return;
            }
        }
    }

    private void updateCell(int row, int col) {

        GradientDrawable shape = new GradientDrawable();

        shape.setShape(GradientDrawable.OVAL);

        if (board[row][col] == 1) {
            shape.setColor(Color.RED);
        }
        else {
            shape.setColor(Color.YELLOW);
        }

        cells[row][col].setBackground(shape);
    }

    private boolean checkWinner(int row, int col) {

        int player = board[row][col];

        return count(row, col, 1,0, player) >= 4 ||
                count(row, col, 0,1, player) >= 4 ||
                count(row, col, 1,1, player) >= 4 ||
                count(row, col, 1,-1, player) >= 4;
    }

    //Winning Line Animation
//    private boolean checkWinner(
//            int row,
//            int col) {
//
//        int player = board[row][col];
//
//        return checkDirection(row,col,1,0,player)
//                || checkDirection(row,col,0,1,player)
//                || checkDirection(row,col,1,1,player)
//                || checkDirection(row,col,1,-1,player);
//    }

    private int count(
            int row,
            int col,
            int dr,
            int dc,
            int player) {

        int total = 1;

        int r = row + dr;
        int c = col + dc;

        while (r >= 0 && r < ROWS &&
                c >= 0 && c < COLS &&
                board[r][c] == player) {

            total++;

            r += dr;
            c += dc;
        }

        r = row - dr;
        c = col - dc;

        while (r >= 0 && r < ROWS &&
                c >= 0 && c < COLS &&
                board[r][c] == player) {

            total++;

            r -= dr;
            c -= dc;
        }

        return total;
    }

    //Winning Line Animation
//    private boolean checkDirection(
//            int row,
//            int col,
//            int dr,
//            int dc,
//            int player) {
//
//        winningCells.clear();
//
//        ArrayList<int[]> temp =
//                new ArrayList<>();
//
//        temp.add(new int[]{row,col});
//
//        int r = row + dr;
//        int c = col + dc;
//
//        while (r >= 0 && r < ROWS &&
//                c >= 0 && c < COLS &&
//                board[r][c] == player) {
//
//            temp.add(new int[]{r,c});
//
//            r += dr;
//            c += dc;
//        }
//
//        r = row - dr;
//        c = col - dc;
//
//        while (r >= 0 && r < ROWS &&
//                c >= 0 && c < COLS &&
//                board[r][c] == player) {
//
//            temp.add(new int[]{r,c});
//
//            r -= dr;
//            c -= dc;
//        }
//
//        if (temp.size() >= 4) {
//
//            winningCells.addAll(temp);
//
//            return true;
//        }
//
//        return false;
//    }

    //Winning Line Animation
    private void animateWinningLine() {

        for (int[] pos : winningCells) {

            ImageView cell =
                    cells[pos[0]][pos[1]];

            startGlowEffect(cell);

            ObjectAnimator scaleX =
                    ObjectAnimator.ofFloat(
                            cell,
                            "scaleX",
                            1f,
                            1.3f,
                            1f);

            ObjectAnimator scaleY =
                    ObjectAnimator.ofFloat(
                            cell,
                            "scaleY",
                            1f,
                            1.3f,
                            1f);

            AnimatorSet set =
                    new AnimatorSet();

            set.playTogether(
                    scaleX,
                    scaleY);

            set.setDuration(700);

            set.start();
        }
    }

    private void undoMove() {

        if (remainingUndo <= 0) {
            SoundManager.playError();
            return;
        }

        if (moveHistory.isEmpty()) {
            SoundManager.playError();
            return;
        }

        SoundManager.playClick();
        int undoCount = 1;

        // AI mode → undo both player + AI
        if (mode == 1) {

            undoCount = Math.min(2,
                    moveHistory.size());
        }

        for (int i = 0; i < undoCount; i++) {

            Move move = moveHistory.pop();

            board[move.row][move.col] = 0;

            GradientDrawable shape =
                    new GradientDrawable();

            shape.setShape(GradientDrawable.OVAL);

            shape.setColor(Color.parseColor("#EEEEEE"));

            shape.setStroke(5,
                    Color.parseColor("#0D47A1"));

            cells[move.row][move.col]
                    .setBackground(shape);
        }

        gameOver = false;

        currentPlayer = 1;

        if (!moveHistory.isEmpty()) {

            currentPlayer =
                    moveHistory.peek().player == 1
                            ? 2 : 1;
        }

        remainingUndo--;

        updateUndoButton();

        updateTurnText();
    }

    private void restartGame() {

        board = new int[ROWS][COLS];

        SoundManager.playClick();
        mode = modeSpinner.getSelectedItemPosition();
        level = levelSpinner.getSelectedItemPosition();

//        winningLineView.clearLine();
        moveHistory.clear();

        currentPlayer = 1;

        gameOver = false;

        remainingUndo = maxUndo;

        updateUndoButton();

        updateTurnText();

        createBoard();
    }

    private void updateTurnText() {

        if (currentPlayer == 1) {

            statusText.setText("Player 1 Turn");
        }
        else {

            if (mode == 0) {
                statusText.setText("Player 2 Turn");
            }
            else {
                statusText.setText("AI Turn");
            }
        }
    }

    private void updateScore() {

        if (mode == 0) {

            scoreText.setText(
                    "Player 1 : " + player1Score +
                            "     Player 2 : " + player2Score);
        }
        else {

            scoreText.setText(
                    "Player : " + player1Score +
                            "     AI : " + player2Score);
        }
    }

    private void updateUndoButton() {

        undoBtn.setText(
                "Undo (" + remainingUndo + ")");
    }

    //AI Logic
    private void aiMove() {

        int level =
                levelSpinner.getSelectedItemPosition();

        int col;

        if (level == 0) {

            // EASY
            col = randomMove();

        }
        else if (level == 1) {

            // MEDIUM
            col = mediumMove();

        }
        else {

            // HARD
            col = hardMove();
        }

        makeMove(col);
    }

    //Random Easy AI
    private int randomMove() {

        int col;

        do {

            col = random.nextInt(COLS);

        } while (board[0][col] != 0);

        return col;
    }

    //Medium AI
    private int mediumMove() {

        // Winning move
        for (int c = 0; c < COLS; c++) {

            int r = getAvailableRow(c);

            if (r != -1) {

                board[r][c] = 2;

                if (checkWinner(r,c)) {

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

                if (checkWinner(r,c)) {

                    board[r][c] = 0;

                    return c;
                }

                board[r][c] = 0;
            }
        }

        return randomMove();
    }

    //Hard AI
    private int hardMove() {

        int bestScore = Integer.MIN_VALUE;

        int bestCol = 0;

        for (int c = 0; c < COLS; c++) {

            int r = getAvailableRow(c);

            if (r != -1) {

                board[r][c] = 2;

                int score =
                        minimax(4, false);

                board[r][c] = 0;

                if (score > bestScore) {

                    bestScore = score;

                    bestCol = c;
                }
            }
        }

        return bestCol;
    }

    private int minimax(
            int depth,
            boolean maximizing) {

        if (depth == 0) {

            return evaluateBoard();
        }

        if (maximizing) {

            int maxEval = Integer.MIN_VALUE;

            for (int c = 0; c < COLS; c++) {

                int r = getAvailableRow(c);

                if (r != -1) {

                    board[r][c] = 2;

                    int eval =
                            minimax(depth - 1,
                                    false);

                    board[r][c] = 0;

                    maxEval =
                            Math.max(maxEval,
                                    eval);
                }
            }

            return maxEval;

        } else {

            int minEval = Integer.MAX_VALUE;

            for (int c = 0; c < COLS; c++) {

                int r = getAvailableRow(c);

                if (r != -1) {

                    board[r][c] = 1;

                    int eval =
                            minimax(depth - 1,
                                    true);

                    board[r][c] = 0;

                    minEval =
                            Math.min(minEval,
                                    eval);
                }
            }

            return minEval;
        }
    }

    private int evaluateBoard() {

        int score = 0;

        // Center preference
        for (int r = 0; r < ROWS; r++) {

            if (board[r][3] == 2) {

                score += 3;
            }
        }

        // Horizontal
        for (int r = 0; r < ROWS; r++) {

            for (int c = 0; c < COLS - 3; c++) {

                score += evaluateWindow(
                        new int[]{
                                board[r][c],
                                board[r][c+1],
                                board[r][c+2],
                                board[r][c+3]
                        });
            }
        }

        // Vertical
        for (int c = 0; c < COLS; c++) {

            for (int r = 0; r < ROWS - 3; r++) {

                score += evaluateWindow(
                        new int[]{
                                board[r][c],
                                board[r+1][c],
                                board[r+2][c],
                                board[r+3][c]
                        });
            }
        }

        return score;
    }

    private int evaluateWindow(int[] window) {

        int ai = 0;
        int player = 0;
        int empty = 0;

        for (int cell : window) {

            if (cell == 2) ai++;

            else if (cell == 1) player++;

            else empty++;
        }

        if (ai == 4) return 100;

        if (ai == 3 && empty == 1)
            return 10;

        if (ai == 2 && empty == 2)
            return 5;

        if (player == 3 && empty == 1)
            return -8;

        return 0;
    }

    //Helper Method
    private int getAvailableRow(int col) {

        for (int r = ROWS - 1; r >= 0; r--) {

            if (board[r][col] == 0) {

                return r;
            }
        }

        return -1;
    }

    private boolean isBoardFull() {

        for (int c = 0; c < COLS; c++) {

            if (board[0][c] == 0) {

                return false;
            }
        }

        return true;
    }

    //Animated Disc Drop Method
    private void animateDiscDrop(int row, int col) {

        GradientDrawable disc = new GradientDrawable();
        disc.setShape(GradientDrawable.OVAL);

        if (board[row][col] == 1) {

            disc =
                    new GradientDrawable(
                            GradientDrawable.Orientation.TOP_BOTTOM,
                            new int[]{
                                    Color.parseColor("#FFFFFF"),
                                    Color.RED
                            });
            disc.setShape(GradientDrawable.OVAL);
            disc.setStroke(
                    5,
                    Color.parseColor("#333333"));
            // Red 3D Disc
//            disc.setColor(Color.RED);
//            disc.setStroke(4, Color.parseColor("#880000"));

        } else {

            disc =
                    new GradientDrawable(
                            GradientDrawable.Orientation.TOP_BOTTOM,
                            new int[]{
                                    Color.parseColor("#FFFFCC"),
                                    Color.YELLOW
                            });
            disc.setShape(GradientDrawable.OVAL);
            disc.setStroke(
                    5,
                    Color.parseColor("#333333"));
            // Yellow 3D Disc
//            disc.setColor(Color.YELLOW);
//            disc.setStroke(4, Color.parseColor("#AA8800"));
        }


        ImageView cell = cells[row][col];

        cell.setElevation(12f);
        cell.setBackground(disc);

        // Top se drop animation
        cell.setTranslationY(-800f);

        ObjectAnimator drop =
                ObjectAnimator.ofFloat(
                        cell,
                        "translationY",
                        -800f,
                        0f);

        drop.setDuration(600);

        drop.setInterpolator(
                new BounceInterpolator());

        drop.start();
    }

    //Winning Glow
    private void startGlowEffect(
            ImageView cell) {

        ValueAnimator glow =
                ValueAnimator.ofObject(
                        new ArgbEvaluator(),
                        Color.WHITE,
                        Color.YELLOW,
                        Color.WHITE);

        glow.setDuration(800);

        glow.setRepeatCount(
                ValueAnimator.INFINITE);

        glow.addUpdateListener(anim -> {

            int color =
                    (int) anim.getAnimatedValue();

            cell.setColorFilter(color);
        });

        glow.start();
    }
}
