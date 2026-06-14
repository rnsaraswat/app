package com.example.sudoku;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;

import java.util.Locale;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    private GridLayout sudokuBoard;

    private Spinner spDifficulty;

    private TextView txtTimer;

    private Button btnNewGame;
    private Button btnHint;
    private Button btnUndo, btnRedo;
    private Button btnTheme;
    private Button btnNotes;

    private TextView[][] cells = new TextView[9][9];

    private TextView selectedCell;

    private int selectedRow = -1;
    private int selectedCol = -1;

    private int[][] puzzle;
    private int[][] solution;

    private SudokuGenerator generator;

    private Button btnCheck;
    private Button btnSolve;
    private SudokuSolver solver;

    private boolean[][] fixed = new boolean[9][9];

    private final Handler timerHandler =
            new Handler(Looper.getMainLooper());
    private long seconds = 0;
    private long startTime  = 0;

    private boolean timerRunning = false;

    private Runnable timerRunnable;

    private SharedPreferences prefs;

    private TextView txtMistakes;

    private int mistakes = 0;
    private int maxMistakes = 4;

    private static final int MAX_MISTAKES = 3;

    private Stack<Move> undoStack = new Stack<>();

    private boolean notesMode = false;

    private SudokuCell[][] sudokuCells = new SudokuCell[9][9];

    private Stack<Move> redoStack = new Stack<>();

    private int score = 1000;

    private MediaPlayer mediaPlayer;

    private Button[] numberButtons = new Button[9];

    private TextView txtStatus;

    private Button btnPause;

    private long pausedElapsedTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences(
                        "SudokuSave", MODE_PRIVATE);

        initViews();

        setupDifficultySpinner();

        spDifficulty.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id) {

                        playSound(R.raw.click);

                        String level =
                                spDifficulty
                                        .getSelectedItem()
                                        .toString();

                        if(level.contains("Easy")){

                            maxMistakes = 4;

                        }else if(level.contains("Medium")){

                            maxMistakes = 7;

                        }else{

                            maxMistakes = 10;
                        }

                        setStatus("Level changed to " +
                                spDifficulty.getSelectedItem().toString());
                        startNewGame();
                    }

                    @Override
                    public void onNothingSelected(
                            AdapterView<?> parent) {

                    }
                });

        createBoard();

        setupNumberPad();

        generator = new SudokuGenerator();

        solver = new SudokuSolver();

        btnCheck.setOnClickListener(v -> {
            checkBoard();
        });

        btnSolve.setOnClickListener(v -> {
            solveBoard();
        });

        btnNewGame.setOnClickListener(v -> {
            playSound(R.raw.click);
            startNewGame();
        });

        btnUndo.setOnClickListener(v -> {
            undoMove();
        });

        btnRedo.setOnClickListener(v -> {
            redoMove();
        });

        btnHint.setOnClickListener(v -> {
            giveHint();
        });

        btnNotes.setOnClickListener(v -> {
            playSound(R.raw.click);
            notesMode = !notesMode;

            btnNotes.setText(
                    notesMode ?
                            "Notes ON" :
                            "Notes OFF");
        });

