package com.github.mikephil.charting.stockChart.renderer;

import android.graphics.Canvas;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.renderer.XAxisRenderer;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

public class DynicDateXAxisRenderer extends XAxisRenderer {
    public void setSuggessLabelIndex(Integer[] all) {
        this.suggesLabelIndxs = all;
    }

    private Integer[] suggesLabelIndxs = new Integer[]{};

    public DynicDateXAxisRenderer(ViewPortHandler viewPortHandler, XAxis xAxis, Transformer trans) {
        super(viewPortHandler, xAxis, trans);
        mXAxis = xAxis;
    }

    protected void computeAxisValues(float min, float max) {

        int startIndex = -1, count = -1, lastIndex = -1;
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
        int interval = count >= labelCount ? count / labelCount : 0;

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
                mAxis.mEntries[j] = suggesLabelIndxs[i];
            }
        } else {
            mAxis.mEntryCount = n;

            if (mAxis.mEntries.length < n) {
                // Ensure stops contains at least numStops elements.
                mAxis.mEntries = new float[n];
            }
            int j = 0;
            for (int i = startIndex; i <= lastIndex ; i += interval, j++) {
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

        for (int i = 0; i < positions.length; i += 2) {

            float x = positions[i];

            if (mViewPortHandler.isInBoundsX(x)) {

                String label = mXAxis.getValueFormatter().getAxisLabel(mXAxis.mEntries[i / 2], mXAxis);

                if (mXAxis.isAvoidFirstLastClippingEnabled()) {

                    // avoid clipping of the last
                    if (i / 2 == mXAxis.mEntryCount - 1 && mXAxis.mEntryCount > 1) {
                        float width = Utils.calcTextWidth(mAxisLabelPaint, label);
                        if ((width / 2 +x) > mViewPortHandler.contentRight()) {
                            x = mViewPortHandler.contentRight() - width / 2;
                        }
                        // avoid clipping of the first
                    } else if (i == 0) {
                        float width = Utils.calcTextWidth(mAxisLabelPaint, label);
                        if ((x - width / 2) < mViewPortHandler.contentLeft()) {
                            x =  mViewPortHandler.contentLeft() + width / 2;
                        }
                    }
                }

                drawLabel(c, label, x, pos, anchor, labelRotationAngleDegrees);
            }
        }
    }
}

