package com.ivan.healthcare.healthcare_android.database;

import com.ivan.healthcare.healthcare_android.AppContext;
import com.ivan.healthcare.healthcare_android.Configurations;
import com.ivan.healthcare.healthcare_android.local.User;

/**
 * 数据库操作方法类
 * @author Ivan
 */

public class DataAccess {

    /**
     * 获取当前用户的uid
     */
    public static int getUid() {
        Result result = AppContext.getDB().query()
                            .table(Configurations.USER_TABLE)
                            .field("uid")
                            .first();
        if (result == null) {
            return -1;
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
                            .add("constellation", User.getConstellationInt())
                            .add("email", User.email)
                            .add("address", User.address)
                            .add("introduction", User.introduction)
                            .add("measure_total_times", User.totalMeasureTimes)
                            .add("measure_total_assessment", User.totalMeasureAssessment)
                            .add("measure_today_times", User.todayMeasureTimes)
                            .where("uid").equal(String.valueOf(User.uid))
                            .update();
        return result >= 1;
    }

}
