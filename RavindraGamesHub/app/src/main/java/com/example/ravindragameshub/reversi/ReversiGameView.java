package com.example.ravindragameshub.reversi;

import android.content.Context;
import android.graphics.*;
        import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.ravindragameshub.common.SoundManager;
import com.example.ravindragameshub.reversi.ReversiActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;

public class ReversiGameView extends View {

    //define variables
    Paint paint = new Paint();

    int[][] board = new int[8][8];
    float[][] animScale = new float[8][8];

    boolean blackTurn = true;
    boolean gameOver = false;

//    private ReversiActivity activity;

    List<ReversiGameState> history = new ArrayList<>();

    int undoCount = 0;
    int maxUndo = 1;

    String mode = "Player vs AI";

    public ReversiGameView(Context context,
                           ReversiActivity activity) {

        super(context);
        this.activity = activity;
    }

    private ReversiActivity activity;
    public void setActivity(ReversiActivity activity) {

        this.activity = activity;
    }

    public ReversiGameView(Context context) {
        super(context);
//        setLayerType(LAYER_TYPE_HARDWARE, null);
//        init(context);
        init();
    }

    public ReversiGameView(Context context, AttributeSet attrs) {
        super(context, attrs);
//        init(context);
        init();
    }

    public ReversiGameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        init(context);
        init();
    }

    private void init() {

        initGame();

        setFocusable(true);

        setFocusableInTouchMode(true);

    }

    void initGame() {

//        Log.d("REVERSI", "INIT");
        for(int i=0;i<8;i++)
            for(int j=0;j<8;j++)
                board[i][j]=0;

        board[3][3]=2;
        board[3][4]=1;
        board[4][3]=1;
        board[4][4]=2;

        blackTurn = true;
        gameOver = false;

        history.clear();
        undoCount = 0;

        updateScore();
        invalidate();

        if(activity != null) {
            activity.updateUndoText();
        }

        for(int i=0;i<8;i++) {

            for(int j=0;j<8;j++) {

                animScale[i][j] = 1f;
            }
        }
    }

    public void setDifficulty(String level){

        if(level.equals("Easy"))
            maxUndo = 1;
        else if(level.equals("Medium"))
            maxUndo = 3;
        else
            maxUndo = 5;

        initGame();
    }

    public void setMode(String m){
        mode = m;
        initGame();
    }

    //draw game board
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        Log.d("REVERSI", "DRAW");

//        canvas.drawColor(Color.RED);
//        float size = getWidth()/8f;

        float boardSize =
                Math.min(getWidth(), getHeight());

        float size = boardSize / 8f;

        //board draw
//        paint.setColor(Color.GREEN);
//        canvas.drawRect(0,0,getWidth(),getHeight(),paint);

        //board draw
        paint.setColor(Color.rgb(0,120,0));

        canvas.drawRect(
                0,
                0,
                boardSize,
                boardSize,
                paint
        );

        paint.setColor(Color.BLACK);

