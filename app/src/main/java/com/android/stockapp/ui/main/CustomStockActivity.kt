package com.android.stockapp.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.android.stockapp.R
import com.android.stockapp.application.MyApplication
import com.github.mikephil.charting.stockChart.dataManage.KLineDataManage
import kotlinx.android.synthetic.main.activity_custom_stock.*
import kotlinx.android.synthetic.main.custom_simple_quotation_chart_view.view.*
import org.json.JSONObject

class CustomStockActivity: AppCompatActivity() {
    fun calMaxScale(count: Float): Float {
        var xScale = 1f
        xScale = if (count >= 800) {
            12f
        } else if (count >= 500) {
            8f
        } else if (count >= 300) {
            5.5f
        } else if (count >= 150) {
            2f
        } else if (count >= 100) {
            1.5f
        } else {
            0.1f
        }
        return xScale
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_stock)
        test.setOnClickListener {
            chartView.setLastPointData(5.0,2.0,1000)
//            val layoutParams = flChart.layoutParams
//            layoutParams.width = layoutParams.width+10
//            flChart.layoutParams = layoutParams
//            flChart.requestLayout()
//            chartView.combinedchart.setFqLableText("0")

//            chartView.oneDayChart.stopHeaderAnimation()
        }
        rg.setOnCheckedChangeListener{_,resId->
            when (resId) {
                R.id.rb01 -> {
                    chartView.setKData(JSONObject(Constains.oneDayData),"601818.SH",2.82,0)
                }
                R.id.rb02 -> {
                    chartView.setKData(JSONObject(Constains.oneDK),"601818.SH",2.82,KLineDataManage.K_1WEEK)
                }
                R.id.rb03 -> {
                    chartView.setKData(JSONObject(Constains.oneDK),"601818.SH",2.82,KLineDataManage.K_1MONTH)
                }
                R.id.rb04 -> {
                    chartView.setKData(JSONObject(Constains.oneMinKData),"601818.SH",2.82,KLineDataManage.K_1MIN)
                }
                else -> {
                }
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

    }

    override fun onPostResume() {
        super.onPostResume()
        MyApplication.getApplication().initDayNight()
    }

}