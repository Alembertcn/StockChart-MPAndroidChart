package com.github.mikephil.charting.stockChart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.github.mikephil.charting.GlobaleConfig;
import com.github.mikephil.charting.R;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.formatter.VolFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.stockChart.customView.TwinklePoint;
import com.github.mikephil.charting.stockChart.customView.TwinklePoint2;
import com.github.mikephil.charting.stockChart.markerView.BarBottomMarkerView;
import com.github.mikephil.charting.stockChart.renderer.ColorContentYAxisRenderer;
import com.github.mikephil.charting.stockChart.charts.CoupleChartGestureListener;
import com.github.mikephil.charting.stockChart.markerView.LeftMarkerView;
import com.github.mikephil.charting.stockChart.charts.TimeBarChart;
import com.github.mikephil.charting.stockChart.charts.TimeLineChart;
import com.github.mikephil.charting.stockChart.markerView.TimeRightMarkerView;
import com.github.mikephil.charting.stockChart.charts.TimeXAxis;
import com.github.mikephil.charting.stockChart.dataManage.TimeDataManage;
import com.github.mikephil.charting.stockChart.enums.ChartType;
import com.github.mikephil.charting.stockChart.event.BaseEvent;
import com.github.mikephil.charting.stockChart.model.CirclePositionTime;
import com.github.mikephil.charting.stockChart.model.TimeDataModel;
import com.github.mikephil.charting.utils.CommonUtil;
import com.github.mikephil.charting.utils.MPPointD;
import com.github.mikephil.charting.utils.NumberUtils;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

/**
 * 当日分时图
 */
public class OneDayChart extends BaseChart {

    private Context mContext;
    TimeLineChart lineChart;
    TimeBarChart barChart;
    FrameLayout cirCleView;
    ViewGroup flAvPrice;
    TextView tvAgPrice;
    TwinklePoint2 tPoint2;

    private LineDataSet d1, d2;
    private BarDataSet barDataSet;

    TimeXAxis xAxisLine;
    YAxis axisRightLine;
    YAxis axisLeftLine;

    TimeXAxis xAxisBar;
    YAxis axisLeftBar;
    YAxis axisRightBar;
    private SparseArray<String> xLabels = new SparseArray<>();//X轴刻度label
    private TimeDataManage mData;
    private int[] colorArray;
    private float defaultCircleWith=getResources().getDimension(R.dimen.circle_size);

    public OneDayChart(Context context) {
        this(context, null);
    }

