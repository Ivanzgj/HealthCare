package com.ivan.healthcare.healthcare_android.database;

import com.ivan.healthcare.healthcare_android.AppContext;
import com.ivan.healthcare.healthcare_android.Configurations;
import com.ivan.healthcare.healthcare_android.local.Time;
import com.ivan.healthcare.healthcare_android.local.Constellation;
import com.ivan.healthcare.healthcare_android.local.User;
import com.ivan.healthcare.healthcare_android.util.TimeUtils;

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

    public static class SrcDataUnit {
        public String recTime;
        public int srcOn;
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
            User.sex = User.UserSex.USER_ALIEN;
            int ok = AppContext.getDB().query()
                                    .table(Configurations.USER_TABLE)
                                    .add("uid", -1)
                                    .add("username", "user_-1")
                                    .add("age", 0)
                                    .add("sex", User.getSexInt())
                                    .add("constellation", Constellation.getConstellationInt(Constellation.ConstellationEnum.Undefine))
                                    .add("birth", "")
                                    .add("email", "")
                                    .add("address", "")
                                    .add("introduction", "")
                                    .add("measure_total_times", 0)
                                    .add("measure_today_times", 0)
                                    .add("measure_total_assessment", 0)
                                    .insert();
            return ok>0?-1:User.UID_UNDEFINE;
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
            User.birthday = result.getString("birth");
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
            AppContext.getDB().query().table(Configurations.USER_TABLE).delete();
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
        return result != 0;
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
        return result != 0;
    }

    /**
     * 删除指定闹钟
     */
    public static boolean deleteAlarm(Time alarm) {
        int result = AppContext.getDB().query().table(Configurations.ALARM_TABLE)
                .where("alarm_id").equal(alarm.getId())
                .delete();
        return result != 0;
    }

    /**
     * 获取指定日期的所有测量数据，月份从1开始
     * @return {@link com.ivan.healthcare.healthcare_android.database.DataAccess.MeasuredDataUnit}
     */
    public static ArrayList<MeasuredDataUnit> getMeasuredData(int year, int month, int dayOfMonth) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, dayOfMonth);
        String dateString = TimeUtils.getDateString(cal.getTime());
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
        String dateString = TimeUtils.getDateString(cal.getTime());
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

    /**
     * 写入振动数据
     * @param time 测量的开始时间
     * @param position 标记该数据是该次测量的第几个数据
     * @param value 该次测量值
     * @return 写入是否成功
     */
    public static boolean writeVibrationData(String time, int position, float value) {
        int result = AppContext.getDB().query()
                .table(Configurations.VIBRATION_TABLE)
                .add("time", time)
                .add("position", position)
                .add("value", value)
                .insert();
        return result != 0;
    }

    /**
     * 写入屏幕控制数据
     * @param time 测量的开始时间
     * @param rec_time 标记该记录的时间
     * @param on 该次测量值，0屏幕熄灭|1屏幕亮起|2解锁
     * @return 写入是否成功
     */
    public static boolean writeSrcData(String time, String rec_time, int on) {
        int result = AppContext.getDB().query()
                .table(Configurations.SRC_TABLE)
                .add("measure_time", time)
                .add("rec_time", rec_time)
                .add("src_on", on)
                .insert();
        return result != 0;
    }

    /**
     * 获得指定测量时间的振动数据
     * @param time 测量开始时间
     */
    public static ArrayList<Float> getVibrationData(String time) {
        ArrayList<Result> results = AppContext.getDB().query()
                .table(Configurations.VIBRATION_TABLE)
                .field("value")
                .where("time").equal(time)
                .order("position ASC")
                .list();

        ArrayList<Float> result = new ArrayList<>();
        for (Result r : results) {
            try {
                result.add(r.getFloat("value"));
            } catch (ClassCastException e) {
                result.add((float) r.getInt("value"));
            }
        }
        return result;
    }

    /**
     * 获得指定测量时间的屏幕控制数据
     * @param time 测量开始时间
     */
    public static ArrayList<SrcDataUnit> getSrcData(String time) {
        ArrayList<Result> results = AppContext.getDB().query()
                .table(Configurations.SRC_TABLE)
                .field("src_on")
                .field("rec_time")
                .where("measure_time").equal(time)
                .order("rec_time ASC")
                .list();

        ArrayList<SrcDataUnit> result = new ArrayList<>();
        for (Result r : results) {
            SrcDataUnit unit = new SrcDataUnit();
            unit.recTime = r.getString("rec_time");
            unit.srcOn = r.getInt("src_on");
            result.add(unit);
        }
        return result;
    }

    /**
     * 获得历史监控的所有时间,降序排列
     */
    public static ArrayList<String> getHistoryMonitorVibrationTime() {
        ArrayList<Result> results = AppContext.getDB().query()
                .table(Configurations.VIBRATION_TABLE)
                .field("distinct time")
                .order("time DESC")
                .list();

        ArrayList<String> result = new ArrayList<>();
        for (Result r : results) {
            result.add(r.getString("time"));
        }
        return result;
    }

//    /**
//     * 获得历史监控的所有时间,降序排列
//     */
//    public static ArrayList<String> getHistoryMonitorScreenTime() {
//        ArrayList<Result> results = AppContext.getDB().query()
//                .table(Configurations.SRC_TABLE)
//                .field("distinct measure_time")
//                .order("measure_time DESC")
//                .list();
//
//        ArrayList<String> result = new ArrayList<>();
//        for (Result r : results) {
//            result.add(r.getString("measure_time"));
//        }
//        return result;
//    }

    public static void clearMeasureTable() {
        AppContext.getDB().query().table(Configurations.MEASURE_TABLE)
                .delete();
    }

    public static void clearAlarmTable() {
        AppContext.getDB().query().table(Configurations.ALARM_TABLE)
                .delete();
    }

    public static void clearVibrationTable() {
        AppContext.getDB().query().table(Configurations.VIBRATION_TABLE)
                .delete();
    }

    public static void clearSrcTable() {
        AppContext.getDB().query().table(Configurations.SRC_TABLE)
                .delete();
    }

}
