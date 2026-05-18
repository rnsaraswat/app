package com.example.ravindragameshub.reversi;

import android.content.Context;
import android.graphics.*;
        import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.ravindragameshub.common.SoundManager;
import com.example.ravindragameshub.reversi.ReversiActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ReversiGameView extends View {

    //define variables
    Paint paint = new Paint();

    int[][] board = new int[8][8];

    boolean blackTurn = true;
    boolean gameOver = false;

    private ReversiActivity activity;

    List<ReversiGameState> history = new ArrayList<>();

    int undoCount = 0;
    int maxUndo = 1;

    String mode = "Player vs AI";

    public ReversiGameView(Context context,
                           ReversiActivity activity) {

        super(context);
        this.activity = activity;
    }

    public ReversiGameView(Context context) {
        super(context);
        init(context);
    }

    public ReversiGameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ReversiGameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {

        if(context instanceof ReversiActivity) {
            activity = (ReversiActivity) context;
        }
    }

    void initGame() {

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

        float size = getWidth()/8f;

        paint.setColor(Color.GREEN);
        canvas.drawRect(0,0,getWidth(),getHeight(),paint);

        paint.setColor(Color.BLACK);

        for(int i=0;i<=8;i++) {
            canvas.drawLine(i*size,0,i*size,8*size,paint);
            canvas.drawLine(0,i*size,8*size,i*size,paint);
        }

        for(int r=0;r<8;r++) {
            for(int c=0;c<8;c++) {

                if(board[r][c]==1) {
                    paint.setColor(Color.BLACK);

                    canvas.drawCircle(
                            c*size+size/2,
                            r*size+size/2,
                            size/2-10,
                            paint
                    );
                }

                if(board[r][c]==2) {
                    paint.setColor(Color.WHITE);

                    canvas.drawCircle(
                            c*size+size/2,
                            r*size+size/2,
                            size/2-10,
                            paint
                    );
                }
            }
        }

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

        if(gameOver)
            return true;

        if(event.getAction()!=MotionEvent.ACTION_DOWN)
            return true;

        float size = getWidth()/8f;

        int col = (int)(event.getX()/size);
        int row = (int)(event.getY()/size);

        if(isValidMove(row,col)) {
            SoundManager.playClick();

            saveState();

            makeMove(row,col);

            if(activity != null) activity.updateUndoText();

            blackTurn = !blackTurn;

            updateScore();

            invalidate();

            if(mode.equals("Player vs AI") && !blackTurn) {

                postDelayed(() -> aiMove(),500);
            }
        }
        SoundManager.playError();
        return true;
    }

    //AI Move
    void aiMove() {

        if(gameOver)
            return;

        List<int[]> moves = new ArrayList<>();

        int bestScore = -999;
        int[] bestMove = null;

        for(int r=0;r<8;r++) {
            for(int c=0;c<8;c++) {

                if(isValidMove(r,c)) {
                    SoundManager.playClick();

                    int score = evaluateMove(r,c);

                    if(score > bestScore) {

                        bestScore = score;
                        bestMove = new int[]{r,c};
                    }

                    moves.add(new int[]{r,c});
                }
                SoundManager.playError();
            }
        }

        if(moves.size()==0) {

            blackTurn = true;

            if(activity != null) {
                SoundManager.playPass();
                activity.txtStatus.setText("AI PASS");
            }
            return;
        }

        int[] move;

        if(activity.selectedDifficulty.equals("Easy")) {

            move = moves.get(new Random().nextInt(moves.size()));

        } else if(activity.selectedDifficulty.equals("Medium")) {

            move = bestMove;

        } else {

            move = bestMove;
        }

        saveState();

        makeMove(move[0], move[1]);

        if(activity != null) activity.updateUndoText();

        blackTurn = true;

        updateScore();

        invalidate();
    }

    //evaluation AI move
    int evaluateMove(int r,int c) {

        int score = 0;

        // Corner priority
        if((r==0 && c==0) ||
                (r==0 && c==7) ||
                (r==7 && c==0) ||
                (r==7 && c==7)) {

            score += 100;
        }

        // Edge priority
        if(r==0 || r==7 || c==0 || c==7) {
            score += 20;
        }

        return score;
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
                    SoundManager.playFlip();
                    board[p[0]][p[1]] = player;
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
            } else if(white > black) {
                result = activity.player2 + " Wins";
            } else {
                result = "Game Draw";
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
}
