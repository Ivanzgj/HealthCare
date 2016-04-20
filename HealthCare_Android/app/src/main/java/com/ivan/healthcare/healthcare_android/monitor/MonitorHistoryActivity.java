package com.ivan.healthcare.healthcare_android.monitor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ivan.healthcare.healthcare_android.R;
import com.ivan.healthcare.healthcare_android.ui.BaseActivity;
import com.ivan.healthcare.healthcare_android.view.chart.ShadowLineChart;
import com.ivan.healthcare.healthcare_android.view.chart.provider.LineChartAdapter;
import com.ivan.healthcare.healthcare_android.view.chart.provider.SimpleChartAdapter;

import java.util.ArrayList;

/**
 * monitor history
 * Created by Ivan on 16/4/20.
 */
public class MonitorHistoryActivity extends BaseActivity {

    public static final String INTENT_EXTRA_DATE = "INTENT_EXTRA_DATE";

    private TextView mDateTextView;
    private ShadowLineChart mAccelerateChart;
    private ShadowLineChart mSrcChart;

    private LineChartAdapter mAccelerateAdapter;
    private LineChartAdapter mSrcAdapter;

    private ArrayList<Float> mAccelerateDataArrayList;
    private ArrayList<Float> mScreenDataArrayList;
    private ArrayList<String> mScreenXLabels;

    private String date = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        date = intent.getStringExtra(INTENT_EXTRA_DATE);
        initView();
    }

    private void initView() {
        View rootView = View.inflate(this, R.layout.activity_monitor_history, null);

        mDateTextView = (TextView) rootView.findViewById(R.id.monitor_history_accelerate_chart_date);
        mAccelerateChart = (ShadowLineChart) rootView.findViewById(R.id.monitor_history_accelerate_chart);
        mSrcChart = (ShadowLineChart) rootView.findViewById(R.id.monitor_history_screen_chart);

        mDateTextView.setText(date);

        mAccelerateDataArrayList = new ArrayList<>();
        mScreenDataArrayList = new ArrayList<>();
        mScreenXLabels = new ArrayList<>();

        mAccelerateAdapter = new SimpleChartAdapter() {
            @Override
            public int getLineCount() {
                return 1;
            }

            @Override
            public ArrayList<Float> getLineData(int index) {
                return mAccelerateDataArrayList;
            }

            @Override
            public int getLineColor(int index) {
                return R.color.colorPrimaryDark;
            }

            @Override
            public int getShadowColor(int position) {
                return R.color.colorPrimaryLight;
            }

            @Override
            public int getXLabelsCount() {
                return mAccelerateDataArrayList.size();
            }

            @Override
            public String getXLabel(int position) {
                return position+"";
            }
        };
        mAccelerateChart.setAdapter(mAccelerateAdapter);

        mSrcAdapter = new SimpleChartAdapter() {
            @Override
            public int getLineCount() {
                return 1;
            }

            @Override
            public ArrayList<Float> getLineData(int index) {
                return mScreenDataArrayList;
            }

            @Override
            public int getLineColor(int index) {
                return R.color.colorPrimaryDark;
            }

            @Override
            public int getShadowColor(int position) {
                return R.color.colorPrimaryLight;
            }

            @Override
            public int getXLabelsCount() {
                return mScreenXLabels.size();
            }

            @Override
            public String getXLabel(int position) {
                if (position < mScreenXLabels.size()) {
                    return mScreenXLabels.get(position);
                }
                return "";
            }
        };
        mSrcChart.setAdapter(mSrcAdapter);

        setContentView(rootView);
    }
}
