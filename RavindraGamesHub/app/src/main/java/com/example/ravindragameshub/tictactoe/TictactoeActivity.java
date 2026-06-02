package com.example.ravindragameshub.tictactoe;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ravindragameshub.R;
import com.example.ravindragameshub.common.SoundManager;
import com.example.ravindragameshub.common.ThemeManager;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

public class TictactoeActivity extends AppCompatActivity {

    public class Move {

        public int row;
        public int col;
        public char player;

        public Move(int row, int col, char player) {

            this.row = row;
            this.col = col;
            this.player = player;

        }
    }

    private TextView statusText;
    private TextView scoreText;

    private Spinner modeSpinner;
    private Spinner levelSpinner;

    private Button restartBtn;
    private Button undoBtn;

    private GridLayout gameBoard;

    private final Button[][] cells = new Button[3][3];

    private final char[][] board = new char[3][3];

    private char currentPlayer = 'X';

    private boolean vsAI = false;

    private int player1Score = 0;
    private int player2Score = 0;
    private int aiScore = 0;

    private int maxUndo = 3;
    private int remainingUndo = 3;

    private final Random random = new Random();

    private final Stack<Move> moveHistory = new Stack<>();

    private int[][] winningCells =
            new int[3][2];

    LinearLayout rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tictactoe);

        //apply theme
        rootLayout = findViewById(R.id.rootLayout);

        // Theme Apply
        ThemeManager.applyTheme(
                this,
                rootLayout
        );

        initViews();

        setupSpinners();

        levelSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id) {
                        SoundManager.playClick();
                        updateUndoLimit();
                    }

                    @Override
                    public void onNothingSelected(
                            AdapterView<?> parent) {

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

                        SoundManager.playClick();
                        vsAI = position == 1;

                        updateScoreText();

                        resetBoard();
                    }

                    @Override
                    public void onNothingSelected(
                            AdapterView<?> parent) {

                    }
                });

        createBoard();

        updateUndoButton();

        Button btnHome = findViewById(R.id.btnHome);

        Button btnShare = findViewById(R.id.btnShare);

        btnHome.setOnClickListener(v -> {
            SoundManager.playClick();
            finish();
        });

        btnShare.setOnClickListener(v -> {
            SoundManager.playClick();
            Intent shareIntent = new Intent(Intent.ACTION_SEND);

            shareIntent.setType("text/plain");

            shareIntent.putExtra(
                    Intent.EXTRA_TEXT,"Play Ravindra Games Hub!"
            );

            SoundManager.playClick();
            startActivity(
                    Intent.createChooser(shareIntent,"Share App")
            );
        });

        restartBtn.setOnClickListener(v -> {

            resetBoard();
        });

        undoBtn.setOnClickListener(v -> {
            undoMove();
        });
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

    private void initViews() {

        statusText = findViewById(R.id.statusText);
        scoreText = findViewById(R.id.scoreText);

        modeSpinner = findViewById(R.id.modeSpinner);
        levelSpinner = findViewById(R.id.levelSpinner);

        restartBtn = findViewById(R.id.restartBtn);
        undoBtn = findViewById(R.id.undoBtn);

        gameBoard = findViewById(R.id.gameBoard);
    }

    private void setupSpinners() {

        String[] modes = {
                "Player vs Player",
                "Player vs AI"
        };

        ArrayAdapter<String> modeAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        modes
                );

        modeSpinner.setAdapter(modeAdapter);

        String[] levels = {
                "Easy",
                "Medium",
                "Hard"
        };

        ArrayAdapter<String> levelAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        levels
                );

        levelSpinner.setAdapter(levelAdapter);
    }

    private void createBoard() {

        gameBoard.removeAllViews();

        for (int row = 0; row < 3; row++) {

            for (int col = 0; col < 3; col++) {

                Button cell = new Button(this);

                GridLayout.LayoutParams params =
                        new GridLayout.LayoutParams();

                params.width = 220;
                params.height = 220;

                cell.setLayoutParams(params);

                cell.setTextSize(30);

                final int r = row;
                final int c = col;

                cell.setOnClickListener(v -> {
                    onCellClicked(r, c);
                });

                cells[row][col] = cell;

                gameBoard.addView(cell);
            }
        }
    }

    private void resetBoard() {

        for (int r = 0; r < 3; r++) {

            for (int c = 0; c < 3; c++) {

                board[r][c] = '\0';

                cells[r][c].setText("");
                cells[r][c].setEnabled(true);

                cells[r][c].setScaleX(1f);
                cells[r][c].setScaleY(1f);

                cells[r][c].setBackgroundResource(
                        android.R.drawable.btn_default);
            }
        }

        SoundManager.playClick();
        moveHistory.clear();

        currentPlayer = 'X';

        remainingUndo = maxUndo;

        updateUndoButton();

        if (vsAI) {
            statusText.setText("Player Turn");
        } else {
            statusText.setText( "Player 1 Turn");
        }
    }

    private void onCellClicked(int row, int col) {

        if (board[row][col] != '\0'){
            SoundManager.playError();
            return;
        }

        SoundManager.playTap();
        board[row][col] = currentPlayer;

        cells[row][col].setText(String.valueOf(currentPlayer));

        moveHistory.push(
                new Move( row, col, currentPlayer));

        if (checkWinner(currentPlayer)) {
            handleWinner(currentPlayer);
            return;
        }

        if (isBoardFull()) {
            SoundManager.playDraw();
            statusText.setText("Draw!");
            disableBoard();
            return;
        }

        switchTurn();

        if (vsAI && currentPlayer == 'O') {
            aiMove();
        }
    }

    //disable board
    private void disableBoard() {

        for(int r=0;r<3;r++) {

            for(int c=0;c<3;c++) {

                cells[r][c].setEnabled(false);

            }
        }
    }

    //change player turn
    private void switchTurn() {

        currentPlayer = currentPlayer == 'X' ? 'O' : 'X';

        if (vsAI) {
            statusText.setText(
                    currentPlayer == 'X' ? "Player Turn" : "AI Turn");
        } else {
            statusText.setText(
                    currentPlayer == 'X' ? "Player 1 Turn" : "Player 2 Turn");
        }
    }

    //check board full for draw
    private boolean isBoardFull() {
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (board[r][c] == '\0')
                    return false;
            }
        }
        return true;
    }

    //check winner
    private boolean checkWinner(char player) {

        //row win
        for (int r = 0; r < 3; r++) {
            if (board[r][0] == player &&
                    board[r][1] == player &&
                    board[r][2] == player) {
                winningCells[0]=new int[]{r,0};
                winningCells[1]=new int[]{r,1};
                winningCells[2]=new int[]{r,2};
                return true;
            }
        }

        //Column Win:
        for (int c = 0; c < 3; c++) {

            if (board[0][c] == player &&
                    board[1][c] == player &&
                    board[2][c] == player) {
                winningCells[0]=new int[]{0,c};
                winningCells[1]=new int[]{1,c};
                winningCells[2]=new int[]{2,c};
                return true;
            }
        }

        //Main Diagonal
        if (board[0][0] == player &&
                board[1][1] == player &&
                board[2][2] == player) {
            winningCells[0] = new int[]{0, 0};
            winningCells[1] = new int[]{1, 1};
            winningCells[2] = new int[]{2, 2};
            return true;
        }

        //Anti Diagonal
        if (board[0][2] == player &&
                board[1][1] == player &&
                board[2][0] == player) {
            winningCells[0]=new int[]{0,2};
            winningCells[1]=new int[]{1,1};
            winningCells[2]=new int[]{2,0};
            return true;
        }
        return false;
    }

    //action on win
    private void handleWinner(char winner) {

        if (vsAI) {
            if (winner == 'X') {
                SoundManager.playWin();
                player1Score++;
                statusText.setText("Player Wins!");
            } else {
                SoundManager.playLose();
                aiScore++;
                statusText.setText("AI Wins!");
            }
        } else {
            if (winner == 'X') {
                SoundManager.playWin();
                player1Score++;
                statusText.setText("Player 1 Wins!");
            } else {
                SoundManager.playLose();
                player2Score++;
                statusText.setText("Player 2 Wins!");
            }
        }
        updateScoreText();
        glowWinningCells();
        disableBoard();
    }

    //update score
    private void updateScoreText() {
        if (vsAI) {
            scoreText.setText(
                    "Player : " + player1Score +
                            "    AI : " + aiScore);
        } else {
            scoreText.setText(
                    "Player 1 : " + player1Score +
                            "    Player 2 : " + player2Score
            );
        }
    }

    //AI Move
    private void aiMove() {
        String level = levelSpinner.getSelectedItem().toString();

        switch (level) {
            case "Easy":
                easyAI();
                break;
            case "Medium":
                mediumAI();
                break;
            case "Hard":
                hardAI();
                break;
        }
    }

    //Easy AI Move
    private void easyAI() {
        ArrayList<int[]> moves = new ArrayList<>();

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (board[r][c] == '\0') {
                    moves.add(new int[]{r, c});
                }
            }
        }

        if (moves.isEmpty())
            return;

        int[] move = moves.get(random.nextInt(moves.size()));

        onCellClicked(move[0], move[1]);
    }

    // Medium AI Move
    private void mediumAI() {

        int[] move = findWinningMove('O');

        if (move == null) {
            move = findWinningMove('X');
        }

        if (move != null) {
            onCellClicked(move[0], move[1]);
            return;
        }
        easyAI();
    }

    private int[] findWinningMove(char player) {

        for (int r = 0; r < 3; r++) {

            for (int c = 0; c < 3; c++) {

                if (board[r][c] == '\0') {
                    board[r][c] = player;

                    boolean win = checkWinner(player);

                    board[r][c] = '\0';

                    if (win) {
                        return new int[]{r, c};
                    }
                }
            }
        }

        return null;
    }

    //hard AI Move
    private void hardAI() {

        int bestScore = Integer.MIN_VALUE;

        int bestRow = -1;
        int bestCol = -1;

        for (int r = 0; r < 3; r++) {

            for (int c = 0; c < 3; c++) {

                if (board[r][c] == '\0') {

                    board[r][c] = 'O';

                    int score = minimax(false,0);

                    board[r][c] = '\0';

                    if (score > bestScore) {
                        bestScore = score;
                        bestRow = r;
                        bestCol = c;
                    }
                }
            }
        }

        if (bestRow != -1) {
            onCellClicked(bestRow, bestCol);
        }
    }

    //Used in hard AI Move
    private int minimax(boolean maximizing, int depth) {

        if (checkWinner('O'))
            return 10;

        if (checkWinner('X'))
            return -10;

        if (isBoardFull())
            return 0;

        if (maximizing) {

            int best = Integer.MIN_VALUE;

            for (int r = 0; r < 3; r++) {

                for (int c = 0; c < 3; c++) {

                    if (board[r][c] == '\0') {

                        board[r][c] = 'O';

                        int score = minimax(false,0);

                        board[r][c] = '\0';

                        best = Math.max(best, score);
                    }
                }
            }
            return best;
        }

        int best = Integer.MAX_VALUE;

        for (int r = 0; r < 3; r++) {

            for (int c = 0; c < 3; c++) {

                if (board[r][c] == '\0') {

                    board[r][c] = 'X';

                    int score = minimax(true,0);

                    board[r][c] = '\0';

                    best = Math.min(best, score);
                }
            }
        }
        return best;
    }

    private void updateUndoLimit() {

        String level = levelSpinner.getSelectedItem().toString();

        switch (level) {
            case "Easy":
                maxUndo = 3;
                break;
            case "Medium":
                maxUndo = 4;
                break;
            case "Hard":
                maxUndo = 5;
                break;
        }

        remainingUndo = maxUndo;
        updateUndoButton();
    }

    //undo
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
        if (vsAI) {
            undoAIMove();
        } else {
            undoPlayerMove();
        }
    }

    //undo player move
    private void undoPlayerMove() {

        if (moveHistory.isEmpty())
            return;

        Move move = moveHistory.pop();

        board[move.row][move.col] = '\0';

        cells[move.row][move.col].setText("");

        currentPlayer = move.player;

        remainingUndo--;

        updateUndoButton();

        statusText.setText(
                currentPlayer == 'X' ? "Player 1 Turn" : "Player 2 Turn"
        );
    }

    //undo AI Move
    private void undoAIMove () {

        if (moveHistory.size() < 2)
            return;

        Move aiMove = moveHistory.pop();

        board[aiMove.row][aiMove.col] = '\0';

        cells[aiMove.row][aiMove.col].setText("");

        Move playerMove = moveHistory.pop();

        board[playerMove.row][playerMove.col] = '\0';

        cells[playerMove.row][playerMove.col].setText("");

        currentPlayer = 'X';

        remainingUndo--;

        updateUndoButton();

        statusText.setText("Player Turn");
    }

    //upadte undo button
    private void updateUndoButton () {
        undoBtn.setText("Undo (" + remainingUndo + ")");
    }

    //show winning cell
    private void glowWinningCells() {

        for(int i=0;i<3;i++) {
            int r = winningCells[i][0];
            int c = winningCells[i][1];
            cells[r][c].animate()
                    .scaleX(1.2f)
                    .scaleY(1.2f)
                    .setDuration(300)
                    .withEndAction(() -> {
                        cells[r][c].animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(300);
                    });
            cells[r][c].setBackgroundColor(0xFF4CAF50);
        }
    }
}
