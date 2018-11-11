package cn.imustacm.heartbeat.chart;

import android.graphics.Color;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名：HeartBeat
 * 包名：cn.imustacm.heartbeat.chart
 * 文件名：DynamicLineChartManager
 * 描述：动态线性图表管理器
 */

public class DynamicLineChartManager {

    // Debug标记
    private static final boolean SHOW_VALUE = false;

    // 图表对象
    private LineChart mLineChart;
    // 底端X轴
    private XAxis xAxisBottom;
    // 左端Y轴
    private YAxis yAxisLeft;
    // 右端Y轴
    private YAxis yAxisRight;

    // 数据
    private LineData mLineData;
    // 线条
    private LineDataSet mLineDataSet;
    // 无数据时的提示文字
    private String noDataText;
    // 提示字体的颜色
    private int noDataTextColor;
    // 线条颜色
    private int lineColor;
    // 填充颜色
    private int fillColor;
    // 背景颜色
    private int backgroundColor;

    // 心率数据（List）
    private List<Integer> dataList;
    // 最后一次加入的数据（默认：80）
    private Integer endData = 80;

    public DynamicLineChartManager(LineChart mLineChart) {
        this(mLineChart, "", Color.BLACK, Color.parseColor("#FF006D"), Color.WHITE, Color.WHITE);
    }

    public DynamicLineChartManager(LineChart mLineChart, String noDataText, int noDataTextColor, int lineColor, int fillColor, int backgroundColor) {
        // 初始化图标
        this.mLineChart = mLineChart;
        // 初始化坐标轴
        this.xAxisBottom = mLineChart.getXAxis();
        this.yAxisLeft = mLineChart.getAxisLeft();
        this.yAxisRight = mLineChart.getAxisRight();
        // 初始化空数据提示文本属性
        this.noDataText = noDataText;
        this.noDataTextColor = noDataTextColor;
        // 初始化其他颜色属性
        this.lineColor = lineColor;
        this.fillColor = fillColor;
        this.backgroundColor = backgroundColor;
        // 设置图表属性
        setLineChart();
        // 设置线条属性
        setLineDataSet();
        // 设置特殊属性
        setShowValue();
        // 初始化
        this.mLineData = new LineData();
        this.mLineData.addDataSet(mLineDataSet);
        this.dataList = new ArrayList<Integer>();
    }

    private void setShowValue() {
        // 是否显示坐标轴
        yAxisLeft.setEnabled(SHOW_VALUE);
        yAxisRight.setEnabled(SHOW_VALUE);
        xAxisBottom.setEnabled(SHOW_VALUE);
        // 是否绘制圆点
        mLineDataSet.setDrawCircles(SHOW_VALUE);
        // 是否绘制数值
        mLineDataSet.setDrawValues(SHOW_VALUE);
    }

    private void setLineChart() {
        // 禁止触控手势
        mLineChart.setTouchEnabled(false);
        // 禁止缩放
        mLineChart.setDragEnabled(false);
        // 设置右下角描述为空
        Description desc = new Description();
        desc.setText("");
        mLineChart.setDescription(desc);
        // 设置没有数据时的默认文字属性
        mLineChart.setNoDataText(noDataText);
        mLineChart.setNoDataTextColor(noDataTextColor);
        // 设置图表内格子背景是否显示
        mLineChart.setDrawGridBackground(false);
        // 显示边界
        mLineChart.setDrawBorders(false);
        // 关闭线条颜色标识
        mLineChart.getLegend().setEnabled(false);
        // 设置图表背景颜色
        mLineChart.setBackgroundColor(backgroundColor);
        // 设置X轴显示位置在底部
        xAxisBottom.setPosition(XAxis.XAxisPosition.BOTTOM);
        // 设置X轴的刻度值
        xAxisBottom.setGranularity(1f);
        // 设置X轴的刻度数目
        xAxisBottom.setLabelCount(10);
        // 设置Y轴的最小值
        yAxisLeft.setAxisMinimum(0f);
        yAxisRight.setAxisMinimum(0f);
    }

    private void setLineDataSet() {
        // 初始化线条
        mLineDataSet = new LineDataSet(null, "");
        // 设置线条宽度
        mLineDataSet.setLineWidth(2.5f);
        // 设置线条颜色
        mLineDataSet.setColor(lineColor);
        // 设置圆点半径
        mLineDataSet.setCircleRadius(1.5f);
        // 设置圆点颜色
        mLineDataSet.setCircleColor(lineColor);
        // 设置数值显示大小
        mLineDataSet.setValueTextSize(10f);
        // 设置线条模式
        mLineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        // 设置曲线填充
        mLineDataSet.setDrawFilled(true);
        // 设置填充颜色
        mLineDataSet.setFillColor(fillColor);
        // 设置填充颜色透明度
        mLineDataSet.setFillAlpha(255);
    }

    public void addEntry(int number) {
        // 添加数据
        dataList.add(number);
        // 添加Entry
        Entry entry = new Entry(mLineDataSet.getEntryCount(), number);
        mLineData.addEntry(entry, 0);
        mLineChart.setData(mLineData);
        // 通知改变
        mLineData.notifyDataChanged();
        mLineChart.notifyDataSetChanged();
        // 设置最大显示数
        mLineChart.setVisibleXRangeMaximum(20);
        // 移动视图
        mLineChart.moveViewToX(mLineData.getEntryCount() - 5);
    }

    public void addEntryList(List<Integer> dataList) {
        // 设置数据
        this.dataList = dataList;
        // 添加Entry
        for (Integer data : dataList) {
            Entry entry = new Entry(mLineDataSet.getEntryCount(), data);
            mLineData.addEntry(entry, 0);
            mLineChart.setData(mLineData);
        }
        // 通知改变
        mLineData.notifyDataChanged();
        mLineChart.notifyDataSetChanged();
        // 设置最大显示数
        mLineChart.setVisibleXRangeMaximum(dataList.size());
        // 刷新
        mLineChart.invalidate();
    }

    public List<Integer> getDataList() {
        return dataList;
    }

    public Integer getEndData() {
        return endData;
    }

    public void setEndData(Integer endData) {
        this.endData = endData;
    }
}