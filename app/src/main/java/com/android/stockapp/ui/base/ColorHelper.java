package com.android.stockapp.ui.base;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.provider.CalendarContract;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.core.content.ContextCompat;

import com.android.stockapp.R;


/**
 * Created by Yin Shudi on 16/8/10.
 */
public class ColorHelper {

    private static String riseDownTheme;
    private static boolean useInternal;

    static {
        useInternal =false;
        riseDownTheme = "QUO_THEME_ONE";
    }

    public static boolean isRedUp(){
        return "QUO_THEME_ONE".equals(riseDownTheme);
    }
    public static String getRiseDownTheme() {
        return riseDownTheme;
    }

    public static void setRiseDownTheme(String riseDownTheme) {
        ColorHelper.riseDownTheme = riseDownTheme;
    }

    public static void setIsInternal(boolean isInternal) {
        ColorHelper.useInternal = isInternal;
    }

    public static int INVALID_COLOR = -1;

    public static void tintImageView(ImageView iv,int color) {
        if(iv!=null && iv.getDrawable() !=null){
            Drawable mutate = iv.getDrawable().mutate();
            mutate.setColorFilter(color,PorterDuff.Mode.SRC_IN);
            iv.setImageDrawable(mutate);
        }
    }

    public static void tintTextViewDrawable(TextView tv,int color){
        Drawable[] compoundDrawables = tv.getCompoundDrawables();
        for (int i = 0; i< compoundDrawables.length; i++) {
            if(compoundDrawables[i] == null)continue;
            Drawable compoundDrawable =  compoundDrawables[i].mutate();
            if(compoundDrawable !=null){
                compoundDrawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
            }
            compoundDrawables[i] = compoundDrawable;
        }
        tv.setCompoundDrawablesWithIntrinsicBounds(compoundDrawables[0],compoundDrawables[1],compoundDrawables[2],compoundDrawables[3]);
    }

    public static Drawable tintDrawable(Drawable drawable,int color){
        if (drawable == null) return null;
        drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        return drawable;
    }

    /**
     * 获取涨的颜色
     * */
    public static int getRiseColor(){
        return Color.RED;
    }

    /**
     * 获取涨的颜色
     * */
    public static int getRiseColorAlpha(int alphaPresent){
        int alpha = 255 * alphaPresent / 100;
        int riseColor = getRiseColor();
        if (alpha >= 0 && alpha <= 255) {
            return riseColor & 16777215 | alpha << 24;
        } else {
            throw new IllegalArgumentException("alpha must be between 0 and 255.");
        }
    }

    /**
     * 获取涨的颜色
     * */
    public static int getColorAlpha(int color,int alphaPresent){
        int alpha = 255 * alphaPresent / 100;
        if (alpha >= 0 && alpha <= 255) {
            return color & 16777215 | alpha << 24;
        } else {
            throw new IllegalArgumentException("alpha must be between 0 and 255.");
        }
    }


    /**
     * 获取涨的颜色
     * */
    public static int getFallColorAlpha(int alphaPresent){
        int alpha = 255 * alphaPresent / 100;
        int riseColor = getFallColor();
        if (alpha >= 0 && alpha <= 255) {
            return riseColor & 16777215 | alpha << 24;
        } else {
            throw new IllegalArgumentException("alpha must be between 0 and 255.");
        }
    }




    /**
     * 获取跌的颜色
     * */
    public static int getFallColor(){
        
        if(riseDownTheme.equalsIgnoreCase("QUO_THEME_TWO")){
            return Color.RED;
        }else {
            return Color.GREEN;
        }
    }


    /**
     * 获取平的颜色
     * */
    public static int getEqualColor(){
        
        return Color.GRAY;
    }

    /**
     * 获取颜色
     * */
    public static int getColor(@ColorRes int colorId,Context context){
        if(context ==null){
            context = BaseApp.getApp();
        }
        return ContextCompat.getColor(context, colorId);
    }

    /**
     * 获取颜色
     * */
    public static int getColor(Context context, @ColorRes int colorId){
        return ContextCompat.getColor(context, colorId);
    }


    /**
     * 获取颜色
     * */
    public static int getPrimaryColor(){
        return Color.RED;
    }

}
