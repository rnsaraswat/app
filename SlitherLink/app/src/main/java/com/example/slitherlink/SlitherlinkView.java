package com.example.slitherlink;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.Stack;

public class SlitherlinkView extends View {



    public class Move {

        boolean horizontal;
        int row;
        int col;
        boolean previousState;

        public Move(
                boolean horizontal,
                int row,
                int col,
                boolean previousState){

            this.horizontal=horizontal;
            this.row=row;
            this.col=col;
            this.previousState=previousState;
        }
    }

    private Paint dotPaint;
    private Paint linePaint;
    private Paint textPaint;

    private int[][] clues;

    private int size = 5;

    private boolean[][] horizontal;
    private boolean[][] vertical;

    private boolean[][] solutionHorizontal;
    private boolean[][] solutionVertical;

    private Stack<SlitherlinkView.Move> undoStack =
            new Stack<>();

    private Stack<SlitherlinkView.Move> redoStack =
            new Stack<>();

    public boolean[][] getHorizontal() {
        return horizontal;
    }

    public boolean[][] getVertical() {
        return vertical;
    }

    private boolean puzzleSolved = false;

    private boolean gameFinished = false;
    private int offsetX;
    private int offsetY;
    private int cellSize;
    private OnSolvedListener solvedListener;
//    public enum Difficulty {
//
//        EASY,
//        MEDIUM,
//        HARD
//    }

    private boolean isDark = false;
//    private Difficulty difficulty;


//    String[] levels={
//            "Easy",
//            "Medium",
//            "Hard"
//    };

