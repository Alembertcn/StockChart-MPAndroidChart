package com.github.mikephil;

import com.github.mikephil.charting.stockChart.enums.ChartType;

import java.util.List;

public interface ISimpleChart {
    public void setMainLatitudeNum(int num);

    public void setSubLatitudeNum(int num);

    public void setTouchable(boolean b);

    public void setMoveable(boolean b);

    public void setZoomable(boolean b);

    public void setOnCrossLineMoveListener(OnCrossLineMoveListener onCrossLineMoveListener);

//    public void setPriceFormatter(IPriceFormatter priceFormatter); 外部格式化

    public void setGlobalChartType(String marketType, ChartType chartType);
//    public void updateOneDayData(List<String> priceList, List<String> avList, List<Histogram.HistogramBean> turnoverList, float maxPrice, float minPrice, float maxTurnover, TimeSharingApiBean timeSharingApiBean);
//public void setMainScaleDataAdapter(Coordinates.CoordinateScaleAdapter adapter)
//public void setSubScaleDataAdapter(Coordinates.CoordinateScaleAdapter adapter)
//public void setMainCoordinatesExtremum(String max, String min)//设置主坐标系的极值
//public void setSubCoordinatesExtremum(float max, float min) //设置副坐标系的极值

    /**
     * 设置K线数据
     */
//    public void setKData(List<CandleLine.CandleLineBean> kList, List<String> ma5List, List<String> ma10List, List<String> ma20List,
//                         List<Histogram.HistogramBean> turnoverList, float maxPrice, float minPrice);

    void setLastPointData(String data, double changePercent);

    //     void onPositionUpdate(int amount, double price);//分时图才显示 可能是持仓变化
//    public void onPendingOrderUpdate(List<PendingOrderElement> pendingOrderList);//分时图才显示 可能是持仓变化
    public int getSubChartType();
    public int getMainLineType();

    public void setSubChartTypes(int chartType);

    //    public void setSkillType(int subChartType);//估计没啥用
    public void subViewLoading();

    public void subViewLoadFinish();

//    public void setKDJData(List<String> kList, List<String> dList, List<String> jList)
//    public void setKTurnoverData(List<Histogram.HistogramBean> list) {
//    public void setMACDData(List<String> difList, List<String> deaList, List<MacdHistogram.MacdBean> macdList) {
//    public void setMAData(List<String> upList, List<String> middleList, List<String> downList) {
//    public void setBOLLData(List<CandleLine.CandleLineBean> kList, List<String> upList, List<String> middleList, List<String> downList) {
//    public void setRSIData(List<String> rList, List<String> sList, List<String> iList) {

//    public void setTagPointList(List<TagPoint> tagPointList);//分时图才显示


    interface OnCrossLineMoveListener {
        void onCrossLineMove(int index, int drawIndex);

        void onCrossLineDismiss();
    }

    interface IPriceFormatter {
        String format(String price);
    }
}
