package com.github.mikephil.charting.stockChart.customView;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import androidx.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * 闪烁的小圆点
 * Created by Yin Shudi on 2016/12/30.
 */

public class TwinklePoint extends View{

    private static final float DEFAULT_TWINKLE_RADIUS = 8.0f;
    private static final float DEFAULT_CENTER_RADIUS = 6.0f;
    private static final int DEFAULT_CENTER_COLOR = Color.parseColor("#F25D5D");
    private static final int DEFAULT_TWINKLE_COLOR = Color.parseColor("#FBAFAF");

    private Paint centerCirclePaint;
    private Paint twinkleCirclePaint;
    private float mTwinkleRadius;
    private float mTwinkleEndRadius = DEFAULT_TWINKLE_RADIUS;
    private ValueAnimator animator;
    private float mCenterRadius = DEFAULT_CENTER_RADIUS;

    public TwinklePoint(Context context) {
        super(context);
        init();
    }

    public TwinklePoint(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TwinklePoint(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mTwinkleRadius = mCenterRadius;

        centerCirclePaint = new Paint();
        centerCirclePaint.setAntiAlias(true);
        centerCirclePaint.setColor(DEFAULT_CENTER_COLOR);

        twinkleCirclePaint = new Paint();
        twinkleCirclePaint.setAntiAlias(true);
        twinkleCirclePaint.setColor(DEFAULT_TWINKLE_COLOR);


        animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(1000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // 获取到动画每次该变得float值，赋值给xpoint
                float progress = (Float)animation.getAnimatedValue();
                twinkleCirclePaint.setAlpha((int)((1-progress) * 255));
                mTwinkleRadius = mCenterRadius+(mTwinkleEndRadius-mCenterRadius)*progress;
                // 通知view重绘
                invalidate();
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mTwinkleRadius = mCenterRadius;
                invalidate();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mTwinkleEndRadius=w / 2;
        mCenterRadius = w / 4;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(getWidth()/2, getHeight()/2, mTwinkleRadius, twinkleCirclePaint);
        canvas.drawCircle(getWidth()/2, getHeight()/2, mCenterRadius, centerCirclePaint);

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        animator.cancel();
    }

    public void startTwinkle(){
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.start();
    }

    public void startTwinkle(int repeatCount){
        if(animator.isRunning()){
            animator.cancel();
        }
        animator.setRepeatCount(repeatCount);
        animator.start();
    }

    public void stopTwinkle(){
        animator.cancel();
        mTwinkleRadius = mCenterRadius;
        invalidate();
    }

    public boolean isTwinkling() {
        return animator.isRunning();
    }

    public void setCenterColor(@ColorInt int centerColor) {
        centerCirclePaint.setColor(centerColor);
    }

    public void setTwinkleColor(@ColorInt int twinkleColor) {
        twinkleCirclePaint.setColor(twinkleColor);
    }

    public void clearData() {
        mTwinkleRadius = mCenterRadius;
    }

    public static class Builder{
        private float twinkleEndRadius;
        private float centerRadius;
        private long duration;
        private int centerColor;
        private int twinkleColor;
        private float coordinateX;
        private float coordinateY;
        private Context context;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setTwinkleEndRadius(float twinkleEndRadius) {
            this.twinkleEndRadius = twinkleEndRadius;
            return this;
        }

        public Builder setCenterRadius(float centerRadius) {
            this.centerRadius = centerRadius;
            return this;
        }

        public Builder setDuration(long duration) {
            this.duration = duration;
            return this;
        }

        public Builder setCenterColor(int centerColor) {
            this.centerColor = centerColor;
            return this;
        }

        public Builder setTwinkleColor(int twinkleColor) {
            this.twinkleColor = twinkleColor;
            return this;
        }

        public Builder setCoordinate(float coordinateX,float coordinateY) {
            this.coordinateX = coordinateX;
            this.coordinateY = coordinateY;
            return this;
        }

        public TwinklePoint build(){
            TwinklePoint twinklePoint = new TwinklePoint(context);
            twinklePoint.mCenterRadius = centerRadius;
            twinklePoint.mTwinkleRadius = centerRadius; //闪烁半径的默认值与实心的半径保持一致
            twinklePoint.mTwinkleEndRadius = twinkleEndRadius;
            twinklePoint.animator.setDuration(duration);
            twinklePoint.centerCirclePaint.setColor(centerColor);
            twinklePoint.twinkleCirclePaint.setColor(twinkleColor);
            return twinklePoint;
        }
    }
}
