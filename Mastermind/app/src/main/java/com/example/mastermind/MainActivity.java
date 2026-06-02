package com.example.mastermind;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class MainActivity extends AppCompatActivity {


    EditText etGuess;

    TextView txtResult;
    TextView txtHistory;

    private int[] guessColors;
    private int exactMatches;
    private int partialMatches;

    private RecyclerView historyRecycler;

    private ArrayList<HistoryItem> historyList;

    private HistoryAdapter historyAdapter;

    private static final int RED = 1;
    private static final int BLUE = 2;
    private static final int GREEN = 3;
    private static final int YELLOW = 4;
    private static final int PURPLE = 5;
    private static final int ORANGE = 6;
    private static final int CYAN = 7;
    private static final int PINK = 8;
    private static final int BROWN = 9;
    private static final int GRAY = 10;

    private Spinner spinnerDifficulty;
    private Switch switchDuplicate;
    private TextView txtAttempts;
    private TextView txtScore;

    private Button btnCheck;
    private Button btnUndo;
    private Button btnRestart;

    private GridLayout feedbackGrid;

    private int currentIndex = 0;

    private int[] playerGuess = new int[4];

    private int[] secretCode = new int[4];

    private int attemptsLeft = 12;

    private int score = 0;

    private LinearLayout guessRow;

    private ArrayList<ImageView> slots =
            new ArrayList<>();

    private int codeLength = 4;

    private int colorCount = 6;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinnerDifficulty = findViewById(R.id.spinnerDifficulty);
        txtAttempts = findViewById(R.id.txtAttempts);
        txtScore = findViewById(R.id.txtScore);
        btnCheck = findViewById(R.id.btnCheck);
        btnUndo = findViewById(R.id.btnUndo);
        btnRestart = findViewById(R.id.btnRestart);
        feedbackGrid = findViewById(R.id.feedbackGrid);
        guessRow = findViewById(R.id.guessRow);
        switchDuplicate = findViewById(R.id.switchDuplicate);

        txtScore.setText("Score: 0");

        String[] levels = {
                "Easy",
                "Medium",
                "Hard"
        };

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_dropdown_item,
                        levels);

        spinnerDifficulty.setAdapter(adapter);

        startNewGame();

        spinnerDifficulty.setOnItemSelectedListener(
                new android.widget.AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            android.widget.AdapterView<?> parent,
                            android.view.View view,
                            int position,
                            long id) {

                        startNewGame();
                    }

                    @Override
                    public void onNothingSelected(
                            android.widget.AdapterView<?> parent) {
                    }
                });

        findViewById(R.id.redBtn)
                .setOnClickListener(v ->
                        addColor(RED));

        findViewById(R.id.blueBtn)
                .setOnClickListener(v ->
                        addColor(BLUE));

        findViewById(R.id.greenBtn)
                .setOnClickListener(v ->
                        addColor(GREEN));

        findViewById(R.id.yellowBtn)
                .setOnClickListener(v ->
                        addColor(YELLOW));

        findViewById(R.id.purpleBtn)
                .setOnClickListener(v ->
                        addColor(PURPLE));

        findViewById(R.id.orangeBtn)
                .setOnClickListener(v ->
                        addColor(ORANGE));

        historyRecycler =
                findViewById(R.id.historyRecycler);

        historyList = new ArrayList<>();

        historyAdapter =
                new HistoryAdapter(historyList);

        historyRecycler.setLayoutManager(
                new LinearLayoutManager(this));

        historyRecycler.setAdapter(
                historyAdapter);

        startNewGame();
