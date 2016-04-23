package com.ivan.healthcare.healthcare_android.local;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * 闹钟时间类
 * Created by Ivan on 16/4/9.
 */
public class Time {

    private int hour;
    private int minute;
    private int id;
    private boolean on;

    public Time(int hour, int minute, int id, boolean on) {
        this.hour = hour;
        this.minute = minute;
        this.id = id;
        this.on = on;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getId() {
        return id;
    }

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
    }

    @Override
    public String toString() {
        return hour + " : " + minute;
    }

    public long getTime() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        // 这里时区需要设置一下，不然会有8个小时的时间差
        cal.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }
}
