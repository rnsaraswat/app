package com.example.watersort;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.view.View;

public class TubeView extends View {

    private Tube tube;

    private boolean selected = false;

    private Paint borderPaint;
    private Paint waterPaint;
    private Paint selectPaint;

    private Paint glassPaint;
    private Paint shinePaint;
    private Paint glowPaint;

    private float glowAlpha = 255;

    private float animationProgress = 1f;
    private float animatedLevel = 1f;
    private float wobbleOffset = 0f;

    private float pourProgress = 0f;

    private boolean pouring = false;

    private int pouringColor;

    public TubeView(Context context) {
        super(context);

        init();
    }

    private void init() {

        borderPaint = new Paint();
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(8);
        borderPaint.setColor(Color.DKGRAY);
        borderPaint.setAntiAlias(true);

        waterPaint = new Paint();
        waterPaint.setStyle(Paint.Style.FILL);
        waterPaint.setAntiAlias(true);

        selectPaint = new Paint();
        selectPaint.setStyle(Paint.Style.STROKE);
        selectPaint.setStrokeWidth(14);
        selectPaint.setColor(Color.GREEN);
        selectPaint.setAntiAlias(true);
//    }
//
//    private void init() {

        glassPaint = new Paint();
        glassPaint.setStyle(Paint.Style.STROKE);
        glassPaint.setStrokeWidth(8);
        glassPaint.setColor(Color.WHITE);
        glassPaint.setAntiAlias(true);

        shinePaint = new Paint();
        shinePaint.setColor(
                Color.argb(
                        120,
                        255,
                        255,
                        255));

        glowPaint = new Paint();
        glowPaint.setStyle(Paint.Style.STROKE);
        glowPaint.setStrokeWidth(16);
        glowPaint.setColor(Color.GREEN);
        glowPaint.setAntiAlias(true);
        glowPaint.setAlpha(
                (int) glowAlpha);

        waterPaint = new Paint();
        waterPaint.setAntiAlias(true);
    }

    public void setTube(Tube tube) {
        this.tube = tube;
        invalidate();
    }

    public Tube getTube() {
        return tube;
    }

