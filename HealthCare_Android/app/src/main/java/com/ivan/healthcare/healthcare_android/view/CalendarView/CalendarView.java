package com.ivan.healthcare.healthcare_android.view.CalendarView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.ivan.healthcare.healthcare_android.R;
import com.ivan.healthcare.healthcare_android.log.L;
import java.util.Calendar;
import java.util.Locale;

/**
 * 自定义日历视图控件
 * Created by Ivan on 16/2/4.
 */
public class CalendarView extends RelativeLayout implements View.OnClickListener {

    private static final String TAG = "com.ivan.healthcare.healthcare_android.view.CalendarView";
    /**
     * 顶部容器的高度
     */
    private static final int TOP_VIEW_HEIGHT_DP = 40;
    /**
     * 手势
     */
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    private Context context;

    /**
     * 顶部layout
     */
    private RelativeLayout topView;
    /**
     * 标题
     */
    private TextView titleView;
    /**
     * 下翻页imageView
     */
    private ImageView nextImageView;
    /**
     * 上翻页imageView
     */
    private ImageView prevImageView;
    /**
     * 日历视图
     */
    private GridView calendarView;
    /**
     * 日历数据源适配器
     */
    private CalendarAdapter mAdapter;
    /**
     * 当前年月的日历实例
     */
    private Calendar calendar;
    /**
     * 日历视图的高度
     */
    private int calendarHeight = -1;
    /**
     * 监听日历视图点击事件
     */
    private OnCalendarItemClickListener onCalendarItemClickListener = new OnCalendarItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            L.d(TAG, "Calendar item clicked --> row:"+position/7+" col:"+position%7+1);
        }
    };
    /**
     * 监听日历左右滑动事件
     */
    private GestureDetector calendarGesture;
    /**
     * 日历主题
     */
    private CalendarTheme theme;

    public CalendarView(Context context) {
        super(context);
        layout(context);
    }

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CalendarView);
        calendarHeight = a.getDimensionPixelSize(R.styleable.CalendarView_calenderHeight, calendarHeight);
        a.recycle();

        layout(context);
    }

    private void layout(Context context) {
        this.context = context;

        calendar = Calendar.getInstance();

        theme = new CalendarTheme(context, CalendarTheme.THEME_LIGHT);
        // 顶部layout
        topView = new RelativeLayout(context);
        topView.setId(R.id.calendar_topView);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, dp2px(TOP_VIEW_HEIGHT_DP));
        addView(topView, params);

        // 上翻页imageView
        prevImageView = new ImageView(context);
        prevImageView.setImageResource(R.mipmap.navigation_previous_item);
        prevImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        prevImageView.setScaleX(0.7f);
        prevImageView.setScaleY(0.7f);
        LayoutParams params1 = new LayoutParams(dp2px(TOP_VIEW_HEIGHT_DP), dp2px(TOP_VIEW_HEIGHT_DP));
        params1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        params1.leftMargin = dp2px(10);
        params1.addRule(RelativeLayout.CENTER_VERTICAL);
        topView.addView(prevImageView, params1);

        // 下翻页imageView
        nextImageView = new ImageView(context);
        nextImageView.setImageResource(R.mipmap.navigation_next_item);
        nextImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        nextImageView.setScaleX(0.7f);
        nextImageView.setScaleY(0.7f);
        LayoutParams params2 = new LayoutParams(dp2px(TOP_VIEW_HEIGHT_DP), dp2px(TOP_VIEW_HEIGHT_DP));
        params2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params2.rightMargin = dp2px(10);
        params2.addRule(RelativeLayout.CENTER_VERTICAL);
        topView.addView(nextImageView, params2);

        // 标题
        titleView = new TextView(context);
        titleView.setGravity(Gravity.CENTER);
        titleView.setTextSize(20);
        titleView.setText(calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()) + " " + calendar.get(Calendar.YEAR));
        LayoutParams p = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        p.addRule(RelativeLayout.CENTER_IN_PARENT);
        topView.addView(titleView, p);

        // 日历gridView
        mAdapter = new CalendarAdapter(context, calendar, theme);
        calendarView = new GridView(context);
        calendarView.setAdapter(mAdapter);
        calendarView.setNumColumns(7);
        calendarView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        calendarView.setChoiceMode(GridView.CHOICE_MODE_SINGLE);
        calendarView.setVerticalSpacing(0);
        calendarView.setHorizontalSpacing(0);
        calendarView.setDrawSelectorOnTop(true);

        addView(calendarView, buildCalendarLayout());

        // 监听点击翻页
        prevImageView.setOnClickListener(this);
        nextImageView.setOnClickListener(this);
        // 监听点击
        calendarView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onCalendarItemClickListener.onItemClick(parent, view, position, id);
            }
        });
        // 监听滑动翻页
        calendarGesture = new GestureDetector(context, new OnFlingListener());
        calendarView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return calendarGesture.onTouchEvent(event);
            }
        });

        // 刷新主题颜色配置
        buildCalendarTheme();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 监听日历视图上左右滑动的手势
     */
    private class OnFlingListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                getNextMonth();
                return true; // Right to left
            } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                getPreviousMonth();
                return true; // Left to right
            }
            return false;
        }
    }

    /**
     * 设置日历主题
     * @param t 日历主题标识
     */
    public void setCalendarTheme(int t) {
        if (theme == null) {
            theme = new CalendarTheme(context, t);
        } else {
            switch (t) {
                case CalendarTheme.THEME_LIGHT:
                    theme.setThemeLight();
                    break;
                case CalendarTheme.THEME_DARK:
                    theme.setThemeDark();
                    break;
                default:
                    break;
            }
        }
        buildCalendarTheme();
    }

    /**
     * 根据日历主题配置日历颜色
     */
    private void buildCalendarTheme() {
        mAdapter.setTheme(theme);
        calendarView.setBackgroundColor(theme.calendar_backgroudColor);
        topView.setBackgroundColor(theme.topView_backgroudColor);
        titleView.setTextColor(theme.title_textColor);
    }

    /**
     * 监听日历视图项的点击
     */
    public interface OnCalendarItemClickListener {
        void onItemClick(AdapterView<?> parent, View view, int position, long id);
    }

    /**
     * 设置日历视图点击回调
     * @param l {@link #onCalendarItemClickListener}
     */
    public void setOnCalendarItemClickListener(OnCalendarItemClickListener l) {
        onCalendarItemClickListener = l;
    }

    /**
     * 获取上一个月
     */
    private void getPreviousMonth() {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        if (month == calendar.getActualMinimum(Calendar.MONTH)) {
            calendar.set(Calendar.YEAR, year-1);
            calendar.set(Calendar.MONTH, calendar.getActualMaximum(Calendar.MONTH));
        } else {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month-1);
        }
        titleView.setText(calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()) + " " + calendar.get(Calendar.YEAR));
        mAdapter.setCurrentCalendar(calendar);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 获取下一个月
     */
    private void getNextMonth() {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        if (month == calendar.getActualMaximum(Calendar.MONTH)) {
            calendar.set(Calendar.YEAR, year+1);
            calendar.set(Calendar.MONTH, calendar.getActualMinimum(Calendar.MONTH));
        } else {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month+1);
        }
        titleView.setText(calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()) + " " + calendar.get(Calendar.YEAR));
        mAdapter.setCurrentCalendar(calendar);
        mAdapter.notifyDataSetChanged();
    }

    private int dp2px(float dpvalue) {
        return (int) (dpvalue * context.getResources().getDisplayMetrics().density + 0.5f);
    }

    @Override
    public void onClick(View v) {
        if (prevImageView.equals(v)) {
            getPreviousMonth();
        } else if (nextImageView.equals(v)) {
            getNextMonth();
        }
    }

    /**
     * 设置日历视图的高度
     * @param h 日历视图的高度（不包括顶部容器）
     */
    public void setCalendarHeight(int h) {
        calendarHeight = h;
        updateViewLayout(calendarView, buildCalendarLayout());
    }

    /**
     * 获得指定位置的日期
     */
    public String getDate(int position) {
        Day d = (Day) mAdapter.getItem(position);
        return d.getDate();
    }

    /**
     * 获得指定位置的日期
     */
    public Day getDay(int position) {
        Day d = (Day) mAdapter.getItem(position);
        return d;
    }

    /**
     * 配置日历视图的layout params
     * @return 根据xml/代码设置的日历视图高度calendarHeight生成日历视图的layout params
     */
    private LayoutParams buildCalendarLayout() {
        if (calendarHeight != -1) {
            mAdapter.setCalendarHeight(calendarHeight);
        }
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_LEFT, topView.getId());
        params.addRule(RelativeLayout.ALIGN_RIGHT, topView.getId());
        params.addRule(RelativeLayout.BELOW, topView.getId());
        return params;
    }
}
