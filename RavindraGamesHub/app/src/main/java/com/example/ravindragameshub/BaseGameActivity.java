package com.example.ravindragameshub;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class BaseGameActivity extends AppCompatActivity {
    //sound variables
    MediaPlayer tapSound, winSound, loseSound, drawSound;
    MediaPlayer bgMusic;
    boolean isMusicOn = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // 🔊 Sound
        //background music
        bgMusic = MediaPlayer.create(this, R.raw.bg_music);
        bgMusic.setLooping(true);
        //sound variables
        tapSound = MediaPlayer.create(this, R.raw.tap);
        winSound = MediaPlayer.create(this, R.raw.win);
        loseSound = MediaPlayer.create(this, R.raw.lose);
        drawSound = MediaPlayer.create(this, R.raw.draw);
    }

    // =========================
    // 🔊 Play Sound
    // =========================
    protected void playSound(MediaPlayer mp) {
        if (mp != null) {
            mp.start();
        }
    }

//    @Override
//    protected void onDestroy(MediaPlayer mp) {
//        super.onDestroy();
//        if (mp != null) {
//            mp.release();
//        }
//    }
//    @Override
//    protected void onDestroy() {
//
//        super.onDestroy();
//
//        if (mp != null) {
//
//            clickSound.release();
//        }
//    }
}
