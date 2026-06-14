package com.example.sudoku;

import androidx.appcompat.app.AppCompatDelegate;

public class ThemeManager {

    public static void toggleTheme() {

        int mode =
                AppCompatDelegate
                        .getDefaultNightMode();

        if(mode ==
                AppCompatDelegate.MODE_NIGHT_YES){

            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO);

        }else{

            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES);
        }
    }
}
