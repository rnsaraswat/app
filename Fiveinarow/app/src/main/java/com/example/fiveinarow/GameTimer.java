package com.example.fiveinarow;

public class GameTimer {
    //variables
    private long startTime = 0;
    private long pauseTime = 0;
    private boolean isPaused = false;

    //timer start method
    public void start() {
        startTime = System.currentTimeMillis();
        isPaused = false;
    }

    // timer paused method
    public void pause() {
        if (!isPaused) {
            pauseTime = System.currentTimeMillis();
            isPaused = true;
        }
    }

    //resume timer method
    public void resume() {
        if (isPaused) {
            long pausedDuration = System.currentTimeMillis() - pauseTime;
            startTime += pausedDuration; //main logic
            isPaused = false;
        }
    }


    public long getElapsedTime() {
        if (isPaused) {
            return pauseTime - startTime;
        } else {
            return System.currentTimeMillis() - startTime;
        }
    }

    public boolean isPaused() {
        return isPaused;
    }
}
