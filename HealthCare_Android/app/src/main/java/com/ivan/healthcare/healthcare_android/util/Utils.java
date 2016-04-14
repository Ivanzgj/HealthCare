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
        DateFormat formater = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
        return formater.format(date).substring(0, 8);
    }

}
