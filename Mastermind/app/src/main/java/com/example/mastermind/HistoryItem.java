package com.example.mastermind;

public class HistoryItem {

    private int[] guessColors;
    private int exactMatches;
    private int partialMatches;

    public HistoryItem(
            int[] guessColors,
            int exactMatches,
            int partialMatches) {

        this.guessColors = guessColors;
        this.exactMatches = exactMatches;
        this.partialMatches = partialMatches;
    }

    public int[] getGuessColors() {
        return guessColors;
    }

    public int getExactMatches() {
        return exactMatches;
    }

    public int getPartialMatches() {
        return partialMatches;
    }
}