package com.github.mikephil.charting.stockChart.dataManage;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;

import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.R;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.GlobaleConfig;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.stockChart.model.KLineDataModel;
import com.github.mikephil.charting.stockChart.model.bean.BOLLEntity;
import com.github.mikephil.charting.stockChart.model.bean.KDJEntity;
import com.github.mikephil.charting.stockChart.model.bean.MACDEntity;
import com.github.mikephil.charting.stockChart.model.bean.RSIEntity;
import com.github.mikephil.charting.utils.DataTimeUtil;
import com.github.mikephil.charting.utils.NumberUtils;
import com.github.mikephil.charting.utils.Utils;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * K线数据解析
 */
public class KLineDataManage extends IDataManager {
    private Context mContext;
    private ArrayList<KLineDataModel> kDatas = new ArrayList<>();
    private float offSet = 0f;//K线图最右边偏移量
    private String assetId;//股票代号

    //MA参数
    public int N1 = 5;
    public int N2 = 10;
    public int N3 = 20;
    //EMA参数
    public int EMAN1 = 5;
    public int EMAN2 = 10;
    public int EMAN3 = 30;
    //SMA参数
    public int SMAN = 14;
    //BOLL参数
//    public int BOLLN = 26;
    public int BOLLN = 20;

    //MACD参数
    public int SHORT = 12;
    public int LONG = 26;
    public int M = 9;
    //KDJ参数
    public int KDJN = 9;
    public int KDJM1 = 3;
    public int KDJM2 = 3;
    //CCI参数
    public int CCIN = 14;
    //RSI参数
    public int RSIN1 = 6;
    public int RSIN2 = 12;
    public int RSIN3 = 24;

//    ONE_DAY(0,""),FIVE_DAY(1,""),
//    K_DAY(2,""),K_WEEK(3,"W"),
//    K_MONTH(4,"M"),
//    K_MINUTE_1(5,"M1"),K_MINUTE_3(6,"M3"),
//    K_MINUTE_5(7,"M5"),K_MINUTE_15(8,"M15"),
//    K_MINUTE_30(9,"M30"),K_MINUTE_60(10,"M60");

    private ArrayList<Integer> xCanUseIndexes = new ArrayList<>();

    private CandleDataSet candleDataSet;//蜡烛图集合
    private BarDataSet volumeDataSet;//成交量集合
    private BarDataSet barDataMACD;//MACD集合
    private CandleDataSet bollCandleDataSet;//BOLL蜡烛图集合

    private List<ILineDataSet> lineDataMA = new ArrayList<>();

    private List<ILineDataSet> lineDataMACD = new ArrayList<>();
    private ArrayList<BarEntry> macdData = new ArrayList<>();
    private ArrayList<Entry> deaData = new ArrayList<>();
    private ArrayList<Entry> difData = new ArrayList<>();

    private List<ILineDataSet> lineDataKDJ = new ArrayList<>();
    private ArrayList<Entry> kData = new ArrayList<>();
    private ArrayList<Entry> dData = new ArrayList<>();
    private ArrayList<Entry> jData = new ArrayList<>();

    private List<ILineDataSet> lineDataBOLL = new ArrayList<>();
    private ArrayList<Entry> bollDataUP = new ArrayList<>();
    private ArrayList<Entry> bollDataMB = new ArrayList<>();
    private ArrayList<Entry> bollDataDN = new ArrayList<>();

    private List<ILineDataSet> lineDataRSI = new ArrayList<>();
    private ArrayList<Entry> rsiData6 = new ArrayList<>();
    private ArrayList<Entry> rsiData12 = new ArrayList<>();
    private ArrayList<Entry> rsiData24 = new ArrayList<>();
    private double preClosePrice;//K线图昨收价

    public KLineDataManage(Context context) {
        mContext = context;
    }


