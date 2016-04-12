package com.ivan.healthcare.healthcare_android.view.CalendarView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.ivan.healthcare.healthcare_android.R;
import com.ivan.healthcare.healthcare_android.util.Compat;

/**
 * 自定义日历视图的基本元素
 * Created by Ivan on 16/2/5.
 */
class DayView extends RelativeLayout {

    private TextView textView;
    private FrameLayout backgroundView;
    private Day day;
    private Context context;
    private boolean isCurrentMonth = true;
    private boolean isToday = false;

    public DayView(Context context) {
        super(context);
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = inflater.inflate(R.layout.view_dayview, this);
        textView = (TextView) rootView.findViewById(R.id.dayView_day_textview);
        backgroundView = (FrameLayout) rootView.findViewById(R.id.dayView_selector);
        backgroundView.setVisibility(View.INVISIBLE);
    }

    /**
     * 设置日期
     * @param d 当前日期
     * @param flag 是否显示强调背景
     */
    protected void setDay(Day d, boolean flag) {
        day = d;
        textView.setText(String.valueOf(d.day));
        showBg(flag);
    }

    protected Day getDay() {
        return day;
    }

    /**
     * 是否显示强调背景
     * @param flag 是否显示强调背景
     */
    protected void showBg(boolean flag) {
        isToday = flag;
        if (flag) {
            backgroundView.setVisibility(View.VISIBLE);
            textView.setTextColor(Compat.getColor(context, R.color.pureWindowBackground));
            Animation anim = AnimationUtils.loadAnimation(context, R.anim.anim_calendar_item_scale);
            backgroundView.setAnimation(anim);
        } else {
            backgroundView.setVisibility(View.INVISIBLE);
            backgroundView.clearAnimation();
            setIsCurrentMonth(isCurrentMonth);
        }
    }

    /**
     * 设置该dayView是否是当前月份的
     * @param flag 标识
     */
    protected void setIsCurrentMonth(boolean flag) {
        isCurrentMonth = flag;
        if (isToday) {
            return;
        }
        if (flag) {
            textView.setTextColor(Compat.getColor(context, R.color.textColorSecondary));
        } else {
            textView.setTextColor(Compat.getColor(context, R.color.textColorSecondaryPressed));
        }
    }

    /**
     * 设置主题
     * @param t 日历主题实例
     */
    protected void setTheme(CalendarTheme t) {
        if (isToday) {
            return;
        }
        if (isCurrentMonth) {
            textView.setTextColor(t.day_textColor);
        } else {
            textView.setTextColor(t.shallow_day_textColor);
        }
    }
}
