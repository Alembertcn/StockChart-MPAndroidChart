package com.github.mikephil.charting;

import android.graphics.Color;
import android.provider.CalendarContract;

public class GlobaleConfig {
    public static int COMMON_TEXT_SIZE=9;
    public static int VALUE_COLOR= Color.BLACK;
    public static int UP_COLOR= Color.RED;
    public static int DOWN_COLOR= Color.GREEN;
    public static int EQ_COLOR= Color.GRAY;
    public static int MARKET_BG= Color.rgb(0x8c,0x9f,0xad);
    public static int getColorByCompare(double pre){
        return pre >= 0 ? UP_COLOR : DOWN_COLOR;
    }
    public static int getFallColor() {
        return DOWN_COLOR;
    }
    public static int getRiseColor() {
        return UP_COLOR;
    }
    public static int getEqualColor() {
        return EQ_COLOR;
    }
}