    @Override
    public void parseData(JSONObject object, String assetId, double preClosePrice, int type) {
        this.currentType = type;
        this.assetId = assetId;
        if (object != null) {
            kDatas.clear();
            lineDataMA.clear();
            JSONArray data = object.optJSONArray("data");
            if (data != null) {
                xCanUseIndexes.clear();
                ArrayList<CandleEntry> candleEntries = new ArrayList<>();
                ArrayList<BarEntry> barEntries = new ArrayList<>();
                ArrayList<Integer> colors = new ArrayList<>();
                List<Entry> line5Entries = new ArrayList<>();
                List<Entry> line10Entries = new ArrayList<>();
                List<Entry> line20Entries = new ArrayList<>();
                for (int i = 0; i < data.length(); i++) {
                    KLineDataModel klineDatamodel = new KLineDataModel();
                    klineDatamodel.setDateMills(data.optJSONArray(i).optLong(0, 0L));
                    klineDatamodel.setOpen(data.optJSONArray(i).optDouble(1));
                    klineDatamodel.setHigh(data.optJSONArray(i).optDouble(2));
                    klineDatamodel.setLow(data.optJSONArray(i).optDouble(3));
                    klineDatamodel.setClose(data.optJSONArray(i).optDouble(4));
                    klineDatamodel.setTotal(NumberUtils.stringNoE10ForVol(Double.isNaN(data.optJSONArray(i).optDouble(5)) ? 0 : data.optJSONArray(i).optDouble(5)));
                    klineDatamodel.setVolume(NumberUtils.stringNoE10ForVol(Double.isNaN(data.optJSONArray(i).optDouble(6)) ? 0 : data.optJSONArray(i).optDouble(6)));
                    klineDatamodel.setMa5(data.optJSONArray(i).optDouble(7));
                    klineDatamodel.setMa10(data.optJSONArray(i).optDouble(8));
                    klineDatamodel.setMa20(data.optJSONArray(i).optDouble(9));
                    klineDatamodel.setMa30(0.0);
                    klineDatamodel.setMa60(0.0);
                    klineDatamodel.setPreClose(data.optJSONArray(i).optDouble(10));
//                    klineDatamodel.setDateMills(data.optJSONArray(i).optLong(0, 0L));
//                    klineDatamodel.setOpen(data.optJSONArray(i).optDouble(1));
//                    klineDatamodel.setHigh(data.optJSONArray(i).optDouble(2));
//                    klineDatamodel.setLow(data.optJSONArray(i).optDouble(3));
//                    klineDatamodel.setClose(data.optJSONArray(i).optDouble(4));
//                    klineDatamodel.setVolume(NumberUtils.stringNoE10ForVol(Double.isNaN(data.optJSONArray(i).optDouble(5)) ? 0 : data.optJSONArray(i).optDouble(5)));
//                    klineDatamodel.setTotal(NumberUtils.stringNoE10ForVol(Double.isNaN(data.optJSONArray(i).optDouble(6)) ? 0 : data.optJSONArray(i).optDouble(6)));
//                    klineDatamodel.setMa5(data.optJSONArray(i).optDouble(7));
//                    klineDatamodel.setMa10(data.optJSONArray(i).optDouble(8));
//                    klineDatamodel.setMa20(data.optJSONArray(i).optDouble(9));
//                    klineDatamodel.setMa30(data.optJSONArray(i).optDouble(10));
//                    klineDatamodel.setMa60(data.optJSONArray(i).optDouble(11));
//                    klineDatamodel.setPreClose(data.optJSONArray(i).optDouble(12));

                    preClosePrice = klineDatamodel.getPreClose();
                    kDatas.add(klineDatamodel);

                    if(isMinKType()){
                        if (DataTimeUtil.isHalfHourTimePoint(getKLineDatas().get(i).getDateMills()) && i > 0 && !DataTimeUtil.isSameMini(getKLineDatas().get(i).getDateMills(), getKLineDatas().get(i - 1).getDateMills())) {
                            xCanUseIndexes.add(i);
                        }
                    }else{
                        if (i > 0 && !DataTimeUtil.isSameMoth(getKLineDatas().get(i).getDateMills(), getKLineDatas().get(i - 1).getDateMills())) {
                            xCanUseIndexes.add(i);
                        }
                    }
                    
                    candleEntries.add(new CandleEntry(i + offSet, (float) getKLineDatas().get(i).getHigh(), (float) getKLineDatas().get(i).getLow(), (float) getKLineDatas().get(i).getOpen(), (float) getKLineDatas().get(i).getClose()));

                    float color = getKLineDatas().get(i).getOpen() == getKLineDatas().get(i).getClose() ? 0f : getKLineDatas().get(i).getOpen() > getKLineDatas().get(i).getClose() ? -1f : 1f;
                    barEntries.add(new BarEntry(i + offSet, (float) getKLineDatas().get(i).getVolume(), color));
                    colors.add(GlobaleConfig.getColorByCompare(getKLineDatas().get(i).getClose()-getKLineDatas().get(i).getOpen()));
                    line5Entries.add(new Entry(i + offSet, (float) getKLineDatas().get(i).getMa5()));
                    line10Entries.add(new Entry(i + offSet, (float) getKLineDatas().get(i).getMa10()));
                    line20Entries.add(new Entry(i + offSet, (float) getKLineDatas().get(i).getMa20()));
                }
                line5Entries = line5Entries.size() > N1 ? line5Entries.subList(N1 - 1, line5Entries.size()) : line5Entries;
                line10Entries = line10Entries.size() > N2 ? line10Entries.subList(N2 - 1, line10Entries.size()) : line10Entries;
                line20Entries = line20Entries.size() > N3 ? line20Entries.subList(N3 - 1, line20Entries.size()) : line20Entries;
                candleDataSet = setACandle(candleEntries);
                bollCandleDataSet = setBOLLCandle(candleEntries);
                volumeDataSet = setABar(barEntries, "成交量",colors);
                lineDataMA.add(setALine(ColorType.blue, line5Entries, false));
                lineDataMA.add(setALine(ColorType.yellow, line10Entries, false));
                lineDataMA.add(setALine(ColorType.purple, line20Entries, false));
                //主图boll线直接初始化
                initBOLL();
            }
        }
    }

