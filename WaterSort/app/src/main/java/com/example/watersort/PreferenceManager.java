package com.example.watersort;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {

    private static final String PREF =
            "water_sort";

    private static final String KEY_LEVEL =
            "current_level";

    private static final String KEY_DARK =
            "dark_mode";

    public static void saveLevel(
            Context context,
            int level) {

        SharedPreferences sp =
                context.getSharedPreferences(
                        PREF,
                        Context.MODE_PRIVATE);

        sp.edit()
                .putInt(KEY_LEVEL, level)
                .apply();
    }

    public static int getLevel(
            Context context) {

        SharedPreferences sp =
                context.getSharedPreferences(
                        PREF,
                        Context.MODE_PRIVATE);

        return sp.getInt(KEY_LEVEL, 1);
    }

    public static void saveDarkMode(
            Context context,
            boolean dark) {

        SharedPreferences sp =
                context.getSharedPreferences(
                        PREF,
                        Context.MODE_PRIVATE);

        sp.edit()
                .putBoolean(KEY_DARK, dark)
                .apply();
    }

    public static boolean isDarkMode(
            Context context) {

        SharedPreferences sp =
                context.getSharedPreferences(
                        PREF,
                        Context.MODE_PRIVATE);

        return sp.getBoolean(KEY_DARK, false);
    }
}