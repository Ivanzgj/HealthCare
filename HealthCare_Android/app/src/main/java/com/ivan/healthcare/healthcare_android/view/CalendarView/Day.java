package com.ivan.healthcare.healthcare_android.view.CalendarView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 自定义日历视图的相关工具类
 * 管理“天”这一元素
 * Created by Ivan on 16/2/5.
 */
public class Day {
    protected int year;
    protected int month;  // 0~11
    protected int day;

    public Day(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public String getDate() {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        Date date = cal.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyMMddHHmmss");
        return format.format(date).substring(0,8);
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }
}