    /**
     * 初始化自己计算MACD
     */
    public void initMACD() {
        lineDataMACD.clear();
        MACDEntity macdEntity = new MACDEntity(getKLineDatas(), SHORT, LONG, M);

        macdData = new ArrayList<>();
        deaData = new ArrayList<>();
        difData = new ArrayList<>();
        for (int i = 0; i < macdEntity.getMACD().size(); i++) {
            macdData.add(new BarEntry(i + offSet, macdEntity.getMACD().get(i), macdEntity.getMACD().get(i)));
            deaData.add(new Entry(i + offSet, macdEntity.getDEA().get(i)));
            difData.add(new Entry(i + offSet, macdEntity.getDIF().get(i)));
        }
        barDataMACD = setABar(macdData);
        lineDataMACD.add(setALine(ColorType.blue, deaData));
        lineDataMACD.add(setALine(ColorType.yellow, difData));
    }

    /**
     * 初始化自己计算KDJ
     */
    public void initKDJ() {
        lineDataKDJ.clear();
        KDJEntity kdjEntity = new KDJEntity(getKLineDatas(), KDJN, KDJM1, KDJM2);

        kData = new ArrayList<>();
        dData = new ArrayList<>();
        jData = new ArrayList<>();
        for (int i = 0; i < kdjEntity.getD().size(); i++) {
            kData.add(new Entry(i + offSet, kdjEntity.getK().get(i)));
            dData.add(new Entry(i + offSet, kdjEntity.getD().get(i)));
            jData.add(new Entry(i + offSet, kdjEntity.getJ().get(i)));
        }
        lineDataKDJ.add(setALine(ColorType.blue, kData, "KDJ" + N1, false));
        lineDataKDJ.add(setALine(ColorType.yellow, dData, "KDJ" + N2, false));
        lineDataKDJ.add(setALine(ColorType.purple, jData, "KDJ" + N3, true));
    }

    /**
     * 初始化自己计算BOLL
     */
    public void initBOLL() {
        lineDataBOLL.clear();
        BOLLEntity bollEntity = new BOLLEntity(getKLineDatas(), BOLLN);
        lineDataBOLL.clear();
        bollDataUP = new ArrayList<>();
        bollDataMB = new ArrayList<>();
        bollDataDN = new ArrayList<>();
        for (int i = 0; i < bollEntity.getUPs().size(); i++) {
            if (i <= BOLLN - 1) continue;
            bollDataUP.add(new Entry(i + offSet, bollEntity.getUPs().get(i)));
            bollDataMB.add(new Entry(i + offSet, bollEntity.getMBs().get(i)));
            bollDataDN.add(new Entry(i + offSet, bollEntity.getDNs().get(i)));
        }
        lineDataBOLL.add(setALine(ColorType.blue, bollDataUP, false));
        lineDataBOLL.add(setALine(ColorType.yellow, bollDataMB, false));
        lineDataBOLL.add(setALine(ColorType.purple, bollDataDN, false));
    }

