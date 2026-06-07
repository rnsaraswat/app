package com.example.mastermind;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.Gravity;
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

//            ImageView peg =
//                    new ImageView(
//                            holder.itemView
//                                    .getContext());

            TextView peg =
                    new TextView(
                            holder.itemView.getContext());

            peg.setGravity(Gravity.CENTER);

            peg.setText(
                    String.valueOf(color));

            peg.setTextColor(
                    Color.WHITE);

            peg.setBackgroundResource(
                    R.drawable.peg_circle);

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

//            TextView peg =
//                    new TextView(
//                            holder.itemView.getContext());
//
//            peg.setGravity(Gravity.CENTER);
//
//            peg.setText(
//                    String.valueOf(color));
//
//            peg.setTextColor(
//                    Color.WHITE);
//
//            peg.setBackgroundResource(
//                    R.drawable.peg_circle);

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

//            TextView peg =
//                    new TextView(
//                            holder.itemView.getContext());
//
//            peg.setGravity(Gravity.CENTER);
//
//            peg.setText(
//                    String.valueOf(color));
//
//            peg.setTextColor(
//                    Color.WHITE);
//
//            peg.setBackgroundResource(
//                    R.drawable.peg_circle);

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

    private void setHistoryPegColor(
            TextView peg,
            int color){

        switch(color){

            case 1:
                peg.setBackgroundTintList(
                        ColorStateList.valueOf(
                                Color.RED));
                break;

            case 2:
                peg.setBackgroundTintList(
                        ColorStateList.valueOf(
                                Color.BLUE));
                break;

            case 3:
                peg.setBackgroundTintList(
                        ColorStateList.valueOf(
                                Color.GREEN));
                break;

            case 4:
                peg.setBackgroundTintList(
                        ColorStateList.valueOf(
                                Color.YELLOW));

                peg.setTextColor(
                        Color.BLACK);
                break;

            // बाकी colors भी इसी प्रकार...
            case 5:
                peg.setBackgroundTintList(
                        ColorStateList.valueOf(
                                Color.MAGENTA));
                break;

            case 6:
                peg.setBackgroundTintList(
                        ColorStateList.valueOf(
                                0xFFFF9800));
                break;

            case 7:
                peg.setBackgroundTintList(
                        ColorStateList.valueOf(
                                Color.CYAN));
                break;
            case 8:
                peg.setBackgroundTintList(
                        ColorStateList.valueOf(
                                0xFFE91E63));
                break;

            case 9:
                peg.setBackgroundTintList(
                        ColorStateList.valueOf(
                                0xFF795548));
                break;

            case 10:
                peg.setBackgroundTintList(
                        ColorStateList.valueOf(
                                Color.GRAY));
                break;
        }
    }
}
