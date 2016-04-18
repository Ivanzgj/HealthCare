package com.ivan.healthcare.healthcare_android.monitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ivan.healthcare.healthcare_android.AppContext;
import com.ivan.healthcare.healthcare_android.R;
import com.ivan.healthcare.healthcare_android.util.Compat;
import com.ivan.healthcare.healthcare_android.util.Utils;
import com.ivan.healthcare.healthcare_android.view.chart.Chart;
import com.ivan.healthcare.healthcare_android.view.chart.ShadowLineChart;
import com.ivan.healthcare.healthcare_android.view.chart.provider.LineChartAdapter;
import com.ivan.healthcare.healthcare_android.view.chart.provider.SimpleChartAdapter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * 全方位长时间监听身体各项数据的页面
 * Created by Ivan on 16/4/18.
 */
public class MonitorFragment extends Fragment implements SensorEventListener {

    private final int ACCELERATE_DATA_COUNT = 10;
    private final int SCREEN_DATA_COUNT = 5;
    private final String TIME_PATTERN = "HH:mm:ss";

    private TextView mAccelerateTextView;

    private ArrayList<Float> mAccelerateDataArrayList;
    private LineChartAdapter mAccelerateAdapter;

    private ArrayList<Float> mScreenDataArrayList;
    private ArrayList<String> mScreenXLabels;
    private LineChartAdapter mScreenAdapter;

    private DecimalFormat formatter;
    private SensorManager mSensorManager;

    /**
     * 监听屏幕亮灭和解锁
     */
    private BroadcastReceiver mScreenReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                mScreenDataArrayList.add(1.f);
                String time = Utils.getTimeString(new Date(), TIME_PATTERN);
                mScreenXLabels.add(time);
                mScreenAdapter.notifyDataSetChanged();
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                mScreenDataArrayList.add(0.f);
                String time = Utils.getTimeString(new Date(), TIME_PATTERN);
                mScreenXLabels.add(time);
                mScreenAdapter.notifyDataSetChanged();
            } else if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {

            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragement_monitor, container, false);
        initView(rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        Sensor accelerator = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerator != null) {
            mSensorManager.registerListener(this, accelerator, SensorManager.SENSOR_DELAY_NORMAL);
        }

        IntentFilter filter = new IntentFilter();
        // 屏幕灭屏广播
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        // 屏幕亮屏广播
        filter.addAction(Intent.ACTION_SCREEN_ON);
        // 屏幕解锁广播
        filter.addAction(Intent.ACTION_USER_PRESENT);
        getActivity().registerReceiver(mScreenReceiver, filter);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSensorManager.unregisterListener(this);
        getActivity().unregisterReceiver(mScreenReceiver);
    }

    private void initView(View rootView) {
        mAccelerateTextView = (TextView) rootView.findViewById(R.id.monitor_accelerate_textview);
        TextView mAccelerateDetailTextView = (TextView) rootView.findViewById(R.id.monitor_accelerate_chart_detail);
        mAccelerateDetailTextView.setText(Utils.getDateString(new Date(), "yyyy-MM-dd"));

        ShadowLineChart mAccelerateLineChart = (ShadowLineChart) rootView.findViewById(R.id.monitor_accelerate_chart);
        mAccelerateLineChart.setBackgroundColor(Compat.getColor(getActivity(), R.color.colorPrimary));
        mAccelerateDataArrayList = new ArrayList<>();
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
                return R.color.colorPrimaryLight;
            }

            @Override
            public int getXLabelsCount() {
                return ACCELERATE_DATA_COUNT;
            }

            @Override
            public String getXLabel(int position) {
                return position+"";
            }
        };
        mAccelerateLineChart.setAdapter(mAccelerateAdapter);

        ShadowLineChart mScreenLineChart = (ShadowLineChart) rootView.findViewById(R.id.monitor_screen_chart);
        mScreenLineChart.setBackgroundColor(Compat.getColor(getActivity(), R.color.colorPrimary));
        mScreenLineChart.selfAdaptive = false;
        mScreenLineChart.setYStep(2);
        ArrayList<Float> yLabels = new ArrayList<>();
        yLabels.add(0.f);
        yLabels.add(1.f);
        mScreenLineChart.setYLabels(yLabels);
        mScreenLineChart.setYAxisValuesFormatter(new Chart.YAxisValueFormatter() {
            @Override
            public String YvaluesString(float v) {
                return (int)v + "";
            }
        });
        mScreenLineChart.setXWidth(AppContext.dp2px(60));
        mScreenDataArrayList = new ArrayList<>();
        mScreenXLabels = new ArrayList<>();
        mScreenDataArrayList.add(1.f);
        mScreenXLabels.add(Utils.getTimeString(new Date(), TIME_PATTERN));
        mScreenAdapter = new SimpleChartAdapter() {
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
                return R.color.colorPrimaryLight;
            }

            @Override
            public int getXLabelsCount() {
                return mScreenXLabels.size()> SCREEN_DATA_COUNT ?mScreenXLabels.size(): SCREEN_DATA_COUNT;
            }

            @Override
            public String getXLabel(int position) {
                if (position < mScreenXLabels.size()) {
                    return mScreenXLabels.get(position);
                }
                return "";
            }
        };
        mScreenLineChart.setAdapter(mScreenAdapter);

        formatter = new DecimalFormat("##.##");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float xLateral = event.values[0];
            float yLateral = event.values[1];
            float zLateral = event.values[2];

            String accelerate = formatter.format(Math.sqrt(xLateral * xLateral + yLateral * yLateral + zLateral * zLateral));
            mAccelerateTextView.setText(accelerate);
            if (mAccelerateDataArrayList.size() > ACCELERATE_DATA_COUNT) {
                mAccelerateDataArrayList.remove(0);
            }
            mAccelerateDataArrayList.add(Float.valueOf(accelerate));
            mAccelerateAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