//        ImageView peg = new ImageView(this);
//
//        if(exactMatch){
//            peg.setBackgroundColor(Color.BLACK);
//        }
//        else{
//            peg.setBackgroundColor(Color.WHITE);
//        }

        if(savedInstanceState != null){

            score =
                    savedInstanceState
                            .getInt("score");

            attemptsLeft =
                    savedInstanceState
                            .getInt("attempts");

            txtScore.setText(
                    "Score: " + score);

            txtAttempts.setText(
                    "Attempts: "
                            + attemptsLeft);
        }


        btnUndo.setOnClickListener(v -> {

            if(currentIndex > 0){

                currentIndex--;

                slots.get(currentIndex)
                        .setBackgroundResource(
                                R.drawable.peg_empty);

                playerGuess[currentIndex] = 0;
            }
        });

        Button btnRestart =
                findViewById(R.id.btnRestart);

        btnCheck.setOnClickListener(v ->
                checkGuess());

        btnUndo.setOnClickListener(v ->
                undoMove());

        btnRestart.setOnClickListener(v ->
                startNewGame());

        showFeedback(exactMatches, partialMatches);
    }

    private void showFeedback(
            int exactMatches,
            int partialMatches) {

        feedbackGrid.removeAllViews();

        // Black Pegs
        for(int i = 0; i < exactMatches; i++) {

            ImageView peg = new ImageView(this);

            peg.setBackgroundResource(
                    R.drawable.feedback_black);

            GridLayout.LayoutParams params =
                    new GridLayout.LayoutParams();

            params.width = 25;
            params.height = 25;

            peg.setLayoutParams(params);

            feedbackGrid.addView(peg);
        }

        // White Pegs
        for(int i = 0; i < partialMatches; i++) {

            ImageView peg = new ImageView(this);

            peg.setBackgroundResource(
                    R.drawable.feedback_white);

            GridLayout.LayoutParams params =
                    new GridLayout.LayoutParams();

            params.width = 25;
            params.height = 25;

            peg.setLayoutParams(params);

            feedbackGrid.addView(peg);
        }
    }

    private void createSlots() {

        guessRow.removeAllViews();

        slots.clear();

        for(int i=0;i<codeLength;i++) {

            ImageView peg =
                    new ImageView(this);

//            LinearLayout.LayoutParams params =
//                    new LinearLayout.LayoutParams(
//                            90,
//                            90);
            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(
                            getResources()
                                    .getDimensionPixelSize(
                                            R.dimen.peg_size),

                            getResources()
                                    .getDimensionPixelSize(
                                            R.dimen.peg_size));

            params.setMargins(
                    5,5,5,5);

            peg.setLayoutParams(params);

            peg.setBackgroundResource(
                    R.drawable.peg_empty);

            final int index = i;

            peg.setOnLongClickListener(v -> {

                playerGuess[index] = 0;

                ((ImageView) v).setBackgroundResource(
                        R.drawable.peg_empty);

                return true;
            });

            guessRow.addView(peg);

            slots.add(peg);
        }

        playerGuess =
                new int[codeLength];

        secretCode =
                new int[codeLength];
    }

    private void startNewGame() {

        String level =
                spinnerDifficulty
                        .getSelectedItem()
                        .toString();

        switch(level){

            case "Easy":

                codeLength = 4;
                colorCount = 6;
                attemptsLeft = 12;

                break;

            case "Medium":

                codeLength = 5;
                colorCount = 8;
                attemptsLeft = 10;

                break;

            default:

                codeLength = 6;
                colorCount = 10;
                attemptsLeft = 8;

                break;
        }

        createSlots();

        generateSecretCode();

        currentIndex = 0;

        txtAttempts.setText(
                "Attempts: "
                        + attemptsLeft);

        clearHistory();

        feedbackGrid.removeAllViews();
    }

    private void generateSecretCode() {

        Random random =
                new Random();

        if(switchDuplicate.isChecked()){

            for(int i=0;i<codeLength;i++){

                secretCode[i] =
                        random.nextInt(
                                colorCount)+1;
            }
        }
        else{

            ArrayList<Integer> colors =
                    new ArrayList<>();

            for(int i=1;
                i<=colorCount;
                i++){

                colors.add(i);
            }

            Collections.shuffle(colors);

            for(int i=0;
                i<codeLength;
                i++){

                secretCode[i] =
                        colors.get(i);
            }
        }
    }

    //    Secret Code Reveal
    private String secretCodeToString(){

        StringBuilder sb =
                new StringBuilder();

        for(int value : secretCode){

            sb.append(value);
        }

        return sb.toString();
    }

    private void addColor(int color){

        if(currentIndex >= codeLength)
            return;

        playerGuess[currentIndex]
                = color;

        switch(color){

            case RED:
                slots.get(currentIndex)
                        .setBackgroundResource(
                                R.drawable.peg_red);
                break;

            case BLUE:
                slots.get(currentIndex)
                        .setBackgroundResource(
                                R.drawable.peg_blue);
                break;

            case GREEN:
                slots.get(currentIndex)
                        .setBackgroundResource(
                                R.drawable.peg_green);
                break;

            case YELLOW:
                slots.get(currentIndex)
                        .setBackgroundResource(
                                R.drawable.peg_yellow);
                break;

            case PURPLE:
                slots.get(currentIndex)
                        .setBackgroundResource(
                                R.drawable.peg_purple);
                break;

            case ORANGE:
                slots.get(currentIndex)
                        .setBackgroundResource(
                                R.drawable.peg_orange);
                break;
        }

        //Peg Click Animation
//        slots.get(currentIndex)
//                .animate()
//                .scaleX(1.15f)
//                .scaleY(1.15f)
//                .setDuration(120)
//                .withEndAction(() ->
//
//                        slots.get(currentIndex)
//                                .animate()
//                                .scaleX(1f)
//                                .scaleY(1f)
//                                .setDuration(120)
//                );

        currentIndex++;
    }

    private void undoMove(){

        if(currentIndex <= 0)
            return;

        currentIndex--;

        playerGuess[currentIndex] = 0;

        slots.get(currentIndex)
                .setBackgroundResource(
                        R.drawable.peg_empty);

        //Undo Animation
        slots.get(currentIndex)
                .animate()
                .alpha(0f)
                .setDuration(150)
                .withEndAction(() -> {

                    slots.get(currentIndex)
                            .setBackgroundResource(
                                    R.drawable.peg_empty);

                    slots.get(currentIndex)
                            .setAlpha(1f);
                });
    }

    private void checkGuess(){

        if(currentIndex < codeLength)
            return;

        int exactMatches = 0;
        int partialMatches = 0;

        boolean[] secretUsed =
                new boolean[codeLength];

        boolean[] guessUsed =
                new boolean[codeLength];

        for(int i=0;i<codeLength;i++){

            if(playerGuess[i]
                    == secretCode[i]){

                exactMatches++;

                secretUsed[i] = true;

                guessUsed[i] = true;
            }
        }

        for(int i=0;i<codeLength;i++){

            if(guessUsed[i])
                continue;

            for(int j=0;j<codeLength;j++){

                if(secretUsed[j])
                    continue;

                if(playerGuess[i]
                        == secretCode[j]){

                    partialMatches++;

                    secretUsed[j] = true;

                    break;
                }
            }
        }

        showFeedback(
                exactMatches,
                partialMatches);


        addHistory(
                playerGuess.clone(),
                exactMatches,
                partialMatches);

        attemptsLeft--;

        txtAttempts.setText(
                "Attempts: "
                        + attemptsLeft);

        if(exactMatches == codeLength){

            switch(codeLength){

                case 4:
                    score += 100;
                    break;

                case 5:
                    score += 200;
                    break;

                case 6:
                    score += 300;
                    break;
            }
            //Fast win bonus:
            score += attemptsLeft * 10;
            showWinDialog();
            return;
        }

        if(attemptsLeft <= 0){
            showLoseDialog();
            return;
        }

        clearGuess();
    }

    private void clearGuess(){

        currentIndex = 0;

        for(int i=0;i<codeLength;i++){

            playerGuess[i] = 0;

            slots.get(i)
                    .setBackgroundResource(
                            R.drawable.peg_empty);
        }
    }

    private void addHistory(
            int[] guessColors,
            int exactMatches,
            int partialMatches){

        historyList.add(
                new HistoryItem(
                        guessColors,
                        exactMatches,
                        partialMatches));

        historyAdapter.notifyItemInserted(
                historyList.size()-1);
    }

    private void clearHistory() {

        if(historyList != null){

            historyList.clear();
        }

        if(historyAdapter != null){

            historyAdapter.notifyDataSetChanged();
        }
    }

    private int getPegDrawable(
            int colorId){

        switch(colorId){

            case 1:
                return R.drawable.peg_red;

            case 2:
                return R.drawable.peg_blue;

            case 3:
                return R.drawable.peg_green;

            case 4:
                return R.drawable.peg_yellow;

            case 5:
                return R.drawable.peg_purple;

            case 6:
                return R.drawable.peg_orange;

            case 7:
                return R.drawable.peg_cyan;

            case 8:
                return R.drawable.peg_pink;

            case 9:
                return R.drawable.peg_brown;

            case 10:
                return R.drawable.peg_gray;
        }

        return R.drawable.peg_empty;
    }

