package com.example.slitherlink;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

public class SlitherlinkView extends View {

    private Paint dotPaint;
    private Paint linePaint;
    private Paint textPaint;

    private int[][] clues;

    private boolean[][] horizontal;
    private boolean[][] vertical;

    private int size = 5;

    public SlitherlinkView(Context context,
                           android.util.AttributeSet attrs) {
        super(context, attrs);

        clues = PuzzleGenerator.getEasyPuzzle();

        horizontal = new boolean[size+1][size];
        vertical   = new boolean[size][size+1];

        dotPaint = new Paint();
        dotPaint.setColor(Color.BLACK);

        linePaint = new Paint();
        linePaint.setColor(Color.BLUE);
        linePaint.setStrokeWidth(8);

        textPaint = new Paint();
        textPaint.setTextSize(40);
        textPaint.setColor(Color.BLACK);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int cell = getWidth() / (size + 1);

        for(int r=0;r<=size;r++) {
            for(int c=0;c<=size;c++) {

                float x = cell + c*cell;
                float y = cell + r*cell;

                canvas.drawCircle(x,y,8,dotPaint);
            }
        }

        for(int r=0;r<size;r++) {
            for(int c=0;c<size;c++) {

                if(clues[r][c] >= 0) {
                    canvas.drawText(
                            String.valueOf(clues[r][c]),
                            cell+c*cell+25,
                            cell+r*cell+60,
                            textPaint);
                }
            }
        }

        for(int r=0;r<=size;r++) {
            for(int c=0;c<size;c++) {

                if(horizontal[r][c]) {

                    float x1 = cell+c*cell;
                    float y1 = cell+r*cell;

                    float x2 = cell+(c+1)*cell;
                    float y2 = y1;

                    canvas.drawLine(
                            x1,y1,x2,y2,linePaint);
                }
            }
        }

        for(int r=0;r<size;r++) {
            for(int c=0;c<=size;c++) {

                if(vertical[r][c]) {

                    float x1 = cell+c*cell;
                    float y1 = cell+r*cell;

                    float x2 = x1;
                    float y2 = cell+(r+1)*cell;

                    canvas.drawLine(
                            x1,y1,x2,y2,linePaint);
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(event.getAction()==MotionEvent.ACTION_DOWN){

            float x = event.getX();
            float y = event.getY();

            int cell = getWidth()/(size+1);

            int row = Math.round(y/cell)-1;
            int col = Math.round(x/cell)-1;

            if(row>=0 && row<=size &&
                    col>=0 && col<size){

                horizontal[row][col] =
                        !horizontal[row][col];

                invalidate();
            }
        }

        return true;
    }

    public void checkPuzzle() {

        // Future:
        // Check clues
        // Verify single loop
        // Show success dialog
    }
}