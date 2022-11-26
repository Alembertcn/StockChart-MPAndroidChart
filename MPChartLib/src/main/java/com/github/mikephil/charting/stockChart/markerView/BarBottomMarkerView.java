package com.github.mikephil.charting.stockChart.markerView;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.stockChart.dataManage.IDataManager;
import com.github.mikephil.charting.utils.MPPointF;

/**
 * Created by Administrator on 2016/9/12.
 */
public class BarBottomMarkerView extends MarkerView {

    private TextView markerTv;
    private IDataManager dataManager;
    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     *
     * @param context
     * @param layoutResource the layout resource to use for the MarkerView
     */
    public BarBottomMarkerView(Context context, int layoutResource,IDataManager dataManager) {
        super(context, layoutResource);
        markerTv = (TextView) findViewById(R.id.marker_tv);
        this.dataManager = dataManager;
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        markerTv.setText(dataManager.getIndexTime(Math.round(highlight.getX())));
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }

}
