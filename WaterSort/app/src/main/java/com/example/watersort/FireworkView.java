package com.example.watersort;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import java.util.Random;

public class FireworkView
        extends View {

    Paint paint =
            new Paint();

    Random random =
            new Random();

    public FireworkView(
            Context context) {

        super(context);
    }

    @Override
    protected void onDraw(
            Canvas canvas) {

        super.onDraw(canvas);

        for(int i=0;i<100;i++){

            paint.setColor(
                    Color.rgb(
                            random.nextInt(255),
                            random.nextInt(255),
                            random.nextInt(255)));

            canvas.drawCircle(
                    random.nextInt(getWidth()),
                    random.nextInt(getHeight()),
                    10,
                    paint);
        }
    }
}