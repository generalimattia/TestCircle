package com.generals.testcircle.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;

import com.generals.testcircle.R;

public class CircleSetting extends View {

    private Paint circlePaint;

    private float radius;
    private float minXCenter1;
    private float minYCenter1;
    private float minRadius;
    private float minYDelta;
    private float minXDelta;
    private PointF center;
    private double angle;

    private boolean isFirstRendering = true;
    private boolean isTouchInsideCircle;
    private boolean isAnimating;

    private float minXTarget1;
    private float minYTarget1;

    public CircleSetting(Context context) {
        super(context);
        init(null, 0);
    }

    public CircleSetting(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public CircleSetting(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        //final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CircleSetting, defStyle, 0);
        //a.recycle();
        inflate(getContext(), R.layout.widget_circle_setting, null);

        circlePaint = new Paint();
        int strokeWidth = getResources().getDimensionPixelSize(R.dimen.circle_width);
        circlePaint.setStrokeWidth(strokeWidth);
    }

    private void initValues() {
        radius = (getWidth() - (getPaddingRight() * 2)) / 2;
        final float xCenter = getWidth() / 2;
        final float yCenter = getHeight() / 2;

        minRadius = radius / 4;
        minXCenter1 = xCenter;
        minYCenter1 = yCenter - radius;
        center = new PointF(xCenter, yCenter);

        minXTarget1 = xCenter - radius;
        minYTarget1 = yCenter;

        Log.i(getClass().getSimpleName(), "XCENTER: " + minXCenter1);
        Log.i(getClass().getSimpleName(), "YCENTER: " + minYCenter1 + "\n");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isFirstRendering) {
            initValues();
            isFirstRendering = false;
        }

        circlePaint.setAntiAlias(true);
        circlePaint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(center.x, center.y, radius, circlePaint);

        circlePaint.setStyle(Paint.Style.FILL);

        if(isAnimating) {
            angle *= 2;
            PointF pointOnCircle = getPositionOnCircle(center, radius, angle);
        }
        canvas.drawCircle(minXCenter1, minYCenter1, minRadius, circlePaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        moveCircle(event);
        // Schedules a repaint.
        invalidate();
        return isTouchInsideCircle;

    }

    private void moveCircle(MotionEvent event) {
        float eventX = event.getX();
        float eventY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isTouchInsideCircle = detectIfCircleIsPressed(eventX, eventY);

            case MotionEvent.ACTION_MOVE:
                if (isTouchInsideCircle) {
                    minXDelta = eventX - center.x;
                    minYDelta = eventY - center.y;

                    Log.i(getClass().getSimpleName(), "eventX: " + eventX);
                    Log.i(getClass().getSimpleName(), "eventY: " + eventY);
                    Log.i(getClass().getSimpleName(), "minXDelta: " + minXDelta);
                    Log.i(getClass().getSimpleName(), "minYDelta: " + minYDelta);

                    final double previousAngle = angle;
                    angle = getAngle(minXDelta, minYDelta);

                    Log.i(getClass().getSimpleName(), "angle: " + angle);
                    Log.i(getClass().getSimpleName(), "delta Angle: " + (angle - previousAngle));

                    PointF pointOnCircle = getPositionOnCircle(center, radius, angle);

                    minXCenter1 = pointOnCircle.x;
                    minYCenter1 = pointOnCircle.y;

                    Log.i(getClass().getSimpleName(), "XCENTER: " + minXCenter1);
                    Log.i(getClass().getSimpleName(), "YCENTER: " + minYCenter1 + "\n");
                }

        }
    }

    private boolean detectIfCircleIsPressed(float eventX, float eventY) {
        if (eventX > (minXCenter1 - minRadius) && eventX < (minXCenter1 + minRadius)) {
            if (eventY > (minYCenter1 - minRadius) && eventY < (minYCenter1 + minRadius)) {
                return true;
            }
        }
        return false;
    }

    private double getAngle(float deltaX, float deltaY) {
        double angle = Math.toDegrees(Math.atan2(deltaY, deltaX));

        if (angle < 0) {
            angle += 360;
        }

        return angle;
    }

    private PointF getPositionOnCircle(PointF center, float radius, double angle) {

        return new PointF((float) (center.x + radius * Math.cos(Math.toRadians(angle))),
                (float) (center.y + radius * Math.sin(Math.toRadians(angle))));
    }
}
