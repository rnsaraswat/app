package com.example.reversi;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import android.media.MediaPlayer;

public class MainActivity extends AppCompatActivity {

    ReversiGameView reversiGameView;
    Button btnUndo;

    TextView txtStatus, txtScore;

    Spinner spinnerMode, spinnerLevel;

    String player1 = "Player 1";
    String player2 = "AI";
    String selectedDifficulty = "Easy";

    MediaPlayer winSound;
    MediaPlayer loseSound;
    MediaPlayer drawSound;
    MediaPlayer tapSound;
    MediaPlayer clickSound;
    MediaPlayer flipSound;
    MediaPlayer errorSound;
    MediaPlayer passSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        winSound = MediaPlayer.create(this, R.raw.win);
        loseSound = MediaPlayer.create(this, R.raw.lose);
        drawSound = MediaPlayer.create(this, R.raw.draw);
        tapSound = MediaPlayer.create(this, R.raw.tap);
        clickSound = MediaPlayer.create(this, R.raw.click);
        flipSound = MediaPlayer.create(this, R.raw.flip);
        errorSound = MediaPlayer.create(this, R.raw.error);
        passSound = MediaPlayer.create(this, R.raw.pass);

        reversiGameView = findViewById(R.id.gameView);
        btnUndo = findViewById(R.id.btnUndo);

        txtStatus = findViewById(R.id.txtStatus);
        txtScore = findViewById(R.id.txtScore);

        spinnerMode = findViewById(R.id.spinnerMode);
        spinnerLevel = findViewById(R.id.spinnerLevel);

        Button btnRestart = findViewById(R.id.btnRestart);
        Button btnUndo = findViewById(R.id.btnUndo);

        SharedPreferences pref =
                getSharedPreferences("game",MODE_PRIVATE);

        player1 = pref.getString("player1","Player 1");

        String[] modes = {
                "Player vs AI",
                "Player vs Player"
        };

        String[] levels = {
                "Easy",
                "Medium",
                "Hard"
        };

//        spinnerMode.setAdapter(
//                new ArrayAdapter<>(
//                        this,
//                        android.R.layout.simple_list_item_1,
//                        modes
//                )
//        );
//
//        spinnerLevel.setAdapter(
//                new ArrayAdapter<>(
//                        this,
//                        android.R.layout.simple_list_item_1,
//                        levels
//                )
//        );

        ArrayAdapter<String> modeAdapter =
                new ArrayAdapter<>(
                        this,
                        R.layout.spinner_item,
                        modes
                );

        modeAdapter.setDropDownViewResource(
                R.layout.spinner_dropdown_item
        );

        spinnerMode.setAdapter(modeAdapter);

        ArrayAdapter<String> levelAdapter =
                new ArrayAdapter<>(
                        this,
                        R.layout.spinner_item,
                        levels
                );

        levelAdapter.setDropDownViewResource(
                R.layout.spinner_dropdown_item
        );

        spinnerLevel.setAdapter(levelAdapter);

        spinnerMode.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> parent,
                                               android.view.View view,
                                               int position,
                                               long id) {

                        if(position==0){

                            player2 = "AI";

                        } else {

                            askPlayer2Name();
                        }

                        reversiGameView.setMode(
                                modes[position]
                        );
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

        spinnerLevel.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> parent,
                                               android.view.View view,
                                               int position,
                                               long id) {

                        reversiGameView.setDifficulty(
                                levels[position]
                        );
                        selectedDifficulty = levels[position];
                    }


                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

        btnRestart.setOnClickListener(v -> {
            playSound(clickSound);
            reversiGameView.initGame();
        });

        btnUndo.setOnClickListener(v -> {
            playSound(clickSound);
            reversiGameView.undo();
        });

//        new Handler().post(() -> {
//            gameView.initGame();
//        });
        new android.os.Handler(
                android.os.Looper.getMainLooper()
        ).post(() -> {
            reversiGameView.initGame();
        });
    }

    void askPlayer2Name(){

        EditText edit = new EditText(this);

        edit.setText("Player 2");

        new AlertDialog.Builder(this)
                .setTitle("Second Player Name")
                .setView(edit)
                .setPositiveButton("OK",(d,w)->{

                    player2 = edit.getText().toString();

                    if(player2.trim().isEmpty())
                        player2 = "Player 2";
                })
                .show();
    }

    public void updateUI(int black,int white,boolean blackTurn){

        txtScore.setText(
                "Black: "+black+
                        "   White: "+white
        );

        String current =
                blackTurn ? player1 : player2;

        txtStatus.setText(
                current + " Turn"
        );

        if(black+white==64){

            String winner;

            if(black>white)
                winner = player1;
            else if(white>black)
                winner = player2;
            else
                winner = "Draw";

            if(winner.equals("Draw"))
                txtStatus.setText("Game Draw");
            else
                txtStatus.setText(winner + " Wins");
        }
    }

    public void updateUndoText() {

        btnUndo.setText(
                "Undo (" +
                        reversiGameView.getRemainingUndo() +
                        ")"
        );
    }

    //Common Sound Method
    void playSound(MediaPlayer mp) {

        if(mp == null)
            return;

        try {

            if(mp.isPlaying()) {
                mp.seekTo(0);
            }

            mp.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(winSound != null) winSound.release();
        if(loseSound != null) loseSound.release();
        if(drawSound != null) drawSound.release();
        if(tapSound != null) tapSound.release();
        if(clickSound != null) clickSound.release();
        if(flipSound != null) flipSound.release();
        if(errorSound != null) errorSound.release();
        if(passSound != null) passSound.release();
    }
}