package com.example.ravindragameshub;

// ==========================================
// MainActivity.java
// main activity - list of games - starting activity for All Games
// ==========================================

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.ravindragameshub.common.SoundManager;
import com.example.ravindragameshub.common.ThemeManager;
import com.example.ravindragameshub.connect4.Connect4Activity;
import com.example.ravindragameshub.fiveinarow.FiveinarowGameActivity;
import com.example.ravindragameshub.mastermind.MastermindActivity;
import com.example.ravindragameshub.reversi.ReversiActivity;
import com.example.ravindragameshub.tictactoe.TictactoeActivity;
import com.google.android.material.card.MaterialCardView;

public class MainActivity extends AppCompatActivity {

    //variables
    //local saved variable
    SharedPreferences prefs;
    Button btnShare, btnSignIn;
    TextView txtWelcome;

    //theme variables
    Spinner themeSpinner;
    LinearLayout rootLayout;
    String[] themes = {"🎨 Light","🌙 Dark","🔵 Blue","🟠 Orange"};
    LinearLayout headerLayout;
    private boolean isSpinnerInitialized = false;
    TextView headerTitle;
    boolean isFirstTime = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ThemeManager.getTheme(this);

        setContentView(R.layout.activity_main);

        View root = findViewById(android.R.id.content);

        ThemeManager.applyTheme(this, root);

        SoundManager.init(this);
        //variable to save local device
        prefs = getSharedPreferences("theme_data", MODE_PRIVATE);

        setupGameCard(
                R.id.cardTicTacToe,
                "Tic Tac Toe",
                "Classic X vs O Game",
                R.drawable.ic_tictactoe,
                TictactoeActivity.class
        );

        setupGameCard(
                R.id.cardReversi,
                "Reversi",
                "Flip The Discs",
                R.drawable.ic_tictactoe,
                ReversiActivity.class
        );

        setupGameCard(
                R.id.cardMemory,
                "Connect 4",
                "Endless ways to connect 4",
                R.drawable.ic_tictactoe,
                Connect4Activity.class
        );

        setupGameCard(
                R.id.cardFiveInARow,
                "Five In A Row",
                "It's not just luck, its strategy",
                R.drawable.ic_tictactoe,
                FiveinarowGameActivity.class
        );

        setupGameCard(
                R.id.cardMasterMind,
                "Master Mind",
                "Thinking without limits",
                R.drawable.ic_tictactoe,
                MastermindActivity.class
        );

        btnShare = findViewById(R.id.btnShare);
        btnSignIn = findViewById(R.id.btnSignIn);
        txtWelcome = findViewById(R.id.txtWelcome);
        ThemeManager.applyButtonTheme(btnShare, this);
        ThemeManager.applyButtonTheme(btnSignIn, this);

        //theme
        themeSpinner = findViewById(R.id.themeSpinner);
        rootLayout = findViewById(R.id.rootLayout);
        headerLayout = findViewById(R.id.headerLayout);
        headerTitle = findViewById(R.id.headerTitle);

        themeSpinner.setSelection(
                ThemeManager.currentTheme
        );

        ThemeManager.applySpinnerTheme(
                themeSpinner,
                this,
                themes);

        //Logo Animation
        ImageView headerLogo = findViewById(R.id.headerLogo);
        headerLogo.animate()
                .rotation(360)
                .setDuration(1200);

        // Animation load
        Animation fadeScale = AnimationUtils.loadAnimation(this, R.anim.fade_scale);

        //header
        TextView headerTitle = findViewById(R.id.headerTitle);
        headerTitle.animate().translationY(0).alpha(1).setDuration(800);
        // Load saved name
        String savedName = prefs.getString("player_name", "Guest");
        txtWelcome.setText("Welcome " + savedName);

        // check first time
        boolean isFirstTime = prefs.getBoolean("first_time", true);

        if (isFirstTime) {
            showNameDialog();
        } else {
            String name = prefs.getString("player_name", "Guest");
            txtWelcome.setText("Welcome " + name);
        }

