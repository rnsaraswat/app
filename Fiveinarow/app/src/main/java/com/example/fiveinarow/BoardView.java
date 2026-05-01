package com.example.fiveinarow;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class BoardView extends View {

    int size = 15;
    int[][] board = new int[size][size];

//    private OnCellTouchListener listener;

//    int boardSize = Math.min(getWidth(), getHeight());
//    int offsetX = (getWidth() - boardSize) / 2;
//    int offsetY = (getHeight() - boardSize) / 2;

    int lastRow = -1, lastCol = -1;

    Paint gridPaint = new Paint();
    Paint glowPaint = new Paint();
    Paint winPaint = new Paint();

    List<int[]> winningCells = new ArrayList<>();

    Paint paint = new Paint();

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size = Math.min(MeasureSpec.getSize(widthMeasureSpec),
                MeasureSpec.getSize(heightMeasureSpec));
        setMeasuredDimension(size, size);
    }
//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//
////        int cell = getWidth() / size;
////
////
////        // Draw grid
////        for (int i = 0; i < size; i++) {
////            canvas.drawLine(i * cell, 0, i * cell, getHeight(), paint);
////            canvas.drawLine(0, i * cell, getWidth(), i * cell, paint);
////        }
//
//        Paint gridPaint = new Paint();
//        gridPaint.setColor(Color.BLACK);
//        gridPaint.setStrokeWidth(3);
//        gridPaint.setAntiAlias(true);
//
//        int cell = boardSize / size;
//
//        for (int i = 0; i < size; i++) {
//            canvas.drawLine(offsetX + i * cell, offsetY,
//                    offsetX + i * cell, offsetY + boardSize, gridPaint);
//
//            canvas.drawLine(offsetX, offsetY + i * cell,
//                    offsetX + boardSize, offsetY + i * cell, gridPaint);
//        }
//
//        // Draw pieces
//        for (int r = 0; r < size; r++) {
//            for (int c = 0; c < size; c++) {
//                if (board[r][c] == 1) {
//                    paint.setColor(Color.BLACK);
//                    canvas.drawCircle(c * cell + cell/2, r * cell + cell/2, cell/3, paint);
//                } else if (board[r][c] == 2) {
//                    paint.setColor(Color.RED);
//                    canvas.drawCircle(c * cell + cell/2, r * cell + cell/2, cell/3, paint);
//                }
//            }
//        }
//    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int boardSize = Math.min(getWidth(), getHeight());
        int offsetX = (getWidth() - boardSize) / 2;
        int offsetY = (getHeight() - boardSize) / 2;

        int cell = boardSize / size;

        // 🔲 Grid draw
        for (int i = 0; i < size; i++) {
            canvas.drawLine(offsetX + i * cell, offsetY,
                    offsetX + i * cell, offsetY + boardSize, gridPaint);

            canvas.drawLine(offsetX, offsetY + i * cell,
                    offsetX + boardSize, offsetY + i * cell, gridPaint);
        }

        // 🟢 Winning highlight
        for (int[] pos : winningCells) {
            int r = pos[0];
            int c = pos[1];

            canvas.drawRect(
                    offsetX + c * cell,
                    offsetY + r * cell,
                    offsetX + (c + 1) * cell,
                    offsetY + (r + 1) * cell,
                    winPaint
            );
        }

        // ⚫ Pieces + glow
        Paint piecePaint = new Paint();
        piecePaint.setAntiAlias(true);

        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {

                if (board[r][c] != 0) {

                    if (board[r][c] == 1) {
                        piecePaint.setColor(Color.BLACK);
                    } else {
                        piecePaint.setColor(Color.RED);
                    }

                    float cx = offsetX + c * cell + cell / 2f;
                    float cy = offsetY + r * cell + cell / 2f;

                    canvas.drawCircle(cx, cy, cell / 3f, piecePaint);

                    // ✨ Glow for last move
                    if (r == lastRow && c == lastCol) {
                        canvas.drawCircle(cx, cy, cell / 2f, glowPaint);
                    }
                }
            }
        }
    }

    public boolean placeMove(int r, int c, int player) {
        if (board[r][c] == 0) {
            board[r][c] = player;

            lastRow = r;
            lastCol = c;

            invalidate();
            return true;
        }
        return false;
    }

    public int[][] getBoard() {
        return board;
    }

    public boolean checkWin(int r, int c) {

        int player = board[r][c];

        winningCells.clear();

        if (checkDirection(r, c, 1, 0, player)) return true;
        if (checkDirection(r, c, 0, 1, player)) return true;
        if (checkDirection(r, c, 1, 1, player)) return true;
        if (checkDirection(r, c, 1, -1, player)) return true;

        return false;

//        int player = board[r][c];
//
//        return count(r,c,1,0,player)+count(r,c,-1,0,player) >= 4 ||
//                count(r,c,0,1,player)+count(r,c,0,-1,player) >= 4 ||
//                count(r,c,1,1,player)+count(r,c,-1,-1,player) >= 4 ||
//                count(r,c,1,-1,player)+count(r,c,-1,1,player) >= 4;
    }

    private boolean checkDirection(int r, int c, int dr, int dc, int player) {

        List<int[]> temp = new ArrayList<>();
        temp.add(new int[]{r, c});

        // forward
        for (int i = 1; i < 5; i++) {
            int nr = r + dr * i;
            int nc = c + dc * i;

            if (nr < 0 || nc < 0 || nr >= size || nc >= size) break;

            if (board[nr][nc] == player) {
                temp.add(new int[]{nr, nc});
            } else break;
        }

        // backward
        for (int i = 1; i < 5; i++) {
            int nr = r - dr * i;
            int nc = c - dc * i;

            if (nr < 0 || nc < 0 || nr >= size || nc >= size) break;

            if (board[nr][nc] == player) {
                temp.add(new int[]{nr, nc});
            } else break;
        }

        if (temp.size() >= 5) {
            winningCells.clear();

            // सिर्फ 5 cells add करो
            for (int i = 0; i < 5; i++) {
                winningCells.add(temp.get(i));
            }

            return true;
        }

        return false;
    }

    private int count(int r, int c, int dr, int dc, int player) {
        int cnt = 0;
        for(int i=1;i<5;i++){
            int nr = r + dr*i, nc = c + dc*i;
            if(nr<0||nc<0||nr>=size||nc>=size||board[nr][nc]!=player) break;
            cnt++;
        }
        return cnt;
    }

    public BoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        gridPaint.setColor(Color.BLACK);
        gridPaint.setStrokeWidth(3);
        gridPaint.setAntiAlias(true);

        glowPaint.setColor(Color.YELLOW);
        glowPaint.setStyle(Paint.Style.STROKE);
        glowPaint.setStrokeWidth(6);

        winPaint.setColor(Color.GREEN);
        winPaint.setStyle(Paint.Style.FILL);

        //Glow effect
        paint.setShadowLayer(10, 0, 0, Color.CYAN);
        // hardware disable (essential to show shadow)
        setLayerType(LAYER_TYPE_SOFTWARE, paint);
    }

    public interface OnCellTouchListener {
        void onCellTouched(int row, int col);
    }

    private OnCellTouchListener listener;

    public void setOnCellTouchListener(OnCellTouchListener listener) {
        this.listener = listener;
    }

