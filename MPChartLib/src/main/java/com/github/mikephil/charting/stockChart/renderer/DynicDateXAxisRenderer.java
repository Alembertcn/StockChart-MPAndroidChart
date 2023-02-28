package com.github.mikephil.charting.stockChart.renderer;

import android.graphics.Canvas;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.renderer.XAxisRenderer;
import com.github.mikephil.charting.stockChart.dataManage.IDataManager;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

public class DynicDateXAxisRenderer extends XAxisRenderer {
    private IDataManager dataManager;

    public DynicDateXAxisRenderer(ViewPortHandler viewPortHandler, XAxis xAxis, Transformer trans,IDataManager dataManager) {
        super(viewPortHandler, xAxis, trans);
        this.dataManager = dataManager;
    }

    protected void computeAxisValues(float min, float max) {

        int startIndex = -1, count = -1, lastIndex = -1;
        Integer[] suggesLabelIndxs = dataManager.getXCanUseIndexes();
        for (int i = 0; i < suggesLabelIndxs.length; i++) {
            int i1 = suggesLabelIndxs[i];
            if (i1 >= min && i1 <= max) {
                if (startIndex == -1) {
                    startIndex = i;
                    count = 0;
                }

                lastIndex = i;
                count++;
            }
        }

        int labelCount = mAxis.getLabelCount();
        boolean isZ = (count % labelCount == 0);
        int interval = count >= labelCount ? (count / (labelCount-1) + (isZ?0:1)) : 0;

        int n = interval > 1.0 ? labelCount : Math.max(count, 0);

        //至少一个坐标
        if (n == 0) {
            mAxis.mEntryCount = 1;
            mAxis.mEntries = new float[]{(float) Math.ceil(min)};
        } else if (n < labelCount) {
            mAxis.mEntryCount = n;
            if (mAxis.mEntries.length < n) {
                // Ensure stops contains at least numStops elements.
                mAxis.mEntries = new float[n];
            }
            int j = 0;
            for (int i = startIndex; i <= lastIndex; i++, j++) {
                Integer suggesLabelIndx = suggesLabelIndxs[i];
                mAxis.mEntries[j] = suggesLabelIndx;
            }
        } else {
            mAxis.mEntryCount = n;

            if (mAxis.mEntries.length < n) {
                // Ensure stops contains at least numStops elements.
                mAxis.mEntries = new float[n];
            }
            int j = 0;
            for (int i = startIndex; i <= lastIndex; i += interval, j++) {
                mAxis.mEntries[j] = suggesLabelIndxs[i];
            }
        }

    }

    @Override
    protected void drawLabels(Canvas c, float pos, MPPointF anchor) {

        final float labelRotationAngleDegrees = mXAxis.getLabelRotationAngle();
        boolean centeringEnabled = mXAxis.isCenterAxisLabelsEnabled();

        float[] positions = new float[mXAxis.mEntryCount * 2];

        for (int i = 0; i < positions.length; i += 2) {

            // only fill x values
            if (centeringEnabled) {
                positions[i] = mXAxis.mCenteredEntries[i / 2];
            } else {
                positions[i] = mXAxis.mEntries[i / 2];
            }
        }

        mTrans.pointValuesToPixel(positions);
        float lastX=-1;
        for (int i = 0; i < positions.length; i += 2) {

            float x = positions[i];

            if (mViewPortHandler.isInBoundsX(x)) {

                String label = mXAxis.getValueFormatter().getAxisLabel(mXAxis.mEntries[i / 2], mXAxis);
                float width = Utils.calcTextWidth(mAxisLabelPaint, label);

                if (mXAxis.isAvoidFirstLastClippingEnabled()) {
                    // avoid clipping of the last
                    if(!mViewPortHandler.isInBoundsX(width / 2 +x)){
                        x = mViewPortHandler.contentRight() - width / 2;
                    }
                    if(!mViewPortHandler.isInBoundsX( x -width / 2)){
                        x =  mViewPortHandler.contentLeft() + width / 2;
                    }
                }
                if(i>=2 && lastX!=-1){
                    int lastI=i-2;
                    String labelLast = mXAxis.getValueFormatter().getAxisLabel(mXAxis.mEntries[lastI / 2], mXAxis);
                    float widthLast = Utils.calcTextWidth(mAxisLabelPaint, labelLast);

                    if(x-widthLast<lastX){
                        continue;
                    }
                }
//                if(i<positions.length-2){
//                    if(x+width>=positions[i+2]){
//                        continue;
//                    }
//                }
                lastX = x;
                drawLabel(c, label, x, pos, anchor, labelRotationAngleDegrees);
            }
        }
    }
}


