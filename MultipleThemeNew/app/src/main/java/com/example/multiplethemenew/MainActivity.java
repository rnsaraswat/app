package com.example.multiplethemenew;

import android.animation.ValueAnimator;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    //variables
    //local saved variable
    SharedPreferences prefs;
    LinearLayout cardTicTacToe, cardMemory, cardGomoku;
    Button btnShare, btnSignIn;
    TextView txtWelcome;

    //theme variables
    Spinner spinnerTheme;
    LinearLayout mainLayout;
    LinearLayout headerLayout;

    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //variable to save local device
        prefs = getSharedPreferences("settings", MODE_PRIVATE);

        cardTicTacToe = findViewById(R.id.cardTicTacToe);
        cardMemory = findViewById(R.id.cardMemory);
        cardGomoku = findViewById(R.id.cardGomoku);
        btnShare = findViewById(R.id.btnShare);
        btnSignIn = findViewById(R.id.btnSignIn);
        txtWelcome = findViewById(R.id.txtWelcome);

        //theme
        spinnerTheme = findViewById(R.id.spinnerTheme);
        mainLayout = findViewById(R.id.mainLayout);
        headerLayout = findViewById(R.id.headerLayout);
        title = findViewById(R.id.title);


        //Logo Animation
        ImageView logo = findViewById(R.id.logo);
        logo.animate()
                .rotation(360)
                .setDuration(1200);

        // Animation load
        Animation fadeScale = AnimationUtils.loadAnimation(this, R.anim.fade_scale);

//        prefs = getSharedPreferences("user_data", MODE_PRIVATE);

        cardTicTacToe.startAnimation(fadeScale);
        cardMemory.startAnimation(fadeScale);
        cardGomoku.startAnimation(fadeScale);

        TextView title = findViewById(R.id.title);
        title.animate().translationY(0).alpha(1).setDuration(800);
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

        cardTicTacToe.setOnClickListener(v -> {
            startActivity(new Intent(this, TicTacToeActivity.class));
        });

        cardTicTacToe.setOnClickListener(v -> {
            startActivity(new Intent(this, Connect4Activity.class));
        });

        cardTicTacToe.setOnClickListener(v -> {
            startActivity(new Intent(this, FiveInARowActivity.class));
        });

        cardTicTacToe.setOnClickListener(v -> {
            v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100)
                    .withEndAction(() -> {
                        v.animate().scaleX(1f).scaleY(1f).setDuration(100);
                        startActivity(new Intent(this, TicTacToeActivity.class));
                    });
        });

        // multiple theme
        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(
                        this,
                        R.array.themes,
                        android.R.layout.simple_spinner_item
                );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerTheme.setAdapter(adapter);
        int savedTheme = prefs.getInt("theme_index", 1);

        spinnerTheme.setSelection(savedTheme);

        applyTheme(savedTheme);

        spinnerTheme.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id) {

                        applyTheme(position);

                        prefs.edit()
                                .putInt("theme_index", position)
                                .apply();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

        btnShare.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, "Download Ravindra Games Hub!");
            startActivity(Intent.createChooser(intent, "Share via"));
        });

        btnSignIn.setOnClickListener(v -> {
            //change name
            showNameDialog();
        });

        //card animation
        setupCardAnimation(cardTicTacToe);
        setupCardAnimation(cardMemory);
        setupCardAnimation(cardGomoku);

        //RGB Glow Function
        startRGBGlow(cardTicTacToe);
        startRGBGlow(cardMemory);
        startRGBGlow(cardGomoku);
//        cardMemory.setOnClickListener(v -> {
//            startActivity(new Intent(this, MemoryGameActivity.class));
//        });
//
//        cardGomoku.setOnClickListener(v -> {
//            startActivity(new Intent(this, GomokuActivity.class));
//        });
    }

    //Multiple Theme apply
