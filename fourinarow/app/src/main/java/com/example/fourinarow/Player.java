package com.example.fourinarow;

public class Player {
    public String name;
    public int score;

    public Player() {} // Firebase के लिए जरूरी

    public Player(String name, int score) {
        this.name = name;
        this.score = score;
    }
}