    public SlitherlinkView(Context context,
                           android.util.AttributeSet attrs) {
        super(context, attrs);

//        clues = PuzzleGenerator.getPuzzle(0);

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

        startNewGame(0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        int boardSize = Math.min(
//                getWidth(),
//                getHeight());
//
//        int cell = boardSize / (size + 2);
//
//        offsetX =
//                (getWidth() -
//                        cell * size) / 2;
//
//        offsetY =
//                (getHeight() -
//                        cell * size) / 2;

        int boardSize = Math.min(getWidth(), getHeight());

        cellSize = boardSize / (size + 1);

        offsetX = (getWidth() - (cellSize * size)) / 2;
        offsetY = (getHeight() - (cellSize * size)) / 2;

//        int cell = getWidth() / (size + 1);

        for(int r=0;r<=size;r++) {
            for(int c=0;c<=size;c++) {

//                float x = cell + c*cell;
//                float y = cell + r*cell;
                float x = offsetX + c * cellSize;
                float y = offsetY + r * cellSize;

                canvas.drawCircle(x,y,8,dotPaint);
            }
        }

        for(int r=0;r<size;r++) {
            for(int c=0;c<size;c++) {

                if(clues[r][c] >= 0) {
                    canvas.drawText(
                            String.valueOf(clues[r][c]),
                            offsetX + c * cellSize + cellSize / 3f,
                            offsetY + r * cellSize + cellSize * 0.7f,
                            textPaint);
                }
            }
        }

        for(int r=0;r<=size;r++) {
            for(int c=0;c<size;c++) {

                if(horizontal[r][c]) {

                    float x1 = offsetX + c * cellSize;
                    float y1 = offsetY + r * cellSize;

                    float x2 = offsetX + (c + 1) * cellSize;
                    float y2 = y1;

                    canvas.drawLine(
                            x1,y1,x2,y2,linePaint);
                }
            }
        }

        for(int r=0;r<size;r++) {
            for(int c=0;c<=size;c++) {

                if(vertical[r][c]) {

                    float x1 = offsetX + c * cellSize;
                    float y1 = offsetY + r * cellSize;

                    float x2 = x1;
                    float y2 = offsetY + (r + 1) * cellSize;

                    canvas.drawLine(
                            x1,y1,x2,y2,linePaint);
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(gameFinished){

            android.util.Log.d(
                    "SLITHER",
                    "TOUCH BLOCKED");
            return true;
        }

        if (event.getAction() != MotionEvent.ACTION_DOWN)
            return true;

        int boardSize = Math.min(getWidth(), getHeight());

        cellSize = boardSize / (size + 1);

        offsetX = (getWidth() - (cellSize * size)) / 2;
        offsetY = (getHeight() - (cellSize * size)) / 2;

        float x = event.getX() - offsetX;
        float y = event.getY() - offsetY;

        int nearestCol = Math.round(x / cellSize);
        int nearestRow = Math.round(y / cellSize);

        float dotX = nearestCol * cellSize;
        float dotY = nearestRow * cellSize;

        float dx = x - dotX;
        float dy = y - dotY;

        // Dot ke aas-paas click ignore karo
        float dotRadius = cellSize * 0.25f;

        if (Math.abs(dx) < dotRadius &&
                Math.abs(dy) < dotRadius) {
            return true;
        }

        // Horizontal edge
        if (Math.abs(dy) < cellSize * 0.20f &&
                Math.abs(dx) > cellSize * 0.25f &&
                Math.abs(dx) < cellSize * 0.75f) {

//            int row = nearestRow - 1;
//
//            int col = (dx > 0)
//                    ? nearestCol - 1
//                    : nearestCol - 2;

            int row = nearestRow;

            int col = (dx > 0)
                    ? nearestCol
                    : nearestCol - 1;

            if (row >= 0 &&
                    row < horizontal.length &&
                    col >= 0 &&
                    col < horizontal[0].length) {

                undoStack.push(
                        new Move(
                                true,
                                row,
                                col,
                                horizontal[row][col]
                        )
                );

                horizontal[row][col] =
                        !horizontal[row][col];

                if(!puzzleSolved && checkPuzzle()){

                    puzzleSolved = true;
                    gameFinished = true;

                    android.util.Log.d(
                            "SLITHER",
                            "GAME FINISHED");
                    if(solvedListener != null){

                        solvedListener.onSolved();
                    }
                }

                redoStack.clear();

                invalidate();
            }

            return true;
        }

        // Vertical edge
        if (Math.abs(dx) < cellSize * 0.20f &&
                Math.abs(dy) > cellSize * 0.25f &&
                Math.abs(dy) < cellSize * 0.75f) {

//            int col = nearestCol - 1;
//
//            int row = (dy > 0)
//                    ? nearestRow - 1
//                    : nearestRow - 2;

            int col = nearestCol;

            int row = (dy > 0)
                    ? nearestRow
                    : nearestRow - 1;

            if (row >= 0 &&
                    row < vertical.length &&
                    col >= 0 &&
                    col < vertical[0].length) {

                undoStack.push(
                        new Move(
                                false,
                                row,
                                col,
                                vertical[row][col]
                        )
                );

                vertical[row][col] =
                        !vertical[row][col];

                if(!puzzleSolved && checkPuzzle()){

                    puzzleSolved = true;
                    gameFinished = true;

                    android.util.Log.d(
                            "SLITHER",
                            "GAME FINISHED");
                    if(solvedListener != null){

                        solvedListener.onSolved();
                    }
                }

                redoStack.clear();

                invalidate();
            }
        }

        return true;
    }






    private boolean validateClues() {

        for(int r=0; r<size; r++) {

            for(int c=0; c<size; c++) {

                if(clues[r][c] < 0)
                    continue;

                int count = 0;

                if(horizontal[r][c]) count++;
                if(horizontal[r+1][c]) count++;

                if(vertical[r][c]) count++;
                if(vertical[r][c+1]) count++;

                if(count != clues[r][c])
                    return false;
            }
        }

        return true;
    }

    private boolean validateDegrees() {

        int dots = size + 1;

        for(int r=0; r<dots; r++) {

            for(int c=0; c<dots; c++) {

                int degree = 0;

                if(c > 0 && horizontal[r][c-1])
                    degree++;

                if(c < size && horizontal[r][c])
                    degree++;

                if(r > 0 && vertical[r-1][c])
                    degree++;

                if(r < size && vertical[r][c])
                    degree++;

                if(degree != 0 && degree != 2)
                    return false;
            }
        }

        return true;
    }

    private int countEdges() {

        int count = 0;

        for(boolean[] row : horizontal)
            for(boolean b : row)
                if(b) count++;

        for(boolean[] row : vertical)
            for(boolean b : row)
                if(b) count++;

        return count;
    }

    private int[] findStartDot() {

        for(int r=0; r<=size; r++) {

            for(int c=0; c<=size; c++) {

                int degree = 0;

                if(c>0 && horizontal[r][c-1]) degree++;
                if(c<size && horizontal[r][c]) degree++;
                if(r>0 && vertical[r-1][c]) degree++;
                if(r<size && vertical[r][c]) degree++;

                if(degree > 0)
                    return new int[]{r,c};
            }
        }

        return null;
    }

    private void dfs(
            int r,
            int c,
            boolean[][] visitedDots) {

        visitedDots[r][c] = true;

        if(c>0 &&
                horizontal[r][c-1] &&
                !visitedDots[r][c-1]) {

            dfs(r,c-1,visitedDots);
        }

        if(c<size &&
                horizontal[r][c] &&
                !visitedDots[r][c+1]) {

            dfs(r,c+1,visitedDots);
        }

        if(r>0 &&
                vertical[r-1][c] &&
                !visitedDots[r-1][c]) {

            dfs(r-1,c,visitedDots);
        }

        if(r<size &&
                vertical[r][c] &&
                !visitedDots[r+1][c]) {

            dfs(r+1,c,visitedDots);
        }
    }

//    private boolean isSingleLoop() {
//
//        // TODO: Real loop validation later
//        return true;
//    }

//    private boolean isSingleLoop() {
//
//        int[] start = findStartDot();
//
//        if(start == null)
//            return false;
//
//        boolean[][] visited =
//                new boolean[size+1][size+1];
//
//        dfs(start[0], start[1], visited);
//
//        for(int r=0; r<=size; r++) {
//
//            for(int c=0; c<=size; c++) {
//
//                int degree = 0;
//
//                if(c>0 && horizontal[r][c-1]) degree++;
//                if(c<size && horizontal[r][c]) degree++;
//                if(r>0 && vertical[r-1][c]) degree++;
//                if(r<size && vertical[r][c]) degree++;
//
//                if(degree>0 && !visited[r][c])
//                    return false;
//            }
//        }
//
//        return true;
//    }

    public boolean checkPuzzle() {

        if(gameFinished){
            return true;
        }

        for(int r=0;r<horizontal.length;r++) {

            for(int c=0;c<horizontal[0].length;c++) {

                if(horizontal[r][c]
                        != solutionHorizontal[r][c]) {

                    return false;
                }
            }
        }

        for(int r=0;r<vertical.length;r++) {

            for(int c=0;c<vertical[0].length;c++) {

                if(vertical[r][c]
                        != solutionVertical[r][c]) {

                    return false;
                }
            }
        }

        return true;
    }

    private boolean isSingleLoop()  {
        int[] start = findStartDot();

        if(start == null)
            return false;

        boolean[][] visited =
                new boolean[size+1][size+1];

        dfs(start[0], start[1], visited);

        // कोई used dot unvisited नहीं होना चाहिए
        for(int r=0;r<horizontal.length;r++) {

            for(int c=0;c<horizontal[0].length;c++) {

                if(horizontal[r][c]
                        != solutionHorizontal[r][c]) {

                    return false;
                }
            }
        }

        for(int r=0;r<vertical.length;r++) {

            for(int c=0;c<vertical[0].length;c++) {

                if(vertical[r][c]
                        != solutionVertical[r][c]) {

                    return false;
                }
            }
        }

        return true;
    }

//    public boolean checkPuzzle()
//    {
//        return validateClues()
//                && validateDegrees()
//                && isSingleLoop();
//    }

    public void startNewGame(int difficulty){

        PuzzleData data =
                PuzzleGenerator.getPuzzle(
                        difficulty);

        clues = data.clues;

        solutionHorizontal =
                data.solutionHorizontal;

        solutionVertical =
                data.solutionVertical;

        size = clues.length;

        horizontal =

                new boolean[size+1][size];

        vertical =
                new boolean[size][size+1];

        undoStack.clear();
        redoStack.clear();

        puzzleSolved = false;
        gameFinished = false;

        invalidate();
    }

    public void giveHint(){

        if(gameFinished){
            return;
        }

        for(int r=0;r<horizontal.length;r++){

            for(int c=0;c<horizontal[0].length;c++){

                if(solutionHorizontal[r][c]
                        && !horizontal[r][c]){

                    horizontal[r][c] = true;

                    invalidate();
                    return;
                }
            }
        }

        for(int r=0;r<vertical.length;r++){

            for(int c=0;c<vertical[0].length;c++){

                if(solutionVertical[r][c]
                        && !vertical[r][c]){

                    vertical[r][c] = true;

                    invalidate();
                    return;
                }
            }
        }
    }

//    public void giveHint(){
//
//        for(int r=0;r<horizontal.length;r++){
//
//            for(int c=0;c<horizontal[0].length;c++){
//
//                if(solutionHorizontal[r][c]
//                        && !horizontal[r][c]){
//
//                    horizontal[r][c]=true;
//
//                    invalidate();
//
//                    return;
//                }
//            }
//        }
//    }

    public void undo(){

        if(gameFinished){
            return;
        }

        if(undoStack.isEmpty())
            return;

        SlitherlinkView.Move move=undoStack.pop();

        horizontal[move.row][move.col]
                = move.previousState;

        redoStack.push(move);

        invalidate();
    }



    public void setDarkMode(boolean dark){

        isDark = dark;

        if(isDark){

            setBackgroundColor(Color.BLACK);

            textPaint.setColor(Color.WHITE);

        }else{

            setBackgroundColor(Color.WHITE);

            textPaint.setColor(Color.BLACK);
        }

        invalidate();
    }

    public void redo() {

        if(gameFinished){
            return;
        }

        if(redoStack.isEmpty())
            return;

        Move move = redoStack.pop();

        horizontal[move.row][move.col] =
                !move.previousState;

        undoStack.push(move);

        invalidate();
    }



    public void restoreState(
            boolean[][] h,
            boolean[][] v) {

        if(h != null)
            horizontal = h;

        if(v != null)
            vertical = v;

        invalidate();
    }

    public interface OnSolvedListener {

        void onSolved();
    }



    public void setOnSolvedListener(
            OnSolvedListener listener){

        solvedListener = listener;
    }

    @Override
    protected void onMeasure(
            int widthMeasureSpec,
            int heightMeasureSpec) {

        int width =
                MeasureSpec.getSize(
                        widthMeasureSpec);

        setMeasuredDimension(
                width,
                width);
    }
}