    /**
     * 初始化自己计算RSI
     */
    public void initRSI() {
        lineDataRSI.clear();
        RSIEntity rsiEntity6 = new RSIEntity(getKLineDatas(), RSIN1);
        RSIEntity rsiEntity12 = new RSIEntity(getKLineDatas(), RSIN2);
        RSIEntity rsiEntity24 = new RSIEntity(getKLineDatas(), RSIN3);

        rsiData6 = new ArrayList<>();
        rsiData12 = new ArrayList<>();
        rsiData24 = new ArrayList<>();
        for (int i = 0; i < rsiEntity6.getRSIs().size(); i++) {
            rsiData6.add(new Entry(i + offSet, rsiEntity6.getRSIs().get(i)));
            rsiData12.add(new Entry(i + offSet, rsiEntity12.getRSIs().get(i)));
            rsiData24.add(new Entry(i + offSet, rsiEntity24.getRSIs().get(i)));
        }
        lineDataRSI.add(setALine(ColorType.blue, rsiData6, "RSI" + RSIN1, false));
        lineDataRSI.add(setALine(ColorType.yellow, rsiData12, "RSI" + RSIN2, false));
        lineDataRSI.add(setALine(ColorType.purple, rsiData24, "RSI" + RSIN3, true));
    }

    private CandleDataSet setACandle(ArrayList<CandleEntry> candleEntries) {
        CandleDataSet candleDataSet = new CandleDataSet(candleEntries, "蜡烛线");
        candleDataSet.setDrawHorizontalHighlightIndicator(false);
        candleDataSet.setHighlightEnabled(true);
        candleDataSet.setHighLightColor(ContextCompat.getColor(mContext, R.color.highLight_Color));
        candleDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        candleDataSet.setDecreasingColor(ContextCompat.getColor(mContext, R.color.down_color));
        candleDataSet.setDecreasingPaintStyle(Paint.Style.STROKE);
        candleDataSet.setBarSpace(0.15f);
        candleDataSet.setIncreasingColor(ContextCompat.getColor(mContext, R.color.up_color));
        candleDataSet.setIncreasingPaintStyle(Paint.Style.FILL_AND_STROKE);
        candleDataSet.setNeutralColor(ContextCompat.getColor(mContext, R.color.equal_color));
        candleDataSet.setShadowColorSameAsCandle(true);
        candleDataSet.setValueTextSize(GlobaleConfig.COMMON_TEXT_SIZE);
        candleDataSet.setValueTextColor(GlobaleConfig.VALUE_COLOR);
        candleDataSet.setDrawValues(true);
        return candleDataSet;
    }

    private CandleDataSet setBOLLCandle(ArrayList<CandleEntry> candleEntries) {
        CandleDataSet candleDataSet = new CandleDataSet(candleEntries, "BOLL叠加蜡烛线");
        candleDataSet.setDrawHorizontalHighlightIndicator(false);
        candleDataSet.setHighlightEnabled(true);
        candleDataSet.setHighLightColor(ContextCompat.getColor(mContext, R.color.highLight_Color));
        candleDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        candleDataSet.setDecreasingColor(ContextCompat.getColor(mContext, R.color.down_color));
        candleDataSet.setDecreasingPaintStyle(Paint.Style.FILL_AND_STROKE);
        candleDataSet.setIncreasingColor(ContextCompat.getColor(mContext, R.color.up_color));
        candleDataSet.setIncreasingPaintStyle(Paint.Style.FILL_AND_STROKE);
        candleDataSet.setNeutralColor(ContextCompat.getColor(mContext, R.color.equal_color));

        candleDataSet.setDrawValues(false);
        candleDataSet.setDrawIcons(false);
        candleDataSet.setShowCandleBar(false);
        return candleDataSet;
    }

    private LineDataSet setALine(ColorType ma, List<Entry> lineEntries) {
        String label = "ma" + ma;
        return setALine(ma, lineEntries, label);
    }

    private LineDataSet setALine(ColorType ma, List<Entry> lineEntries, boolean highlightEnable) {
        String label = "ma" + ma;
        return setALine(ma, lineEntries, label, highlightEnable);
    }

    private LineDataSet setALine(ColorType ma, List<Entry> lineEntries, String label) {
        boolean highlightEnable = false;
        return setALine(ma, lineEntries, label, highlightEnable);
    }

