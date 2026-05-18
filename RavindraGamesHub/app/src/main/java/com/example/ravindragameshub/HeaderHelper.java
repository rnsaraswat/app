package com.example.ravindragameshub;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class HeaderHelper {

    public static void setupHeader(
            Activity activity
    ) {

        // =========================
        // FIND VIEWS
        // =========================

        Button btnSignIn =
                activity.findViewById(R.id.btnSignIn);

        Button btnShare =
                activity.findViewById(R.id.btnShare);

        Button btnHome =
                activity.findViewById(R.id.btnHome);

        Spinner spinnerTheme =
                activity.findViewById(R.id.spinnerTheme);

        TextView txtWelcome =
                activity.findViewById(R.id.txtWelcome);

        // =========================
        // LOAD PLAYER NAME
        // =========================

        SharedPreferences prefs =
                activity.getSharedPreferences(
                        "player_data",
                        Activity.MODE_PRIVATE
                );

        String playerName =
                prefs.getString(
                        "player_name",
                        "Guest"
                );

        txtWelcome.setText(
                "Welcome " + playerName
        );

        // =========================
        // THEME SPINNER
        // =========================

        String[] themes = {
                "🎨 Light",
                "🌙 Dark",
                "🔵 Blue Gradient",
                "🟠 Orange Gradient"
        };

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        activity,
                        android.R.layout.simple_spinner_item,
                        themes
                );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerTheme.setAdapter(adapter);

        // load saved theme

        int savedTheme =
                ThemeManager.getTheme(activity);

        spinnerTheme.setSelection(savedTheme);

        // save new theme

        spinnerTheme.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id
                    ) {

                        ThemeManager.saveTheme(
                                activity,
                                position
                        );

                        activity.recreate();
                    }

                    @Override
                    public void onNothingSelected(
                            AdapterView<?> parent
                    ) {

                    }
                });

        // =========================
        // SHARE BUTTON
        // =========================

        btnShare.setOnClickListener(v -> {

            Intent shareIntent =
                    new Intent(Intent.ACTION_SEND);

            shareIntent.setType("text/plain");

            shareIntent.putExtra(
                    Intent.EXTRA_TEXT,
                    "Play Ravindra Games Hub!"
            );

            activity.startActivity(
                    Intent.createChooser(
                            shareIntent,
                            "Share App"
                    )
            );
        });

        // =========================
        // HOME BUTTON
        // =========================

        btnHome.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            activity,
                            MainActivity.class
                    );

            activity.startActivity(intent);

            activity.finish();
        });
    }
}