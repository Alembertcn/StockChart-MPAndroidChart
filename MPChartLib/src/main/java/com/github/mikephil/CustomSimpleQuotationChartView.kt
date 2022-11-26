package com.github.mikephil

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.github.mikephil.charting.GlobaleConfig
import com.github.mikephil.charting.R
import com.github.mikephil.charting.stockChart.BaseChart
import com.github.mikephil.charting.stockChart.charts.CoupleChartGestureListener.CoupleClick
import com.github.mikephil.charting.stockChart.dataManage.KLineDataManage
import com.github.mikephil.charting.stockChart.dataManage.TimeDataManage
import com.github.mikephil.charting.stockChart.enums.ChartType
import com.github.mikephil.charting.stockChart.model.TimeDataModel
import kotlinx.android.synthetic.main.custom_simple_quotation_chart_view.view.*
import org.json.JSONObject

class CustomSimpleQuotationChartView @JvmOverloads constructor (context: Context, attrs: AttributeSet? =null) :FrameLayout(context,attrs),ISimpleChart {
    var land=true
    var mOnChartClickListener:OnClickListener? =null;
    private val kTimeData = TimeDataManage()// 分时图数据
    private val kLineData = KLineDataManage(getContext())// k线图数据
    private var mMainChartType = 0//0:分时图 1：日K  2：周K：3：月  4：1分钟  5：5分钟 6：15分钟 7：30分钟
    /**
     *  K_VOLUME: 1;
     *  case MACD : 2;
     *  case KDJ : 3;
     *  case RSI: 4;
     *  case MA: 5;
     *  case BOLL: 6;
     */
    private var mSubChartType = 1
    private var mMainLineType = 1 //MA:1 BOLL 2

    /**
     *  K_VOLUME: 1;
     *  case MACD : 2;
     *  case KDJ : 3;
     *  case RSI: 4;
     *  case MA: 5;
     *  case BOLL: 6;
     */
    var currentSubChartType=1;

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
//                    val intent = Intent(context, StockDetailLandActivity::class.java)
//                    context.startActivity(intent)
                }
            })
        }

        combinedchart.initChart(land)


        combinedchart.getGestureListenerBar().setCoupleClick {
            if (land) {
                combinedchart.doBarChartSwitch((mSubChartType++)%4+1)
            } else {
//                val intent = Intent(context, StockDetailLandActivity::class.java)
//                context.startActivity(intent)
            }
        }
        combinedchart.getGestureListenerCandle().setCoupleClick {
//            combinedchart.doMainChartSwitch((mMainLineType++)%2 + 1)
        }
        combinedchart.setHighlightValueSelectedListener(object:
            BaseChart.OnHighlightValueSelectedListener {
            override fun onDayHighlightValueListener(
                mData: TimeDataManage?,
                index: Int,
                isSelect: Boolean
            ) {
                if(isSelect){
                    mOnCrossLineMoveListener?.onCrossLineMove(index,-1)
                }else{
                    mOnCrossLineMoveListener?.onCrossLineDismiss()
                }
            }

            override fun onKHighlightValueListener(
                data: KLineDataManage?,
                index: Int,
                isSelect: Boolean
            ) {

                if(isSelect){
                    mOnCrossLineMoveListener?.onCrossLineMove(index,-1)
                }else{
                    mOnCrossLineMoveListener?.onCrossLineDismiss()
                }
            }
        })
    }



    /**
     * 设置K线数据
     */
    fun setKData(srcDate: JSONObject,assetId:String, preClosePrice:Double=0.0,kType:Int) {
        mMainChartType = kType
        when (mMainChartType) {
            0 -> {
                flOneDayChart.visibility = View.VISIBLE
                flKChart.visibility = View.GONE

                kTimeData.parseData(srcDate, assetId, preClosePrice,kType)
                oneDayChart.setDataToChart(kTimeData)

            }
            else -> {
                flOneDayChart.visibility = View.GONE
                flKChart.visibility = View.VISIBLE

                //上证指数代码000001.IDX.SH
                kLineData.parseData(srcDate, assetId, preClosePrice,kType)
                kLineData.candleDataSet.apply {
//                    decreasingColor = GlobaleConfig.getFallColor()
//                    increasingColor = GlobaleConfig.getRiseColor()
//                    neutralColor = GlobaleConfig.getEqualColor()
                }

                kLineData.volumeDataSet.apply {
//                    decreasingColor = GlobaleConfig.getFallColor()
//                    increasingColor = GlobaleConfig.getRiseColor()
//                    neutralColor = GlobaleConfig.getEqualColor()
                }
                combinedchart.setDataToChart(kLineData)
            }
        }


    }

    override fun setMainLatitudeNum(num: Int) {
    }

    override fun setSubLatitudeNum(num: Int) {
    }

    override fun setTouchable(b: Boolean) {
        oneDayChart.isEnabled=false
        combinedchart.isEnabled=false
    }

    override fun setMoveable(b: Boolean) {
    }

    override fun setZoomable(b: Boolean) {
    }
    var mOnCrossLineMoveListener:ISimpleChart.OnCrossLineMoveListener?=null;
    override fun setOnCrossLineMoveListener(onCrossLineMoveListener: ISimpleChart.OnCrossLineMoveListener?) {
        mOnCrossLineMoveListener = onCrossLineMoveListener
    }

    var currentChartType: ChartType? =null;
    override fun setGlobalChartType(marketType: String?, chartType: ChartType?) {
        this.currentChartType = chartType
    }

    override fun  setLastPointData(data:String, changePercent:Double) {
        // TODO: 缺少数据
        if ( flOneDayChart.visibility == View.VISIBLE) {
            var data = TimeDataModel().apply {
                nowPrice = data.toDouble()
                averagePrice = data.toDouble()
                volume = 0
            }
           var animView:ImageView = oneDayChart.findViewById(com.github.mikephil.charting.R.id.anim_view)
            animView.setColorFilter(GlobaleConfig.getColorByCompare(changePercent))

            oneDayChart.dynamicsUpdateOne(data,kTimeData.realTimeData.size-1)
//            oneDayChart.dynamicsAddOne(data)
        }
    }

    override fun getSubChartType() = mSubChartType
    override fun getMainLineType() = mMainLineType

    /**
     *  K_VOLUME: 1;
     *  case MACD : 2;
     *  case KDJ : 3;
     *  case RSI: 4;
     *  case MA: 5;
     *  case BOLL: 6;
     */
    override fun setSubChartTypes(chartType: Int) {
        if (flKChart.visibility == View.VISIBLE) {
            currentSubChartType = chartType

            if(chartType<=4){
                mSubChartType = chartType
                combinedchart.doBarChartSwitch(mSubChartType)
            }else {
                mMainLineType = currentSubChartType-4
                combinedchart.doMainChartSwitch(mMainLineType)
            }
        }
    }

    override fun subViewLoading() {
    }

    override fun subViewLoadFinish() {
    }


}