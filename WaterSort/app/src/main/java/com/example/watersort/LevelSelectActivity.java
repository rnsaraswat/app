package com.example.watersort;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class LevelSelectActivity
        extends AppCompatActivity {

    @Override
    protected void onCreate(
            Bundle savedInstanceState) {

        super.onCreate(
                savedInstanceState);

        setContentView(
                R.layout.activity_level);

        GridView grid =
                findViewById(
                        R.id.gridLevels);

        ArrayList<String> levels =
                new ArrayList<>();

        for(int i=1;i<=100;i++){

            levels.add(
                    "Level "+i);
        }

        ArrayAdapter<String>
                adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout
                                .simple_list_item_1,
                        levels);

        grid.setAdapter(adapter);

        grid.setOnItemClickListener(
                (p,v,pos,id)->{

                    Intent intent =
                            new Intent(
                                    this,
                                    MainActivity.class);

                    intent.putExtra(
                            "level",
                            pos+1);

                    startActivity(
                            intent);
                });
    }
}