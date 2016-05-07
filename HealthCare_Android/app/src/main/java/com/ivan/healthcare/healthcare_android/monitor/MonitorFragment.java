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
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.ivan.healthcare.healthcare_android.AppContext;
import com.ivan.healthcare.healthcare_android.MainActivity;
import com.ivan.healthcare.healthcare_android.R;
import com.ivan.healthcare.healthcare_android.database.DataAccess;
import com.ivan.healthcare.healthcare_android.local.Preference;
import com.ivan.healthcare.healthcare_android.settings.ProfileFragment;
import com.ivan.healthcare.healthcare_android.util.TimeUtils;
import com.ivan.healthcare.healthcare_android.view.chart.Chart;
import com.ivan.healthcare.healthcare_android.view.chart.ShadowLineChart;
import com.ivan.healthcare.healthcare_android.view.chart.provider.LineChartAdapter;
import com.ivan.healthcare.healthcare_android.view.chart.provider.SimpleChartAdapter;
import com.ivan.healthcare.healthcare_android.view.material.ButtonFlat;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 全方位长时间监听身体各项数据的页面
 * Created by Ivan on 16/4/18.
 */
public class MonitorFragment extends Fragment implements SensorEventListener, View.OnClickListener {

    private final int ACCELERATE_DATA_COUNT = 10;
    private final int SCREEN_DATA_COUNT = 6;
    private final String TIME_PATTERN = "HH:mm:ss";

    private TextView mAccelerateTextView;
    private FloatingActionButton mMonitorButton;
    private ButtonFlat mHistoryButton;

    private ArrayList<Float> mAccelerateDataArrayList;
    private float maxAccelerateValue = Float.MIN_VALUE;
    private float minAccelerateValue = Float.MAX_VALUE;
    private ShadowLineChart mAccelerateLineChart;
    private LineChartAdapter mAccelerateAdapter;

    private ArrayList<Float> mScreenDataArrayList;
    private ArrayList<String> mScreenXLabels;
    private ShadowLineChart mScreenLineChart;
    private LineChartAdapter mScreenAdapter;

    private DecimalFormat formatter;
    private SensorManager mSensorManager;

    private Boolean isMonitoring = false;
    private String monitorTime = null;
    private int accelerateNum = 0;
    private int vibSum = 0;
    private long startTimeMillis;

    private final Object lock = new Object();

