package com.github.mikephil.charting.stockChart.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.interfaces.dataprovider.CandleDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ICandleDataSet;
import com.github.mikephil.charting.renderer.CandleStickChartRenderer;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.NumberUtils;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.List;

/**
 * 描述：蜡烛图渲染
 */
public class MyCandleStickChartRenderer extends CandleStickChartRenderer {
    public MyCandleStickChartRenderer(CandleDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(chart, animator, viewPortHandler);
    }

    @Override
    public void drawValues(Canvas c) {

        List<ICandleDataSet> dataSets = mChart.getCandleData().getDataSets();

        mValuePaint.setColor(Color.parseColor("#406ebc"));
        for (int i = 0; i < dataSets.size(); i++) {

            ICandleDataSet dataSet = dataSets.get(i);

            if (!shouldDrawValues(dataSet) || dataSet.getEntryCount() < 1) {
                continue;
            }

            // apply the text-styling defined by the DataSet
            applyValueTextStyle(dataSet);

            Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

            mXBounds.set(mChart, dataSet);

            float[] positions = trans.generateTransformedValuesCandle(
                    dataSet, mAnimator.getPhaseX(), mAnimator.getPhaseY(), mXBounds.min, mXBounds.max);

            //计算最大值和最小值
            float maxValue = 0, minValue = 0;
            int maxIndex = 0, minIndex = 0;
            CandleEntry maxEntry = null, minEntry = null;
            boolean firstInit = true;
            for (int j = 0; j < positions.length; j += 2) {

                float x = positions[j];
                float y = positions[j + 1];

                if (!mViewPortHandler.isInBoundsRight(x)) {
                    break;
                }

                if (!mViewPortHandler.isInBoundsLeft(x) || !mViewPortHandler.isInBoundsY(y)) {
                    continue;
                }

                CandleEntry entry = dataSet.getEntryForIndex(j / 2 + mXBounds.min);

                if (firstInit) {
                    maxValue = entry.getHigh();
                    minValue = entry.getLow();
                    firstInit = false;
                    maxEntry = entry;
                    minEntry = entry;
                } else {
                    if (entry.getHigh() > maxValue) {
                        maxValue = entry.getHigh();
                        maxIndex = j;
                        maxEntry = entry;
                    }

                    if (entry.getLow() < minValue) {
                        minValue = entry.getLow();
                        minIndex = j;
                        minEntry = entry;
                    }

                }
            }

            //绘制最大值和最小值
            if (maxIndex > minIndex) {
                //画右边
                String highString = NumberUtils.keepPrecisionR(minValue, dataSet.getPrecision());

                //计算显示位置
                //计算文本宽度
                int highStringWidth = Utils.calcTextWidth(mValuePaint, "← " + highString);
                int highStringHeight = Utils.calcTextHeight(mValuePaint, "← " + highString);

                float[] tPosition = new float[2];
                tPosition[0] = minEntry == null ? 0f : minEntry.getX();
                tPosition[1] = minEntry == null ? 0f : minEntry.getLow();
                trans.pointValuesToPixel(tPosition);
                if (tPosition[0] + highStringWidth / 2 > mViewPortHandler.contentRight()) {
                    c.drawText(highString + " →", tPosition[0] - highStringWidth / 2, tPosition[1] + highStringHeight / 2, mValuePaint);
                } else {
                    c.drawText("← " + highString, tPosition[0] + highStringWidth / 2, tPosition[1] + highStringHeight / 2, mValuePaint);
                }
            } else {
                //画左边
                String highString = NumberUtils.keepPrecisionR(minValue, dataSet.getPrecision());

                //计算显示位置
                int highStringWidth = Utils.calcTextWidth(mValuePaint, highString + " →");
                int highStringHeight = Utils.calcTextHeight(mValuePaint, highString + " →");

                float[] tPosition = new float[2];
                tPosition[0] = minEntry == null ? 0f : minEntry.getX();
                tPosition[1] = minEntry == null ? 0f : minEntry.getLow();
                trans.pointValuesToPixel(tPosition);
                if (tPosition[0] - highStringWidth / 2 < mViewPortHandler.contentLeft()) {
                    c.drawText("← " + highString, tPosition[0] + highStringWidth / 2, tPosition[1] + highStringHeight / 2, mValuePaint);
                } else {
                    c.drawText(highString + " →", tPosition[0] - highStringWidth / 2, tPosition[1] + highStringHeight / 2, mValuePaint);
                }
            }


            //这里画的是上面两个点
            if (maxIndex > minIndex) {
                //画左边
                String highString = NumberUtils.keepPrecisionR(maxValue, dataSet.getPrecision());

                int highStringWidth = Utils.calcTextWidth(mValuePaint, highString + " →");
                int highStringHeight = Utils.calcTextHeight(mValuePaint, highString + " →");

                float[] tPosition = new float[2];
                tPosition[0] = maxEntry == null ? 0f : maxEntry.getX();
                tPosition[1] = maxEntry == null ? 0f : maxEntry.getHigh();
                trans.pointValuesToPixel(tPosition);
                if ((tPosition[0] - highStringWidth / 2) < mViewPortHandler.contentLeft()) {
                    c.drawText("← " + highString, tPosition[0] + highStringWidth / 2, tPosition[1] + highStringHeight / 2, mValuePaint);
                } else {
                    c.drawText(highString + " →", tPosition[0] - highStringWidth / 2, tPosition[1] + highStringHeight / 2, mValuePaint);
                }
            } else {
                //画右边
                String highString = NumberUtils.keepPrecisionR(maxValue, dataSet.getPrecision());

                //计算显示位置
                int highStringWidth = Utils.calcTextWidth(mValuePaint, "← " + highString);
                int highStringHeight = Utils.calcTextHeight(mValuePaint, "← " + highString);

                float[] tPosition = new float[2];
                tPosition[0] = maxEntry == null ? 0f : maxEntry.getX();
                tPosition[1] = maxEntry == null ? 0f : maxEntry.getHigh();
                trans.pointValuesToPixel(tPosition);
                if (tPosition[0] + highStringWidth / 2 > mViewPortHandler.contentRight()) {
                    c.drawText(highString + " →", tPosition[0] - highStringWidth / 2, tPosition[1] + highStringHeight / 2, mValuePaint);
                } else {
                    c.drawText("← " + highString, tPosition[0] + highStringWidth / 2, tPosition[1] + highStringHeight / 2, mValuePaint);
                }
            }
        }
    }



