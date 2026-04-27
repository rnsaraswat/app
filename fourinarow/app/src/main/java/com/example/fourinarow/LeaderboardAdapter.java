package com.example.fourinarow;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {

    List<Player> list;

    public LeaderboardAdapter(List<Player> list) {
        this.list = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView rank, name, score;

        public ViewHolder(View v) {
            super(v);
            rank = v.findViewById(R.id.rank);
            name = v.findViewById(R.id.name);
            score = v.findViewById(R.id.score);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_leaderboard, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Player p = list.get(position);
        int rank = position + 1;

        holder.name.setText(p.name);
        holder.score.setText(String.valueOf(p.score));

        // 🎯 Rank text
        if (rank == 1) holder.rank.setText("🥇");
        else if (rank == 2) holder.rank.setText("🥈");
        else if (rank == 3) holder.rank.setText("🥉");
        else holder.rank.setText(String.valueOf(rank));

        // 🎨 Highlight Top 3 Cards
        if (rank == 1) {
            holder.itemView.setBackgroundColor(0xFFFFD700); // Gold
        } else if (rank == 2) {
            holder.itemView.setBackgroundColor(0xFFC0C0C0); // Silver
        } else if (rank == 3) {
            holder.itemView.setBackgroundColor(0xFFCD7F32); // Bronze
        } else {
            holder.itemView.setBackgroundColor(0xFFFFFFFF); // Normal
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