//        for(int i=0;i<=8;i++) {
//            canvas.drawLine(i*size,0,i*size,8*size,paint);
//            canvas.drawLine(0,i*size,8*size,i*size,paint);
//        }

        //Grid Lines
        paint.setShader(null);
        paint.clearShadowLayer();

        paint.setStyle(Paint.Style.STROKE);

        paint.setStrokeWidth(3);

        paint.setColor(Color.BLACK);

        for(int i=0;i<=8;i++) {

            canvas.drawLine(
                    i*size,
                    0,
                    i*size,
                    8*size,
                    paint
            );

            canvas.drawLine(
                    0,
                    i*size,
                    8*size,
                    i*size,
                    paint
            );
        }

        paint.setStyle(Paint.Style.FILL);

        //3d disc drawing loop
        for(int r=0;r<8;r++) {

            for(int c=0;c<8;c++) {

                if(board[r][c] != 0) {

                    float cx = c * size + size/2;
                    float cy = r * size + size/2;

                    float scaleX = animScale[r][c];

                    canvas.save();

                    canvas.translate(cx, cy);

                    canvas.scale(scaleX, 1f);

                    if(board[r][c] == 1) {

                        RadialGradient blackGradient =
                                new RadialGradient(
                                        -10,
                                        -10,
                                        size/2,
                                        Color.LTGRAY,
                                        Color.BLACK,
                                        Shader.TileMode.CLAMP
                                );

                        paint.setShader(blackGradient);

                        paint.setShadowLayer(
                                12,
                                4,
                                4,
                                Color.BLACK
                        );

                    } else {

                        RadialGradient whiteGradient =
                                new RadialGradient(
                                        -10,
                                        -10,
                                        size/2,
                                        Color.WHITE,
                                        Color.GRAY,
                                        Shader.TileMode.CLAMP
                                );

                        paint.setShader(whiteGradient);

                        paint.setShadowLayer(
                                12,
                                4,
                                4,
                                Color.DKGRAY
                        );
                    }

                    canvas.drawCircle(
                            0,
                            0,
                            Math.max(size/2 - 10, 8),
                            paint
                    );
                    canvas.restore();
                }
            }

            paint.setShader(null);
            paint.clearShadowLayer();
            paint.setStyle(Paint.Style.FILL);
        }

        //draw yellow dots
        paint.setColor(Color.YELLOW);

        for(int r=0;r<8;r++) {
            for(int c=0;c<8;c++) {

                if(isValidMove(r,c)) {

                    canvas.drawCircle(
                            c*size + size/2,
                            r*size + size/2,
                            8,
                            paint
                    );
                }
            }
        }
    }

    //on tap
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(event.getAction() != MotionEvent.ACTION_DOWN)
            return false;

        float size = getWidth() / 8f;

        int col = (int)(event.getX() / size);
        int row = (int)(event.getY() / size);

        if(row < 0 || row >= 8 ||
                col < 0 || col >= 8) {

            return false;
        }

        Log.d("REVERSI",
                "row=" + row +
                        " col=" + col);

        if(isValidMove(row, col)) {

            SoundManager.playClick();

            saveState();

            makeMove(row, col);

            blackTurn = !blackTurn;

            updateScore();

            invalidate();

        } else {

            SoundManager.playError();
        }

        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec,
                             int heightMeasureSpec) {

        int size = MeasureSpec.getSize(widthMeasureSpec);

        setMeasuredDimension(size, size);
    }