    public OneDayChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.view_time, this);
        lineChart = (TimeLineChart) findViewById(R.id.line_chart);
        barChart = (TimeBarChart) findViewById(R.id.bar_chart);
        cirCleView = (FrameLayout) findViewById(R.id.circle_frame_time);
        flAvPrice = (ViewGroup) findViewById(R.id.flAvPrice);
        tvAgPrice = (TextView) findViewById(R.id.tvAgPrice);
        tPoint2 = (TwinklePoint2) findViewById(R.id.tPoint2);

        EventBus.getDefault().register(this);

        colorArray = new int[]{ ContextCompat.getColor(mContext, R.color.up_color), ContextCompat.getColor(mContext, R.color.equal_color),ContextCompat.getColor(mContext, R.color.down_color)};
        initTwinklePoint();
    }


    public void playHeaderAnimation(int count) {
        stopHeaderAnimation();
        playHeartbeatAnimation(cirCleView.findViewById(R.id.anim_view),count);
    }
    public void stopHeaderAnimation() {
        stopHeartbeatAnimation(cirCleView.findViewById(R.id.anim_view));
    }



    /**
     * 初始化图表属性
     */
    public void initChart(boolean landscape) {
        tPoint2.setLineColor(ContextCompat.getColor(mContext, R.color.minute_blue));
        tPoint2.setLineWidth(Utils.convertDpToPixel(.8f));
        this.landscape = landscape;
        //主图
        lineChart.setScaleEnabled(false);//是否可以缩放
        lineChart.setDrawBorders(false);//是否画外框线
        lineChart.setBorderColor(ContextCompat.getColor(mContext, R.color.border_color));
        lineChart.setBorderWidth(0.7f);
        lineChart.setNoDataText(getResources().getString(R.string.no_data));
        Legend lineChartLegend = lineChart.getLegend();
        lineChartLegend.setEnabled(false);

        lineChartLegend.setEnabled(false);
//        lineChart.setDescription(null);
        //副图
        barChart.setScaleEnabled(false);
        barChart.getLegend().setEnabled(false);
        barChart.setDrawBorders(false);
        barChart.setBorderColor(ContextCompat.getColor(mContext, R.color.border_color));
        barChart.setBorderWidth(0.7f);
        barChart.setNoDataText(getResources().getString(R.string.no_data));
        //主图X轴
        xAxisLine = (TimeXAxis) lineChart.getXAxis();
        xAxisLine.setDrawAxisLine(false);
        xAxisLine.setDrawGridLines(false);
        xAxisLine.setTextColor(ContextCompat.getColor(mContext, R.color.label_text));
        xAxisLine.setPosition(XAxis.XAxisPosition.BOTTOM);//x轴刻度值显示在底部
        xAxisLine.setAvoidFirstLastClipping(true);
        xAxisLine.setGridColor(ContextCompat.getColor(mContext, R.color.grid_color));
        xAxisLine.setGridLineWidth(0.7f);
        xAxisLine.setYOffset(2F);
        xAxisLine.setSpaceMax(0.5f);
        xAxisLine.setSpaceMin(0.4f);

        //主图左Y轴
        axisLeftLine = lineChart.getAxisLeft();
        axisLeftLine.setDrawGridLines(false);
        axisLeftLine.setDrawAxisLine(false);
        axisLeftLine.setDrawLabels(true);
        axisLeftLine.setLabelCount(5, true);
        axisLeftLine.enableGridDashedLine(CommonUtil.dip2px(mContext, 4), CommonUtil.dip2px(mContext, 3), 0);
        axisLeftLine.setTextColor(ContextCompat.getColor(mContext, R.color.axis_y_label));
        axisLeftLine.setGridColor(ContextCompat.getColor(mContext, R.color.grid_color));
        axisLeftLine.setGridLineWidth(0.7f);
        axisLeftLine.setXOffset(0);
        axisLeftLine.setValueLineInside(true);
        axisLeftLine.setDrawTopBottomGridLine(false);
//        axisLeftK.setPosition(landscape ? YAxis.YAxisLabelPosition.OUTSIDE_CHART : YAxis.YAxisLabelPosition.INSIDE_CHART);
        axisLeftLine.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        axisLeftLine.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return NumberUtils.keepPrecisionR(value, precision);
            }
        });

        //主图右Y轴
        axisRightLine = lineChart.getAxisRight();
        axisRightLine.setEnabled(false);


        //副图X轴
        xAxisBar = (TimeXAxis) barChart.getXAxis();
        xAxisBar.setEnabled(false);

        //副图左Y轴
        axisLeftBar = barChart.getAxisLeft();
        axisLeftBar.setEnabled(false);
        axisLeftBar.setDrawGridLines(false);
        axisLeftBar.setDrawAxisLine(false);
        axisLeftBar.setTextColor(ContextCompat.getColor(mContext, R.color.axis_text));
        axisLeftBar.setPosition(landscape ? YAxis.YAxisLabelPosition.OUTSIDE_CHART : YAxis.YAxisLabelPosition.INSIDE_CHART);
        axisLeftBar.setDrawLabels(false);
        axisLeftBar.setLabelCount(2, true);
        axisLeftBar.setAxisMinimum(0);
        axisLeftBar.setSpaceTop(5);
        axisLeftBar.setValueLineInside(true);

        //副图右Y轴
        axisRightBar = barChart.getAxisRight();
        axisRightBar.setEnabled(false);
        axisRightBar.setDrawLabels(false);
        axisRightBar.setDrawGridLines(false);
        axisRightBar.setDrawAxisLine(false);
        axisRightBar.setLabelCount(3, true);
        axisRightBar.setDrawTopBottomGridLine(false);
        axisRightBar.setGridColor(ContextCompat.getColor(mContext, R.color.grid_color));
        axisRightBar.setGridLineWidth(0.7f);
        axisRightBar.enableGridDashedLine(CommonUtil.dip2px(mContext, 4), CommonUtil.dip2px(mContext, 3), 0);

        //手势联动监听
        gestureListenerLine = new CoupleChartGestureListener(lineChart, new Chart[]{barChart});
        gestureListenerBar = new CoupleChartGestureListener(barChart, new Chart[]{lineChart});
        lineChart.setOnChartGestureListener(gestureListenerLine);
        barChart.setOnChartGestureListener(gestureListenerBar);

        lineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                lineChart.highlightValue(h);
                barChart.highlightValue(new Highlight(h.getX(), h.getDataSetIndex(), -1));
                updateText((int) e.getX());

                flAvPrice.setVisibility(VISIBLE);

                if (mHighlightValueSelectedListener != null) {
                    mHighlightValueSelectedListener.onDayHighlightValueListener(mData,(int)e.getX(), true);
                }
            }

            @Override
            public void onNothingSelected() {
                barChart.highlightValues(null);
                if (mHighlightValueSelectedListener != null) {
                    mHighlightValueSelectedListener.onDayHighlightValueListener(mData, 0, false);
                }
                flAvPrice.setVisibility(GONE);
                updateText(mData.getDatas().size() - 1);

                if (mHighlightValueSelectedListener != null) {
                    mHighlightValueSelectedListener.onDayHighlightValueListener(mData,mData.getDatas().size() - 1, false);
                }
            }
        });
        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                barChart.highlightValue(h);
                lineChart.highlightValue(new Highlight(h.getX(), h.getDataSetIndex(), -1));
                updateText((int) e.getX());

                flAvPrice.setVisibility(VISIBLE);

                if (mHighlightValueSelectedListener != null) {
                    mHighlightValueSelectedListener.onDayHighlightValueListener(mData,(int) e.getX(), true);
                }
            }

            @Override
            public void onNothingSelected() {
                lineChart.highlightValues(null);
                updateText(mData.getDatas().size() - 1);

                flAvPrice.setVisibility(GONE);
                if (mHighlightValueSelectedListener != null) {
                    mHighlightValueSelectedListener.onDayHighlightValueListener(mData,mData.getDatas().size() - 1, false);
                }
            }
        });

    }


    private void updateText(int index) {
        if(index<0||index>mData.getDatas().size()-1)return;

        TimeDataModel timeDataModel = mData.getDatas().get(index);

        barChart.setDescriptionCustom(new int[]{ContextCompat.getColor(mContext, R.color.fit_black), ContextCompat.getColor(mContext, R.color.theme)}, new String[]{getResources().getString(R.string.vol_name), "VOL:" + NumberUtils.formatVol(mContext, mData.getAssetId(), timeDataModel.getVolume())}, 2);
//        lineChart.setDescriptionCustom(new int[]{ContextCompat.getColor(mContext, R.color.fit_black), GlobaleConfig.getColorByCompare(timeDataModel.getNowPrice()-timeDataModel.getPreClose())}, new String[]{getResources().getString(R.string.age_price),NumberUtils.stringNoE10(timeDataModel.getAveragePrice())}, 2);
        flAvPrice.setSelected(timeDataModel.getAveragePrice()-timeDataModel.getPreClose()>=0);
        tvAgPrice.setTextColor(GlobaleConfig.getColorByCompare(timeDataModel.getAveragePrice()-timeDataModel.getPreClose()));
        tvAgPrice.setText(NumberUtils.keepPrecisionR(timeDataModel.getAveragePrice(), 3));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        lineChart.getDescription().setEnabled(false);
        barChart.getDescription().setPosition(0, CommonUtil.dip2px(mContext, 10));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.d("oneDayChart", "onLayout");
        super.onLayout(changed, l, t, r, b);
        // 实时更新小圆点位置 应对尺寸变化
        if(tPoint2.getVisibility() == View.VISIBLE && mData!=null && mData.getLastData()!=null){
            updateLastPoint((float)tPoint2.getSrcY());
        }
    }

    /**
     * 设置分时数据
     *
     * @param mData
     */
    public void setDataToChart(TimeDataManage mData) {
        this.mData = mData;
        if (mData.getDatas().size() == 0) {
            lineChart.setNoDataText(getResources().getString(R.string.no_data));
            barChart.setNoDataText(getResources().getString(R.string.no_data));
            lineChart.invalidate();
            barChart.invalidate();
            return;
        }

        ArrayList<Entry> lineCJEntries = new ArrayList<>();
        ArrayList<Entry> lineJJEntries = new ArrayList<>();
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        float color;//成交量柱状图的颜色
        for (int i = 0, j = 0; i < mData.getDatas().size(); i++, j++) {
            TimeDataModel t = mData.getDatas().get(j);
            if (t == null) {
                lineCJEntries.add(new Entry( i, Float.NaN));
                lineJJEntries.add(new Entry( i, Float.NaN));
                barEntries.add(new BarEntry( i, Float.NaN,0f));
                continue;
            }
            lineCJEntries.add(new Entry( i, (float) mData.getDatas().get(i).getNowPrice()));
            lineJJEntries.add(new Entry( i, (float) mData.getDatas().get(i).getAveragePrice()));
            if (i == 0) {
                color = mData.getDatas().get(i).getNowPrice() == mData.getPreClose() ? 0f : mData.getDatas().get(i).getNowPrice() > mData.getPreClose() ? 1f : -1f;
            } else {
                color = mData.getDatas().get(i).getNowPrice() == mData.getDatas().get(i - 1).getNowPrice() ? 0f : mData.getDatas().get(i).getNowPrice() > mData.getDatas().get(i - 1).getNowPrice() ? 1f : -1f;
            }
            barEntries.add(new BarEntry(i, mData.getDatas().get(i).getVolume(),color));
        }

        if(lineChart.getData() != null &&lineChart.getData().getDataSetCount() > 0
                &&barChart.getData() != null &&barChart.getData().getDataSetCount() > 0){
            d1 = (LineDataSet) lineChart.getData().getDataSetByIndex(0);
            d1.setValues(lineCJEntries);
            d1.notifyDataSetChanged();
            d2 = (LineDataSet) lineChart.getData().getDataSetByIndex(1);
            d2.setValues(lineJJEntries);
            d2.notifyDataSetChanged();
            lineChart.getData().notifyDataChanged();
            lineChart.notifyDataSetChanged();

            barDataSet = (BarDataSet) barChart.getData().getDataSetByIndex(0);
            barDataSet.setValues(barEntries);
            barDataSet.notifyDataSetChanged();
            barChart.getData().notifyDataChanged();
            barChart.notifyDataSetChanged();
        }else {
            setPrecision(mData.getAssetId().contains("IDX") ? 2 : 3);

            setXLabels(mData.getOneDayXLabels(landscape));
            setMarkerView(mData);

            //左Y轴label渲染颜色
            Transformer leftYTransformer = lineChart.getRendererLeftYAxis().getTransformer();
            ColorContentYAxisRenderer leftColorContentYAxisRenderer = new ColorContentYAxisRenderer(lineChart.getViewPortHandler(), axisLeftLine, leftYTransformer);
            leftColorContentYAxisRenderer.setLabelColor(colorArray);
            leftColorContentYAxisRenderer.setClosePrice(mData.getPreClose());
            leftColorContentYAxisRenderer.setLandscape(landscape);
            lineChart.setRendererLeftYAxis(leftColorContentYAxisRenderer);
            //右Y轴label渲染颜色
            Transformer rightYTransformer = lineChart.getRendererRightYAxis().getTransformer();
            ColorContentYAxisRenderer rightColorContentYAxisRenderer = new ColorContentYAxisRenderer(lineChart.getViewPortHandler(), axisRightLine, rightYTransformer);
            rightColorContentYAxisRenderer.setLabelColor(colorArray);
            rightColorContentYAxisRenderer.setClosePrice(mData.getPreClose());
            rightColorContentYAxisRenderer.setLandscape(landscape);
            lineChart.setRendererRightYAxis(rightColorContentYAxisRenderer);

//            暂时不用这些坐标了
//            if (Float.isNaN(mData.getPercentMax()) || Float.isNaN(mData.getPercentMin()) || Float.isNaN(mData.getVolMaxTime())) {
//                axisLeftBar.setAxisMaximum(0);
//                axisRightLine.setAxisMinimum(-0.01f);
//                axisRightLine.setAxisMaximum(0.01f);
//            } else {
//                axisLeftBar.setAxisMaximum(mData.getVolMaxTime());
//                axisRightLine.setAxisMinimum(mData.getPercentMin());
//                axisRightLine.setAxisMaximum(mData.getPercentMax());
//            }

            axisLeftBar.setValueFormatter(new VolFormatter(mContext, mData.getAssetId()));

            d1 = new LineDataSet(lineCJEntries, "分时线");
            d2 = new LineDataSet(lineJJEntries, "均价");
            d1.setDrawCircleDashMarker(landscape);
            d2.setDrawCircleDashMarker(false);
            d1.setDrawValues(false);
            d2.setDrawValues(false);
            d1.setLineWidth(0.8f);
            d2.setLineWidth(1f);
            d1.setColor(ContextCompat.getColor(mContext, R.color.minute_blue));
            d2.setColor(ContextCompat.getColor(mContext, R.color.minute_yellow));
            d1.setDrawFilled(false);
            Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.fade_fill_color);
            d1.setFillDrawable(drawable);
            d1.setHighLightColor(ContextCompat.getColor(mContext, R.color.highLight_Color));
            d1.setHighlightEnabled(true);//是否显示高亮十字线
            d2.setHighlightEnabled(false);
            d1.setDrawCircles(false);
            d2.setDrawCircles(false);
            d1.setAxisDependency(YAxis.AxisDependency.LEFT);
            d1.setPrecision(precision);
            d1.setTimeDayType(1);//设置分时图类型
            d2.setTimeDayType(1);
            ArrayList<ILineDataSet> sets = new ArrayList<>();
            sets.add(d1);
            sets.add(d2);
            LineData cd = new LineData(sets);
            lineChart.setData(cd);

            barDataSet = new BarDataSet(barEntries, "成交量");
            barDataSet.setHighLightColor(ContextCompat.getColor(mContext, R.color.highLight_Color));
            barDataSet.setHighlightEnabled(true);//是否显示高亮十字线
            barDataSet.setDrawValues(false);
            barDataSet.setNeutralColor(ContextCompat.getColor(mContext, R.color.equal_color));
            barDataSet.setIncreasingColor(ContextCompat.getColor(mContext, R.color.up_color));
            barDataSet.setDecreasingColor(ContextCompat.getColor(mContext, R.color.down_color));
            barDataSet.setIncreasingPaintStyle(Paint.Style.FILL);
            barDataSet.setDecreasingPaintStyle(Paint.Style.FILL);
            BarData barData = new BarData(barDataSet);
            barData.setBarWidth(.7f);
            barChart.setData(barData);

            //请注意，修改视口的所有方法需要在为Chart设置数据之后调用。
            //设置当前视图四周的偏移量。 设置这个，将阻止图表自动计算它的偏移量。使用 resetViewPortOffsets()撤消此设置。
            if (landscape) {
                float volwidth = Utils.calcTextWidthForVol(mPaint, mData.getVolMaxTime());
                float pricewidth = Utils.calcTextWidth(mPaint, NumberUtils.keepPrecision(Float.isNaN(mData.getMax()) ? "0" : mData.getMax() + "", precision) + "#");
                float left = CommonUtil.dip2px(mContext, pricewidth > volwidth ? pricewidth : volwidth);
                float right = CommonUtil.dip2px(mContext, Utils.calcTextWidth(mPaint, "-10.00%"));
                lineChart.setViewPortOffsets(left, CommonUtil.dip2px(mContext, 5), right, CommonUtil.dip2px(mContext, 15));
                barChart.setViewPortOffsets(left, 0, right, CommonUtil.dip2px(mContext, 15));
            } else {
                lineChart.setViewPortOffsets(CommonUtil.dip2px(mContext, 5), CommonUtil.dip2px(mContext, 5), CommonUtil.dip2px(mContext, 5), CommonUtil.dip2px(mContext, 15));
                barChart.setViewPortOffsets(CommonUtil.dip2px(mContext, 5), 0, CommonUtil.dip2px(mContext, 5), CommonUtil.dip2px(mContext, 15));
            }


            //右边3是为了适配小圆点遮挡问题
            lineChart.setViewPortOffsets(defaultCircleWith/2, CommonUtil.dip2px(mContext, 0), defaultCircleWith/2, CommonUtil.dip2px(mContext, 20));
            barChart.setViewPortOffsets(defaultCircleWith/2, CommonUtil.dip2px(mContext, 15), defaultCircleWith/2, 2);
//            lineChart.setViewPortOffsets(0, CommonUtil.dip2px(mContext, 0), 0, CommonUtil.dip2px(mContext, 20));
//            barChart.setViewPortOffsets(0, CommonUtil.dip2px(mContext, 15), 0, 0);
        }

        //下面方法需在填充数据后调用
        xAxisLine.setXLabels(mData.getOneDayXLabels(landscape));
        xAxisLine.setLabelCount(mData.getOneDayXLabels(landscape).size(), true);
        xAxisBar.setXLabels(mData.getOneDayXLabels(landscape));
        xAxisBar.setLabelCount(mData.getOneDayXLabels(landscape).size(), true);
        int maxCount =mData.getDataTypeMaxCount();
        lineChart.setVisibleXRange(maxCount, maxCount);
        barChart.setVisibleXRange(maxCount, maxCount);

        //moveViewTo(...) 方法会自动调用 invalidate()
        updateAxisLeft(mData.getMin(),mData.getMax());

        lineChart.moveViewToX(mData.getDatas().size() - 1);
        barChart.moveViewToX(mData.getDatas().size() - 1);

        updateText(mData.getDatas().size() - 1);

        // 实时更新小圆点位置 应对尺寸变化
        updateLastPoint((float) mData.getLastData().getNowPrice());
    }

    private void updateLastPoint(float lastPrice) {
        if(tPoint2.getVisibility() == View.VISIBLE && mData !=null){
            Transformer transformer = lineChart.getTransformer(YAxis.AxisDependency.LEFT);
            TimeDataModel lastData = mData.getLastData();
            MPPointD startPointion;
            MPPointD updatePointion;
            if(lastData !=null){
                startPointion = transformer.getPixelForValues(mData.getDatas().size()-1, (float) lastData.getNowPrice());
                updatePointion = transformer.getPixelForValues(mData.getDatas().size(), lastPrice);
            }else{
                startPointion = transformer.getPixelForValues(0, lastPrice);
                updatePointion = startPointion;
            }

            tPoint2.setLineStartPoint((float)startPointion.x,(float) startPointion.y);
            tPoint2.setCoordinateX((float) updatePointion.x,mData.getDatas().size());
            tPoint2.setCoordinateY((float) updatePointion.y,lastPrice);
            tPoint2.postInvalidate();
        }
    }

    /**
     * 动态增加一个点数据
     * @param timeDatamodel
     */
    public void dynamicsAddOne(TimeDataModel timeDatamodel) {
        if(mData ==null)return;

        if(timeDatamodel.getTimeMills()==0 && mData.getLastData()!=null && mData.getDatas().size()>=2){
            long l = mData.getDatas().get(1).getTimeMills() - mData.getDatas().get(0).getTimeMills();
            timeDatamodel.setTimeMills(mData.getLastData().getTimeMills()+l);
        }

        mData.addLastData(timeDatamodel);
        setDataToChart(mData);
        int size = mData.getDatas().size();
        float color=1;
        //和上一个比较或者和昨收价比较
        if(size>1) {
            int index = size;
            color = timeDatamodel.getNowPrice() == d1.getEntryForIndex(index - 1).getY() ? 0f : timeDatamodel.getNowPrice() > d1.getEntryForIndex(index - 1).getY() ? 1f : -1f;
        }else {
            color = timeDatamodel.getNowPrice() - mData.getPreClose()>=0 ?1f : -1f;
        }
//        twinklePoint.setCenterColor(GlobaleConfig.getColorByCompare(color));
//        twinklePoint.setTwinkleColor(GlobaleConfig.getColorByCompare(color));
//        twinklePoint.startTwinkle(2);

        TimeDataModel lastData = mData.getLastData();
        int endX = mData.getDatas().size() - 1;
        float endY = (float) timeDatamodel.getNowPrice();
        MPPointD startPointion = lineChart.getTransformer(YAxis.AxisDependency.LEFT).getPixelForValues(endX, (float) lastData.getNowPrice());
        MPPointD updatePointion = lineChart.getTransformer(YAxis.AxisDependency.LEFT).getPixelForValues(endX+1, endY);
            tPoint2.setLineStartPoint((float)startPointion.x,(float) startPointion.y);
            tPoint2.setCoordinateX((float) updatePointion.x, endX);
            tPoint2.setCoordinateY((float) updatePointion.y, endY);
            tPoint2.setCenterColor(GlobaleConfig.getColorByCompare(color));
            tPoint2.setTwinkleColor(GlobaleConfig.getColorByCompare(color));
            tPoint2.startTwinkle(2);

        if(!lineChart.valuesToHighlight()){
            updateText(endX);
        }
//        playHeaderAnimation(4);
    }
    public void dynamicsUpdateOne(TimeDataModel timeDatamodel) {
        double updateNowPrice = timeDatamodel.getNowPrice();
        if(mData ==null ||  mData.getLastData()==null || NumberUtils.keepPrecision( mData.getLastData().getNowPrice(),3) == NumberUtils.keepPrecision(updateNowPrice,3))return;

        //更新极坐标
        mData.updateLastDataWithOutAdd(timeDatamodel);
        updateAxisLeft(mData.getMin(),mData.getMax());

        TimeDataModel lastData = mData.getLastData();

        tPoint2.setVisibility(View.VISIBLE);
        timeDatamodel.setTimeMills(lastData.getTimeMills());

        double compare = updateNowPrice >=mData.getPreClose()?1:-1;
        MPPointD startPosition = lineChart.getTransformer(YAxis.AxisDependency.LEFT).getPixelForValues(mData.getDatas().size()-1, (float) lastData.getNowPrice());
        int endX = mData.getDatas().size();
        //最大为最后一个点坐标
        if(endX>=mData.getDataTypeMaxCount()){
            endX=mData.getDataTypeMaxCount()-1;
        }

        float endY = (float) updateNowPrice;
        MPPointD updatePointion = lineChart.getTransformer(YAxis.AxisDependency.LEFT).getPixelForValues(endX, endY);
        tPoint2.setLineStartPoint((float)startPosition.x,(float) startPosition.y);
        tPoint2.setCoordinateX((float) updatePointion.x,endX);
        tPoint2.setCoordinateY((float) updatePointion.y,endY);
            tPoint2.setCenterColor(GlobaleConfig.getColorByCompare(compare));
            tPoint2.setTwinkleColor(GlobaleConfig.getColorByCompare(compare));
            tPoint2.startTwinkle(2);

//            double compare = timeDatamodel.getNowPrice() == mData.getPreClose() ? 1 : (timeDatamodel.getNowPrice() - mData.getPreClose());
//            twinklePoint.setCenterColor(GlobaleConfig.getColorByCompare(compare));
//            twinklePoint.setTwinkleColor(GlobaleConfig.getColorByCompare(compare));
//            twinklePoint.startTwinkle(2);
        //        playHeaderAnimation(4);

        if(!lineChart.valuesToHighlight()){
            updateText(mData.getDatas().size()-1);
        }

    }


    public void updateAxisLeft(float min,float max) {
        float axisMinimum = axisLeftLine.getAxisMinimum();
        float axisMaximum = axisLeftLine.getAxisMaximum();
        boolean isChange=false;
        if(axisMaximum<max){
            axisLeftLine.setAxisMaximum(max);
            isChange = true;
        }
        if(axisMinimum>min){
            axisLeftLine.setAxisMinimum(min);
            isChange = true;
        }
        if(isChange){
            lineChart.notifyDataSetChanged();
            barChart.notifyDataSetChanged();
            lineChart.invalidate();
            barChart.invalidate();
        }
    }

    public void updateAxisLeft(float currentPrice) {
        float axisMinimum = axisLeftLine.getAxisMinimum();
        float axisMaximum = axisLeftLine.getAxisMaximum();
        boolean isChange=false;
        if(axisMaximum<currentPrice){
            axisLeftLine.setAxisMaximum(currentPrice);
            isChange = true;
        }
        if(axisMinimum>currentPrice){
            axisLeftLine.setAxisMinimum(currentPrice);
            isChange = true;
        }

        if(isChange){
            lineChart.notifyDataSetChanged();
            barChart.notifyDataSetChanged();
            lineChart.invalidate();
            barChart.invalidate();
        }
    }

    public void cleanData() {
        if (lineChart != null && lineChart.getLineData() != null) {
            lineChart.clearValues();
            barChart.clearValues();
        }
        if (cirCleView != null) {
            cirCleView.setVisibility(View.GONE);
        }
    }

    private void setMarkerView(TimeDataManage mData) {
        LeftMarkerView leftMarkerView = new LeftMarkerView(mContext, R.layout.my_markerview, precision);
        TimeRightMarkerView rightMarkerView = new TimeRightMarkerView(mContext, R.layout.my_markerview);
        BarBottomMarkerView bottomMarkerView = new BarBottomMarkerView(mContext, R.layout.my_markerview,mData);
        lineChart.setMarker(null, null, mData,bottomMarkerView);
//        BarBottomMarkerView bottomMarkerView = new BarBottomMarkerView(mContext, R.layout.my_markerview,mData);
//        barChart.setMarker(bottomMarkerView, mData);
    }


    @Override
    public void onEventMainThread(BaseEvent event) {
        if (event.method == 1) {
            CirclePositionTime position = (CirclePositionTime) event.obj;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                cirCleView.setX(position.cx - cirCleView.getWidth() / 2);
                cirCleView.setY(position.cy - cirCleView.getHeight() / 2);
            }
        }
    }

    public void setXLabels(SparseArray<String> xLabels) {
        this.xLabels = xLabels;
    }

    public SparseArray<String> getXLabels() {
        return xLabels;
    }


    public void eventBusUnregister() {
        EventBus.getDefault().unregister(this);
    }


    public void doBarChartSwitch(int indexTypeOnyDay) {

    }



    TwinklePoint twinklePoint;
    private void initTwinklePoint() {
        twinklePoint = new TwinklePoint.Builder(getContext())
                .setTwinkleColor(getResources().getColor(R.color.fit_black))
                .setCenterColor(getResources().getColor(R.color.up_color))
                .setDuration(1000)
                .build();
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        twinklePoint.setLayoutParams(layoutParams);
        ((ViewGroup)findViewById(R.id.circle_frame_time)).addView(twinklePoint);
    }


    public boolean valuesToHighlight(){
        return lineChart.valuesToHighlight() || barChart.valuesToHighlight();
    }
}
