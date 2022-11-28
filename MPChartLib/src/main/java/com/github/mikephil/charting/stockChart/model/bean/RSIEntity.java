package com.github.mikephil.charting.stockChart.model.bean;


import com.github.mikephil.charting.stockChart.model.KLineDataModel;

import java.util.ArrayList;

/**
 * Created by loro on 2017/3/7.
 */

public class RSIEntity {

    private ArrayList<Float> RSIs;

    /**
     * @param kLineBeens
     * @param n          几日
     */
    public RSIEntity(ArrayList<KLineDataModel> kLineBeens, int n) {
        this(kLineBeens, n, 100);
    }

    /**
     * @param kLineBeens
     * @param n          几日
     * @param defult     不足N日时的默认值
     *                   https://github.com/kimboqi/stock-indicators/blob/master/indicator.js
     */
    public RSIEntity(ArrayList<KLineDataModel> kLineBeens, int n, float defult) {
        RSIs = new ArrayList<>();
        double lastClosePx = kLineBeens.get(0).getPreClose();
        double lastSm=0f;
        double lastSa=0f;
        for (int i = 0 ; i < kLineBeens.size(); i ++) {
            double c = kLineBeens.get(i).getClose();
            double m = Math.max(c-lastClosePx, 0), a = Math.abs(c-lastClosePx);
//                if (!result.hasOwnProperty("rsi"+d)) {
                if (i==0) {
                    lastSm=0f;
                    lastSa=0f;
                    RSIs.add(0f);
                } else {
//                    result["lastSm"+n] = (m + (n - 1) * result["lastSm"+n]) / n;
//                    result["lastSa"+n] = (a + (n - 1) * result["lastSa"+n]) / n;
                    lastSm = (m + (n - 1) * lastSm) / n;
                    lastSa = (a + (n - 1) * lastSa) / n;
//                    if (result["lastSa"+n] != 0) {
                    if (lastSa != 0) {
//                        result["rsi"+n].push(result["lastSm"+n] / result["lastSa"+n] * 100);
                        RSIs.add((float) (lastSm / lastSa * 100));
                    } else {
//                        result["rsi"+n].push(0);
                        RSIs.add(0f);

                    }
                }
            lastClosePx = c;
        }
        if (true)return;
//        return {"rsi6": result["rsi6"], "rsi12": result["rsi12"], "rsi24": result["rsi24"]};
        
        
        
        float sum = 0.0f;
        float dif = 0.0f;
        float rs = 0.0f;
        float rsi = 0.0f;
        int index = n - 1;
        if (kLineBeens != null && kLineBeens.size() > 0) {
            for (int i = 0; i < kLineBeens.size(); i++) {
//                if (i > 0) {
                if (n == 0) {
                    sum = 0.0f;
                    dif = 0.0f;
                } else {
                    int k = i - n + 1;
                    Float[] wrs = getAAndB(k, i, (ArrayList<KLineDataModel>) kLineBeens);
                    sum = wrs[0];
                    dif = wrs[1];
                }
//                }
                if (dif != 0) {
                    rs = sum / dif;
//                    float c = 100.0f / (1 + rs);
//                    rsi = 100 - c;
                    rsi = 100* rs/(1+rs);

//                    float h = sum + dif;
//                    rsi = sum / h * 100;
                } else {
                    rsi = 100;
                }

                if (i < index) {
                    rsi = defult;
                }
                RSIs.add(rsi);
            }
        }
    }

    private Float[] getAAndB(Integer a, Integer b, ArrayList<KLineDataModel> kLineBeens) {
        if (a < 0) {
            a = 0;
        }
        float sum = 0.0f;
        float dif = 0.0f;
        Float[] abs = new Float[2];
        float closeT, closeY;
        for (int i = a; i <= b; i++) {
            double v = kLineBeens.get(i).getClose() - kLineBeens.get(i).getPreClose();
            if(v>0){
                sum+=v;
            }else{
                dif += v;
            }
//            if (i > a) {
//                closeT =  (float) kLineBeens.get(i).getClose();
//                closeY = (float) kLineBeens.get(i - 1).getClose();
//
//                float c = closeT - closeY;
//                if (c > 0) {
//                    sum = sum + c;
//                } else {
//                    dif = dif + Math.abs(c);
//                }
//            }
        }

        abs[0] = sum;
        abs[1] = Math.abs(dif);
        return abs;
    }

    public ArrayList<Float> getRSIs() {
        return RSIs;
    }
}
