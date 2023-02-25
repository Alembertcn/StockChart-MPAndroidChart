package com.android.stockapp.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Message
import androidx.appcompat.app.AppCompatActivity
import com.android.stockapp.R
import com.android.stockapp.application.MyApplication
import com.android.stockapp.common.data.ChartData
import com.android.stockapp.ui.main.Constains.oneDK
import com.github.mikephil.charting.stockChart.dataManage.KLineDataManage
import kotlinx.android.synthetic.main.activity_custom_stock.*
import org.json.JSONObject
import java.util.*
import kotlin.math.roundToInt
import kotlin.random.Random

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

    var  mHandler= @SuppressLint("HandlerLeak")
    object :Handler(){
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            chartView.setLastPointData(2.0 + Random.nextDouble(2.0),2.0,2000,false)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_stock)
        test.setOnClickListener {
            Timer().schedule(object : TimerTask() {
                override fun run() {
                    mHandler.sendMessageDelayed(android.os.Message.obtain(), 2000)
                }
            }, 2000, 2000)


//            chartView.setLastPointData(5.0, 2.0, 1000, true)
//            chartView.setLastPointData(3.6,2.95,1000,true)
//            val layoutParams = flChart.layoutParams
//            layoutParams.width = layoutParams.width+10
//            flChart.layoutParams = layoutParams
//            flChart.requestLayout()
//            chartView.combinedchart.setFqLableText("0")

//            chartView.oneDayChart.stopHeaderAnimation()
        }

        test2.setOnClickListener {
//            chartView.setLastPointData(2.0 + Random.nextDouble(2.0),2.0,2000,true)
           chartView.setKData(JSONObject(Constains.oneMinKData),"601818.SH",2.82,KLineDataManage.K_1DAY)

        }
        test3.setOnClickListener {
            val layoutParams = flChart.layoutParams
            layoutParams.width = layoutParams.width + 10
            flChart.layoutParams = layoutParams
            flChart.requestLayout()
        }
        rg.setOnCheckedChangeListener{_,resId->
            when (resId) {
                R.id.rb01 -> {
                    chartView.setKData(JSONObject(Constains.oneDayData),"601818.SH",2.82,0)
                }
                R.id.rb02 -> {
//                    chartView.setKData(JSONObject(Constains.oneDK),"601818.SH",2.82,KLineDataManage.K_1DAY)
                    chartView.setKData(JSONObject(ChartData.KLINEDATA),"601818.SH",2.82,KLineDataManage.K_1DAY)
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