package com.example.slitherlink;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.slintherlink.R;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    public class Move {

        boolean horizontal;
        int row;
        int col;
        boolean previousState;

        public Move(
                boolean horizontal,
                int row,
                int col,
                boolean previousState){

            this.horizontal=horizontal;
            this.row=row;
            this.col=col;
            this.previousState=previousState;
        }
    }

    private SlitherlinkView gameView;

    private Stack<Move> undoStack =
            new Stack<>();

    private Stack<Move> redoStack =
            new Stack<>();

    private int score;
    private int hintsUsed;
    private long elapsedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs =
                getSharedPreferences(
                        "theme",
                        MODE_PRIVATE);

        prefs.edit()
                .putBoolean(
                        "dark",
                        true)
                .apply();

        if(isDark){

            setBackgroundColor(
                    Color.BLACK);

            textPaint.setColor(
                    Color.WHITE);
        }

        String[] difficulties = {
                "Easy (5x5)",
                "Medium (8x8)",
                "Hard (12x12)"
        };

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        difficulties);

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);

        spDifficulty.setAdapter(adapter);

        gameView = findViewById(R.id.gameView);

        Button btnCheck = findViewById(R.id.btnCheck);

        btnCheck.setOnClickListener(v -> {
            gameView.checkPuzzle();
        });

        btnNew.setOnClickListener(v -> {

            gameView.startNewGame(
                    spDifficulty.getSelectedItemPosition());

        });

        btnHint.setOnClickListener(v -> {

            gameView.giveHint();

        });

        btnUndo.setOnClickListener(v -> {

            gameView.undo();

        });

        btnRedo.setOnClickListener(v -> {

            gameView.redo();

        });

        switchDark.setOnCheckedChangeListener(
                (buttonView,isChecked)->{

                    AppCompatDelegate.setDefaultNightMode(

                            isChecked
                                    ? AppCompatDelegate.MODE_NIGHT_YES
                                    : AppCompatDelegate.MODE_NIGHT_NO
                    );
                });

    }

    private boolean validateDegrees() {

        int dots = size + 1;

        for (int r = 0; r < dots; r++) {
            for (int c = 0; c < dots; c++) {

                int degree = 0;

                if (c > 0 && horizontal[r][c - 1]) degree++;
                if (c < size && horizontal[r][c]) degree++;

                if (r > 0 && vertical[r - 1][c]) degree++;
                if (r < size && vertical[r][c]) degree++;

                if (degree != 0 && degree != 2)
                    return false;
            }
        }

        return true;
    }

    private boolean validateClues() {

        for (int r = 0; r < size; r++) {

            for (int c = 0; c < size; c++) {

                if (clues[r][c] == -1)
                    continue;

                int count = 0;

                if (horizontal[r][c]) count++;
                if (horizontal[r + 1][c]) count++;
                if (vertical[r][c]) count++;
                if (vertical[r][c + 1]) count++;

                if (count != clues[r][c])
                    return false;
            }
        }

        return true;
    }

    private boolean isSingleLoop() {

        Set<String> visited = new HashSet<>();

        String start = findFirstEdge();

        if (start == null)
            return false;

        dfs(start, visited);

        int totalEdges = countEdges();

        return visited.size() == totalEdges;
    }

    public boolean checkPuzzle() {

        return validateDegrees()
                && validateClues()
                && isSingleLoop();
    }

    //Puzzle Generator Basic Version
    public static int[][] generate(int size){

        Random random = new Random();

        int[][] puzzle = new int[size][size];

        for(int r=0;r<size;r++){
            for(int c=0;c<size;c++){

                if(random.nextBoolean())
                    puzzle[r][c]=random.nextInt(4);
                else
                    puzzle[r][c]=-1;
            }
        }

        return puzzle;
    }

    public enum Difficulty {

        EASY,
        MEDIUM,
        HARD
    }

    switch (difficulty){

        case EASY:
            size = 5;
            break;

        case MEDIUM:
            size = 8;
            break;

        case HARD:
            size = 12;
            break;
    }


    public void giveHint(){

        for(int r=0;r<horizontal.length;r++){

            for(int c=0;c<horizontal[0].length;c++){

                if(solutionHorizontal[r][c]
                        && !horizontal[r][c]){

                    horizontal[r][c]=true;

                    invalidate();

                    return;
                }
            }
        }
    }

    public void undo(){

        if(undoStack.isEmpty())
            return;

        Move move=undoStack.pop();

        horizontal[move.row][move.col]
                = move.previousState;

        redoStack.push(move);

        invalidate();
    }

    private void showWinDialog() {

        AlertDialog.Builder builder =
                new AlertDialog.Builder(this);

        builder.setTitle("Congratulations!");

        builder.setMessage(
                "Puzzle Solved\n\nScore: "
                        + score);

        builder.setPositiveButton(
                "New Game",
                (d,w)->startNewGame());

        builder.show();
    }

}