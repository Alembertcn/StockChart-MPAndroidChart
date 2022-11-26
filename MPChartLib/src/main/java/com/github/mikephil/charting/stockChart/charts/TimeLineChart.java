package com.github.mikephil.charting.stockChart.charts;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.stockChart.dataManage.TimeDataManage;
import com.github.mikephil.charting.stockChart.markerView.BarBottomMarkerView;
import com.github.mikephil.charting.stockChart.markerView.LeftMarkerView;
import com.github.mikephil.charting.stockChart.markerView.TimeRightMarkerView;
import com.github.mikephil.charting.stockChart.renderer.TimeLineChartRenderer;
import com.github.mikephil.charting.stockChart.renderer.TimeXAxisRenderer;


public class TimeLineChart extends LineChart {
    private MarkerView myMarkerViewLeft;
    private MarkerView myMarkerViewRight;
    private MarkerView myMarkerViewBottom;
    private TimeDataManage kTimeData;
    private VolSelected volSelected;

    public void setVolSelected(VolSelected volSelected) {
        this.volSelected = volSelected;
    }

    public interface VolSelected {
        void onVolSelected(int value);

        void onValuesSelected(double price, double upDown, int vol, double avg);
    }

    public TimeLineChart(Context context) {
        super(context);
    }

    public TimeLineChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TimeLineChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void initRenderer() {
        mRenderer = new TimeLineChartRenderer(this, mAnimator, mViewPortHandler);
    }

    @Override
    protected void initXAxisRenderer() {
        mXAxisRenderer = new TimeXAxisRenderer(mViewPortHandler, (TimeXAxis) mXAxis, mLeftAxisTransformer, this);
    }

    @Override
    public void initXAxis() {
        mXAxis = new TimeXAxis();
    }

    /*返回转型后的左右轴*/
    public void setMarker(LeftMarkerView markerLeft, TimeRightMarkerView markerRight, TimeDataManage kLineData, BarBottomMarkerView markerBottom) {
        this.myMarkerViewLeft = markerLeft;
        this.myMarkerViewRight = markerRight;
        this.myMarkerViewBottom =markerBottom;
        this.kTimeData = kLineData;
    }

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
//
//            float yValForXIndex1 = (float) kTimeData.getDatas().get((int) mIndicesToHighlight[i].getX()).getNowPrice();
//            float yValForXIndex2 = (float) kTimeData.getDatas().get((int) mIndicesToHighlight[i].getX()).getPer();

            if (volSelected != null) {
                volSelected.onVolSelected(kTimeData.getDatas().get((int) mIndicesToHighlight[i].getX()).getVolume());
                volSelected.onValuesSelected(kTimeData.getDatas().get((int) mIndicesToHighlight[i].getX()).getNowPrice(),
                        kTimeData.getDatas().get((int) mIndicesToHighlight[i].getX()).getPer(),
                        kTimeData.getDatas().get((int) mIndicesToHighlight[i].getX()).getVolume(),
                        kTimeData.getDatas().get((int) mIndicesToHighlight[i].getX()).getAveragePrice());
            }

//            myMarkerViewLeft.setData(yValForXIndex1);
//            myMarkerViewRight.setData(yValForXIndex2);

            drawSingleMask(canvas, i, e, pos, myMarkerViewLeft,false);
            drawSingleMask(canvas, i, e, pos, myMarkerViewRight,false);
            drawSingleMask(canvas, i, e, pos, myMarkerViewBottom,true);
        }
    }

    private void drawSingleMask(Canvas canvas, int i, Entry e, float[] pos, MarkerView markerView,boolean isBottom) {
        if(markerView !=null){
            markerView.refreshContent(e, mIndicesToHighlight[i]);
            /*重新计算大小*/
            markerView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            markerView.layout(0, 0, markerView.getMeasuredWidth(), markerView.getMeasuredHeight());

            if(isBottom){
                int width = markerView.getWidth() / 2;
                if (mViewPortHandler.contentRight() - pos[0] <= width) {
                    markerView.draw(canvas, mViewPortHandler.contentRight() - markerView.getWidth() / 2, mViewPortHandler.contentBottom() + markerView.getHeight());//-markerBottom.getHeight()   CommonUtil.dip2px(getContext(),65.8f)
                } else if (pos[0] - mViewPortHandler.contentLeft() <= width) {
                    markerView.draw(canvas, mViewPortHandler.contentLeft() + markerView.getWidth() / 2, mViewPortHandler.contentBottom() + markerView.getHeight());
                } else {
                    markerView.draw(canvas, pos[0], mViewPortHandler.contentBottom() + markerView.getHeight());
                }
            }else{
                if (getAxisLeft().getLabelPosition() == YAxis.YAxisLabelPosition.OUTSIDE_CHART) {
                    markerView.draw(canvas, mViewPortHandler.contentLeft() - markerView.getWidth() / 2, pos[1] + markerView.getHeight() / 2);
                } else {
                    markerView.draw(canvas, mViewPortHandler.contentLeft() + markerView.getWidth() / 2, pos[1] + markerView.getHeight() / 2);
                }
            }

        }
    }

    //    public void setHighlightValue(Highlight h) {