    //行情走势线属性设置
    private LineDataSet setALine(ColorType colorType, List<Entry> lineEntries, String label, boolean highlightEnable) {
        LineDataSet lineDataSetMa = new LineDataSet(lineEntries, label);
        lineDataSetMa.setDrawHorizontalHighlightIndicator(false);
        lineDataSetMa.setHighlightEnabled(highlightEnable);//是否画高亮十字线
        lineDataSetMa.setHighLightColor(ContextCompat.getColor(mContext, R.color.highLight_Color));//高亮十字线颜色
        lineDataSetMa.setDrawValues(false);//是否画出每个蜡烛线的数值
        if (colorType == ColorType.blue) {
            lineDataSetMa.setColor(ContextCompat.getColor(mContext, R.color.ma5));
        } else if (colorType == ColorType.yellow) {
            lineDataSetMa.setColor(ContextCompat.getColor(mContext, R.color.ma10));
        } else if (colorType == ColorType.purple) {
            lineDataSetMa.setColor(ContextCompat.getColor(mContext, R.color.ma20));
        }
        lineDataSetMa.setLineWidth(0.6f);
        lineDataSetMa.setDrawCircles(false);
        lineDataSetMa.setAxisDependency(YAxis.AxisDependency.LEFT);
        return lineDataSetMa;
    }

    private BarDataSet setABar(ArrayList<BarEntry> barEntries) {
        String label = "BarDataSet";
        return setABar(barEntries, label,null);
    }

    //成交量柱状图属性设置
    private BarDataSet setABar(ArrayList<BarEntry> barEntries, String label,List<Integer> colors) {
        BarDataSet barDataSet = new BarDataSet(barEntries, label);
        barDataSet.setHighlightEnabled(true);//是否画高亮十字线
        barDataSet.setHighLightColor(ContextCompat.getColor(mContext, R.color.highLight_Color));//高亮十字线颜色
        barDataSet.setValueTextSize(10);
        barDataSet.setDrawValues(false);//是否画出每个蜡烛线的数值
        barDataSet.setNeutralColor(ContextCompat.getColor(mContext, R.color.equal_color));//行情平势时蜡烛的标识颜色
        barDataSet.setIncreasingColor(ContextCompat.getColor(mContext, R.color.up_color));//行情涨势时蜡烛的标识颜色
        barDataSet.setDecreasingColor(ContextCompat.getColor(mContext, R.color.down_color));//行情跌势时蜡烛的标识颜色
        barDataSet.setIncreasingPaintStyle(Paint.Style.FILL);
        barDataSet.setDecreasingPaintStyle(Paint.Style.FILL);
        barDataSet.setBarBorderWidth(Utils.convertPixelsToDp(1.5f));
        barDataSet.setBarBorderColor(Color.TRANSPARENT);
        if(colors!=null){
            barDataSet.setColors(colors);
        }
        return barDataSet;
    }


    float sum = 0;

    private float getSum(Integer a, Integer b) {
        sum = 0;
        if (a < 0) {
            return 0;
        }
        for (int i = a; i <= b; i++) {
            sum += getKLineDatas().get(i).getClose();
        }
        return sum;
    }

    public void addAKLineData(KLineDataModel kLineData) {
        kDatas.add(kLineData);
    }

    public void addKLineDatas(List<KLineDataModel> kLineData) {
        kDatas.addAll(kLineData);
    }

    public synchronized ArrayList<KLineDataModel> getKLineDatas() {
        return kDatas;
    }

    public void resetKLineData() {
        kDatas.clear();
    }

    public void setKLineData(ArrayList<KLineDataModel> datas) {
        kDatas.clear();
        kDatas.addAll(datas);
    }

    public List<ILineDataSet> getLineDataMA() {
        return lineDataMA;
    }

    public List<ILineDataSet> getLineDataBOLL() {
        return lineDataBOLL;
    }

    public List<ILineDataSet> getLineDataKDJ() {
        return lineDataKDJ;
    }

    public List<ILineDataSet> getLineDataRSI() {
        return lineDataRSI;
    }

    public List<ILineDataSet> getLineDataMACD() {
        return lineDataMACD;
    }

    public BarDataSet getBarDataMACD() {
        return barDataMACD;
    }

    public BarDataSet getVolumeDataSet() {
        return volumeDataSet;
    }

    public CandleDataSet getCandleDataSet() {
        return candleDataSet;
    }

    public CandleDataSet getBollCandleDataSet() {
        return bollCandleDataSet;
    }

    public float getOffSet() {
        return offSet;
    }

    public ArrayList<BarEntry> getMacdData() {
        return macdData;
    }

    public ArrayList<Entry> getDeaData() {
        return deaData;
    }

    public ArrayList<Entry> getDifData() {
        return difData;
    }

    public ArrayList<Entry> getkData() {
        return kData;
    }

    public ArrayList<Entry> getdData() {
        return dData;
    }

    public ArrayList<Entry> getjData() {
        return jData;
    }

    public ArrayList<Entry> getBollDataUP() {
        return bollDataUP;
    }

    public ArrayList<Entry> getBollDataMB() {
        return bollDataMB;
    }

