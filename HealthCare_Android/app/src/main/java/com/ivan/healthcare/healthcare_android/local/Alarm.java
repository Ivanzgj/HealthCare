package com.ivan.healthcare.healthcare_android.local;

import com.ivan.healthcare.healthcare_android.customobj.Time;
import com.ivan.healthcare.healthcare_android.database.DataAccess;

import java.util.ArrayList;

/**
 * 闹钟管理类
 * Created by Ivan on 16/4/13.
 */
public class Alarm {

    private static ArrayList<Time> alarmArrayList = new ArrayList<>();

    public static ArrayList<Time> readAlarms(boolean refreshCache) {
        if (refreshCache){
            alarmArrayList = DataAccess.getAlarmList();
        } else if (alarmArrayList == null) {
            alarmArrayList = new ArrayList<>();
        }
        return alarmArrayList;
    }

    public static int getMaxAlarmId(boolean refreshCache) {
        readAlarms(refreshCache);
        int max = Integer.MIN_VALUE;
        for (Time alarm : alarmArrayList) {
            if (max < alarm.getId()) {
                max = alarm.getId();
            }
        }
        return max;
    }

    public static boolean updateAlarm(Time alarm) {
        if (DataAccess.updateAlarm(alarm)) {
            int i = 0;
            for (Time a : alarmArrayList) {
                if (a.getId() == alarm.getId()) {
                    alarmArrayList.set(i, alarm);
                }
                i++;
            }
            return true;
        }
        return false;
    }

    public static boolean addAlarm(Time alarm) {
        if (DataAccess.updateAlarm(alarm)) {
            alarmArrayList.add(alarm);
            return true;
        }
        return false;
    }

    public static boolean deleteAlarm(Time alarm) {
        if (DataAccess.deleteAlarm(alarm)) {
            alarmArrayList.remove(alarm);
            return true;
        }
        return false;
    }
}