//        if (mData == null)
//            mIndicesToHighlight = null;
//        else {
//            mIndicesToHighlight = new Highlight[]{h};
//        }
//        invalidate();
//    }

    //调换画数据和右轴数据的位置，防止label数据被覆盖
//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//        if (mData == null)
//            return;
//        long starttime = System.currentTimeMillis();
//
//        mXAxisRenderer.calcXBounds(this, mXAxis.mAxisLabelModulus);
//        mRenderer.calcXBounds(this, mXAxis.mAxisLabelModulus);
//
//        // execute all drawing commands
//        drawGridBackground(canvas);
//
//        if (mAxisLeft.isEnabled())
//            mAxisRendererLeft.computeAxis(mAxisLeft.mAxisMinimum, mAxisLeft.mAxisMaximum);
//        if (mAxisRight.isEnabled())
//            mAxisRendererRight.computeAxis(mAxisRight.mAxisMinimum, mAxisRight.mAxisMaximum);
//
//        mXAxisRenderer.renderAxisLine(canvas);
//        mAxisRendererLeft.renderAxisLine(canvas);
//        mAxisRendererRight.renderAxisLine(canvas);
//
//        // make sure the graph values and grid cannot be drawn outside the
//        // content-rect
//        int clipRestoreCount = canvas.save();
//        canvas.clipRect(mViewPortHandler.getContentRect());
//
//        mXAxisRenderer.renderGridLines(canvas);
//        mAxisRendererLeft.renderGridLines(canvas);
//        mAxisRendererRight.renderGridLines(canvas);
//
//        if (mXAxis.isDrawLimitLinesBehindDataEnabled())
//            mXAxisRenderer.renderLimitLines(canvas);
//
//        if (mAxisLeft.isDrawLimitLinesBehindDataEnabled())
//            mAxisRendererLeft.renderLimitLines(canvas);
//
//        if (mAxisRight.isDrawLimitLinesBehindDataEnabled())
//            mAxisRendererRight.renderLimitLines(canvas);
//
//        //mRenderer.drawData(canvas);
//
//        // if highlighting is enabled
//        if (valuesToHighlight())
//            mRenderer.drawHighlighted(canvas, mIndicesToHighlight);
//
//        // Removes clipping rectangle
//        canvas.restoreToCount(clipRestoreCount);
//
//        mRenderer.drawExtras(canvas);
//
//        clipRestoreCount = canvas.save();
//        canvas.clipRect(mViewPortHandler.getContentRect());
//
//        if (!mXAxis.isDrawLimitLinesBehindDataEnabled())
//            mXAxisRenderer.renderLimitLines(canvas);
//
//        if (!mAxisLeft.isDrawLimitLinesBehindDataEnabled())
//            mAxisRendererLeft.renderLimitLines(canvas);
//
//        if (!mAxisRight.isDrawLimitLinesBehindDataEnabled())
//            mAxisRendererRight.renderLimitLines(canvas);
//
//        canvas.restoreToCount(clipRestoreCount);
//
//        mXAxisRenderer.renderAxisLabels(canvas);
//        mAxisRendererLeft.renderAxisLabels(canvas);
//        mAxisRendererRight.renderAxisLabels(canvas);
//
//        mRenderer.drawData(canvas);
//
//        mRenderer.drawValues(canvas);
//
//        mLegendRenderer.renderLegend(canvas);
//
//        drawMarkers(canvas);
//
//        drawDescription(canvas);
//    }


}
