package com.ivan.healthcare.healthcare_android.view.chart.provider;

import com.ivan.healthcare.healthcare_android.view.chart.Chart;
import com.ivan.healthcare.healthcare_android.view.chart.LineChart;
import com.ivan.healthcare.healthcare_android.view.chart.ParallelColumnChart;
import com.ivan.healthcare.healthcare_android.view.chart.ShadowLineChart;
import com.ivan.healthcare.healthcare_android.view.chart.StackedColumnChart;

/**
 * 图表内容的提供者
 * Created by Ivan on 16/3/16.
 */
public abstract class ChartAdapter {

    public Chart chart;

    /**
     * x轴标签的个数
     * @return x轴标签的个数
     */
    public abstract int getXLabelsCount();

    /**
     * x轴指定位置的内容
     * @param position 指定位置
     * @return x轴指定位置的内容
     */
    public abstract String getXLabel(int position);

    /**
     * 获得图例个数
     * @return 图例个数
     */
    public abstract int getLegendCount();

    /**
     * 获得图例
     * @return 图例
     */
    public abstract String getLegend(int position);

    /**
     * 获得图例的颜色
     * @return 图例的颜色
     */
    public abstract int getLegendColorId(int position);

    public void notifyDataSetChanged() {
        if (chart != null) {
            if (chart instanceof LineChart) {
                ((LineChart) chart).setAdapter((LineChartAdapter) chart.getAdapter());
            } else if (chart instanceof ShadowLineChart) {
                ((ShadowLineChart) chart).setAdapter((LineChartAdapter) chart.getAdapter());
            } else if (chart instanceof ParallelColumnChart) {
                ((ParallelColumnChart) chart).setAdapter((ColumnChartAdapter) chart.getAdapter());
            } else if (chart instanceof StackedColumnChart) {
                ((StackedColumnChart) chart).setAdapter((ColumnChartAdapter) chart.getAdapter());
            }
            chart.clearAll();
            chart.postInvalidate();
        }
    }

    public boolean drawXLabels() {
        return true;
    }

}
