package com.ivan.healthcare.healthcare_android.local;

import java.util.Calendar;

/**
 * 闹钟时间类
 * Created by Ivan on 16/4/9.
 */
public class Time {

    private int hour;
    private int minute;
    private int id;
    private boolean on;

    public Time(){
        hour = 0;
        minute = 0;
    }

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
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        return cal.getTimeInMillis();
    }
}
