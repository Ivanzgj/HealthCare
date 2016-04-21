package com.ivan.healthcare.healthcare_android.util;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import com.ivan.healthcare.healthcare_android.MainActivity;
import com.ivan.healthcare.healthcare_android.R;
import com.ivan.healthcare.healthcare_android.local.Time;
import com.ivan.healthcare.healthcare_android.receiver.NotifyReceiver;

/**
 * 通知工具类
 * Created by Ivan on 16/4/15.
 */
public class NotifyUtil {

    public static final int ALARM_NOTIFY_ID_BASE = 0x34567;
    private static final int ALARM_PENDING_REQUEST = 0x12345;
    public static final String ALARM_ID = "ALARM_ID";

    public static Notification getNotify(Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentTitle(context.getString(R.string.alarm_notify_title));
        builder.setContentText(context.getString(R.string.alarm_notify_message));
        builder.setLights(Compat.getColor(context, R.color.default_main_color), 1000, 1000);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setColor(0xFFFFFFFF);
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));
        builder.setPriority(NotificationCompat.PRIORITY_MAX);
        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, ALARM_PENDING_REQUEST, intent, 0);
        builder.setContentIntent(pendingIntent);
        return builder.build();
    }

    /**
     * 打开对应闹钟的通知
     */
    public static void openAlarmNotification(Context context, Time alarm) {
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent();
        intent.setClass(context, NotifyReceiver.class);
        intent.putExtra(ALARM_ID, alarm.getId());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, ALARM_PENDING_REQUEST,
                intent, PendingIntent.FLAG_ONE_SHOT);
        am.setRepeating(AlarmManager.RTC_WAKEUP, alarm.getTime(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    /**
     * 关闭对应闹钟的通知
     */
    public static void closeAlarmNotification(Context context, Time alarm) {
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent();
        intent.setClass(context, NotifyReceiver.class);
        intent.putExtra(ALARM_ID, alarm.getId());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, ALARM_PENDING_REQUEST,
                intent, PendingIntent.FLAG_ONE_SHOT);
        am.cancel(pendingIntent);
    }
}
