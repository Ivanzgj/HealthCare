package com.ivan.healthcare.healthcare_android.view.chart.theme;

import android.content.Context;
import android.graphics.Color;

import com.ivan.healthcare.healthcare_android.R;
import com.ivan.healthcare.healthcare_android.util.Compat;

/**
 * 图表主题
 * Created by Ivan on 16/2/4.
 */
public class ChartTheme {

    public static final int THEME_LIGHT = 0x31;
    public static final int THEME_DARK = 0x32;

    public int deepGridColor;
    public int shallowGridColor;
    public int xLabelTextColor;
    public int yLabelTextColor;
    public int legendTextColor;
    public int gridLineColor;
    public int todayXLabelBGColor;
    public int todayXLabelTextColor;
    public int chartBGColor;

    private Context context;

    public ChartTheme(Context context, int theme) {
        this.context = context;
        setChartTheme(theme);
    }

    public void setChartTheme(int theme) {
        switch (theme) {
            case THEME_LIGHT:
                setThemeLight();
                break;
            case THEME_DARK:
                setThemeDark();
                break;
            default:
                setThemeLight();
                break;
        }
    }

    void setThemeLight() {
        deepGridColor = Compat.getColor(context, R.color.chart_grid);
        gridLineColor = Compat.getColor(context, R.color.chart_line);
        xLabelTextColor = Compat.getColor(context, R.color.textColorSecondary);
        todayXLabelBGColor = Compat.getColor(context, R.color.default_main_color);
        todayXLabelTextColor = Color.WHITE;
        shallowGridColor = Color.WHITE;
        yLabelTextColor = Compat.getColor(context, R.color.textColorSecondary);
        legendTextColor = Compat.getColor(context, R.color.textColorPrimary);
        chartBGColor = -1;
    }

    void setThemeDark() {
        deepGridColor = Compat.getColor(context, R.color.night_chart_grid1);
        gridLineColor = Compat.getColor(context, R.color.night_chart_line);
        xLabelTextColor = Compat.getColor(context, R.color.textColorSecondary);
        todayXLabelBGColor = Compat.getColor(context, R.color.default_main_color);
        todayXLabelTextColor = Color.WHITE;
        shallowGridColor = Compat.getColor(context, R.color.night_chart_grid2);
        yLabelTextColor = Compat.getColor(context, R.color.textColorSecondary);
        legendTextColor = Compat.getColor(context, R.color.textColorSecondary);
        chartBGColor = Compat.getColor(context, R.color.night_chart_gray);
    }
}
