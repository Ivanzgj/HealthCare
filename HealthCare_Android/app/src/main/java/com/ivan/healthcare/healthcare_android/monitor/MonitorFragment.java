package com.ivan.healthcare.healthcare_android.monitor;

import android.content.Context;
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
import com.ivan.healthcare.healthcare_android.R;
import com.ivan.healthcare.healthcare_android.util.Utils;
import com.ivan.healthcare.healthcare_android.view.chart.ShadowLineChart;
import com.ivan.healthcare.healthcare_android.view.chart.provider.LineChartAdapter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * 全方位长时间监听身体各项数据的页面
 * Created by Ivan on 16/4/18.
 */
public class MonitorFragment extends Fragment implements SensorEventListener {

    private final int ACCELERATE_DATA_COUNT = 10;

    private TextView mAccelerateTextView;

    private ArrayList<Float> mAccelerateDataArrayList;
    private LineChartAdapter mAdapter;

    private DecimalFormat formater;
    private SensorManager mSensorManager;

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
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSensorManager.unregisterListener(this);
    }

    private void initView(View rootView) {
        mAccelerateTextView = (TextView) rootView.findViewById(R.id.monitor_accelerate_textview);
        TextView mAccelerateDetailTextView = (TextView) rootView.findViewById(R.id.monitor_accelerate_chart_detail);
        mAccelerateDetailTextView.setText(Utils.getDateString(new Date(), "yyyy-MM-dd"));

        ShadowLineChart mShadowLineChart = (ShadowLineChart) rootView.findViewById(R.id.monitor_accelerate_chart);
        mAccelerateDataArrayList = new ArrayList<>();
        mAccelerateDataArrayList.add(10.f);mAccelerateDataArrayList.add(20.f);mAccelerateDataArrayList.add(10.f);
        mAccelerateDataArrayList.add(20.f);mAccelerateDataArrayList.add(10.f);mAccelerateDataArrayList.add(20.f);
        mAdapter = new LineChartAdapter() {
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

            @Override
            public int getLegendCount() {
                return 0;
            }

            @Override
            public String getLegend(int position) {
                return null;
            }

            @Override
            public int getLegendColorId(int position) {
                return 0;
            }
        };
        mShadowLineChart.setAdapter(mAdapter);

        formater = new DecimalFormat("##.##");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float xLateral = event.values[0];
            float yLateral = event.values[1];
            float zLateral = event.values[2];

            String accelerate = formater.format(Math.sqrt(xLateral * xLateral + yLateral * yLateral + zLateral * zLateral));
            mAccelerateTextView.setText(accelerate);
            if (mAccelerateDataArrayList.size() > ACCELERATE_DATA_COUNT) {
                mAccelerateDataArrayList.remove(0);
            }
            mAccelerateDataArrayList.add(Float.valueOf(accelerate));
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