////    private void showFeedback(
////            int exactMatches,
////            int partialMatches){
////
////        feedbackGrid.removeAllViews();
//    public void onBindViewHolder(
//                RecyclerView.ViewHolder holder,
//        int position) {
//
//            HistoryItem item =
//                    historyList.get(position);
//
//            holder.guessContainer.removeAllViews();
//
//            holder.feedbackContainer
//                    .removeAllViews();
//
//            int[] colors =
//                    item.getGuessColors();
//
//        // Guess Pegs
//
//        for(int color : colors){
//
//            ImageView peg =
//                    new ImageView(
//                            holder.itemView
//                                    .getContext());
//
//            LinearLayout.LayoutParams params =
//                    new LinearLayout.LayoutParams(
//                            50,
//                            50);
//
//            params.setMargins(
//                    4,4,4,4);
//
//            peg.setLayoutParams(params);
//
//            peg.setBackgroundResource(
//                    getPegDrawable(color));
//
//            holder.guessContainer
//                    .addView(peg);
//        }
//
//        //black feedback
//         for(int i=0;
//                i<item.getExactMatches();
//                i++){
//
//                ImageView peg =
//                        new ImageView(
//                                holder.itemView
//                                        .getContext());
//
//                peg.setBackgroundResource(
//                        R.drawable.feedback_black);
//
//                GridLayout.LayoutParams params =
//                        new GridLayout.LayoutParams();
//
//                params.width = 18;
//                params.height = 18;
//
//                peg.setLayoutParams(params);
//
//                holder.feedbackContainer
//                        .addView(peg);
//            }
////        for(int i=0;i<exactMatches;i++){
////
////            ImageView peg =
////                    new ImageView(this);
////
////            peg.setBackgroundResource(
////                    R.drawable.feedback_black);
////
////            GridLayout.LayoutParams params =
////                    new GridLayout.LayoutParams();
////
////            params.width = 25;
////            params.height = 25;
////
////            peg.setLayoutParams(params);
////
////            feedbackGrid.addView(peg);
////
////            // Feedback Peg Animation
////            peg.setScaleX(0f);
////            peg.setScaleY(0f);
////
////            peg.animate()
////                    .scaleX(1f)
////                    .scaleY(1f)
////                    .setDuration(250);
////        }
//
//        //white feedback
//        for(int i=0;
//            i<item.getPartialMatches();
//            i++){
//
//            ImageView peg =
//                    new ImageView(
//                            holder.itemView
//                                    .getContext());
//
//            peg.setBackgroundResource(
//                    R.drawable.feedback_white);
//
//            GridLayout.LayoutParams params =
//                    new GridLayout.LayoutParams();
//
//            params.width = 18;
//            params.height = 18;
//
//            peg.setLayoutParams(params);
//
//            holder.feedbackContainer
//                    .addView(peg);
//        }
////        for(int i=0;i<partialMatches;i++){
////
////            ImageView peg =
////                    new ImageView(this);
////
////            peg.setBackgroundResource(
////                    R.drawable.feedback_white);
////
////            GridLayout.LayoutParams params =
////                    new GridLayout.LayoutParams();
////
////            params.width = 25;
////            params.height = 25;
////
////            peg.setLayoutParams(params);
////
////            feedbackGrid.addView(peg);
////
////            // Feedback Peg Animation
////            peg.setScaleX(0f);
////            peg.setScaleY(0f);
////
////            peg.animate()
////                    .scaleX(1f)
////                    .scaleY(1f)
////                    .setDuration(250);
////        }
//
//
//    }

    private String guessToString(){

        StringBuilder sb =
                new StringBuilder();

        for(int value : playerGuess){

            sb.append(value);
        }

        return sb.toString();
    }

    private void showWinDialog(){

        new AlertDialog.Builder(this)

                .setTitle("You Win!")

                .setMessage(
                        "Secret Code: "
                                + secretCodeToString())

                .setPositiveButton(
                        "New Game",
                        (d,w)->startNewGame())

                .show();
    }

    private void showLoseDialog(){

        new AlertDialog.Builder(this)
                .setTitle("Game Over")
                .setMessage(
                        "Secret Code\n\n"
                                + secretCodeToString())
                .setCancelable(false)
                .setPositiveButton(
                        "New Game",
                        (d,w)->startNewGame())
                .show();
    }

    @Override
    protected void onSaveInstanceState(
            Bundle outState) {

        super.onSaveInstanceState(
                outState);

        outState.putInt(
                "score",
                score);

        outState.putInt(
                "attempts",
                attemptsLeft);
    }

    //Secret Code Preview Dialog, Debug mode के लिए:
    private void showSecretCode() {

        new AlertDialog.Builder(this)

                .setTitle("Secret")

                .setMessage(
                        secretCodeToString())

                .show();
    }
}