//    if (listener != null) {
//        listener.onCellTouched(row, col);   // ✅ यही call करो
//    }

    // जब touch हो तब call करो
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        if (event.getAction() == MotionEvent.ACTION_DOWN) {
//
//            int row = (int)(event.getY() / (getHeight() / 6));
//            int col = (int)(event.getX() / (getWidth() / 7));
//
//            if (listener != null) {
//                listener.onCellTouch(row, col);
//            }
//        }
//        return true;
//    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            int boardSize = Math.min(getWidth(), getHeight());
            int offsetX = (getWidth() - boardSize) / 2;
            int offsetY = (getHeight() - boardSize) / 2;

            int cell = boardSize / size;

            float x = event.getX();
            float y = event.getY();

            if (x < offsetX || y < offsetY ||
                    x > offsetX + boardSize || y > offsetY + boardSize) {
                return true;
            }

            // 👉 offset subtract करो
            int col = (int)((x - offsetX) / cell);
            int row = (int)((y - offsetY) / cell);



            // 👉 safe check
            if (row >= 0 && row < size && col >= 0 && col < size) {
                if (listener != null) {
                    listener.onCellTouched(row, col);
                }
            }

            return true;
        }
        return super.onTouchEvent(event);
    }

    public void resetBoard() {
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                board[r][c] = 0;
            }
        }

        winningCells.clear();
        lastRow = -1;
        lastCol = -1;
        //redraw
        invalidate();
    }

    //draw check full board
    public boolean isBoardFull() {
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                if (board[r][c] == 0) return false;
            }
        }
        return true;
    }

    //undo remove
    public void removeMove(int r, int c) {
        board[r][c] = 0;

        lastRow = -1;
        lastCol = -1;

        invalidate();
    }

}
