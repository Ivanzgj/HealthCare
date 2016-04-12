package com.ivan.healthcare.healthcare_android.chart.provider;

import java.util.ArrayList;

/**
 * 柱状图的数据源适配器
 * Created by Ivan on 16/3/16.
 */
public abstract class ColumnChartAdapter extends ChartAdapter {

    public abstract int getColumnCount();
    public abstract ArrayList<Float> getColumnData(int index);
    public abstract int getColumnColor(int index);

}
