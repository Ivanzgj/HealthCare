package com.ivan.healthcare.healthcare_android.view.CalendarView;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * 自定义日历视图的数据源适配器
 * Created by Ivan on 16/2/4.
 */
public class CalendarAdapter extends BaseAdapter {

    private Context context;

    private Calendar currentCalendar;
    private Calendar prevCalendar;
    private Calendar nextCalendar;

    private String today;

    private ArrayList<Day> dayList;
    private String[] weeks;
    private int row = 7;
    private int col = 7;

    /**
     * 星期布局的高度
     */
    protected int weekDayHeight = -1;
    /**
     * 日期布局的高度
     */
    protected int dayHeight = -1;
    /**
     * 日历主题
     */
    private CalendarTheme theme;

    public CalendarAdapter(Context context, Calendar cal, CalendarTheme theme) {
        this.context = context;
        this.theme = theme;
        SimpleDateFormat format = new SimpleDateFormat("yyMMddHHmmss");
        today = format.format(new Date()).substring(0,8);
        weeks = new String[] {"Sun","Mon","Tue","Wed","Thur","Fri","Sat"};
        setCurrentCalendar(cal);
    }

    @Override
    public int getCount() {
        return col*row;
    }

    @Override
    public Object getItem(int position) {
        return dayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 第一行的星期
        if (position<7) {
            WeekDayView weekdayView;
            if (convertView != null) {
                if (convertView instanceof WeekDayView) {
                    weekdayView = (WeekDayView) convertView;
                } else {
                    weekdayView = new WeekDayView(context);
                }
            } else {
                weekdayView = new WeekDayView(context);
            }
            weekdayView.setWeekDay(weeks[position]);
            if (weekDayHeight != -1) {
                AbsListView.LayoutParams param = new AbsListView.LayoutParams(
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                        weekDayHeight);
                weekdayView.setLayoutParams(param);
            }
            weekdayView.setTheme(theme);
            return weekdayView;
        }
        // 日期
        DayView view;
        if (convertView != null) {
            if (convertView instanceof DayView) {
                view = (DayView) convertView;
            } else {
                view = new DayView(context);
            }
        } else {
            view = new DayView(context);
        }
        Day d = dayList.get(position - 7);
        view.setDay(d, false);
        if (today.equals(d.getDate())) {
            view.showBg(true);
        }
        if (d.month == currentCalendar.get(Calendar.MONTH)) {
            view.setIsCurrentMonth(true);
        } else {
            view.setIsCurrentMonth(false);
        }
        if (dayHeight != -1) {
            AbsListView.LayoutParams param = new AbsListView.LayoutParams(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    dayHeight);
            view.setLayoutParams(param);
        }
        view.setTheme(theme);
        return view;
    }

    /**
     * 初始化日历实例
     * @param cal 当前年月日的日历实例
     */
    protected void setCurrentCalendar(Calendar cal) {
        currentCalendar = cal;
        if (prevCalendar == null) {
            prevCalendar = Calendar.getInstance();
        }
        if (nextCalendar == null) {
            nextCalendar = Calendar.getInstance();
        }
        // 当前年月
        int year = currentCalendar.get(Calendar.YEAR);
        int month = currentCalendar.get(Calendar.MONTH);
        // 上一个月
        if (month == currentCalendar.getActualMinimum(Calendar.MONTH)) {
            prevCalendar.set(Calendar.YEAR, year-1);
            prevCalendar.set(Calendar.MONTH, prevCalendar.getActualMaximum(Calendar.MONTH));
        } else {
            prevCalendar.set(Calendar.YEAR, year);
            prevCalendar.set(Calendar.MONTH, month - 1);
        }
        // 下一个月
        if (month == currentCalendar.getActualMaximum(Calendar.MONTH)) {
            nextCalendar.set(Calendar.YEAR, year+1);
            nextCalendar.set(Calendar.MONTH, nextCalendar.getActualMinimum(Calendar.MONTH));
        } else {
            nextCalendar.set(Calendar.YEAR, year);
            nextCalendar.set(Calendar.MONTH, month + 1);
        }

        buildDayList();
    }

    /**
     * 通知日历主题，以适配设置
     * @param t 日历主题实例
     */
    protected void setTheme(CalendarTheme t) {
        theme = t;
        refreshTheme();
    }

    private void refreshTheme() {
        notifyDataSetChanged();
    }

    /**
     * 计算当前日历视图所要显示的日期
     */
    private void buildDayList() {
        if (dayList == null) {
            dayList = new ArrayList<>();
        } else {
            dayList.clear();
        }

        // 上一个月的后几日
        int weekDay = currentCalendar.get(Calendar.DAY_OF_WEEK);
        if (weekDay != Calendar.SUNDAY) {
            int prev_month_days = weekDay - Calendar.SUNDAY;
            int prev_month_last_day = prevCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            int prev_year = prevCalendar.get(Calendar.YEAR);
            int prev_month = prevCalendar.get(Calendar.MONTH);

            for (int i=prev_month_days-1;i>=0;i--) {
                Day d = new Day(prev_year, prev_month, prev_month_last_day-i);
                dayList.add(d);
            }
        }
        // 今个月的日期
        int year = currentCalendar.get(Calendar.YEAR);
        int month = currentCalendar.get(Calendar.MONTH);
        for (int i=1;i<=currentCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);i++) {
            Day d = new Day(year, month, i);
            dayList.add(d);
        }
        // 下一个月的前几日
        if (dayList.size()<row*col) {
            int next_year = nextCalendar.get(Calendar.YEAR);
            int next_month = nextCalendar.get(Calendar.MONTH);
            int count = dayList.size();
            for (int i=count+1;i<=row*col;i++) {
                Day d = new Day(next_year, next_month, i-count);
                dayList.add(d);
            }
        }
    }

    /**
     * 通知适配器日历视图的高度，以计算适配item的布局
     * @param h 日历视图的高度
     */
    protected void setCalendarHeight(int h) {
        dayHeight = h/row;
        weekDayHeight = (int) (dayHeight*1.2);
        dayHeight = (h-weekDayHeight)/(row-1);
    }
}
