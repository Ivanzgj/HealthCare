package com.ivan.healthcare.healthcare_android.local;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.ivan.healthcare.healthcare_android.util.NotifyUtil;

/**
 * 通知广播的接收者
 * Created by Ivan on 16/4/15.
 */
public class NotifyReciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(NotifyUtil.ALARM_NOTIFY_ID_BASE);
        nm.notify(NotifyUtil.ALARM_NOTIFY_ID_BASE, NotifyUtil.getNotify(context));
    }
}