    @SuppressWarnings("ResourceAsColor")
    protected void drawDataSet(Canvas c, ICandleDataSet dataSet) {
        Paint.Style saveStyle = mRenderPaint.getStyle();
        Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

        float phaseY = mAnimator.getPhaseY();
        float barSpace = dataSet.getBarSpace();
        boolean showCandleBar = dataSet.getShowCandleBar();

        mXBounds.set(mChart, dataSet);

        float shadowWidth = dataSet.getShadowWidth();
        mRenderPaint.setStrokeWidth(shadowWidth);

        // draw the body
        for (int j = mXBounds.min; j <= mXBounds.range + mXBounds.min; j++) {

            // get the entry
            CandleEntry e = null;
            try {
                e = dataSet.getEntryForIndex(j);
            } catch (Exception e1) {
                e1.printStackTrace();
            }

            if (e == null) {
                continue;
            }

            final float xPos = e.getX();

            final float open = e.getOpen();
            final float close = e.getClose();
            final float high = e.getHigh();
            final float low = e.getLow();

            if (showCandleBar) {
                // calculate the shadow

                mShadowBuffers[0] = xPos;
                mShadowBuffers[2] = xPos;
                mShadowBuffers[4] = xPos;
                mShadowBuffers[6] = xPos;

                if (open > close) {
                    mShadowBuffers[1] = high * phaseY;
                    mShadowBuffers[3] = open * phaseY;
                    mShadowBuffers[5] = low * phaseY;
                    mShadowBuffers[7] = close * phaseY;
                } else if (open < close) {
                    mShadowBuffers[1] = high * phaseY;
                    mShadowBuffers[3] = close * phaseY;
                    mShadowBuffers[5] = low * phaseY;
                    mShadowBuffers[7] = open * phaseY;
                } else {
                    mShadowBuffers[1] = high * phaseY;
                    mShadowBuffers[3] = open * phaseY;
                    mShadowBuffers[5] = low * phaseY;
                    mShadowBuffers[7] = mShadowBuffers[3];
                }

                trans.pointValuesToPixel(mShadowBuffers);

                // draw the shadows

                if (dataSet.getShadowColorSameAsCandle()) {

                    if (open > close) {
                        mRenderPaint.setColor(
                                dataSet.getDecreasingColor() == ColorTemplate.COLOR_NONE ?
                                        dataSet.getColor(j) :
                                        dataSet.getDecreasingColor()
                        );
                    } else if (open < close) {
                        mRenderPaint.setColor(
                                dataSet.getIncreasingColor() == ColorTemplate.COLOR_NONE ?
                                        dataSet.getColor(j) :
                                        dataSet.getIncreasingColor()
                        );
                    } else {
                        mRenderPaint.setColor(
                                dataSet.getNeutralColor() == ColorTemplate.COLOR_NONE ?
                                        dataSet.getColor(j) :
                                        dataSet.getNeutralColor()
                        );
                    }

                } else {
                    mRenderPaint.setColor(
                            dataSet.getShadowColor() == ColorTemplate.COLOR_NONE ?
                                    dataSet.getColor(j) :
                                    dataSet.getShadowColor()
                    );
                }


                c.drawLines(mShadowBuffers, mRenderPaint);

                // calculate the body

                mBodyBuffers[0] = xPos - 0.5f + barSpace;
                mBodyBuffers[1] = close * phaseY;
                mBodyBuffers[2] = (xPos + 0.5f - barSpace);
                mBodyBuffers[3] = open * phaseY;

                trans.pointValuesToPixel(mBodyBuffers);

                // draw body differently for increasing and decreasing entry
                if (open > close) { // decreasing

                    if (dataSet.getDecreasingColor() == ColorTemplate.COLOR_NONE) {
                        mRenderPaint.setColor(dataSet.getColor(j));
                    } else {
                        mRenderPaint.setColor(dataSet.getDecreasingColor());
                    }

                    mRenderPaint.setStyle(dataSet.getDecreasingPaintStyle());

                    c.drawRect(
                            mBodyBuffers[0]+shadowWidth/2, mBodyBuffers[3],
                            mBodyBuffers[2]-shadowWidth/2, mBodyBuffers[1],
                            mRenderPaint);

                } else if (open < close) {

                    if (dataSet.getIncreasingColor() == ColorTemplate.COLOR_NONE) {
                        mRenderPaint.setColor(dataSet.getColor(j));
                    } else {
                        mRenderPaint.setColor(dataSet.getIncreasingColor());
                    }

                    mRenderPaint.setStyle(dataSet.getIncreasingPaintStyle());

                    c.drawRect(
                            mBodyBuffers[0]+shadowWidth/2, mBodyBuffers[1],
                            mBodyBuffers[2]-shadowWidth/2, mBodyBuffers[3],
                            mRenderPaint);
                } else { // equal values

                    if (dataSet.getNeutralColor() == ColorTemplate.COLOR_NONE) {
                        mRenderPaint.setColor(dataSet.getColor(j));
                    } else {
                        mRenderPaint.setColor(dataSet.getNeutralColor());
                    }

                    c.drawLine(
                            mBodyBuffers[0], mBodyBuffers[1],
                            mBodyBuffers[2], mBodyBuffers[3],
                            mRenderPaint);
                }
            } else {

                mRangeBuffers[0] = xPos;
                mRangeBuffers[1] = high * phaseY;
                mRangeBuffers[2] = xPos;
                mRangeBuffers[3] = low * phaseY;

                mOpenBuffers[0] = xPos - 0.5f + barSpace;
                mOpenBuffers[1] = open * phaseY;
                mOpenBuffers[2] = xPos;
                mOpenBuffers[3] = open * phaseY;

                mCloseBuffers[0] = xPos + 0.5f - barSpace;
                mCloseBuffers[1] = close * phaseY;
                mCloseBuffers[2] = xPos;
                mCloseBuffers[3] = close * phaseY;

                trans.pointValuesToPixel(mRangeBuffers);
                trans.pointValuesToPixel(mOpenBuffers);
                trans.pointValuesToPixel(mCloseBuffers);

                // draw the ranges
                int barColor;

                if (open > close) {
                    barColor = dataSet.getDecreasingColor() == ColorTemplate.COLOR_NONE
                            ? dataSet.getColor(j)
                            : dataSet.getDecreasingColor();
                } else if (open < close) {
                    barColor = dataSet.getIncreasingColor() == ColorTemplate.COLOR_NONE
                            ? dataSet.getColor(j)
                            : dataSet.getIncreasingColor();
                } else {
                    barColor = dataSet.getNeutralColor() == ColorTemplate.COLOR_NONE
                            ? dataSet.getColor(j)
                            : dataSet.getNeutralColor();
                }

                mRenderPaint.setColor(barColor);
                c.drawLine(
                        mRangeBuffers[0], mRangeBuffers[1],
                        mRangeBuffers[2], mRangeBuffers[3],
                        mRenderPaint);
                c.drawLine(
                        mOpenBuffers[0], mOpenBuffers[1],
                        mOpenBuffers[2], mOpenBuffers[3],
                        mRenderPaint);
                c.drawLine(
                        mCloseBuffers[0], mCloseBuffers[1],
                        mCloseBuffers[2], mCloseBuffers[3],
                        mRenderPaint);
            }
        }
        mRenderPaint.setStyle(saveStyle);
    }

}
