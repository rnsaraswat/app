package com.example.tictactoe;

import android.content.Context;
import android.graphics.*;
import android.view.View;
import java.util.*;

public class FireworksView extends View {

    Paint paint = new Paint();
    List<Particle> particles = new ArrayList<>();
    Random rand = new Random();

    public FireworksView(Context context) {
        super(context);
    }

    void start() {
        particles.clear();

        for (int i = 0; i < 80; i++) {
            particles.add(new Particle(
                    getWidth()/2,
                    getHeight()/2,
                    rand.nextInt(10) - 5,
                    rand.nextInt(10) - 5,
                    Color.rgb(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255))
            ));
        }

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (Particle p : particles) {
            paint.setColor(p.color);
            canvas.drawCircle(p.x, p.y, 6, paint);

            p.x += p.dx;
            p.y += p.dy;
        }

        if (!particles.isEmpty()) {
            postInvalidateDelayed(30);
        }
    }

    static class Particle {
        float x, y, dx, dy;
        int color;

        Particle(float x, float y, float dx, float dy, int color) {
            this.x = x;
            this.y = y;
            this.dx = dx;
            this.dy = dy;
            this.color = color;
        }
    }
}
