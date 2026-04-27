package com.example.fourinarow;

import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LeaderboardActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<Player> list = new ArrayList<>();
    LeaderboardAdapter adapter;

    DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_leaderboard);

        Button backBtn = findViewById(R.id.backBtn);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new LeaderboardAdapter(list);
        recyclerView.setAdapter(adapter);

        dbRef = FirebaseDatabase
                .getInstance("https://fourinarow-31ccf-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("leaderboard");

        loadData();

        backBtn.setOnClickListener(v -> {
            finish(); // वापस MainActivity
        });
    }

    void loadData() {
        // test data for testing
        //list.clear();
        //list.add(new Player("Ravi", 50));
        //list.add(new Player("Amit", 40));
        //list.add(new Player("Sohan", 30));
        //adapter.notifyDataSetChanged();
        dbRef.orderByChild("score").limitToLast(20)
                .addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        list.clear();

                        for (DataSnapshot data : snapshot.getChildren()) {
                            Player p = data.getValue(Player.class);
                            list.add(p);
                        }

                        Collections.reverse(list); // highest first
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {}
                });
    }
}