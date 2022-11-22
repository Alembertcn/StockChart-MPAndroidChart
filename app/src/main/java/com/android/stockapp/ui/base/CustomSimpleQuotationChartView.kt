package com.android.stockapp.ui.base

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.android.stockapp.R
import com.android.stockapp.ui.market.activity.StockDetailLandActivity
import com.github.mikephil.charting.stockChart.charts.CoupleChartGestureListener.CoupleClick
import com.github.mikephil.charting.stockChart.dataManage.KLineDataManage
import com.github.mikephil.charting.stockChart.dataManage.TimeDataManage
import kotlinx.android.synthetic.main.custom_simple_quotation_chart_view.view.*
import org.json.JSONObject

class CustomSimpleQuotationChartView @JvmOverloads constructor (context: Context, attrs: AttributeSet? =null) :FrameLayout(context,attrs) {
    var land=true
    var mOnChartClickListener:OnClickListener? =null;
    private val kTimeData = TimeDataManage()// 分时图数据
    private val kLineData = KLineDataManage(getContext())// k线图数据
    private var mType = 0//日K：1；周K：7；月K：30
    private var indexType = 1
    private var indexTypeMain = 1 //MA:1 BOLL 2

    init {
        inflate(context, R.layout.custom_simple_quotation_chart_view,this)
        //非横屏页单击转横屏页
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        //初始化
        oneDayChart.initChart(land)
        //非横屏页单击转横屏页
        if (!land) {
            oneDayChart.getGestureListenerLine().setCoupleClick(CoupleClick {
                mOnChartClickListener?.onClick(oneDayChart)
            })

            oneDayChart.getGestureListenerBar().setCoupleClick(CoupleClick {
                mOnChartClickListener?.onClick(oneDayChart)
                if (land) {
//                    loadIndexDataOneDay(if (indexTypeOnyDay < 4) ++indexTypeOnyDay else 1)
                } else {
                    val intent = Intent(context, StockDetailLandActivity::class.java)
                    context.startActivity(intent)
                }
            })
        }

        combinedchart.initChart(land)

        combinedchart.getGestureListenerBar().setCoupleClick {
            if (land) {
                combinedchart.doBarChartSwitch((indexType++)%4+1)
            } else {
                val intent = Intent(context, StockDetailLandActivity::class.java)
                context.startActivity(intent)
            }
        }
        combinedchart.getGestureListenerCandle().setCoupleClick {
            combinedchart.doMainChartSwitch((indexTypeMain++)%2 + 1)
        }
    }


    /**
     * 设置K线数据
     */
    fun setKData(srcDate: JSONObject, assetId:String, preClosePrice:Double=0.0) {
        setKData(srcDate,assetId,preClosePrice,0)
    }

    /**
     * 设置K线数据
     */
    fun setKData(srcDate: JSONObject,assetId:String, preClosePrice:Double=0.0,kType:Int) {
        mType = kType
        when (mType) {
            0 -> {
                flOneDayChart.visibility = View.VISIBLE
                flKChart.visibility = View.GONE

                kTimeData.parseTimeData(srcDate, assetId, preClosePrice)
                oneDayChart.setDataToChart(kTimeData)
            }
            else -> {
                flOneDayChart.visibility = View.GONE
                flKChart.visibility = View.VISIBLE

                //上证指数代码000001.IDX.SH
                kLineData.parseKlineData(srcDate, assetId, land)
                combinedchart.setDataToChart(kLineData)
            }
        }


    }

    fun  setLastPointData( data:String,  changePercent:Double) {
//        val centerColor = ColorHelper.getColorByCompare(changePercent)
//        oneDayChart.setDrawCircleDashMarker(landscape)
//        oneDayChart.setDrawCircleDashMarker(false)

//        var event = BaseEvent(1)
//        event.obj = CirclePositionTime().apply {
//            cx = 100;
//            cy =
//        }
//        oneDayChart.onEventMainThread(data, centerColor)
    }



}