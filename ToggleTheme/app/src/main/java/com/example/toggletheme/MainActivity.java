package com.example.toggletheme;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

//    Dark Mode Toggle
    boolean isDark = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Button ko ID se find karein
        Button btn = findViewById(R.id.themeBtn);

        //Dark Mode Toggle
        btn.setOnClickListener(v -> toggleTheme());
        // Button click par function call karna
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleTheme();
            }
        });

        //hello world
        TextView tv = findViewById(R.id.HWtextView);
        tv.setText("Hello! Updated from Java.");

        EdgeToEdge.enable(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    //    Dark Mode Toggle
    void toggleTheme() {
        //code for simple theme change
//        if (isDark) {
//            getWindow().getDecorView().setBackgroundColor(Color.WHITE);
//        } else {
//            getWindow().getDecorView().setBackgroundColor(Color.BLACK);
//        }
//        isDark = !isDark;

        //Dark Mode Smooth Transition
        View root = getWindow().getDecorView();

        root.animate()
                .alpha(0f)
                .setDuration(150)
                .withEndAction(() -> {
                    root.setBackgroundColor(isDark ? Color.WHITE : Color.BLACK);
                    root.animate().alpha(1f).setDuration(150).start();
                });

        isDark = !isDark;
    }

}