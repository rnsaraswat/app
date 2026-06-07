package com.example.ravindragameshub.mastermind;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
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

import com.example.ravindragameshub.R;
import com.example.ravindragameshub.common.SoundManager;
import com.example.ravindragameshub.common.ThemeManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class MastermindActivity extends AppCompatActivity{

    private int[] guessColors;
    private int exactMatches;
    private int partialMatches;

    private RecyclerView historyRecycler;

    private ArrayList<MastermindHistoryItem> historyList;

    private MastermindHistoryAdapter historyAdapter;

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
    private int level;
    private boolean allowDuplicates;
    private TextView txtAttempts;
    private TextView txtScore;
    private TextView txtDuplicateStatus;

    private Button btnCheck;
    //    private Button btnUndo;
    private Button btnRules;
    private Button btnRestart;

    private GridLayout feedbackGrid;
    LinearLayout rootLayout;

    private int selectedSlot = -1;

    private int[] playerGuess = new int[4];

    private int[] secretCode = new int[4];

    private int attemptsLeft = 12;
    private int maxAttempts = 10;
    private int attemptsUsed = 0;

    private int score = 0;

    private LinearLayout guessRow;

    private ArrayList<TextView> slots =
            new ArrayList<>();

    private int codeLength = 4;

    private View cyanBtn;
    private View pinkBtn;
    private View brownBtn;
    private View grayBtn;

    private TextView statusText;

    private LinearLayout secretCodeRow;

    private int colorCount = 6;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mastermind);

        rootLayout = findViewById(R.id.rootLayout);

        // Theme Apply
        ThemeManager.applyTheme(
                this,
                rootLayout
        );

        spinnerDifficulty = findViewById(R.id.spinnerDifficulty);
        txtAttempts = findViewById(R.id.txtAttempts);
        txtScore = findViewById(R.id.txtScore);
        btnCheck = findViewById(R.id.btnCheck);
        btnRules = findViewById(R.id.btnRules);
        btnRestart = findViewById(R.id.btnRestart);
        feedbackGrid = findViewById(R.id.feedbackGrid);
        guessRow = findViewById(R.id.guessRow);
        switchDuplicate = findViewById(R.id.switchDuplicate);
        txtDuplicateStatus = findViewById(R.id.txtDuplicateStatus);
        statusText = findViewById(R.id.statusText);
        secretCodeRow = findViewById( R.id.secretCodeRow);

        cyanBtn = findViewById(R.id.cyanBtn);
        pinkBtn = findViewById(R.id.pinkBtn);
        brownBtn = findViewById(R.id.brownBtn);
        grayBtn = findViewById(R.id.grayBtn);

        txtScore.setText("Score: 0");

        Button btnHome =
                findViewById(R.id.btnHome);

        Button btnShare =
                findViewById(R.id.btnShare);

        btnHome.setOnClickListener(v -> {
            SoundManager.playClick();
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

            startActivity(
                    Intent.createChooser(
                            shareIntent,
                            "Share App"
                    )
            );
        });

        switchDuplicate.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    allowDuplicates = isChecked;
                    SoundManager.playClick();
                    txtDuplicateStatus.setText(
                            isChecked
                                    ? "Allowed"
                                    : "Not Allowed");

                    statusText.setText(
                            isChecked
                                    ? "Duplicate Colors Allowed"
                                    : "Duplicate Colors Not Allowed");

                    startNewGame();
                });

        String[] levels = {
                "Easy - 4 slots, 6 colors",
                "Medium - 5 slots, 8 colors",
                "Hard - 6 slots, 10 colors"
        };

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_dropdown_item,
                        levels);

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);

        spinnerDifficulty.setAdapter(adapter);

