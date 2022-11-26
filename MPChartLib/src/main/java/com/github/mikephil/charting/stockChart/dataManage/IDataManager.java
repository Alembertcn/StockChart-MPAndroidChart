package com.github.mikephil.charting.stockChart.dataManage;

import org.json.JSONObject;

public interface IDataManager {

    /**
     * 解析数据
     */
    public void parseData(JSONObject object, String assetId, double preClosePrice,int type);
    public String getIndexTime(int index);
}
