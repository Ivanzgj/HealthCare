package com.ivan.healthcare.healthcare_android.util;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 常用工具类
 * Created by Ivan on 16/4/14.
 */
public class Utils {

    public static String getDateString(Date date) {
        DateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
        return formatter.format(date).substring(0, 8);
    }

    public static String getDateString(Date date, String pattern) {
        DateFormat formatter = new SimpleDateFormat(pattern, Locale.CHINA);
        return formatter.format(date).substring(0, pattern.length());
    }

    public static String getTimeString(Date date, String pattern) {
        DateFormat formatter = new SimpleDateFormat("yyyyMMdd"+pattern, Locale.CHINA);
        return formatter.format(date).substring(8, pattern.length()+8);
    }

    public static String getTimeString(Date date) {
        DateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
        return formatter.format(date);
    }

}