//        startNewGame();

        spinnerDifficulty.setOnItemSelectedListener(
                new android.widget.AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            android.widget.AdapterView<?> parent,
                            android.view.View view,
                            int position,
                            long id) {

                        SoundManager.playClick();
                        level = position;
                        statusText.setText("Level Changed : " + level);
                        switch(position){
                            case 0:
                                codeLength = 4;
                                colorCount = 6;
                                statusText.setText(
                                        "Easy Level Selected");
                                break;

                            case 1:
                                codeLength = 5;
                                colorCount = 8;
                                statusText.setText(
                                        "Medium Level Selected");
                                break;

                            case 2:
                                codeLength = 6;
                                colorCount = 10;
                                statusText.setText(
                                        "Hard Level Selected");
                                break;
                        }
                        startNewGame();
                    }

                    @Override
                    public void onNothingSelected(
                            android.widget.AdapterView<?> parent) {
                    }
                });

        statusText.setText(
                switchDuplicate.isChecked()
                        ? "Duplicates Allowed"
                        : "Duplicates Not Allowed");

        findViewById(R.id.redBtn)
                .setOnClickListener(v -> {
                            SoundManager.playClick();
                            addColor(RED);});

        findViewById(R.id.blueBtn)
                .setOnClickListener(v -> {
                        SoundManager.playClick();
                        addColor(BLUE);});

        findViewById(R.id.greenBtn)
                .setOnClickListener(v -> {
                        SoundManager.playClick();
                        addColor(GREEN);});

        findViewById(R.id.yellowBtn)
                .setOnClickListener(v -> {
                        SoundManager.playClick();
                        addColor(YELLOW);});

        findViewById(R.id.purpleBtn)
                .setOnClickListener(v -> {
                        SoundManager.playClick();
                        addColor(PURPLE);});

        findViewById(R.id.orangeBtn)
                .setOnClickListener(v -> {
                        SoundManager.playClick();
                        addColor(ORANGE);});

        findViewById(R.id.cyanBtn)
                .setOnClickListener(v -> {
                        SoundManager.playClick();
                        addColor(CYAN);});

        findViewById(R.id.pinkBtn)
                .setOnClickListener(v -> {
                        SoundManager.playClick();
                        addColor(PINK);});

        findViewById(R.id.brownBtn)
                .setOnClickListener(v -> {
                        SoundManager.playClick();
                        addColor(BROWN);});

        findViewById(R.id.grayBtn)
                .setOnClickListener(v -> {
                        SoundManager.playClick();
                        addColor(GRAY);});

        historyRecycler =
                findViewById(R.id.historyRecycler);

        historyList = new ArrayList<>();

        historyAdapter =
                new MastermindHistoryAdapter(historyList);

        historyRecycler.setLayoutManager(
                new LinearLayoutManager(this));

        historyRecycler.setAdapter(
                historyAdapter);

        startNewGame();

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


        cyanBtn.setOnClickListener(v -> {
            SoundManager.playClick();
            addColor(7);});
        pinkBtn.setOnClickListener(v -> {
                SoundManager.playClick();
                addColor(8);});
        brownBtn.setOnClickListener(v -> {
                SoundManager.playClick();
                addColor(9);});
        grayBtn.setOnClickListener(v -> {
                SoundManager.playClick();
                addColor(10);});

        Button btnRestart =
                findViewById(R.id.btnRestart);

        btnCheck.setOnClickListener(v -> {
                SoundManager.playClick();
                checkGuess();});

        btnRestart.setOnClickListener(v -> {
            SoundManager.playClick();
                startNewGame();});

        btnRules.setOnClickListener(
                v -> {
                    SoundManager.playClick();
                    showRulesDialog();
                });

        showFeedback(exactMatches, partialMatches);
    }

    private void showFeedback(
            int exactMatches,
            int partialMatches) {

        feedbackGrid.removeAllViews();

        // Black Pegs
        for(int i = 0; i < exactMatches; i++) {

//            ImageView peg = new ImageView(this);

            TextView peg = new TextView(this);

            peg.setGravity(Gravity.CENTER);

            peg.setTextSize(18);

            peg.setTextColor(Color.WHITE);

            peg.setBackgroundResource(
                    R.drawable.mm_peg_circle);

            peg.setBackgroundResource(
                    R.drawable.mm_feedback_black);

            GridLayout.LayoutParams params =
                    new GridLayout.LayoutParams();

            params.width = 25;
            params.height = 25;

            peg.setLayoutParams(params);

            feedbackGrid.addView(peg);
        }

        // White Pegs
        for(int i = 0; i < partialMatches; i++) {

//            ImageView peg = new ImageView(this);

            TextView peg = new TextView(this);

            peg.setGravity(Gravity.CENTER);

            peg.setTextSize(18);

            peg.setTextColor(Color.WHITE);

            peg.setBackgroundResource(
                    R.drawable.mm_peg_circle);

            peg.setBackgroundResource(
                    R.drawable.mm_feedback_white);

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
        TextView peg = new TextView(this);

        peg.setGravity(Gravity.CENTER);

        peg.setTextSize(18);

        peg.setTextColor(Color.WHITE);

        peg.setBackgroundResource(
                R.drawable.mm_peg_circle);

        slots.clear();

        for(int i=0;i<codeLength;i++) {

//            ImageView peg =
//                    new ImageView(this);

            peg = new TextView(this);

            peg.setGravity(Gravity.CENTER);

            peg.setTextSize(18);

            peg.setTextColor(Color.WHITE);

            peg.setBackgroundResource(
                    R.drawable.mm_peg_circle);

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
                    R.drawable.mm_peg_empty);

            final int index = i;

            peg.setClickable(true);

            peg.setOnClickListener(v -> {
                SoundManager.playClick();
                selectSlot(index);
                updateSlotViews();
            });

            peg.setOnLongClickListener(v -> {

                playerGuess[index] = 0;
                SoundManager.playError();
                statusText.setText(
                        "Guess all Slots : slot " + index);
//
//                ((ImageView) v).setBackgroundResource(
//                        R.drawable.peg_empty);
                ((TextView) v).setBackgroundResource(
                        R.drawable.mm_peg_empty);

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


    private void selectSlot(int index){

        statusText.setText(
                "Selected Slot "
                        + (index + 1));

        selectedSlot = index;

        updateSlotViews();
    }

    private void startNewGame() {

        statusText.setText(
                "New Game Started");

        switch(level){

            case 0:

                codeLength = 4;
                colorCount = 6;
                maxAttempts = 12;

                break;

            case 1:

                codeLength = 5;
                colorCount = 8;
                maxAttempts = 10;

                break;

            default:

                codeLength = 6;
                colorCount = 10;
                maxAttempts = 8;

                break;
        }

        createSlots();

        updateColorButtons();

        generateSecretCode();

        selectedSlot = 0;

        attemptsUsed = 0;

        txtAttempts.setText(
                "Attempts: "
                        + maxAttempts + "/" + maxAttempts);

        clearHistory();

        secretCodeRow.removeAllViews();
        feedbackGrid.removeAllViews();
        //Secret Code Preview Dialog, for Debug mode secretCodeToString()
//        statusText.setText("Make Your Guess " + secretCodeToString());
        statusText.setText("Make Your Guess ");
    }

    private void generateSecretCode() {

        Random random = new Random();

//        if(switchDuplicate.isChecked()){
        if(allowDuplicates){
            for(int i=0;i<codeLength;i++){
                secretCode[i] = random.nextInt(colorCount)+1;
            }
        }
        else{
            ArrayList<Integer> colors = new ArrayList<>();

            for(int i=1; i<=colorCount; i++){
                colors.add(i);
            }

            Collections.shuffle(colors);

            for(int i=0; i<codeLength; i++){
                secretCode[i] = colors.get(i);
            }
        }
    }

    // Secret Code display numbers in text
    private String secretCodeToString(){

        StringBuilder sb = new StringBuilder();

        for(int value : secretCode){
            sb.append(value);
        }
        return sb.toString();
    }

    //add colour to slot
    private void addColor(int color){

        if(selectedSlot == -1) {
            SoundManager.playError();
            statusText.setText("Select slot first ");
            return;
        }

        statusText.setText("Slot " + (selectedSlot + 1)
                        + " = Color " + color);

        playerGuess[selectedSlot] = color;

        updateSlotViews();
    }

    //update slt view
    private void updateSlotViews(){

        for(int i=0;i<codeLength;i++){

            TextView peg = slots.get(i);

            if(playerGuess[i] == 0){

                peg.setText("");

                peg.setBackgroundResource(
                        R.drawable.mm_peg_circle);
                peg.getBackground()
                        .setTint(Color.LTGRAY);

                slots.get(i).setText("");

                slots.get(i)
                        .setBackgroundTintList(
                                ColorStateList.valueOf(
                                        Color.LTGRAY));
            }
            else{

                peg.setText(
                        String.valueOf(
                                playerGuess[i]));

                switch(playerGuess[i]){

                    case 1:
                        peg.setBackgroundResource(
                                R.drawable.mm_peg_circle);
                        peg.getBackground()
                                .setTint(Color.RED);
                        break;

                    case 2:
                        peg.setBackgroundResource(
                                R.drawable.mm_peg_circle);
                        peg.getBackground()
                                .setTint(Color.BLUE);
                        break;

                    case 3:
                        peg.setBackgroundResource(
                                R.drawable.mm_peg_circle);
                        peg.getBackground()
                                .setTint(Color.GREEN);
                        break;

                    case 4:
                        peg.setBackgroundResource(
                                R.drawable.mm_peg_circle);
                        peg.getBackground()
                                .setTint(Color.YELLOW);

                        peg.setTextColor(
                                Color.BLACK);
                        break;

                    case 5:
                        peg.setBackgroundResource(
                                R.drawable.mm_peg_circle);
                        peg.getBackground()
                                .setTint(Color.MAGENTA);
                        break;

                    case 6:
                        peg.setBackgroundResource(
                                R.drawable.mm_peg_circle);
                        peg.getBackground()
                                .setTint(0xFFFF9800);
                        break;

                    case 7:

                        peg.setBackgroundResource(
                                R.drawable.mm_peg_circle);

                        peg.getBackground()
                                .setTint(Color.CYAN);

                        break;

                    case 8:

                        peg.setBackgroundResource(
                                R.drawable.mm_peg_circle);

                        peg.getBackground()
                                .setTint(0xFFE91E63);

                        break;

                    case 9:

                        peg.setBackgroundResource(
                                R.drawable.mm_peg_circle);

                        peg.getBackground()
                                .setTint(0xFF795548);

                        break;

                    case 10:

                        peg.setBackgroundResource(
                                R.drawable.mm_peg_circle);

                        peg.getBackground()
                                .setTint(Color.GRAY);

                        break;
                }
            }
        }

        //remove select cell border
        for(int i=0;i<slots.size();i++){
            slots.get(i)
                    .setForeground(null);
        }


        if(selectedSlot >= 0){
            slots.get(selectedSlot)
                    .setForeground(
                            getDrawable(
                                    R.drawable.mm_selected_slot_border));
        }
    }

    private void setPegColor(
            TextView peg,
            int colorNumber){

        peg.setBackgroundResource(
                R.drawable.mm_peg_circle);

        peg.setTextColor(
                Color.WHITE);

        switch(colorNumber){

            case 1:
                peg.setBackgroundTintList(
                        ColorStateList.valueOf(
                                Color.RED));
                break;

            case 2:
                peg.setBackgroundTintList(
                        ColorStateList.valueOf(
                                Color.BLUE));
                break;

            case 3:
                peg.setBackgroundTintList(
                        ColorStateList.valueOf(
                                Color.GREEN));
                break;

            case 4:
                peg.setBackgroundTintList(
                        ColorStateList.valueOf(
                                Color.YELLOW));

                peg.setTextColor(
                        Color.BLACK);
                break;

            case 5:
                peg.setBackgroundTintList(
                        ColorStateList.valueOf(
                                Color.MAGENTA));
                break;

            case 6:
                peg.setBackgroundTintList(
                        ColorStateList.valueOf(
                                0xFFFF9800));
                break;

            case 7:
                peg.setBackgroundTintList(
                        ColorStateList.valueOf(
                                Color.CYAN));
                break;

            case 8:
                peg.setBackgroundTintList(
                        ColorStateList.valueOf(
                                0xFFE91E63));
                break;

            case 9:
                peg.setBackgroundTintList(
                        ColorStateList.valueOf(
                                0xFF795548));
                break;

            case 10:
                peg.setBackgroundTintList(
                        ColorStateList.valueOf(
                                Color.GRAY));
                break;
        }
    }

    //check guess
    private void checkGuess(){

        //check for empty slot
        for(int color : playerGuess){

            if(color == 0){
                SoundManager.playError();
                statusText.setText(
                        "Guess all Slots");
                return;
            }
        }

        exactMatches = 0;
        partialMatches = 0;

        boolean[] secretUsed = new boolean[codeLength];

        boolean[] guessUsed = new boolean[codeLength];

        for(int i=0;i<codeLength;i++){
            if(playerGuess[i] == secretCode[i]){
                exactMatches++;
                SoundManager.playRight();
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

                if(playerGuess[i] == secretCode[j]){

                    partialMatches++;
                    SoundManager.playWrong();
                    secretUsed[j] = true;

                    break;
                }
            }
        }

        showFeedback(exactMatches, partialMatches);

        addHistory(
                playerGuess.clone(),
                exactMatches,
                partialMatches);

        attemptsUsed++;

        txtAttempts.setText("Attempts: " + (maxAttempts - attemptsUsed)
                + "/" + maxAttempts);

        statusText.setText("Black: " + exactMatches
                        + "  White: " + partialMatches);

        if(exactMatches == codeLength){

            statusText.setText("🎉 You WIN! 🎉 Secret Code:");
            SoundManager.playWin();
            secretCodeRow.removeAllViews();
            showSecretCode();

            //level bonus
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
            score += (maxAttempts - attemptsUsed) * 10;
            txtScore.setText("Score: " + score);
            return;
        }

        if(attemptsUsed >= maxAttempts){
            statusText.setText("❌ You LOSE! Secret Code:");
            SoundManager.playLose();
            txtAttempts.setText("Attempts: "
                            + (maxAttempts - attemptsUsed)
                            + "/" + maxAttempts);
            secretCodeRow.removeAllViews();
            showSecretCode();
            return;
        }

        selectedSlot = -1;

        clearGuess();
    }

    //clear guess
    private void clearGuess(){

        for(int i=0;i<codeLength;i++){
            playerGuess[i] = 0;
        }

        selectedSlot = -1;

        updateSlotViews();
    }

    //add history
    private void addHistory(
            int[] guessColors,
            int exactMatches,
            int partialMatches){

        historyList.add(
                new MastermindHistoryItem(
                        guessColors,
                        exactMatches,
                        partialMatches));

        historyAdapter.notifyItemInserted(
                historyList.size()-1);
    }

    //remove history
    private void clearHistory() {

        if(historyList != null){
            historyList.clear();
        }

        if(historyAdapter != null){
            historyAdapter.notifyDataSetChanged();
        }
    }

    //update coloue buttons as per level
    private void updateColorButtons() {

        cyanBtn.setVisibility(View.GONE);
        pinkBtn.setVisibility(View.GONE);
        brownBtn.setVisibility(View.GONE);
        grayBtn.setVisibility(View.GONE);

        if(colorCount >= 8){
            cyanBtn.setVisibility(View.VISIBLE);
            pinkBtn.setVisibility(View.VISIBLE);
        }

        if(colorCount >= 10){
            brownBtn.setVisibility(View.VISIBLE);
            grayBtn.setVisibility(View.VISIBLE);
        }
    }

    //Show Secret Code Method in status
    private void showSecretCode(){

        for(int color : secretCode){

            TextView peg = new TextView(this);

            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams( getResources()
                                    .getDimensionPixelSize(
                                            R.dimen.peg_size), getResources()
                                    .getDimensionPixelSize(
                                            R.dimen.peg_size));

            params.setMargins(6,6,6,6);

            peg.setLayoutParams(params);

            peg.setGravity(Gravity.CENTER);

            peg.setText(String.valueOf(color));

            peg.setTextSize(18);

            setPegColor(peg, color);

            secretCodeRow.addView(peg);
        }
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

    //show rules dialog box
    private void showRulesDialog() {

        Dialog dialog = new Dialog(this);

        dialog.setContentView(R.layout.mm_rules_dialog);

        ImageButton closeBtn = dialog.findViewById(R.id.btnCloseRules);

        TextView txtRules = dialog.findViewById(R.id.txtRules);

        txtRules.setText(getRulesText());

        closeBtn.setOnClickListener(v -> {
            dialog.dismiss();
            SoundManager.playClick();
        });

        Window window = dialog.getWindow();

        if(window != null){

            DisplayMetrics dm =
                    getResources().getDisplayMetrics();

            int width = (int)(dm.widthPixels * 0.95);

            int height = (int)(dm.heightPixels * 0.95);

            window.setLayout(
                    width,
                    height);
        }

        dialog.show();
    }

    //rules
    private String getRulesText(){

        return
                "Mastermind is a classic code-breaking game for two players and Computer.\n\n" +
                "Computer acts as the Code Maker and the Player as the Code Breaker.\n\n" +
                "The objective is for the Player(Code Breaker) to guess the Code Maker’s hidden sequence of coloured pegs in limited number of attempts.\n\n" +
                "Guess slots: for choose guess colours for player.\n\n" +
                "Secret Code Slots: for display computer generated secret colours codes if player win or lose.\n\n" +
                "Colours slots: to choose colours in slots to find secret code.\n\n" +
                "Check Button: to check player guess colours in all slots.\n\n" +

                "Rules button: to see game rules (current window).\n\n" +
                "Restart button: to restart game - computer choose new secret code\n\n" +
                "Level: choose difficulty level\n\n" +
                "Easy Level:\n" +
                "- 4 slots\n" +
                "- 6 colours\n\n" +

                "Medium Level:\n" +
                "- 5 slots\n" +
                "- 8 colours\n\n" +

                "Hard Level:\n" +
                "- 6 slots\n" +
                "- 10 colours\n\n" +

                "Duplicate Allowed: If duplicates are allowed, the same colour may appear multiple times.\n\n" +

                "Win by finding the entire secret code with correct sequence. (all feedback peg are black)\n\n" +

                "Lose when secret code in correct sequence not find in given number of attempts\n\n" +

                "how to play\n\n" +

                "1. The computer generates a secret code and keeps hidden. (same Colours may or may not be appear multiple times).\n\n" +

                "2. Select a slot and choose a colour for this slot.\n\n" +

                "3. Fill all slots and press CHECK.\n\n" +

                "4. Black feedback peg = correct colour in correct position.\n\n" +

                "5. White feedback peg = correct colour but wrong position.\n";
    }
}
