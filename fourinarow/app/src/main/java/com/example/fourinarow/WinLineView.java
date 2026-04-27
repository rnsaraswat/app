package com.example.fourinarow;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class WinLineView extends View {
    Paint paint = new Paint();
            float startX, startY, endX, endY;
    float progress = 0f;
    // 👇 XML inflation के लिए जरूरी
    public WinLineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    // 👇 optional लेकिन अच्छा practice
    public WinLineView(Context context) {
        super(context);
        init();
    }
    void init() {
        paint.setColor(0xFFFFFFFF);
        paint.setStrokeWidth(12f);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        // 🔥 Glow effect
        paint.setShadowLayer(10, 0, 0, 0xFFFFFFFF);
        // ⚠️ shadow दिखाने के लिए जरूरी
        setLayerType(LAYER_TYPE_SOFTWARE, paint);
    }
    public void setLine(float sx, float sy, float ex, float ey) {
        startX = sx;
        startY = sy;
        endX = ex;
        endY = ey;
    }
    public void setProgress(float p) {
        progress = p;
        invalidate();
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float stopX = startX + (endX - startX) * progress;
        float stopY = startY + (endY - startY) * progress;
        canvas.drawLine(startX, startY, stopX, stopY, paint);
    }
}

//public class WinLineView extends View {
//
//        private Paint paint;
//        private float sx, sy, ex, ey;
//
//        public WinLineView(Context context) {
//            super(context);
//            init();
//        }
//
//        public WinLineView(Context context, AttributeSet attrs) {
//            super(context, attrs);
//            init();
//        }
//
//        public WinLineView(Context context, AttributeSet attrs, int defStyleAttr) {
//            super(context, attrs, defStyleAttr);
//            init();
//        }
//
//        private void init() {
//            paint = new Paint();
//            paint.setColor(Color.WHITE);
//            paint.setStrokeWidth(12f);
//            paint.setStyle(Paint.Style.STROKE);
//
//            paint.setShadowLayer(10, 0, 0, Color.WHITE);
//            setLayerType(LAYER_TYPE_SOFTWARE, paint);
//        }
//
//        // ✅ सिर्फ यही method रहना चाहिए
//        public void setLine(float startX, float startY, float endX, float endY) {
//            this.sx = startX;
//            this.sy = startY;
//            this.ex = endX;
//            this.ey = endY;
//            invalidate();
//        }
//
//        @Override
//        protected void onDraw(Canvas canvas) {
//            super.onDraw(canvas);
//
//            if (sx == 0 && sy == 0 && ex == 0 && ey == 0) return;
//
//            canvas.drawLine(sx, sy, ex, ey, paint);
//        }
//
//        public void clearLine() {
//            sx = sy = ex = ey = 0f;
//            invalidate();
//        }
//    }