//    public boolean onTouchEvent(MotionEvent event) {
//
//        Log.d("REVERSI", "TOUCH");
//        if(gameOver)
//            return true;
//
////        if(event.getAction()!=MotionEvent.ACTION_DOWN)
////            return true;
//        if(event.getAction() == MotionEvent.ACTION_DOWN){
//
//            Log.d("REVERSI", "TOUCH WORKING");
//
//            invalidate();
//        }
//
////        float size = getWidth()/8f;
////
////        int col = (int)(event.getX()/size);
////        int row = (int)(event.getY()/size);
//
////        float size = getWidth() / 8f;
//
//        float boardSize =
//                Math.min(getWidth(), getHeight());
//
//        float size = boardSize / 8f;
//
//        float touchX = event.getX();
//        float touchY = event.getY();
//
//        int col = (int)(touchX / size);
//        int row = (int)(touchY / size);
//
//        if(row < 0 || row >= 8 ||
//                col < 0 || col >= 8) {
//
//            return true;
//        }
//
//        if(isValidMove(row,col)) {
//            SoundManager.playClick();
//
//            saveState();
//
//            makeMove(row,col);
//
//            if(activity != null) activity.updateUndoText();
//
//            blackTurn = !blackTurn;
//
//            updateScore();
//
//            invalidate();
//
//            if(mode.equals("Player vs AI") && !blackTurn) {
//
//                postDelayed(() -> aiMove(),500);
//            }
//        }else {
//
//            if(activity != null) {
//
//                SoundManager.playError();
//            }
//        }
//
//        return true;
//    }

    //AI Move
    void aiMove() {

        if(gameOver)
            return;

        List<int[]> moves = new ArrayList<>();

        int bestScore = -999999;
        int[] bestMove = null;

        for(int r=0;r<8;r++) {

            for(int c=0;c<8;c++) {

                if(isValidMove(r,c)) {

                    int score;

                    if(activity.selectedDifficulty.equals("Easy")) {

                        score = new Random().nextInt(20);

                    } else if(activity.selectedDifficulty.equals("Medium")) {

                        score = evaluateMediumMove(r,c);

                    } else {

                        score = evaluateHardMove(r,c);
                    }

                    if(score > bestScore) {

                        bestScore = score;
                        bestMove = new int[]{r,c};
                    }

                    moves.add(new int[]{r,c});
                }
            }
        }

        if(bestMove == null) {

            blackTurn = true;

            checkPassTurn();

            return;
        }

        saveState();

        makeMove(bestMove[0], bestMove[1]);

        blackTurn = true;

        updateScore();

        invalidate();
    }

    //Medium AI
    int evaluateMediumMove(int r,int c) {

        int score = 0;

        score += countFlips(r,c) * 10;

        if(r==0 || r==7 || c==0 || c==7)
            score += 20;

        if((r==0 && c==0) ||
                (r==0 && c==7) ||
                (r==7 && c==0) ||
                (r==7 && c==7)) {

            score += 80;
        }

        return score;
    }

    //Hard AI
    int evaluateHardMove(int r,int c) {

        int score = 0;

        // Corner
        if((r==0 && c==0) ||
                (r==0 && c==7) ||
                (r==7 && c==0) ||
                (r==7 && c==7)) {

            score += 1000;
        }

        // Edge
        if(r==0 || r==7 || c==0 || c==7) {

            score += 120;
        }

        // Avoid dangerous near-corner positions
        if((r==1 && c==1) ||
                (r==1 && c==6) ||
                (r==6 && c==1) ||
                (r==6 && c==6)) {

            score -= 200;
        }

        // Avoid near edges beside corners
        if((r==0 && (c==1 || c==6)) ||
                (r==7 && (c==1 || c==6)) ||
                (c==0 && (r==1 || r==6)) ||
                (c==7 && (r==1 || r==6))) {

            score -= 150;
        }

        // More flips
        score += countFlips(r,c) * 30;

        // Mobility reduction
        score -= opponentMoveCountAfterMove(r,c) * 25;

        return score;
    }

    //Flip Count
    int countFlips(int r,int c) {

        int flips = 0;

        int player = 2;
        int enemy = 1;

        int[] dx = {-1,-1,-1,0,0,1,1,1};
        int[] dy = {-1,0,1,-1,1,-1,0,1};

        for(int d=0; d<8; d++) {

            int rr = r + dx[d];
            int cc = c + dy[d];

            int temp = 0;

            while(rr>=0 && cc>=0 && rr<8 && cc<8) {

                if(board[rr][cc] == enemy) {

                    temp++;

                } else if(board[rr][cc] == player) {

                    flips += temp;
                    break;

                } else {

                    break;
                }

                rr += dx[d];
                cc += dy[d];
            }
        }

        return flips;
    }

    //Opponent Mobility
    int opponentMoveCountAfterMove(int r,int c) {

        int[][] temp = new int[8][8];

        for(int i=0;i<8;i++) {

            for(int j=0;j<8;j++) {

                temp[i][j] = board[i][j];
            }
        }

        boolean oldTurn = blackTurn;

        makeMove(r,c);

        blackTurn = true;

        int count = 0;

        for(int i=0;i<8;i++) {

            for(int j=0;j<8;j++) {

                if(isValidMove(i,j)) {

                    count++;
                }
            }
        }

        // Restore board
        for(int i=0;i<8;i++) {

            for(int j=0;j<8;j++) {

                board[i][j] = temp[i][j];
            }
        }

        blackTurn = oldTurn;

        return count;
    }

    //check for valid move
    boolean isValidMove(int r, int c) {

        if(r < 0 || c < 0 || r >= 8 || c >= 8)
            return false;

        if(board[r][c] != 0)
            return false;

        int player = blackTurn ? 1 : 2;
        int enemy = blackTurn ? 2 : 1;

        int[] dx = {-1,-1,-1,0,0,1,1,1};
        int[] dy = {-1,0,1,-1,1,-1,0,1};

        for(int d=0; d<8; d++) {

            int rr = r + dx[d];
            int cc = c + dy[d];

            boolean enemyFound = false;

            while(rr>=0 && cc>=0 && rr<8 && cc<8) {

                if(board[rr][cc] == enemy) {

                    enemyFound = true;

                } else if(board[rr][cc] == player) {

                    if(enemyFound)
                        return true;

                    break;

                } else {
                    break;
                }

                rr += dx[d];
                cc += dy[d];
            }
        }

        return false;
    }

    //make move
    void makeMove(int r,int c){
        int player = blackTurn ? 1 : 2;

        board[r][c] = player;
        animateFlip(r, c);

        flip(r,c,1,0);
        flip(r,c,-1,0);
        flip(r,c,0,1);
        flip(r,c,0,-1);
        flip(r,c,1,1);
        flip(r,c,-1,-1);
        flip(r,c,1,-1);
        flip(r,c,-1,1);
    }

    //flip
    void flip(int r,int c,int dr,int dc){

        int player = blackTurn ? 1 : 2;
        int enemy = blackTurn ? 2 : 1;

        int rr = r+dr;
        int cc = c+dc;

        List<int[]> list = new ArrayList<>();

        while(rr>=0 && cc>=0 && rr<8 && cc<8){

            if(board[rr][cc]==enemy){
                list.add(new int[]{rr,cc});
            } else if(board[rr][cc]==player){
                for(int[] p:list) {
                    board[p[0]][p[1]] = player;
                    animateFlip(p[0], p[1]);
                    if(activity != null) {
                        SoundManager.playFlip();
                    }
                }
                return;
            } else {
                return;
            }
            rr += dr;
            cc += dc;
        }
    }

    //save game state
    void saveState(){
        SoundManager.tapClick();
        history.add(new ReversiGameState(board,blackTurn));
    }

    //undo
    public void undo() {

        if(gameOver)
            return;

        if(undoCount >= maxUndo)
            return;

        if(mode.equals("Player vs AI")) {

            // Undo AI move
            if(history.size() > 0) {
                ReversiGameState s = history.remove(history.size()-1);
                restoreState(s);
            }

            // Undo Player move
            if(history.size() > 0) {
                ReversiGameState s = history.remove(history.size()-1);
                restoreState(s);
            }
            blackTurn = true;
        } else {
            if(history.size() > 0) {
                ReversiGameState s = history.remove(history.size()-1);
                restoreState(s);
            }
        }
        undoCount++;
        updateScore();
        invalidate();
        if(activity != null) activity.updateUndoText();
    }

    //restore state on undo
    void restoreState(ReversiGameState s) {

        for(int i=0;i<8;i++) {
            for(int j=0;j<8;j++) {
                board[i][j] = s.board[i][j];
            }
        }
        blackTurn = s.blackTurn;
    }

    //update remaining undo
    public int getRemainingUndo() {
        return maxUndo - undoCount;
    }

    //update score
    void updateScore() {

        int black = 0;
        int white = 0;

        for(int i=0;i<8;i++) {
            for(int j=0;j<8;j++) {
                if(board[i][j] == 1)
                    black++;
                if(board[i][j] == 2)
                    white++;
            }
        }

        if(activity != null) {
            activity.updateUI(
                    black,
                    white,
                    blackTurn
            );
        }
        checkGameOver();
        if(!gameOver) {

            checkPassTurn();
        }
        invalidate();
    }

    //check move possible
    boolean hasAnyMove() {

        for(int r=0;r<8;r++) {

            for(int c=0;c<8;c++) {

                if(isValidMove(r,c)) {

                    return true;
                }
            }
        }

        return false;
    }

    //check move possible for player
    boolean hasMoveForPlayer(boolean black) {

        boolean oldTurn = blackTurn;

        blackTurn = black;

        for(int r=0;r<8;r++) {
            for(int c=0;c<8;c++) {
                if(isValidMove(r,c)) {
                    blackTurn = oldTurn;
                    return true;
                }
            }
        }
        blackTurn = oldTurn;
        return false;
    }

    //check for game over
    void checkGameOver() {

        boolean blackHasMove = hasMoveForPlayer(true);
        boolean whiteHasMove = hasMoveForPlayer(false);

        boolean boardFull = true;

        int black = 0;
        int white = 0;

        for(int i=0;i<8;i++) {
            for(int j=0;j<8;j++) {
                if(board[i][j] == 0)
                    boardFull = false;
                if(board[i][j] == 1)
                    black++;
                if(board[i][j] == 2)
                    white++;
            }
        }

        // Game Over
        if(boardFull || (!blackHasMove && !whiteHasMove)) {

            gameOver = true;
            String result;
            if(black > white) {
                result = activity.player1 + " Wins";
                if(activity != null) {
                    SoundManager.playWin();
                }
            } else if(white > black) {
                result = activity.player2 + " Wins";
                if(activity != null) {
                    if(mode.equals("Player vs AI")) {
                        SoundManager.playLose();
                    } else {
                        SoundManager.playWin();
                    }
                }
            } else {
                result = "Game Draw";
                if(activity != null) {
                    SoundManager.playDraw();
                }
            }
            if(activity != null) {
                activity.txtStatus.setText(result);
            }
        }
    }

    //check for pass
    void checkPassTurn() {

        // Current player has move
        if(hasAnyMove()) {
            return;
        }

        // PASS current player
        blackTurn = !blackTurn;

        if(activity != null && !gameOver) {

            activity.txtStatus.setText(
                    "PASS - Next Turn"
            );
            SoundManager.playPass();
        }

        // Opponent also no move
        if(!hasAnyMove()) {

            checkGameOver();
            return;
        }

        updateScore();

        invalidate();

        // If AI turn after pass
        if(mode.equals("Player vs AI") && !blackTurn && !gameOver) {

            postDelayed(() -> aiMove(), 500);
        }
    }

    //flip animation
    void animateFlip(int r, int c) {

        ValueAnimator animator =
                ValueAnimator.ofFloat(1f, 0f, 1f);

        animator.setDuration(300);

        animator.addUpdateListener(animation -> {

            animScale[r][c] =
                    (float) animation.getAnimatedValue();

            invalidate();
        });

        animator.start();
    }
}
