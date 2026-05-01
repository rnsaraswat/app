package com.example.fiveinarow;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    Spinner modeSpinner, difficultySpinner;
    Button startBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        modeSpinner = findViewById(R.id.modeSpinner);
//        difficultySpinner = findViewById(R.id.difficultySpinner);
//        startBtn = findViewById(R.id.startBtn);
//
//        startBtn.setOnClickListener(v -> showGameDialog());

        // App खुलते ही popup दिखाओ
        showGameDialog();

//        // Mode Spinner Data
//        String[] modes = {"Player vs Player", "Player vs AI"};
//        ArrayAdapter<String> modeAdapter = new ArrayAdapter<>(
//                this,
//                android.R.layout.simple_spinner_dropdown_item,
//                modes
//        );
//        modeSpinner.setAdapter(modeAdapter);
//
//// Difficulty Spinner Data
//        String[] levels = {"Easy", "Medium", "Hard"};
//        ArrayAdapter<String> diffAdapter = new ArrayAdapter<>(
//                this,
//                android.R.layout.simple_spinner_dropdown_item,
//                levels
//        );
//        difficultySpinner.setAdapter(diffAdapter);

//        startBtn.setOnClickListener(v -> {
//            Intent i = new Intent(MainActivity.this, GameActivity.class);
//            i.putExtra("mode", modeSpinner.getSelectedItemPosition());
//            i.putExtra("difficulty", difficultySpinner.getSelectedItemPosition());
//            startActivity(i);
//        });
    }

    private void showGameDialog() {

        View view = getLayoutInflater().inflate(R.layout.dialog_game_settings, null);

        Spinner modeSpinner = view.findViewById(R.id.dialogModeSpinner);
        Spinner difficultySpinner = view.findViewById(R.id.dialogDifficultySpinner);
        Button startBtn = view.findViewById(R.id.dialogStartBtn);

        // Data
        String[] modes = {"Player vs Player", "Player vs AI"};
        String[] levels = {"Easy", "Medium", "Hard"};

        modeSpinner.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, modes));

        difficultySpinner.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, levels));

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();

        startBtn.setOnClickListener(v -> {

            int mode = modeSpinner.getSelectedItemPosition();
            int difficulty = difficultySpinner.getSelectedItemPosition();

            Intent i = new Intent(MainActivity.this, GameActivity.class);
            i.putExtra("mode", mode);
            i.putExtra("difficulty", difficulty);

            startActivity(i);
            dialog.dismiss();
        });

        dialog.show();
    }
}
