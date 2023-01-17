package com.github.mikephil.charting.stockChart.dataManage;

import com.github.mikephil.charting.formatter.ValueFormatter;

import org.json.JSONObject;

public abstract class IDataManager {
    // k线类型
    public static final int K_1MIN = 5;
    public static final int K_5MIN = 7;
    public static final int K_15MIN = 8;
    public static final int K_30MIN = 9;
    public static final int K_60MIN = 10;
    public static final int K_1DAY = 6;
    public static final int K_1WEEK = 11;
    public static final int K_1MONTH = 4;
    
    public static final int ONE_DAY = 0;
    public static final int ONE_DAY_PRE = 2;//盘前
    public static final int ONE_DAY_AFTER = 3;//盘后
    public static final int FIVE_DAY = 1;
    int currentType = K_1DAY;

    /**
     * 解析数据
     */
    public abstract void parseData(JSONObject object, String assetId, double preClosePrice, int type);

    public abstract String getIndexTime(int index);

    public abstract Integer[] getXCanUseIndexes();

    abstract ValueFormatter getXValueFormatter();
    
}