//    private void applyTheme(int theme) {
//
//        switch (theme) {
//
//            // 🎨 LIGHT
//            case 0:
//
//                mainLayout.setBackgroundColor(
//                        Color.WHITE);
//
//                txtWelcome.setTextColor(
//                        Color.BLACK);
//
//                break;
//
//            // 🌙 DARK
//            case 1:
//
//                mainLayout.setBackgroundColor(
//                        Color.parseColor("#020617"));
//
//                txtWelcome.setTextColor(
//                        Color.WHITE);
//
//                break;
//
//            // 🔵 BLUE
//            case 2:
//
//                GradientDrawable blueGradient =
//                        new GradientDrawable(
//                                GradientDrawable.Orientation.TOP_BOTTOM,
//                                new int[]{
//                                        Color.parseColor("#0f172a"),
//                                        Color.parseColor("#2563eb")
//                                });
//
//                mainLayout.setBackground(
//                        blueGradient);
//
//                txtWelcome.setTextColor(
//                        Color.WHITE);
//
//                break;
//
//            // 🟠 ORANGE
//            case 3:
//
//                GradientDrawable orangeGradient =
//                        new GradientDrawable(
//                                GradientDrawable.Orientation.TOP_BOTTOM,
//                                new int[]{
//                                        Color.parseColor("#7c2d12"),
//                                        Color.parseColor("#fb923c")
//                                });
//
//                mainLayout.setBackground(
//                        orangeGradient);
//
//                txtWelcome.setTextColor(
//                        Color.WHITE);
//
//                break;
//        }
//    }

    //RGB Glow Function
    private void startRGBGlow(View view) {

        ValueAnimator animator =
                ValueAnimator.ofArgb(
                        Color.RED,
                        Color.GREEN,
                        Color.BLUE,
                        Color.MAGENTA,
                        Color.CYAN,
                        Color.RED
                );

        animator.setDuration(4000);

        animator.setRepeatCount(
                ValueAnimator.INFINITE
        );

        animator.addUpdateListener(animation -> {

            int color = (int) animation.getAnimatedValue();

            GradientDrawable drawable =
                    new GradientDrawable();

            drawable.setColor(
                    Color.parseColor("#0f172a")
            );

            drawable.setCornerRadius(28);

            drawable.setStroke(4, color);

            view.setBackground(drawable);
        });

        animator.start();
    }

    //final theme engine
    private void applyTheme(int theme) {
        mainLayout.animate()
                .alpha(0f)
                .setDuration(150)
                .withEndAction(() -> {

                    applyThemeNow(theme);

                    mainLayout.animate()
                            .alpha(1f)
                            .setDuration(150);

                });
    }

    private void applyThemeNow(int theme) {
        int bgColor;
        int textColor;
        int cardColor;
        int borderColor;
        int buttonColor;
        int buttonTextColor;
        int shadowColor;

        GradientDrawable gradient = null;

        switch (theme) {

            // 🎨 LIGHT
            case 0:

                bgColor = Color.parseColor("#f8fafc");
                textColor = Color.BLACK;
                cardColor = Color.WHITE;
                borderColor = Color.parseColor("#cbd5e1");
                buttonColor = Color.parseColor("#2563eb");
                buttonTextColor = Color.WHITE;
                shadowColor = Color.GRAY;

                mainLayout.setBackgroundColor(bgColor);

                break;

            // 🌙 DARK
            case 1:

                bgColor = Color.parseColor("#020617");
                textColor = Color.WHITE;
                cardColor = Color.parseColor("#0f172a");
                borderColor = Color.parseColor("#22c55e");
                buttonColor = Color.parseColor("#14532d");
                buttonTextColor = Color.WHITE;
                shadowColor = Color.BLACK;

                mainLayout.setBackgroundColor(bgColor);

                break;

            // 🔵 BLUE
            case 2:

                textColor = Color.WHITE;
                cardColor = Color.parseColor("#1e3a8a");
                borderColor = Color.parseColor("#60a5fa");
                buttonColor = Color.parseColor("#2563eb");
                buttonTextColor = Color.WHITE;
                shadowColor = Color.parseColor("#1e40af");

                gradient = new GradientDrawable(
                        GradientDrawable.Orientation.TOP_BOTTOM,
                        new int[]{
                                Color.parseColor("#0f172a"),
                                Color.parseColor("#2563eb")
                        });

                mainLayout.setBackground(gradient);

                break;

            // 🟠 ORANGE
            default:

                textColor = Color.WHITE;
                cardColor = Color.parseColor("#9a3412");
                borderColor = Color.parseColor("#fdba74");
                buttonColor = Color.parseColor("#ea580c");
                buttonTextColor = Color.WHITE;
                shadowColor = Color.parseColor("#7c2d12");

                gradient = new GradientDrawable(
                        GradientDrawable.Orientation.TOP_BOTTOM,
                        new int[]{
                                Color.parseColor("#7c2d12"),
                                Color.parseColor("#fb923c")
                        });

                mainLayout.setBackground(gradient);

                break;
        }

        // =========================
        // TEXT COLORS
        // =========================

        txtWelcome.setTextColor(textColor);

        title.setTextColor(textColor);

        title.setShadowLayer(
                8,
                2,
                2,
                shadowColor
        );

        // =========================
        // BUTTONS
        // =========================

        styleButton(btnSignIn,
                buttonColor,
                buttonTextColor,
                borderColor);

        styleButton(btnShare,
                buttonColor,
                buttonTextColor,
                borderColor);

        // =========================
        // SPINNER
        // =========================

        GradientDrawable spinnerBg =
                new GradientDrawable();

        spinnerBg.setColor(cardColor);

        spinnerBg.setCornerRadius(16);

        spinnerBg.setStroke(
                3,
                borderColor
        );

        spinnerTheme.setBackground(spinnerBg);

        // =========================
        // CARDS
        // =========================

        styleCard(cardTicTacToe,
                cardColor,
                borderColor);

        styleCard(cardMemory,
                cardColor,
                borderColor);

        styleCard(cardGomoku,
                cardColor,
                borderColor);
    }

    private void styleButton(
            Button button,
            int bgColor,
            int textColor,
            int borderColor) {

        GradientDrawable drawable =
                new GradientDrawable();

        drawable.setColor(bgColor);

        drawable.setCornerRadius(18);

        drawable.setStroke(3, borderColor);

        button.setBackground(drawable);

        button.setTextColor(textColor);
    }

    private void styleCard(
            LinearLayout card,
            int bgColor,
            int borderColor) {

        GradientDrawable drawable =
                new GradientDrawable();

        drawable.setColor(bgColor);

        drawable.setCornerRadius(28);

        drawable.setStroke(3, borderColor);

        card.setBackground(drawable);

        card.setElevation(12f);
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
}