package com.example.sudoku;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;

public class StatisticsManager {

    private SharedPreferences prefs;

    public StatisticsManager(
            Context context){

        prefs =
                context.getSharedPreferences(
                        "stats",
                        Context.MODE_PRIVATE);
    }

    public void saveWin() {

        int wins =
                prefs.getInt("wins",0);

        prefs.edit()
                .putInt("wins",
                        wins + 1)
                .apply();
    }

    public int getWins() {

        return prefs.getInt(
                "wins",
                0);
    }


}