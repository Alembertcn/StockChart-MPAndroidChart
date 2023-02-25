package com.github.mikephil.charting.stockChart.charts;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.stockChart.dataManage.KLineDataManage;
import com.github.mikephil.charting.stockChart.enums.TimeType;
import com.github.mikephil.charting.stockChart.markerView.BarBottomMarkerView;
import com.github.mikephil.charting.stockChart.markerView.KRightMarkerView;
import com.github.mikephil.charting.stockChart.markerView.LeftMarkerView;
import com.github.mikephil.charting.stockChart.renderer.MyCombinedChartRenderer;
import com.github.mikephil.charting.utils.CommonUtil;
import com.github.mikephil.charting.utils.DataTimeUtil;

import kotlin.jvm.functions.Function0;


/**
 * Created by ly on 2016/9/12.
 */
public class CandleCombinedChart extends CombinedChart {
    private LeftMarkerView myMarkerViewLeft;
    private KRightMarkerView myMarkerViewRight;
    private BarBottomMarkerView markerBottom;
    public KLineDataManage kLineData;

    public CandleCombinedChart(Context context) {
        super(context);
    }

    public CandleCombinedChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CandleCombinedChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void initRenderer() {
        mRenderer = new MyCombinedChartRenderer(this, mAnimator, mViewPortHandler);
    }

    public void setMarker(LeftMarkerView markerLeft, KRightMarkerView markerRight, KLineDataManage kLineData, BarBottomMarkerView markerBottom,TimeType timeType) {
        this.myMarkerViewLeft = markerLeft;
        this.myMarkerViewRight = markerRight;
        this.markerBottom = markerBottom;
        this.timeType = timeType;

        this.kLineData = kLineData;
    }

    //暂时无用
    public void setHighlightValue(Highlight h) {
        if (mData == null) {
            mIndicesToHighlight = null;
        } else {
            mIndicesToHighlight = new Highlight[]{h};
        }
        invalidate();
    }
    private TimeType timeType = TimeType.TIME_DATE;

    @Override
    protected void drawMarkers(Canvas canvas) {
        // if there is no marker view or drawing marker is disabled
        if (!isDrawMarkersEnabled() || !valuesToHighlight()) {
            return;
        }

        for (int i = 0; i < mIndicesToHighlight.length; i++) {

            Highlight highlight = mIndicesToHighlight[i];

            IDataSet set = mData.getDataSetByIndex(highlight.getDataSetIndex());

            Entry e = mData.getEntryForHighlight(mIndicesToHighlight[i]);
            int entryIndex = set.getEntryIndex(e);

            // make sure entry not null
            if (e == null || entryIndex > set.getEntryCount() * mAnimator.getPhaseX()) {
                continue;
            }

            float[] pos = getMarkerPosition(highlight);

            // check bounds
            if (!mViewPortHandler.isInBounds(pos[0], pos[1])) {
                continue;
            }

            if (pos[0] >= CommonUtil.getWindowWidth(getContext()) / 2 && myMarkerViewLeft!=null) {
                float yValForXIndex1 = (float) kLineData.getKLineDatas().get((int) mIndicesToHighlight[i].getX()).getClose();
                myMarkerViewLeft.setData(yValForXIndex1);
                myMarkerViewLeft.refreshContent(e, mIndicesToHighlight[i]);
                myMarkerViewLeft.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                myMarkerViewLeft.layout(0, 0, myMarkerViewLeft.getMeasuredWidth(), myMarkerViewLeft.getMeasuredHeight());
                if (getAxisLeft().getLabelPosition() == YAxis.YAxisLabelPosition.OUTSIDE_CHART) {
                    myMarkerViewLeft.draw(canvas, mViewPortHandler.contentLeft() - myMarkerViewLeft.getWidth() / 2, pos[1] + myMarkerViewLeft.getHeight() / 2);//+ CommonUtil.dip2px(getContext(),20)   - myMarkerViewLeft.getHeight() / 2
                } else {
                    myMarkerViewLeft.draw(canvas, mViewPortHandler.contentLeft() + myMarkerViewLeft.getWidth() / 2, pos[1] + myMarkerViewLeft.getHeight() / 2);//+ CommonUtil.dip2px(getContext(),20)   - myMarkerViewLeft.getHeight() / 2
                }
            } else if(myMarkerViewRight!=null){
                float yValForXIndex2 = (float) kLineData.getKLineDatas().get((int) mIndicesToHighlight[i].getX()).getClose();
                myMarkerViewRight.setData(yValForXIndex2);
                myMarkerViewRight.refreshContent(e, mIndicesToHighlight[i]);
                myMarkerViewRight.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                myMarkerViewRight.layout(0, 0, myMarkerViewRight.getMeasuredWidth(), myMarkerViewRight.getMeasuredHeight());// - myMarkerViewRight.getHeight() / 2
                if (getAxisRight().getLabelPosition() == YAxis.YAxisLabelPosition.OUTSIDE_CHART) {
                    myMarkerViewRight.draw(canvas, mViewPortHandler.contentRight() + myMarkerViewRight.getWidth() / 2, pos[1] + myMarkerViewLeft.getHeight() / 2);// - CommonUtil.dip2px(getContext(),20)
                } else {
                    myMarkerViewRight.draw(canvas, mViewPortHandler.contentRight() - myMarkerViewRight.getWidth() / 2, pos[1] + myMarkerViewLeft.getHeight() / 2);// - CommonUtil.dip2px(getContext(),20)
                }
            }

            if(markerBottom==null)return;

    
            markerBottom.refreshContent(e, highlight);
            markerBottom.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            markerBottom.layout(0, 0, markerBottom.getMeasuredWidth(), markerBottom.getMeasuredHeight());

            int width = markerBottom.getWidth() / 2;
            if (mViewPortHandler.contentRight() - pos[0] <= width) {
                markerBottom.draw(canvas, mViewPortHandler.contentRight() - markerBottom.getWidth() / 2, mViewPortHandler.contentBottom() + markerBottom.getHeight());//-markerBottom.getHeight()   CommonUtil.dip2px(getContext(),65.8f)
            } else if (pos[0] - mViewPortHandler.contentLeft() <= width) {
                markerBottom.draw(canvas, mViewPortHandler.contentLeft() + markerBottom.getWidth() / 2, mViewPortHandler.contentBottom() + markerBottom.getHeight());
            } else {
                markerBottom.draw(canvas, pos[0], mViewPortHandler.contentBottom() + markerBottom.getHeight());
            }
        }
    }
    @Override
    public boolean isDragXEnabled() {
        float highestVisibleX = getHighestVisibleX();
        int entryCount = getData().getMaxEntryCountSet().getEntryCount();
        return highestVisibleX < entryCount;
    }
}
