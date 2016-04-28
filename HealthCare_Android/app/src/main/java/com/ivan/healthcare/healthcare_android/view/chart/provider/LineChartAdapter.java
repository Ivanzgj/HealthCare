package com.ivan.healthcare.healthcare_android.view.chart.provider;

import java.util.ArrayList;

/**
 * 折线图的数据源适配器
 * Created by Ivan on 16/3/16.
 */
public abstract class LineChartAdapter extends ChartAdapter {

    public abstract int getLineCount();
    public abstract ArrayList<Float> getLineData(int index);
    public abstract int getLineColorId(int index);

    public int getShadowColor(int position) {
        return android.support.design.R.color.abc_color_highlight_material;
    }

}
