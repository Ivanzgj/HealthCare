package com.ivan.healthcare.healthcare_android.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.ivan.healthcare.healthcare_android.AppContext;

/**
 * 圆环型的进度条
 * Created by Ivan on 16/4/1.
 */
public class CircleProgressView extends View {

    private static final int PROGREE_STROKE_WIDTH = AppContext.dp2px(5);
    private static final int PROGRESS_STROKE_COLOR = Color.rgb(0x00, 0xBC, 0xD4);
    private static final int PROGRESS_BACKGROUND_COLOR = Color.rgb(0xB6, 0xB6, 0xB6);
    private static final int PROGRESS_MILLIS = 2500;

    private RectF rect;

    private float progress = 0.f;

    private Paint paint;

    public CircleProgressView(Context context) {
        super(context);
        paint = new Paint();
        rect = new RectF(0, 0, 0, 0);
    }

    public CircleProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        rect = new RectF(0, 0, 0, 0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getWidth();
        int height = getHeight();
        int radius;
        if (width > height) {
            radius = height;
            rect.set(width/2-radius*0.4f, radius*0.1f, width/2+radius*0.4f, radius*0.9f);
        } else {
            radius = width;
            rect.set(radius*0.1f, height/2-radius*0.4f, radius*0.9f, height/2+radius*0.9f);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);

        paint.setStrokeWidth(PROGREE_STROKE_WIDTH);
        paint.setColor(PROGRESS_BACKGROUND_COLOR);
        canvas.drawArc(rect, progress * 360 - 90, (1 - progress) * 360, false, paint);

        paint.setColor(PROGRESS_STROKE_COLOR);
        canvas.drawArc(rect, -90, progress * 360, false, paint);

    }

    public void setProgress(float progress) {
        _animate(progress);
        this.progress = progress;
    }

    private void _animate(float progress) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(this.progress, progress);
        valueAnimator.setDuration((long) (Math.abs(this.progress - progress) * PROGRESS_MILLIS));
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                CircleProgressView.this.progress = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        valueAnimator.start();
    }
}
