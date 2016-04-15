package com.ivan.healthcare.healthcare_android.database;

import com.ivan.healthcare.healthcare_android.AppContext;
import com.ivan.healthcare.healthcare_android.Configurations;
import com.ivan.healthcare.healthcare_android.customobj.Time;
import com.ivan.healthcare.healthcare_android.local.Constellation;
import com.ivan.healthcare.healthcare_android.local.User;
import com.ivan.healthcare.healthcare_android.util.Utils;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * 数据库操作方法类
 * @author Ivan
 */

public class DataAccess {

    /**
     * 测量数据，包括心率数据，血压（上/下压），心率大小，综合评价
     */
    public static class MeasuredDataUnit {
        public String date;
        public ArrayList<Float> data;
        public int pressureHigh;
        public int pressureLow;
        public int beepRate;
        public int assessment;
    }

    /**
     * 获取当前用户的uid
     */
    public static int getUid() {
        Result result = AppContext.getDB().query()
                            .table(Configurations.USER_TABLE)
                            .field("uid")
                            .first();
        if (result == null) {
            return User.UID_UNDEFINE;
        } else {
            return Integer.valueOf(result.getString("uid"));
        }
    }

    /**
     * 根据数据库信息初始化用户
     */
    public static boolean initUserInfo() {
        Result result = AppContext.getDB().query()
                            .table(Configurations.USER_TABLE)
                            .field("uid")
                            .field("username")
                            .field("age")
                            .field("sex")
                            .field("birth")
                            .field("constellation")
                            .field("email")
                            .field("address")
                            .field("introduction")
                            .field("measure_total_times")
                            .field("measure_total_assessment")
                            .field("measure_today_times")
                            .first();
        if (result != null) {
            User.uid = Integer.valueOf(result.getString("uid"));
            User.userName = result.getString("username");
            User.age = result.getInt("age");
            User.setSexInt(result.getInt("sex"));
            User.birthday = result.getString("birthday");
            User.setConstellationInt(result.getInt("constellation"));
            User.email = result.getString("email");
            User.address = result.getString("address");
            User.introduction = result.getString("introduction");
            User.todayMeasureTimes = result.getInt("measure_today_times");
            User.totalMeasureTimes = result.getInt("measure_total_times");
            User.totalMeasureAssessment = result.getInt("measure_total_assessment");
            return true;
        } else {
            return false;
        }
    }

    /**
     * 更新用户数据库信息
     */
    public static boolean updateUserInfo() {
        int result = AppContext.getDB().query()
                            .table(Configurations.USER_TABLE)
                            .add("username", User.userName)
                            .add("age", User.age)
                            .add("sex", User.getSexInt())
                            .add("birth", User.birthday)
                            .add("constellation", Constellation.getConstellationInt(User.constellation))
                            .add("email", User.email)
                            .add("address", User.address)
                            .add("introduction", User.introduction)
                            .add("measure_total_times", User.totalMeasureTimes)
                            .add("measure_total_assessment", User.totalMeasureAssessment)
                            .add("measure_today_times", User.todayMeasureTimes)
                            .where("uid").equal(String.valueOf(User.uid))
                            .update();
        if (result == 0) {
            result = AppContext.getDB().query()
                                .table(Configurations.USER_TABLE)
                                .add("uid", String.valueOf(User.uid))
                                .add("username", User.userName)
                                .add("age", User.age)
                                .add("sex", User.getSexInt())
                                .add("birth", User.birthday)
                                .add("constellation", Constellation.getConstellationInt(User.constellation))
                                .add("email", User.email)
                                .add("address", User.address)
                                .add("introduction", User.introduction)
                                .add("measure_total_times", User.totalMeasureTimes)
                                .add("measure_total_assessment", User.totalMeasureAssessment)
                                .add("measure_today_times", User.todayMeasureTimes)
                                .insert();
        }
        return result > 0;
    }

    /**
     * 获取闹钟列表
     */
    public static ArrayList<Time> getAlarmList() {
        ArrayList<Result> resultArrayList = AppContext.getDB().query().table(Configurations.ALARM_TABLE)
                                                        .field("hour").field("minute").field("alarm_id").field("enable")
                                                        .list();
        ArrayList<Time> alarmList = new ArrayList<>();
        for (Result r : resultArrayList) {
            Time alarm = new Time(r.getInt("hour"), r.getInt("minute"), r.getInt("alarm_id"), r.getInt("enable") != 0);
            alarmList.add(alarm);
        }
        return alarmList;
    }