    public void setSelectedTube(boolean selected) {
        this.selected = selected;
//        invalidate();

        ValueAnimator animator =
                ValueAnimator.ofFloat(
                        0f,
                        1f);

        animator.setDuration(250);

        animator.addUpdateListener(a -> {

            animationProgress =
                    (float)
                            a.getAnimatedValue();

            invalidate();
        });

        animator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {

//        super.onDraw(canvas);
//
//        if (tube == null)
//            return;
//
//        float left = 20;
//        float top = 20;
//        float right = getWidth() - 20;
//        float bottom = getHeight() - 20;
//
//        RectF bottle =
//                new RectF(
//                        left,
//                        top,
//                        right,
//                        bottom
//                );
//
//        // Draw Bottle
//
//        canvas.drawRoundRect(
//                bottle,
//                20,
//                20,
//                borderPaint
//        );
//
//        // Selection Highlight
//
//        if (selected) {
//
//            canvas.drawRoundRect(
//                    bottle,
//                    20,
//                    20,
//                    selectPaint
//            );
//        }
//
//        bottom =
//                bottom *
//                        animationProgress;
//
//        drawWater(canvas);


        super.onDraw(canvas);

        float w = getWidth();
        float h = getHeight();

        drawGlassTube(
                canvas,
                w,
                h);

        drawWater(canvas);

        drawShine(
                canvas,
                w,
                h);

        float left = w * 0.25f;
        float right = w * 0.75f;

        //adjust tube height
//        float top = 30;
//        float bottom = h - 20;

//        float top = 50;
//        float bottom = h - 60;

        float top = h * 0.08f;
        float bottom = h * 0.88f;

        // Left Wall
        canvas.drawLine(
                left,
                top,
                left,
                bottom,
                borderPaint);

        // Right Wall
        canvas.drawLine(
                right,
                top,
                right,
                bottom,
                borderPaint);

        // Bottom Curve
        RectF arcRect =
                new RectF(
                        left,
                        bottom - 40,
                        right,
                        bottom + 40);

        canvas.drawArc(
                arcRect,
                0,
                180,
                false,
                borderPaint);

        // Tube Mouth
        canvas.drawLine(
                left,
                top,
                left + 20,
                top,
                borderPaint);

        canvas.drawLine(
                right - 20,
                top,
                right,
                top,
                borderPaint);

        if (selected) {

            Paint glow =
                    new Paint(borderPaint);

            glow.setColor(Color.GREEN);
            glow.setStrokeWidth(14);

            canvas.drawLine(
                    left,
                    top,
                    left,
                    bottom,
                    glow);

            canvas.drawLine(
                    right,
                    top,
                    right,
                    bottom,
                    glow);
        }

        if (pouring) {

            drawPourStream(canvas);
        }

        drawWater(canvas);
    }

    private void drawGlassTube(
            Canvas canvas,
            float w,
            float h) {

        float left = w * 0.25f;
        float right = w * 0.75f;

        float neckLeft =
                w * 0.35f;

        float neckRight =
                w * 0.65f;

        float top = 25;

        float bodyTop = 60;

        float bottom =
                h - 20;

        if (selected) {

            canvas.drawLine(
                    left,
                    bodyTop,
                    left,
                    bottom,
                    glowPaint);

            canvas.drawLine(
                    right,
                    bodyTop,
                    right,
                    bottom,
                    glowPaint);
        }

        // Neck

        canvas.drawLine(
                neckLeft,
                top,
                neckLeft,
                bodyTop,
                glassPaint);

        canvas.drawLine(
                neckRight,
                top,
                neckRight,
                bodyTop,
                glassPaint);

        // Body

        canvas.drawLine(
                left,
                bodyTop,
                left,
                bottom,
                glassPaint);

        canvas.drawLine(
                right,
                bodyTop,
                right,
                bottom,
                glassPaint);

        // Bottom Curve

        RectF arc =
                new RectF(
                        left,
                        bottom - 40,
                        right,
                        bottom + 40);

        canvas.drawArc(
                arc,
                0,
                180,
                false,
                glassPaint);
    }

    private void drawWater(Canvas canvas) {

        int count =
                tube.getColors().size();

        if (count == 0)
            return;

        float layerHeight =
                (getHeight() - 50f)
                        / Tube.CAPACITY;

        for (int i = 0; i < count; i++) {

            int color =
                    tube.getColors().get(i);

            //            float left = 25;
//            float right = getWidth() - 25;
            float left = getWidth() * 0.27f;
            float right = getWidth() * 0.73f;

            float bottom =
                    getHeight() - 25
                            - (i * layerHeight);

            float top =
                    bottom - layerHeight;

            bottom =
                    bottom *
                            animatedLevel;
//            waterPaint.setColor(color);

            LinearGradient gradient =
                    new LinearGradient(
                            left,
                            top,
                            right,
                            bottom,

                            lighten(color),

                            color,

                            Shader.TileMode.CLAMP);

            waterPaint.setShader(
                    gradient);



            Paint topPaint =
                    new Paint();

            topPaint.setColor(
                    Color.argb(
                            80,
                            255,
                            255,
                            255));

            canvas.drawRect(
                    left,
                    top,
                    right,
                    top + 6,
                    topPaint);


//            canvas.drawRect(
//                    left,
//                    top,
//                    right,
//                    bottom,
//                    waterPaint
//            );

            RectF waterRect =
                    new RectF(
                            left,
                            top,
                            right,
                            bottom);

//            canvas.drawRoundRect(
//                    waterRect,
//                    12,
//                    12,
//                    waterPaint);

            canvas.drawRoundRect(
                    waterRect,
                    16,
                    16,
                    waterPaint);


        }
    }

    private int lighten(
            int color) {

        int r =
                Math.min(
                        Color.red(color)+60,
                        255);

        int g =
                Math.min(
                        Color.green(color)+60,
                        255);

        int b =
                Math.min(
                        Color.blue(color)+60,
                        255);

        return Color.rgb(r,g,b);
    }

    private void drawShine(
            Canvas canvas,
            float w,
            float h) {

        float shineLeft =
                w * 0.33f;

        float shineRight =
                w * 0.38f;

        RectF shine =
                new RectF(
                        shineLeft,
                        80,
                        shineRight,
                        h - 80);

        canvas.drawRoundRect(
                shine,
                20,
                20,
                shinePaint);
    }

    private void startGlow() {

        ValueAnimator anim =
                ValueAnimator.ofFloat(
                        50,
                        255);

        anim.setDuration(700);

        anim.setRepeatCount(
                ValueAnimator.INFINITE);

        anim.setRepeatMode(
                ValueAnimator.REVERSE);

        anim.addUpdateListener(a -> {

            glowAlpha =
                    (float)
                            a.getAnimatedValue();

            invalidate();
        });

        anim.start();
    }

    private void drawLiquidLayer(
            Canvas canvas,
            RectF rect,
            Paint paint) {

        Path path = new Path();

        path.moveTo(
                rect.left,
                rect.bottom);

        path.lineTo(
                rect.left,
                rect.top);

        float center =
                (rect.left + rect.right) / 2;

        path.quadTo(
                center,
                rect.top + wobbleOffset,
                rect.right,
                rect.top);

        path.lineTo(
                rect.right,
                rect.bottom);

        path.close();

        canvas.drawPath(
                path,
                paint);
    }

    public void startWobble() {

        ValueAnimator animator =
                ValueAnimator.ofFloat(
                        -12f,
                        12f);

        animator.setDuration(200);

        animator.setRepeatCount(3);

        animator.setRepeatMode(
                ValueAnimator.REVERSE);

        animator.addUpdateListener(a -> {

            wobbleOffset =
                    (float)
                            a.getAnimatedValue();

            invalidate();
        });

        animator.start();
    }

    public void startPour(
            int color) {

        pouring = true;

        pouringColor = color;

        ValueAnimator animator =
                ValueAnimator.ofFloat(
                        0f,
                        1f);

        animator.setDuration(400);

        animator.addUpdateListener(a -> {

            pourProgress =
                    (float)
                            a.getAnimatedValue();

            invalidate();
        });

        animator.addListener(
                new AnimatorListenerAdapter() {

                    @Override
                    public void onAnimationEnd(
                            Animator animation) {

                        pouring = false;
                        pourProgress = 0f;

                        invalidate();
                    }
                });

        animator.start();
    }

    private void drawPourStream(
            Canvas canvas) {

        Paint p = new Paint();

        p.setColor(pouringColor);

        p.setStrokeWidth(16);

        p.setStrokeCap(
                Paint.Cap.ROUND);

        float startX =
                getWidth() * 0.70f;

        float startY = 70;

        float endX =
                getWidth() + 50;

        float endY =
                getHeight() *
                        pourProgress;

        canvas.drawLine(
                startX,
                startY,
                endX,
                endY,
                p);
    }

    public void animateLevel() {

        ValueAnimator animator =
                ValueAnimator.ofFloat(
                        0f,
                        1f);

        animator.setDuration(250);

        animator.addUpdateListener(a -> {

            animatedLevel =
                    (float)
                            a.getAnimatedValue();

            invalidate();
        });

        animator.start();
    }

    public void splash() {

        ValueAnimator animator =
                ValueAnimator.ofFloat(
                        1f,
                        1.08f,
                        1f);

        animator.setDuration(180);

        animator.addUpdateListener(a -> {

            float scale =
                    (float) a.getAnimatedValue();

            setScaleX(scale);
            setScaleY(scale);
        });

        animator.start();
    }
}