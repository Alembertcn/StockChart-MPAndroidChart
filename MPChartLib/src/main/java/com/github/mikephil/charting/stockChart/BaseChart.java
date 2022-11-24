package com.github.mikephil.charting.stockChart;

import android.content.Context;
import android.graphics.Paint;

import androidx.annotation.Nullable;

import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;

import com.github.mikephil.charting.stockChart.charts.CoupleChartGestureListener;
import com.github.mikephil.charting.stockChart.dataManage.KLineDataManage;
import com.github.mikephil.charting.stockChart.dataManage.TimeDataManage;
import com.github.mikephil.charting.stockChart.event.BaseEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.function.Consumer;

public class BaseChart extends LinearLayout {

    public boolean landscape = false;//横屏还是竖屏
    public int precision = 3;//小数精度
    public Paint mPaint;

    public BaseChart(Context context) {
        this(context, null);
    }

    public OnHighlightValueSelectedListener mHighlightValueSelectedListener;
    public CoupleChartGestureListener gestureListenerLine;
    public CoupleChartGestureListener gestureListenerBar;
    public CoupleChartGestureListener gestureListenerCandle;

    public BaseChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    public void setPrecision(int precision) {
        this.precision = precision;
    }

    public void setHighlightValueSelectedListener(OnHighlightValueSelectedListener l) {
        mHighlightValueSelectedListener = l;
    }

    public interface OnHighlightValueSelectedListener {
        void onDayHighlightValueListener(TimeDataManage mData, int index, boolean isSelect);

        void onKHighlightValueListener(KLineDataManage data, int index, boolean isSelect);
    }

    public CoupleChartGestureListener getGestureListenerLine() {
        return gestureListenerLine;
    }

    public CoupleChartGestureListener getGestureListenerBar() {
        return gestureListenerBar;
    }

    public CoupleChartGestureListener getGestureListenerCandle() {
        return gestureListenerCandle;
    }

    /**
     * 分时图最后一点的圆圈无限动画
     *
     * @param heartbeatView
     */
    public void playHeartbeatAnimation(final View heartbeatView) {
        playHeartbeatAnimation(heartbeatView, -1);
    }

    /**
     * 分时图最后一点的圆圈动画
     *
     * @param heartbeatView
     * @param repeatCount 重复次数
     */
    public void playHeartbeatAnimation(final View heartbeatView,int repeatCount) {
        AnimationSet swellAnimationSet = new AnimationSet(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 2.0f, 1.0f, 2.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setRepeatMode(AnimationSet.REVERSE);
        scaleAnimation.setDuration(1000);
        scaleAnimation.setRepeatCount(repeatCount);
        scaleAnimation.setFillBefore(true);
        swellAnimationSet.addAnimation(scaleAnimation);
        swellAnimationSet.setInterpolator(new AccelerateInterpolator());
        heartbeatView.startAnimation(swellAnimationSet);
    }

    public void stopHeartbeatAnimation(View heartbeatView) {
        AnimationSet animation = (AnimationSet) heartbeatView.getAnimation();
        if(animation!=null && animation.hasStarted()){
            animation.cancel();
            List<Animation> animations = animation.getAnimations();
            for (int i = 0; i < animations.size(); i++) {
                animations.get(i).cancel();
            }
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(BaseEvent event) {

    }

}