        //Spinner Setup
        // Array multiple theme
//        String[] themes = {"🎨 Light","🌙 Dark","🔵 Blue Gradient","🟠 Orange Gradient"};
        // multiple theme addapter
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        themes
                );

        themeSpinner.setAdapter(adapter);

        // Load Saved Theme
        int currentTheme =
                ThemeManager.getTheme(this);

        for(int i=0; i<themes.length; i++) {

            if(themes[i].equals(currentTheme)) {

                themeSpinner.setSelection(i);
                break;
            }
        }

        // Apply Theme
        ThemeManager.applyTheme(this, rootLayout);

        // Theme Change
        themeSpinner.setOnItemSelectedListener(
                new android.widget.AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            android.widget.AdapterView<?> parent,
                            View view,
                            int position,
                            long id) {

                        SoundManager.playClick();

                        int selectedTheme = ThemeManager.THEME_LIGHT;

                        switch (position) {

                            case 0:
                                selectedTheme = ThemeManager.THEME_LIGHT;
                                break;

                            case 1:
                                selectedTheme = ThemeManager.THEME_DARK;
                                break;

                            case 2:
                                selectedTheme = ThemeManager.THEME_BLUE;
                                break;

                            case 3:
                                selectedTheme = ThemeManager.THEME_ORANGE;
                                break;
                        }

                        // SAME THEME → DO NOTHING
                        if (ThemeManager.currentTheme
                                == selectedTheme) {
                            return;
                        }

                        // SAVE THEME
                        ThemeManager.saveTheme(
                                MainActivity.this,
                                selectedTheme
                        );

                        // REFRESH ACTIVITY
                        recreate();
                    }

                    @Override
                    public void onNothingSelected(
                            android.widget.AdapterView<?> parent) {
                    }
                });

        //button share
        btnShare.setOnClickListener(v -> {
            SoundManager.playClick();
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, "Download Ravindra Games Hub!");
            startActivity(Intent.createChooser(intent, "Share via"));
        });

        //button signin
        btnSignIn.setOnClickListener(v -> {
            SoundManager.playClick();
            //change name
            showNameDialog();
        });
    }

    //Game Card Hover / Click Animations
    private void setupCardAnimation(LinearLayout card) {
        card.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    card.animate()
                            .scaleX(0.95f)
                            .scaleY(0.95f)
                            .setDuration(100);

                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    card.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(100);
                    break;
            }
            return false;
        });
    }

    //name enter method popup
    private void showNameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Your Name");

        final EditText input = new EditText(this);

        // show saved name
        String savedName = prefs.getString("player_name", "Guest");

        input.setText(savedName);
        input.setSelection(savedName.length());

        builder.setView(input);

        // Cancel button
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.dismiss();
        });

        // Save button
        builder.setPositiveButton("Save", (dialog, which) -> {

            String name = input.getText().toString().trim();

            if (name.isEmpty()) {
                name = "Guest";
            }

            final String finalName = name;
            // Save name
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("player_name", name);
            editor.putBoolean("first_time", false);
            editor.apply();

            // Update text instantly
//            txtWelcome.setText("Welcome " + finalName);
            txtWelcome.animate().alpha(0f).setDuration(150).withEndAction(() -> {
                txtWelcome.setText("Welcome " + finalName);
                txtWelcome.animate().alpha(1f).setDuration(150);
            });
        });

        builder.show();
    }



    //setup game card
    private void setupGameCard(
            int cardId,
            String title,
            String desc,
            int icon,
            Class<?> activityClass
    ) {

//        View card = findViewById(cardId);
        MaterialCardView cardView = findViewById(cardId);
        TextView titleView = cardView.findViewById(R.id.gameTitle);
        TextView descView = cardView.findViewById(R.id.gameDesc);
        ImageView iconView = cardView.findViewById(R.id.gameIcon);
//        MaterialCardView cardView = card.findViewById(R.id.gameCard);

        titleView.setText(title);
        descView.setText(desc);
        iconView.setImageResource(icon);

        // APPLY THEME
        ThemeManager.applyCardTheme(
                this,
                cardView,
                titleView,
                descView
        );

        // Animation
        cardView.setScaleX(0.8f);
        cardView.setScaleY(0.8f);
        cardView.setAlpha(0f);

        cardView.animate()
                .scaleX(1f)
                .scaleY(1f)
                .alpha(1f)
                .setDuration(500)
                .start();

        // Click
        cardView.setOnClickListener(v -> {

            SoundManager.playTap();
            // Click Animation
            v.animate()
                    .scaleX(0.95f)
                    .scaleY(0.95f)
                    .setDuration(100)
                    .withEndAction(() -> {


                        v.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(100)
                                .start();

                        startActivity(
                                new Intent(
                                        MainActivity.this,
                                        activityClass
                                )
                        );

                    }).start();

        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        SoundManager.release();
    }
}


