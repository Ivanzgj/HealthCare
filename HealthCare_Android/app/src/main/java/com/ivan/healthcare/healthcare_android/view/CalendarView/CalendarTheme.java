package com.ivan.healthcare.healthcare_android.view.CalendarView;

import android.content.Context;
import android.graphics.Color;
import com.ivan.healthcare.healthcare_android.R;
import com.ivan.healthcare.healthcare_android.util.Compat;

/**
 * 自定义日历主题
 * Created by Ivan on 16/2/5.
 */
public class CalendarTheme {

    public static final int THEME_DARK = 0x31;
    public static final int THEME_LIGHT = 0x32;

    int calendar_backgroudColor;
    int topView_backgroudColor;
    int title_textColor;
    int weekDay_textColor;
    int day_textColor;
    int shallow_day_textColor;

    private Context context;

    public CalendarTheme(Context context, int theme) {
        this.context = context;
        setTheme(theme);
    }

    public void setTheme(int theme) {
        switch (theme) {
            case THEME_DARK:
                setThemeDark();
                break;
            case THEME_LIGHT:
                setThemeLight();
                break;
            default:
                setThemeLight();
                break;
        }
    }

    void setThemeDark() {
        title_textColor = Compat.getColor(context, R.color.night_day_textColor);
        calendar_backgroudColor = Compat.getColor(context, R.color.night_calendar_background);
        topView_backgroudColor = Compat.getColor(context, R.color.night_topView_background);
        weekDay_textColor = Compat.getColor(context, R.color.night_weekDay_textColor);
        day_textColor = Compat.getColor(context, R.color.night_day_textColor);
        shallow_day_textColor = Compat.getColor(context, R.color.night_textColorSecondary);
    }

    void setThemeLight() {
        title_textColor = Compat.getColor(context, R.color.textColorPrimary);
        calendar_backgroudColor = Color.parseColor("#00000000");
        topView_backgroudColor = Color.parseColor("#00000000");
        weekDay_textColor = Compat.getColor(context, R.color.textColorPrimary);
        day_textColor = Compat.getColor(context, R.color.textColorPrimary);
        shallow_day_textColor = Compat.getColor(context, R.color.textColorSecondary);
    }
}
