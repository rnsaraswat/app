package com.example.connect4;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class WinningLineView extends View {

    private final Paint paint = new Paint();

    private float startX;
    private float startY;
    private float endX;
    private float endY;

    private boolean visible = false;

    public WinningLineView(Context context) {
        super(context);

        paint.setStrokeWidth(18f);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);

        startGlowAnimation();
    }

    public void setLine(
            float sx,
            float sy,
            float ex,
            float ey) {

        startX = sx;
        startY = sy;

        endX = ex;
        endY = ey;

        visible = true;

        invalidate();
    }

    public void clearLine() {

        visible = false;

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        if (!visible) return;

        canvas.drawLine(
                startX,
                startY,
                endX,
                endY,
                paint);
    }

    private void startGlowAnimation() {

        ValueAnimator animator =
                ValueAnimator.ofArgb(
                        Color.GREEN,
                        Color.RED,
                        Color.GREEN);

        animator.setDuration(700);

        animator.setRepeatCount(
                ValueAnimator.INFINITE);

        animator.addUpdateListener(a -> {

            paint.setColor(
                    (int) a.getAnimatedValue());

            invalidate();
        });

        animator.start();
    }
}