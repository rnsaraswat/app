package com.example.fiveinarow;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;

import java.util.*;

public class FireworkView extends View {

    List<Particle> particles = new ArrayList<>();
    Random rand = new Random();
    Paint paint = new Paint();

    public FireworkView(Context context) {
        super(context);
    }

    public FireworkView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FireworkView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void startFireworks() {

        particles.clear();

        int bursts = 50; // 👉 कितने bursts चाहिए (change कर सकते हो)

//        for (int i = 0; i < 120; i++) {
//            particles.add(new Particle(
//                    getWidth() / 2f,
//                    getHeight() / 2f
//            ));
//        }

        for (int b = 0; b < bursts; b++) {

            float cx = rand.nextInt(getWidth());
            float cy = rand.nextInt(getHeight() / 2) + getHeight() / 4;
            // 👉 center area में burst (top/bottom avoid)

            for (int i = 0; i < 80; i++) {
                particles.add(new Particle(cx, cy));
            }
        }

        invalidate();
    }

    public void startFireworksWithDelay() {

        particles.clear();

        postDelayed(() -> addBurst(), 0);
        postDelayed(() -> addBurst(), 300);
        postDelayed(() -> addBurst(), 600);
        postDelayed(() -> addBurst(), 900);
    }

    private void addBurst() {

        if (getWidth() == 0 || getHeight() == 0) return;  // safety

        float cx = rand.nextInt(getWidth());
        //for fireworks on full screen
        float cy = rand.nextInt(getHeight());
        //for fireworks on grid view only
        //float cy = rand.nextInt(getHeight() / 2) + getHeight() / 4;

        for (int i = 0; i < 80; i++) {
            particles.add(new Particle(cx, cy));
        }

        invalidate();
    }

    public void stopFireworks() {
        particles.clear();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Iterator<Particle> it = particles.iterator();

        while (it.hasNext()) {
            Particle p = it.next();

            p.update();

            paint.setColor(p.color);
            //Glow effect
            paint.setMaskFilter(new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL));
            //Particle trail fade
            paint.setAlpha(p.life * 4);
            canvas.drawCircle(p.x, p.y, p.size, paint);

            if (p.life <= 0) it.remove();
        }

        if (!particles.isEmpty()) {
            postInvalidateOnAnimation();
        }
    }

    class Particle {
        float x, y, dx, dy;
        //particle life - increase value (100) for smmoth fade otherwise 60
        int life = 100;
        int color;
        float size;

        Particle(float x, float y) {
            this.x = x;
            this.y = y;

            dx = (rand.nextFloat() - 0.5f) * 20;
            dy = (rand.nextFloat() - 0.5f) * 20;

            //size = rand.nextFloat() * 8 + 4;
            //Particle size random
            size = rand.nextFloat() * 10 + 3;

            color = Color.rgb(rand.nextInt(256),
                    rand.nextInt(256),
                    rand.nextInt(256));
        }

        void update() {
            x += dx;
            y += dy;
            dy += 0.5f; // gravity
            life--;
        }
    }
}