//        btnTheme.setOnClickListener(v -> {
//            playSound(R.raw.click);
//            ThemeManager.toggleTheme();
//        });

        btnPause.setOnClickListener(v -> {
            playSound(R.raw.click);
            pauseGame();
        });

        startNewGame();
    }

    private void initViews() {

        sudokuBoard = findViewById(R.id.sudokuBoard);

        spDifficulty = findViewById(R.id.spDifficulty);

        txtTimer = findViewById(R.id.txtTimer);

        btnNewGame = findViewById(R.id.btnNewGame);

        btnHint = findViewById(R.id.btnHint);

        btnUndo = findViewById(R.id.btnUndo);
        btnRedo = findViewById(R.id.btnRedo);
        btnPause = findViewById(R.id.btnPause);
        txtStatus = findViewById(R.id.txtStatus);

//        btnTheme = findViewById(R.id.btnTheme);
        btnCheck = findViewById(R.id.btnCheck);
        btnSolve = findViewById(R.id.btnSolve);
        btnNotes = findViewById(R.id.btnNotes);
        txtMistakes = findViewById(R.id.txtMistakes);

        numberButtons[0] = findViewById(R.id.btn1);
        numberButtons[1] = findViewById(R.id.btn2);
        numberButtons[2] = findViewById(R.id.btn3);
        numberButtons[3] = findViewById(R.id.btn4);
        numberButtons[4] = findViewById(R.id.btn5);
        numberButtons[5] = findViewById(R.id.btn6);
        numberButtons[6] = findViewById(R.id.btn7);
        numberButtons[7] = findViewById(R.id.btn8);
        numberButtons[8] = findViewById(R.id.btn9);
    }

    private void setupDifficultySpinner() {

        String[] levels = {

                "Easy",
                "Medium",
                "Hard"

        };

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        levels);

        spDifficulty.setAdapter(adapter);
    }

    private void createBoard() {
        sudokuBoard.removeAllViews();

        sudokuBoard.post(() -> {

            int boardSize = sudokuBoard.getMeasuredWidth();

            int cellSize =
                    (boardSize - sudokuBoard.getPaddingLeft()
                            - sudokuBoard.getPaddingRight()) / 9;

            for (int row = 0; row < 9; row++) {

                for (int col = 0; col < 9; col++) {

                    TextView cell = new TextView(this);

                    GridLayout.LayoutParams params =
                            new GridLayout.LayoutParams();

                    params.width = cellSize;
                    params.height = cellSize;

                    cell.setLayoutParams(params);

                    cell.setGravity(Gravity.CENTER);
                    cell.setIncludeFontPadding(false);
                    cell.setPadding(0, 0, 0, 0);

                    cell.setTextSize(20);

                    cell.setSingleLine(true);

                    cell.setMaxLines(1);

                    cell.setEllipsize(null);

                    cell.setBackgroundResource(
                            R.drawable.sudoku_cell);

                    if (puzzle[row][col] != 0) {

                        cell.setText(
                                String.valueOf(
                                        puzzle[row][col]));

                        cell.setTextColor(Color.BLACK);

                        fixed[row][col] = true;

                    } else {

                        fixed[row][col] = false;

                        cell.setText("");

                        cell.setTextColor(
                                getColor(
                                        R.color.userNumber));
                    }

                    final int r = row;
                    final int c = col;

                    cell.setOnClickListener(v ->
                            selectCell(r, c));

                    cells[row][col] = cell;

                    sudokuBoard.addView(cell);

                    sudokuCells[row][col] =
                            new SudokuCell();
                }
            }
        });
    }

    private void selectCell(int row, int col) {

        if (selectedCell != null) {

            selectedCell.setBackgroundResource(
                    R.drawable.sudoku_cell);
        }

        selectedRow = row;
        selectedCol = col;

        selectedCell = cells[row][col];

        selectedCell.setBackgroundResource(
                R.drawable.selected_cell);

        setStatus("Cell selected (" +
                (row + 1) +
                "," +
                (col + 1) +
                ")");
        playSound(R.raw.tap);
    }

    private void setupNumberPad() {

        int[] ids = {

                R.id.btn1,
                R.id.btn2,
                R.id.btn3,
                R.id.btn4,
                R.id.btn5,
                R.id.btn6,
                R.id.btn7,
                R.id.btn8,
                R.id.btn9

        };

        for (int i = 0; i < ids.length; i++) {

            int value = i + 1;

            Button btn = findViewById(ids[i]);

            btn.setOnClickListener(v ->
                    enterNumber(value));
        }

        Button erase =
                findViewById(R.id.btnErase);

        erase.setOnClickListener(v ->
                eraseNumber());
    }

    private void enterNumber(int number) {

        if (selectedRow == -1 ||
                selectedCol == -1) {
                    playSound(R.raw.error);
                    setStatus("Select Cell first");
            return;
        }

        if (selectedRow == -1) {
            playSound(R.raw.error);
            setStatus("Select Cell first");
            return;
        }

        if (fixed[selectedRow][selectedCol]) {
            playSound(R.raw.error);
            setStatus("Select Empty cell");
            return;
        }

        if(notesMode){

            if (sudokuCells[selectedRow][selectedCol] == null) {

                sudokuCells[selectedRow][selectedCol]
                        = new SudokuCell();
            }

            String oldNotes =
                    sudokuCells[selectedRow]
                            [selectedCol]
                            .getNotes();

            if(!oldNotes.contains(
                    String.valueOf(number))){
                playSound(R.raw.click);
                oldNotes += number;
            }

            cells[selectedRow][selectedCol].setTextSize(14);

            cells[selectedRow][selectedCol]
                    .setSingleLine(true);

            cells[selectedRow][selectedCol]
                    .setHorizontallyScrolling(true);

            sudokuCells[selectedRow]
                    [selectedCol]
                    .setNotes(oldNotes);

            cells[selectedRow][selectedCol]
                    .setText(oldNotes);
            return;
        }

        String oldValue =
                cells[selectedRow][selectedCol]
                        .getText()
                        .toString();

        cells[selectedRow][selectedCol]
                .setTextSize(20);
        String newValue =
                String.valueOf(number);

        undoStack.push(
                new Move(
                        selectedRow,
                        selectedCol,
                        oldValue,
                        newValue));

        redoStack.clear();

        cells[selectedRow][selectedCol]
                .setText(newValue);

        playSound(R.raw.tap);
        txtStatus.setText("Fill " + newValue + " in Cell selected (" +
                (selectedRow + 1) + "," + (selectedCol + 1) + ")");

        updateNumberTracker();

        checkForWin();

        if (number != solution
                [selectedRow]
                [selectedCol]) {

            playSound(R.raw.error);
            score -= 50;
            mistakes++;

            txtMistakes.setText(
                    "Mistakes: "
                            + mistakes
                            + "/" + maxMistakes);

            setStatus(mistakes + "Mistakes out of 3");

            if (mistakes >= maxMistakes) {

                gameOver();
            }
        }

    }

    private void gameOver() {

        stopTimer();
        playSound(R.raw.lose);
        setStatus(
                "❌ You Lose!",
                Color.parseColor("#F44336")
        );
    }

    private void eraseNumber() {

        if (selectedRow == -1)
            return;

        if (fixed[selectedRow][selectedCol])
            return;

        String oldValue =
                cells[selectedRow][selectedCol]
                        .getText()
                        .toString();

        undoStack.push(
                new Move(
                        selectedRow,
                        selectedCol,
                        oldValue,
                        ""));

        redoStack.clear();

        cells[selectedRow][selectedCol]
                .setText("");

        setStatus("Cell cleared");
        playSound(R.raw.click);
//        updateNumberCounts();
        updateNumberTracker();
    }

    private void startNewGame() {

        Difficulty difficulty;

        String level =
                spDifficulty.getSelectedItem().toString();

        if (level.equals("Easy")) {

            difficulty = Difficulty.EASY;

        } else if (level.equals("Medium")) {

            difficulty = Difficulty.MEDIUM;

        } else {

            difficulty = Difficulty.HARD;
        }

        seconds = 0;

        mistakes = 0;

        txtMistakes.setText(
                "Mistakes: 0/" + maxMistakes);

        undoStack.clear();
        redoStack.clear();

        resetTimer();

        startTimer();

        solution =
                generator.generateSolution();

        puzzle =
                generator.createPuzzle(
                        solution,
                        difficulty);

        setStatus("New " +
                spDifficulty.getSelectedItem().toString() +
                " game started");
        loadPuzzle();
        updateNumberTracker();
    }

    private void loadPuzzle() {

        sudokuBoard.removeAllViews();

        for(int row=0; row<9; row++) {

            for(int col=0; col<9; col++) {

                TextView cell = new TextView(this);

                int size =
                        sudokuBoard.getWidth()/9;

                GridLayout.LayoutParams params =
                        new GridLayout.LayoutParams();

                params.width = size;
                params.height = size;

                cell.setLayoutParams(params);

                cell.setGravity(Gravity.CENTER);

                cell.setTextSize(20);

                cell.setBackgroundResource(
                        R.drawable.sudoku_cell);

                if(puzzle[row][col] != 0){

                    cell.setText(
                            String.valueOf(
                                    puzzle[row][col]));

                    cell.setTextColor(Color.BLACK);
                    // Fixed Number big size
                    cell.setTextSize(20);
                    cell.setTypeface(null, Typeface.BOLD);

                    fixed[row][col] = true;

                }else{

                    fixed[row][col] = false;

                    cell.setText("");

                    cell.setTextSize(20);
                    cell.setTextColor(
                            getColor(
                                    R.color.userNumber));
                }

                final int r = row;
                final int c = col;

                cell.setOnClickListener(v ->
                        selectCell(r,c));

                cells[row][col] = cell;

                sudokuBoard.addView(cell);

                int boxRow = row / 3;
                int boxCol = col / 3;

                if ((boxRow + boxCol) % 2 == 0) {

                    cell.setBackgroundResource(
                            R.drawable.sudoku_cell_dark);

                } else {

                    cell.setBackgroundResource(
                            R.drawable.sudoku_cell_light);
                }
            }
        }
        updateNumberTracker();
    }

    private void solveBoard() {

        for (int row = 0; row < 9; row++) {

            for (int col = 0; col < 9; col++) {

                cells[row][col]
                        .setText(
                                String.valueOf(
                                        solution[row][col]));

                cells[row][col]
                        .setTextColor(
                                Color.BLACK);

                cells[row][col]
                        .setTextSize(20);
            }
        }

        stopTimer();
        playSound(R.raw.lose);
        setStatus("Puzzle Solution");
    }
    //get user board
    private int[][] getCurrentBoard() {

        int[][] board = new int[9][9];

        for (int row = 0; row < 9; row++) {

            for (int col = 0; col < 9; col++) {

                String text =
                        cells[row][col]
                                .getText()
                                .toString();

                if (text.isEmpty()) {

                    board[row][col] = 0;

                } else {

                    board[row][col] =
                            Integer.parseInt(text);
                }
            }
        }

        return board;
    }

    //Check Solution
    private void checkBoard() {
        playSound(R.raw.click);
        boolean complete = true;

        boolean correct = true;

        for (int row = 0; row < 9; row++) {

            for (int col = 0; col < 9; col++) {

                String text =
                        cells[row][col]
                                .getText()
                                .toString();

                if (text.isEmpty()) {

                    complete = false;

                    continue;
                }

                int value =
                        Integer.parseInt(text);

                if (value != solution[row][col]) {

                    correct = false;

                    cells[row][col]
                            .setBackgroundColor(
                                    0xFFFFCDD2); // red

                } else {

                    cells[row][col]
                            .setBackgroundResource(
                                    R.drawable.sudoku_cell);
                }
            }
        }

        if (!complete) {

            setStatus("Puzzle Incomplete");
            return;
        }

        if (correct) {

            showWinMessage();

        } else {

            setStatus("Some cells are incorrect");

        }
    }

    private void showWinMessage() {
        playSound(R.raw.win);
        stopTimer();
        setStatus(
                "🏆 You Win! Puzzle solved!",
                Color.parseColor("#4CAF50")
        );
    }

    private void checkForWin() {

        for (int row = 0; row < 9; row++) {

            for (int col = 0; col < 9; col++) {

                String text =
                        cells[row][col]
                                .getText()
                                .toString();

                if (text.isEmpty())
                    return;

                int value =
                        Integer.parseInt(text);

                if (value != solution[row][col])
                    return;
            }
        }

        showWinMessage();
    }

    private void startTimer() {

        stopTimer();

        startTime = System.currentTimeMillis();

        timerRunning = true;

        timerRunnable = new Runnable() {

            @Override
            public void run() {

                if (!timerRunning)
                    return;

                long elapsed =
                        System.currentTimeMillis()
                                - startTime;

                int totalSeconds =
                        (int) (elapsed / 1000);

                int hours =
                        totalSeconds / 3600;

                int minutes =
                        totalSeconds / 60;

                int seconds =
                        totalSeconds % 60;

                txtTimer.setText(
                        String.format(
                                Locale.getDefault(),
                                "%02d:%02d:%02d",
                                hours,
                                minutes,
                                seconds));

                timerHandler.postDelayed(
                        this,
                        1000);
            }
        };

        timerRunnable.run();
    }

    private void stopTimer() {

        timerRunning = false;

        if (timerRunnable != null) {

            timerHandler.removeCallbacks(
                    timerRunnable);
        }
    }

    private void resetTimer() {

        stopTimer();

        txtTimer.setText("00:00:00");
    }

    private void undoMove() {

        if(undoStack.isEmpty()) {

            setStatus("Nothing to undo");

            return;
        }

        Move move =
                undoStack.pop();

        redoStack.push(move);

        cells[move.row][move.col]
                .setText(move.oldValue);


        cells[move.row][move.col]
                .setText(move.oldValue);

        setStatus("Undo successful");
        playSound(R.raw.click);
//        updateNumberCounts();
        updateNumberTracker();
    }

    private void giveHint() {

        if (selectedRow == -1)
            return;

        if (fixed[selectedRow][selectedCol])
            return;

        score -= 25;
        cells[selectedRow][selectedCol]
                .setText(
                        String.valueOf(
                                solution[selectedRow]
                                        [selectedCol]));

        cells[selectedRow][selectedCol]
                .setTextSize(20);
        setStatus("Hint used");
        playSound(R.raw.click);
    }

    private void saveGame() {

        SharedPreferences.Editor editor =
                prefs.edit();

        editor.putLong(
                "seconds",
                seconds);

        editor.putInt(
                "mistakes",
                mistakes);

        for(int row=0; row<9; row++) {

            for(int col=0; col<9; col++) {

                editor.putString(
                        "cell_"+row+"_"+col,
                        cells[row][col]
                                .getText()
                                .toString());
            }
        }

        editor.apply();
    }

    private void loadGame() {

        seconds =
                prefs.getLong(
                        "seconds",
                        0);

        mistakes =
                prefs.getInt(
                        "mistakes",
                        0);

        txtMistakes.setText(
                "Mistakes: "
                        + mistakes
                        + "/" + maxMistakes);



        for(int row=0; row<9; row++) {

            for(int col=0; col<9; col++) {

                String value =
                        prefs.getString(
                                "cell_"+row+"_"+col,
                                "");

                cells[row][col]
                        .setText(value);
            }
        }
    }

    @Override
    protected void onPause() {

        super.onPause();

        saveGame();
    }

    @Override
    protected void onResume() {

        super.onResume();

        loadGame();

        startTimer();
    }

    //Win Score
    private int calculateScore() {

        int timePenalty =
                (int)(seconds / 5);

        return Math.max(
                0,
                score - timePenalty);
    }

    private void saveBestTime() {

        long best =
                prefs.getLong(
                        "best_time",
                        Long.MAX_VALUE);

        if(seconds < best){

            prefs.edit()
                    .putLong(
                            "best_time",
                            seconds)
                    .apply();
        }
    }

    private void redoMove() {

        if(redoStack.isEmpty()) {

            setStatus("Nothing to redo");

            return;
        }

        Move move =
                redoStack.pop();

        undoStack.push(move);

        cells[move.row][move.col]
                .setText(move.newValue);

        setStatus("Redo successful");
        playSound(R.raw.click);
//        updateNumberCounts();
        updateNumberTracker();
    }



    private void playSound(int sound){

        if(mediaPlayer != null){

            mediaPlayer.release();
        }

        mediaPlayer =
                MediaPlayer.create(
                        this,
                        sound);

        mediaPlayer.start();

        //to use sound place this line
//        playSound(R.raw.click);
    }


    private void updateNumberTracker() {

        int[] count = new int[9];

        for (int row = 0; row < 9; row++) {

            for (int col = 0; col < 9; col++) {

                String value =
                        cells[row][col]
                                .getText()
                                .toString()
                                .trim();

                if (!value.isEmpty()) {

                    try {

                        int num =
                                Integer.parseInt(value);

                        if (num >= 1 && num <= 9) {

                            count[num - 1]++;
                        }

                    } catch (Exception ignored) {
                    }
                }
            }
        }

        for (int i = 0; i < 9; i++) {

            numberButtons[i].setText(
                    (i + 1) +
                            " (" +
                            count[i] +
                            ")");

            if (count[i] >= 9) {

                numberButtons[i].setEnabled(false);

                numberButtons[i].setAlpha(0.4f);

                numberButtons[i].setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(
                                android.graphics.Color.parseColor("#4CAF50")));

            } else {

                numberButtons[i].setEnabled(true);

                numberButtons[i].setAlpha(1f);

                numberButtons[i].setBackgroundTintList(null);
            }
        }
    }
    private void setStatus(String message) {

        txtStatus.setText(message);
    }

    //Color Feedback
    private void setStatus(String message,
                           int backgroundColor) {

        txtStatus.setText(message);

        txtStatus.setBackgroundColor(
                backgroundColor);
    }

    private void pauseGame() {

        pausedElapsedTime =
                System.currentTimeMillis()
                        - startTime;

        stopTimer();

        showPauseDialog();
    }

    private void resumeGame() {

        startTime =
                System.currentTimeMillis()
                        - pausedElapsedTime;

        timerRunning = true;

        timerHandler.post(timerRunnable);
    }

    private void showPauseDialog() {

        Dialog dialog =
                new Dialog(this);

        dialog.setCancelable(false);

        dialog.setContentView(
                R.layout.dialog_pause);

        Window window =
                dialog.getWindow();

        if(window != null){

            window.setLayout(
                    (int)(getResources()
                            .getDisplayMetrics()
                            .widthPixels * 0.95),

                    (int)(getResources()
                            .getDisplayMetrics()
                            .heightPixels * 0.95));
        }

        Button btnResume =
                dialog.findViewById(
                        R.id.btnResume);

        btnResume.setOnClickListener(v -> {
            playSound(R.raw.click);
            dialog.dismiss();

            resumeGame();
        });

        dialog.show();
    }
}