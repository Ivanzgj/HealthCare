package com.ivan.healthcare.healthcare_android.log;

import android.util.Log;
import com.ivan.healthcare.healthcare_android.Configurations;

/**
 * 自定义跟随debug的log类
 * Created by Ivan on 16/1/24.
 */
public final class L {

    public static void i(String tag, String msg) {
        if (Configurations.DEBUG)   Log.i(tag, msg);
    }

    public static void d(String tag, String msg) {
        if (Configurations.DEBUG)   Log.d(tag, msg);
    }

    public static void v(String tag, String msg) {
        if (Configurations.DEBUG)   Log.v(tag, msg);
    }

    public static void e(String tag, String msg) {
        if (Configurations.DEBUG)   Log.e(tag, msg);
    }

    public static void w(String tag, String msg) {
        if (Configurations.DEBUG)   Log.w(tag, msg);
    }
}
