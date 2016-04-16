package com.ivan.healthcare.healthcare_android.view;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.ivan.healthcare.healthcare_android.AppContext;
import com.ivan.healthcare.healthcare_android.R;

/**
 * 自定义外观toast
 * 默认显示在水平居中，上边距100dp的位置
 * Created by Ivan on 16/2/4.
 */
public class AIToast {

    static Toast toast;

    /**
     * 自定义外观toast
     * 默认显示在水平居中，上边距100dp的位置。
     * @see Toast
     * @param context android.widget.Toast所需的context，
     *                详见Toast类的makeText方法
     * @param text 要显示的字符串
     * @param duration 显示时间长度
     */
    public static void show(Context context, String text, int duration) {
        toast = new Toast(context);
        toast.setDuration(duration);
        AIToastView view = new AIToastView(context);
        view.setText(text);
        toast.setView(view);
        toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.TOP, 0, AppContext.dp2px(100));
        toast.show();
    }

    /**
     * 自定义toast view
     */
    private static class AIToastView extends RelativeLayout {

        private TextView tv;

        public AIToastView(Context context) {
            super(context);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rootView = inflater.inflate(R.layout.view_aitoast, this);
            tv = (TextView) rootView.findViewById(R.id.aitoast_textview);
        }

        public void setText(String text) {
            tv.setText(text);
        }
    }
}
