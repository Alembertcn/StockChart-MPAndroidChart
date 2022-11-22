package com.android.stockapp.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.android.stockapp.R
import com.android.stockapp.application.MyApplication
import kotlinx.android.synthetic.main.activity_custom_stock.*
import kotlinx.android.synthetic.main.custom_simple_quotation_chart_view.view.*
import org.json.JSONObject

class CustomStockActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_stock)
        test.setOnClickListener {
            chartView.oneDayChart.stopHeaderAnimation()
        }
        rg.setOnCheckedChangeListener{_,resId->
            when (resId) {
                R.id.rb01 -> {
                    chartView.setKData(JSONObject(Constains.oneDayData),"601818.SH",2.82)
                }
                R.id.rb02 -> {
                    chartView.setKData(JSONObject(Constains.oneDK),"601818.SH",2.82,1)
                }
                R.id.rb03 -> {
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