package com.example.mastermind;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistoryAdapter
        extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

//    static class ViewHolder
//            extends RecyclerView.ViewHolder {
//
//        TextView txtHistory;
//
//        public ViewHolder(
//                @NonNull View itemView) {
//
//            super(itemView);
//
//            txtHistory =
//                    itemView.findViewById(
//                            R.id.txtHistoryItem);
//        }
//    }

    private List<HistoryItem> historyList;

    public HistoryAdapter(List<HistoryItem> historyList) {
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(
                        parent.getContext())
                .inflate(
                        R.layout.item_history,
                        parent,
                        false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            ViewHolder holder,
            int position) {

        HistoryItem item =
                historyList.get(position);

        holder.guessContainer.removeAllViews();

        holder.feedbackContainer
                .removeAllViews();

        int[] colors =
                item.getGuessColors();

        // Guess Pegs

        for(int color : colors){

            ImageView peg =
                    new ImageView(
                            holder.itemView
                                    .getContext());

            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(
                            50,
                            50);

            params.setMargins(
                    4,4,4,4);

            peg.setLayoutParams(params);

            peg.setBackgroundResource(
                    getPegDrawable(color));

            holder.guessContainer
                    .addView(peg);
        }

        // Black Pegs

        for(int i=0;
            i<item.getExactMatches();
            i++){

            ImageView peg =
                    new ImageView(
                            holder.itemView
                                    .getContext());

            peg.setBackgroundResource(
                    R.drawable.feedback_black);

            GridLayout.LayoutParams params =
                    new GridLayout.LayoutParams();

            params.width = 18;
            params.height = 18;

            peg.setLayoutParams(params);

            holder.feedbackContainer
                    .addView(peg);
        }

        // White Pegs

        for(int i=0;
            i<item.getPartialMatches();
            i++){

            ImageView peg =
                    new ImageView(
                            holder.itemView
                                    .getContext());

            peg.setBackgroundResource(
                    R.drawable.feedback_white);

            GridLayout.LayoutParams params =
                    new GridLayout.LayoutParams();

            params.width = 18;
            params.height = 18;

            peg.setLayoutParams(params);

            holder.feedbackContainer
                    .addView(peg);
        }
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout guessContainer;
        GridLayout feedbackContainer;

        public ViewHolder(View itemView) {

            super(itemView);

            guessContainer =
                    itemView.findViewById(
                            R.id.guessContainer);

            feedbackContainer =
                    itemView.findViewById(
                            R.id.feedbackContainer);
        }
    }

    private int getPegDrawable(int colorId){

        switch(colorId){

            case 1:
                return R.drawable.peg_red;

            case 2:
                return R.drawable.peg_blue;

            case 3:
                return R.drawable.peg_green;

            case 4:
                return R.drawable.peg_yellow;

            case 5:
                return R.drawable.peg_purple;

            case 6:
                return R.drawable.peg_orange;

            case 7:
                return R.drawable.peg_cyan;

            case 8:
                return R.drawable.peg_pink;

            case 9:
                return R.drawable.peg_brown;

            case 10:
                return R.drawable.peg_gray;
        }

        return R.drawable.peg_empty;
    }
}