    /**
     * 监听屏幕亮灭和解锁
     */
    private BroadcastReceiver mScreenReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            float value = 0.f;
            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                value = 1.f;
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                value = 0.f;
            } else if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
                value = 2.f;
            }
            synchronized (lock) {
                mScreenDataArrayList.add(value);
                String time = TimeUtils.getTimeString(new Date(), TIME_PATTERN);
                mScreenXLabels.add(time);
                mScreenLineChart.scrollToEnd();
                if (isMonitoring && monitorTime != null) {
                    DataAccess.writeSrcData(monitorTime, TimeUtils.getTimeString(new Date()), (int) value);
                }
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
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isMonitoring) {
            mSensorManager.unregisterListener(this);
            getActivity().unregisterReceiver(mScreenReceiver);
        }
        getActivity().unregisterReceiver(mScreenReceiver);
    }

    private void initView(View rootView) {

        formatter = new DecimalFormat("###.##");

        mAccelerateTextView = (TextView) rootView.findViewById(R.id.monitor_accelerate_textview);
        TextView mAccelerateDetailTextView = (TextView) rootView.findViewById(R.id.monitor_accelerate_chart_detail);
        mAccelerateDetailTextView.setText(TimeUtils.getDateString(new Date(), "yyyy-MM-dd"));

        mAccelerateLineChart = (ShadowLineChart) rootView.findViewById(R.id.monitor_accelerate_chart);
//        mAccelerateLineChart.setBackgroundColor(Compat.getColor(getActivity(), R.color.colorPrimary));
        mAccelerateLineChart.setYAxisValuesFormatter(new Chart.YAxisValueFormatter() {
            @Override
            public String yValuesString(float v) {
                return formatter.format(v);
            }
        });
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
            public int getLineColorId(int index) {
                return R.color.colorPrimary;
            }

            @Override
            public int getShadowColor(int position) {
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

        mScreenLineChart = (ShadowLineChart) rootView.findViewById(R.id.monitor_screen_chart);
//        mScreenLineChart.setBackgroundColor(Compat.getColor(getActivity(), R.color.colorPrimary));
        mScreenLineChart.selfAdaptive = false;
        ArrayList<Float> yLabels = new ArrayList<>();
        yLabels.add(0.f);
        yLabels.add(1.f);
        yLabels.add(2.f);
        mScreenLineChart.setYLabels(yLabels);
        mScreenLineChart.setYAxisValuesFormatter(new Chart.YAxisValueFormatter() {
            @Override
            public String yValuesString(float v) {
                if (v == 0.f) return "off";
                if (v == 1.f) return "on";
                else return "in";
            }
        });
        mScreenLineChart.setXWidth(AppContext.dp2px(60));
        mScreenLineChart.setDrawPointMiddle(true);
        mScreenDataArrayList = new ArrayList<>();
        mScreenXLabels = new ArrayList<>();
        mScreenDataArrayList.add(1.f);
        mScreenXLabels.add(TimeUtils.getTimeString(new Date(), TIME_PATTERN));
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
            public int getLineColorId(int index) {
                return R.color.colorPrimary;
            }

            @Override
            public int getShadowColor(int position) {
                return R.color.colorPrimaryLight;
            }

            @Override
            public int getXLabelsCount() {
                return mScreenXLabels.size()>SCREEN_DATA_COUNT ? mScreenXLabels.size() : SCREEN_DATA_COUNT;
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

        mMonitorButton = (FloatingActionButton) rootView.findViewById(R.id.monitor_float_button);
        mMonitorButton.setOnClickListener(this);
        mHistoryButton = (ButtonFlat) rootView.findViewById(R.id.monitor_history_btn);
        mHistoryButton.setOnClickListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float xLateral = event.values[0];
            float yLateral = event.values[1];
            float zLateral = event.values[2];

            String accelerate = formatter.format(Math.sqrt(xLateral * xLateral + yLateral * yLateral + zLateral * zLateral));
            float value = Float.valueOf(accelerate);

            if (value > maxAccelerateValue) {
                maxAccelerateValue = value;
            }
            if (value < minAccelerateValue) {
                minAccelerateValue = value;
            }
            String f = getResources().getString(R.string.monitor_accelerate_value_string);
            mAccelerateTextView.setText(String.format(f, value, maxAccelerateValue, minAccelerateValue));
            synchronized (lock) {
                if (mAccelerateDataArrayList.size() > ACCELERATE_DATA_COUNT) {
                    mAccelerateDataArrayList.remove(0);
                }
                mAccelerateDataArrayList.add(value);
                mAccelerateAdapter.notifyDataSetChanged();
                if (isMonitoring && monitorTime != null) {
                    long curTimeMillis = new Date().getTime();
                    if (curTimeMillis - startTimeMillis > 59*1000) {
                        if (DataAccess.writeVibrationData(monitorTime, accelerateNum, vibSum)) {
                            accelerateNum++;
                        }
                        startTimeMillis = curTimeMillis;
                        vibSum = 0;
                    } else {
                        float d = Math.abs(value - SensorManager.GRAVITY_EARTH);
                        vibSum += (d>0.6 ? (d-0.6) : 0);
                    }
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onClick(View v) {
        if (mMonitorButton.equals(v)) {
            synchronized (lock) {
                if (!isMonitoring) {
                    ((MainActivity) getActivity()).getTabViewController().hideTabbar();
                    ((MainActivity) getActivity()).getTabViewController().setScrollable(false);
                    ((MainActivity) getActivity()).getToolbar().setVisibility(View.GONE);
                    mHistoryButton.setVisibility(View.GONE);
                    startMonitor();
                } else {
                    ((MainActivity) getActivity()).getTabViewController().showTabbar();
                    ((MainActivity) getActivity()).getTabViewController().setScrollable(true);
                    ((MainActivity) getActivity()).getToolbar().setVisibility(View.VISIBLE);
                    mHistoryButton.setVisibility(View.VISIBLE);
                    stopMonitor();
                }
                mAccelerateDataArrayList.clear();
                mScreenDataArrayList.clear();
                mScreenDataArrayList.add(2.f);
                mScreenXLabels.clear();
                String time = TimeUtils.getTimeString(new Date(), TIME_PATTERN);
                mScreenXLabels.add(time);

                DataAccess.writeSrcData(monitorTime, TimeUtils.getTimeString(new Date()), 2);
                mAccelerateAdapter.notifyDataSetChanged();
                mScreenAdapter.notifyDataSetChanged();
            }
        } else if (mHistoryButton.equals(v)) {
            Intent intent = new Intent();
            intent.setClass(getActivity(), MonitorHistoryActivity.class);
            startActivity(intent);
        }
    }

    public void startMonitor() {
        monitorTime = TimeUtils.getTimeString(new Date());
        isMonitoring = true;

        Sensor accelerator = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerator != null) {
            startTimeMillis = new Date().getTime();
            int mode = AppContext.getPreference().getInt(Preference.MONITOR_MODE, ProfileFragment.MONITOR_MODE_AUTO);
            int speed;
            if (mode == ProfileFragment.MONITOR_MODE_AUTO) {
                speed = ProfileFragment.MONITOR_CUSTOM_MODE_DEFAULT_SPEED;
            } else {
                speed = AppContext.getPreference().getInt(Preference.MONITOR_SPEED, ProfileFragment.MONITOR_CUSTOM_MODE_DEFAULT_SPEED);
            }
            mSensorManager.registerListener(this, accelerator, (int) TimeUnit.MILLISECONDS.toMicros(speed));
        }
    }

    public void stopMonitor() {
        monitorTime = null;
        accelerateNum = 0;
        isMonitoring = false;

        mSensorManager.unregisterListener(this);
    }

}
