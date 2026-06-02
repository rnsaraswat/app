package com.example.ravindragameshub.common;

// ==========================================
// ThemeManager.java
// Common sound System for All Games
// ==========================================
import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;

import com.example.ravindragameshub.R;

public class SoundManager {

    private static SoundPool soundPool;

    private static int clickSound;
    private static int tapSound;
    private static int passSound;
    private static int winSound;
    private static int loseSound;
    private static int drawSound;
    private static int errorSound;
    private static int flipSound;
    private static int ballTapSound;

    private static boolean loaded = false;

    // Initialize once
    public static void init(Context context) {

        if (loaded) return;

        AudioAttributes attributes =
                new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(
                                AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build();

        soundPool =
                new SoundPool.Builder()
                        .setMaxStreams(5)
                        .setAudioAttributes(attributes)
                        .build();

        clickSound =
                soundPool.load(context,
                        R.raw.click, 1);

        tapSound =
                soundPool.load(context,
                        R.raw.tap, 1);

        passSound =
                soundPool.load(context,
                        R.raw.pass, 1);

        winSound =
                soundPool.load(context,
                        R.raw.win, 1);

        loseSound =
                soundPool.load(context,
                        R.raw.lose, 1);

        drawSound =
                soundPool.load(context,
                        R.raw.draw, 1);

        errorSound =
                soundPool.load(context,
                        R.raw.error, 1);

        flipSound =
                soundPool.load(context,
                        R.raw.flip, 1);

        ballTapSound =
                soundPool.load(context,
                        R.raw.balltap, 1);

        loaded = true;
    }

    public static void playClick() {

        if (soundPool != null)
            soundPool.play(clickSound,
                    1, 1, 1, 0, 1);
    }

    public static void playTap() {

        if (soundPool != null)
            soundPool.play(tapSound,
                    1, 1, 1, 0, 1);
    }

    public static void playPass() {

        if (soundPool != null)
            soundPool.play(passSound,
                    1, 1, 1, 0, 1);
    }

    public static void playWin() {

        if (soundPool != null)
            soundPool.play(winSound,
                    1, 1, 1, 0, 1);
    }

    public static void playLose() {

        if (soundPool != null)
            soundPool.play(loseSound,
                    1, 1, 1, 0, 1);
    }

    public static void playDraw() {

        if (soundPool != null)
            soundPool.play(drawSound,
                    1, 1, 1, 0, 1);
    }

    public static void playError() {

        if (soundPool != null)
            soundPool.play(errorSound,
                    1, 1, 1, 0, 1);
    }

    public static void playFlip() {

        if (soundPool != null)
            soundPool.play(flipSound,
                    1, 1, 1, 0, 1);
    }

    public static void playBallTap() {

        if (soundPool != null)
            soundPool.play(ballTapSound,
                    1, 1, 1, 0, 1);
    }

    public static void release() {

        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }

        loaded = false;
    }
}
