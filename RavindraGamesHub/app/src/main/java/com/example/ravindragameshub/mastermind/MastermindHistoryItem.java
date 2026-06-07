package com.example.ravindragameshub.mastermind;

public class MastermindHistoryItem {

    private int[] guessColors;
    private int exactMatches;
    private int partialMatches;

    public MastermindHistoryItem(
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