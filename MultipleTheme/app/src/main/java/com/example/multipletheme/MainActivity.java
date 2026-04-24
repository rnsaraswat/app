package com.example.multipletheme;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    // Multiple Themes
    class Theme {
        int bgColor;
        int btnColor;
        int textColor;

        Theme(int bg, int btn, int text) {
            bgColor = bg;
            btnColor = btn;
            textColor = text;
        }
    }

    // Themes यहीं define करो
    Theme lightTheme = new Theme(Color.WHITE, Color.LTGRAY, Color.BLACK);
    Theme darkTheme = new Theme(Color.BLACK, Color.DKGRAY, Color.WHITE);
    Theme blueTheme = new Theme(Color.parseColor("#0D47A1"),
            Color.parseColor("#1976D2"),
            Color.WHITE);

    // Theme Array
    Theme[] themes = {lightTheme, darkTheme, blueTheme};

    // Current theme index
    int currentThemeIndex = 0;

    // theme variables
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        //Multiple Themes
        protected void onCreate(Bundle savedInstanceState) {
            Theme lightTheme = new Theme(...); ❌
        }

        // 1. Button ko ID se find karein
        Button themeBtn = findViewById(R.id.themeBtn);

//        Multiple Themes
        prefs = getSharedPreferences("settings", MODE_PRIVATE);
        currentThemeIndex = prefs.getInt("theme", 0);
        applyTheme();

        themeBtn.setOnClickListener(v -> {
            currentThemeIndex = (currentThemeIndex + 1) % themes.length;
            applyTheme();
            saveTheme();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}

// Current theme saved method
void saveTheme() {
    prefs.edit().putInt("theme", currentThemeIndex).apply();
}

// Multiple Themes apply method
void applyTheme() {

    MainActivity.Theme t = themes[currentThemeIndex];

    // Background
    getWindow().getDecorView().setBackgroundColor(t.bgColor);

    // Buttons
//    for (Button b : buttons) {
//        b.setBackgroundColor(t.btnColor);
//        b.setTextColor(t.textColor);
//    }

    // Text
//    statusText.setTextColor(t.textColor);
//    scoreText.setTextColor(t.textColor);
}