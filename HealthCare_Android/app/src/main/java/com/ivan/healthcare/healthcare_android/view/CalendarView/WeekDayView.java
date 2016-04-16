package com.ivan.healthcare.healthcare_android.view.CalendarView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.ivan.healthcare.healthcare_android.R;

/**
 * 自定义日历视图上显示星期的view
 * Created by Ivan on 16/2/5.
 */
class WeekDayView extends RelativeLayout {

    private TextView textView;

    public WeekDayView(Context context) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = inflater.inflate(R.layout.layout_weekday, this);
        textView = (TextView) rootView.findViewById(R.id.weekday_textView);
    }

    /**
     * 设置星期
     * @param weekday 字符串
     */
    protected void setWeekDay(String weekday) {
        textView.setText(weekday);
    }

    /**
     * 设置主题
     * @param t 日历主题实例
     */
    protected void setTheme(CalendarTheme t) {
        textView.setTextColor(t.day_textColor);
    }
}