    /**
     * 更新/添加闹钟
     */
    public static boolean updateAlarm(Time alarm) {
        int result = AppContext.getDB().query().table(Configurations.ALARM_TABLE)
                                        .add("hour", alarm.getHour())
                                        .add("minute", alarm.getMinute())
                                        .add("enable", alarm.isOn() ? 1 : 0)
                                        .where("alarm_id").equal(alarm.getId())
                                        .update();
        if (result == 0) {
            result = AppContext.getDB().query().table(Configurations.ALARM_TABLE)
                                    .add("hour", alarm.getHour())
                                    .add("minute", alarm.getMinute())
                                    .add("alarm_id", alarm.getId())
                                    .add("enable", alarm.isOn()?1:0)
                                    .insert();
        }
        return result > 0;
    }

    /**
     * 获取指定日期的所有测量数据，月份从1开始
     * @return {@link com.ivan.healthcare.healthcare_android.database.DataAccess.MeasuredDataUnit}
     */
    public static ArrayList<MeasuredDataUnit> getMeasuredData(int year, int month, int dayOfMonth) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, dayOfMonth);
        String dateString = Utils.getDateString(cal.getTime());
        ArrayList<Result> resultList = AppContext.getDB().query().table(Configurations.MEASURE_TABLE)
                                            .field("press_high")
                                            .field("press_low")
                                            .field("beep_rate")
                                            .field("hour")
                                            .field("minute")
                                            .where("date").equal(dateString)
                                            .list();
        if (resultList == null || resultList.size() == 0) {
            return null;
        } else {
            ArrayList<MeasuredDataUnit> dataList = new ArrayList<>();

            for (Result result : resultList) {
                MeasuredDataUnit dataUnit = new MeasuredDataUnit();
                dataUnit.date = dateString+"-"+result.getInt("hour")+":"+result.getInt("minute");

                dataUnit.pressureHigh = result.getInt("press_high");
                dataUnit.pressureLow = result.getInt("press_low");
                dataUnit.beepRate = result.getInt("beep_rate");

                dataList.add(dataUnit);
            }

            return dataList;
        }
    }

    /**
     * 获取指定时间的测量数据，月份从1开始
     * @return {@link com.ivan.healthcare.healthcare_android.database.DataAccess.MeasuredDataUnit}
     */
    public static MeasuredDataUnit getLatestMeasuredData() {
        Result result = AppContext.getDB().query().table(Configurations.MEASURE_TABLE)
                                        .field("date")
                                        .field("hour")
                                        .field("minute")
                                        .field("beep_data")
                                        .field("press_high")
                                        .field("press_low")
                                        .field("beep_rate")
                                        .field("assessment")
                                        .order("date, hour, minute DESC")
                                        .limit(1)
                                        .first();
        if (result == null) {
            return null;
        } else {
            MeasuredDataUnit dataUnit = new MeasuredDataUnit();
            dataUnit.date = result.getString("date");

            ArrayList<Float> dataList = new ArrayList<>();
            String data = result.getString("beep_data");
            String[] datas = data.split("|");
            for (String d : datas) {
                dataList.add(Float.valueOf(d));
            }
            dataUnit.data = dataList;

            dataUnit.pressureHigh = result.getInt("press_high");
            dataUnit.pressureLow = result.getInt("press_low");
            dataUnit.beepRate = result.getInt("beep_rate");
            dataUnit.assessment = result.getInt("assessment");

            return dataUnit;
        }
    }

    /**
     * 获取指定日期的所有测量评估值，月份从1开始
     * @return {@link com.ivan.healthcare.healthcare_android.database.DataAccess.MeasuredDataUnit}
     */
    public static ArrayList<Float> getMeasuredAssessment(int year, int month, int dayOfMonth) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, dayOfMonth);
        String dateString = Utils.getDateString(cal.getTime());
        ArrayList<Result> resultList = AppContext.getDB().query().table(Configurations.MEASURE_TABLE)
                                                        .field("assessment")
                                                        .where("date").equal(dateString)
                                                        .list();
        if (resultList == null || resultList.size() == 0) {
            return null;
        } else {
            ArrayList<Float> dataList = new ArrayList<>();

            for (Result result : resultList) {
                dataList.add((float) result.getInt("assessment"));
            }

            return dataList;
        }
    }

}
