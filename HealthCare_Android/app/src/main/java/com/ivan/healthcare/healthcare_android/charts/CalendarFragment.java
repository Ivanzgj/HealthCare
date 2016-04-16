package com.ivan.healthcare.healthcare_android.charts;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;

import com.ivan.healthcare.healthcare_android.R;
import com.ivan.healthcare.healthcare_android.charts.ChartActivity;
import com.ivan.healthcare.healthcare_android.util.Compat;
import com.ivan.healthcare.healthcare_android.view.CalendarView.CalendarTheme;
import com.ivan.healthcare.healthcare_android.view.CalendarView.CalendarView;
import com.ivan.healthcare.healthcare_android.view.CalendarView.Day;

/**
 * 显示数据的图表fragemnt
 * Created by Ivan on 16/1/24.
 */
public class CalendarFragment extends Fragment {

    private CalendarView mCalendarView;
    private WebView mSuggestWebView;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_calendar, container, false);
        initView(rootView);

        return rootView;
    }

    private void initView(View rootView) {
        mCalendarView = (CalendarView) rootView.findViewById(R.id.history_calendar);
        mCalendarView.setCalendarTheme(CalendarTheme.THEME_LIGHT);
        mCalendarView.setOnCalendarItemClickListener(new CalendarView.OnCalendarItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), ChartActivity.class);
                Day day = mCalendarView.getDay(position);
                intent.putExtra(ChartActivity.CHART_YEAR, day.getYear());
                intent.putExtra(ChartActivity.CHART_MONTH, day.getMonth());
                intent.putExtra(ChartActivity.CHART_DAYOFMONTH, day.getDay());
                startActivity(intent);
            }
        });

        mSuggestWebView = (WebView) rootView.findViewById(R.id.suggestwebview);
        mSuggestWebView.getSettings().setDefaultTextEncodingName("utf-8") ;
//        mSuggestWebView.setBackgroundColor(Compat.getColor(getActivity(), R.color.colorPrimaryDark)); // 设置背景色
    }

}
