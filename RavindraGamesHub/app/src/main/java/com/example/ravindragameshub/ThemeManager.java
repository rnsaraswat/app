package com.example.ravindragameshub;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ThemeManager {

    public static final String PREF_NAME =
            "theme_data";

    public static final String KEY_THEME =
            "theme_index";

    // =========================
    // SAVE THEME
    // =========================

    public static void saveTheme(
            Context context,
            int theme
    ) {

        SharedPreferences prefs =
                context.getSharedPreferences(
                        PREF_NAME,
                        Context.MODE_PRIVATE
                );

        prefs.edit()
                .putInt(KEY_THEME, theme)
                .apply();
    }

    // =========================
    // GET THEME
    // =========================

    public static int getTheme(
            Context context
    ) {

        SharedPreferences prefs =
                context.getSharedPreferences(
                        PREF_NAME,
                        Context.MODE_PRIVATE
                );

        return prefs.getInt(KEY_THEME, 1);
    }

    // =========================
    // APPLY BACKGROUND
    // =========================

    public static void applyBackground(
            Context context,
            LinearLayout layout
    ) {

        int theme = getTheme(context);

        switch (theme) {

            // 🎨 LIGHT
            case 0:

                layout.setBackgroundColor(
                        Color.WHITE
                );

                break;

            // 🌙 DARK
            case 1:

                layout.setBackgroundColor(
                        Color.parseColor("#020617")
                );

                break;

            // 🔵 BLUE
            case 2:

                GradientDrawable blue =
                        new GradientDrawable(
                                GradientDrawable.Orientation.TOP_BOTTOM,
                                new int[]{
                                        Color.parseColor("#0f172a"),
                                        Color.parseColor("#2563eb")
                                });

                layout.setBackground(blue);

                break;

            // 🟠 ORANGE
            case 3:

                GradientDrawable orange =
                        new GradientDrawable(
                                GradientDrawable.Orientation.TOP_BOTTOM,
                                new int[]{
                                        Color.parseColor("#7c2d12"),
                                        Color.parseColor("#fb923c")
                                });

                layout.setBackground(orange);

                break;
        }
    }

    // =========================
    // TEXT COLOR
    // =========================

    public static void applyTextColor(
            Context context,
            TextView textView
    ) {

        int theme = getTheme(context);

        switch (theme) {

            case 0:

                textView.setTextColor(
                        Color.BLACK
                );

                break;

            default:

                textView.setTextColor(
                        Color.WHITE
                );

                break;
        }
    }

    // =========================
    // BUTTON STYLE
    // =========================

    public static void styleButton(
            Context context,
            Button button
    ) {

        int theme = getTheme(context);

        int bgColor;
        int borderColor;
        int textColor;

        switch (theme) {

            // 🎨 LIGHT
            case 0:

                bgColor =
                        Color.parseColor("#2563eb");

                borderColor =
                        Color.parseColor("#1d4ed8");

                textColor =
                        Color.WHITE;

                break;

            // 🌙 DARK
            case 1:

                bgColor =
                        Color.parseColor("#14532d");

                borderColor =
                        Color.parseColor("#22c55e");

                textColor =
                        Color.WHITE;

                break;

            // 🔵 BLUE
            case 2:

                bgColor =
                        Color.parseColor("#2563eb");

                borderColor =
                        Color.parseColor("#60a5fa");

                textColor =
                        Color.WHITE;

                break;

            // 🟠 ORANGE
            default:

                bgColor =
                        Color.parseColor("#ea580c");

                borderColor =
                        Color.parseColor("#fdba74");

                textColor =
                        Color.WHITE;

                break;
        }

        GradientDrawable drawable =
                new GradientDrawable();

        drawable.setColor(bgColor);

        drawable.setCornerRadius(18);

        drawable.setStroke(
                3,
                borderColor
        );

        button.setBackground(drawable);

        button.setTextColor(textColor);
    }
}