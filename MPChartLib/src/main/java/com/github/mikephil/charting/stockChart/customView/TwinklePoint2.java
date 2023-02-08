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

public class TwinklePoint2 extends View{

    private static final float DEFAULT_TWINKLE_RADIUS = 12.0f;
    private static final float DEFAULT_CENTER_RADIUS = 4.0f;
    private static final int DEFAULT_CENTER_COLOR = Color.parseColor("#F25D5D");
    private static final int DEFAULT_TWINKLE_COLOR = Color.parseColor("#FBAFAF");

    private Paint centerCirclePaint;
    private Paint twinkleCirclePaint;
    private Paint linePaint;
    private float mCoordinateX;
    private float mData;
    private float mTwinkleRadius;
    private float mTwinkleEndRadius = DEFAULT_TWINKLE_RADIUS;
    private ValueAnimator animator;
    private float mCenterRadius = DEFAULT_CENTER_RADIUS;
    private float mCoordinateY;
    private float startX;
    private float startY;

    public TwinklePoint2(Context context) {
        super(context);
        init();
    }

    public TwinklePoint2(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TwinklePoint2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mTwinkleRadius = mCenterRadius;

        centerCirclePaint = new Paint();
        centerCirclePaint.setAntiAlias(true);
//        centerCirclePaint.setColor(DEFAULT_CENTER_COLOR);

        twinkleCirclePaint = new Paint();
        twinkleCirclePaint.setAntiAlias(true);
//        twinkleCirclePaint.setColor(DEFAULT_TWINKLE_COLOR);

        linePaint = new Paint();
        linePaint.setAntiAlias(true);

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
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mCoordinateX == 0) { //横坐标为0属于异常情况,不画点
            return;
        }
        if(startX != 0 || startY != 0){
            canvas.drawLine(startX,startY,mCoordinateX,mCoordinateY,linePaint);
        }
        canvas.drawCircle(mCoordinateX, mCoordinateY, mTwinkleRadius, twinkleCirclePaint);
        canvas.drawCircle(mCoordinateX, mCoordinateY, mCenterRadius, centerCirclePaint);

//        if(mTwinkleRadius < 20){
//            mTwinkleRadius++;
//        }else{
//            mTwinkleRadius = mCenterRadius;
//        }
//        invalidate();
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

    public void setData(float data) {
        this.mData = data;
    }

    public float getData() {
        return mData;
    }

    public void setCenterColor(@ColorInt int centerColor) {
        centerCirclePaint.setColor(centerColor);
    }

    public void setTwinkleColor(@ColorInt int twinkleColor) {
        twinkleCirclePaint.setColor(twinkleColor);
    }

    public void setLineColor(@ColorInt int twinkleColor) {
        linePaint.setColor(twinkleColor);
    }

    public void setLineWidth(float px) {
        linePaint.setStrokeWidth(px);
    }

    public float getCoordinateX() {
        return mCoordinateX;
    }


    public float getSrcY() {
        return srcY;
    }

    public void setCoordinateX(float coordinateX) {
        this.mCoordinateX = coordinateX;
    }

    public float getCoordinateY() {
        return mCoordinateY;
    }

    public void setSrcY(float srcY) {
        this.srcY = srcY;
    }

    float srcY;
    public void setCoordinateY(float coordinateY) {
        this.mCoordinateY = coordinateY;
    }

    public void setLineStartPoint(float startX,float startY) {
        this.startX = startX;
        this.startY = startY;
    }

    public void clearData() {
        startX = 0;
        startY = 0;
        mCoordinateX = 0;
        mCoordinateY = 0;
        mData = 0;
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

        public TwinklePoint2 build(){
            TwinklePoint2 twinklePoint = new TwinklePoint2(context);
            twinklePoint.mCenterRadius = centerRadius;
            twinklePoint.mTwinkleRadius = centerRadius; //闪烁半径的默认值与实心的半径保持一致
            twinklePoint.mTwinkleEndRadius = twinkleEndRadius;
            twinklePoint.animator.setDuration(duration);
            twinklePoint.centerCirclePaint.setColor(centerColor);
            twinklePoint.twinkleCirclePaint.setColor(twinkleColor);
            twinklePoint.mCoordinateX = coordinateX;
            twinklePoint.mCoordinateY = coordinateY;
            return twinklePoint;
        }
    }
}