    public ArrayList<Entry> getBollDataDN() {
        return bollDataDN;
    }

    public ArrayList<Entry> getRsiData6() {
        return rsiData6;
    }

    public ArrayList<Entry> getRsiData12() {
        return rsiData12;
    }

    public ArrayList<Entry> getRsiData24() {
        return rsiData24;
    }

    public void setOneMaValue(LineData lineData, int i) {
        for (int k = 0; k < lineData.getDataSets().size(); k++) {
            ILineDataSet lineDataSet = lineData.getDataSetByIndex(k);
            lineDataSet.removeEntryByXValue(i);
            if (k == 0) {
                if (i >= N1) {
                    sum = 0;
                    float all5 = getSum(i - (N1 - 1), i) / N1;
                    lineDataSet.addEntry(new Entry(i + offSet, all5));
                }
            } else if (k == 1) {
                if (i >= N2) {
                    sum = 0;
                    float all10 = getSum(i - (N2 - 1), i) / N2;
                    lineDataSet.addEntry(new Entry(i + offSet, all10));
                }
            } else if (k == 2) {
                if (i >= N3) {
                    sum = 0;
                    float all20 = getSum(i - (N3 - 1), i) / N3;
                    lineDataSet.addEntry(new Entry(i + offSet, all20));
                }
            }
        }
    }

    public int getCurrentKType() {
        return currentType;
    }


    @Override
    public String getIndexTime(int index) {
        String timeFormat = "";
        if (index < kDatas.size()) {
            Long dateMills = kDatas.get(index).getDateMills();
            switch (currentType) {
                case K_1MIN:
                case K_5MIN:
                case K_15MIN:
                case K_30MIN:
                case K_60MIN:
                    timeFormat = DataTimeUtil.secToTime(dateMills, "yy/MM/dd hh:mm");
                    break;
                case K_1MONTH:
                    timeFormat = DataTimeUtil.secToTime(dateMills, "yy/MM");
                    break;
                default:
                    timeFormat = DataTimeUtil.secToTime(dateMills, "yy/MM/dd");
                    break;
            }
        }
        return timeFormat;
    }

    @Override
    public Integer[] getXCanUseIndexes() {
        return xCanUseIndexes.toArray(new Integer[]{});
    }

    @Override
    public ValueFormatter getXValueFormatter() {
        return mGlobalXFormatter;
    }

    private boolean isMinKType(){
        return currentType == K_1MIN
                || currentType == K_5MIN
                || currentType == K_15MIN
                || currentType == K_30MIN
                || currentType == K_60MIN;
    }

    ValueFormatter mGlobalXFormatter = new ValueFormatter() {
        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            int index = (int) (value - getOffSet());
            if (index < 0 || index >= kDatas.size()) {
                return "";
            } else {
                float[] mEntries = axis.mEntries;
                int lastIndex = -1;
                for (int i = 0; i < mEntries.length; i++) {
                    //只取第一个防止缓存影响
                    if (mEntries[i] == value) {
                        if (i != 0) {
                            lastIndex = Math.round(mEntries[i - 1]);
                        }
                        //数组是增量的只需要绘制和获取最前面的 后面的是缓存其他的数据
                        break;
                    }
                }

                String currentStr = getIndexTime(index);
                Long timeSrc = kDatas.get(index).getDateMills();

                if (lastIndex == -1 || kDatas.size() <= lastIndex) {
                    currentStr = DataTimeUtil.secToTime(timeSrc, isMinKType()?"MM/dd HH:mm":"yyyy/MM/dd");
                } else {
                    Long timeLast = kDatas.get(lastIndex).getDateMills();
                    if(isMinKType()){
                        if (DataTimeUtil.isSameDay(timeLast, timeSrc)) {
                            currentStr = DataTimeUtil.secToTime(timeSrc, "HH:mm");
                        } else {
                            currentStr = DataTimeUtil.secToTime(timeSrc, "MM/dd HH:mm");
                        }
                    }else{
                        if (DataTimeUtil.isSameYear(timeLast, timeSrc)) {
                            currentStr = DataTimeUtil.secToTime(timeSrc, "MM/dd");
                        } else {
                            currentStr = DataTimeUtil.secToTime(timeSrc, "yyyy/MM/dd");
                        }
                    }

                }
                return currentStr;
            }
        }
    };


    enum ColorType {
        blue,
        yellow,
        purple
    }

    public String getAssetId() {
        return assetId;
    }

    public double getPreClosePrice() {
        return preClosePrice;
    }
}
