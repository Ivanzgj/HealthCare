package com.ivan.healthcare.healthcare_android.monitor;

import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.ivan.healthcare.healthcare_android.AppContext;
import com.ivan.healthcare.healthcare_android.R;
import com.ivan.healthcare.healthcare_android.database.DataAccess;
import com.ivan.healthcare.healthcare_android.local.Preference;
import com.ivan.healthcare.healthcare_android.settings.ProfileFragment;
import com.ivan.healthcare.healthcare_android.ui.BaseActivity;
import com.ivan.healthcare.healthcare_android.util.Compat;
import com.ivan.healthcare.healthcare_android.util.TimeUtils;
import com.ivan.healthcare.healthcare_android.view.chart.Chart;
import com.ivan.healthcare.healthcare_android.view.chart.LineChart;
import com.ivan.healthcare.healthcare_android.view.chart.provider.LineChartAdapter;
import com.ivan.healthcare.healthcare_android.view.chart.provider.SimpleChartAdapter;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * monitor history
 * Created by Ivan on 16/4/20.
 */
public class MonitorHistoryActivity extends BaseActivity {

    private final int STATUS_MAX = 10;
    private final int ACCELERATE_DATA_COUNT = 15;
    private final int SCREEN_DATA_COUNT = 6;

    private TextView mStatusTextView;
    private TextView mVibrationDateTextView;
    private TextView mScreenDataTextView;

    private LineChart mStatusChart;
    private LineChart mAccelerateChart;
    private LineChart mSrcChart;

    private LineChartAdapter mStatusAdapter;
    private LineChartAdapter mAccelerateAdapter;
    private LineChartAdapter mSrcAdapter;

    private ArrayList<Float> mStatusArrayList;
    private ArrayList<String> mTimeArrayList;
    private BaseAdapter mTimeAdapter;
    private ArrayList<Float> mAccelerateDataArrayList;
    private ArrayList<Float> mScreenDataArrayList;
    private ArrayList<String> mScreenXLabels;

    private DrawerLayout mDrawerLayout;

    private DecimalFormat formatter;

    private String date;

    private int speed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int mode = AppContext.getPreference().getInt(Preference.MONITOR_MODE, ProfileFragment.MONITOR_MODE_AUTO);
        if (mode == ProfileFragment.MONITOR_MODE_AUTO) {
            speed = ProfileFragment.MONITOR_CUSTOM_MODE_DEFAULT_SPEED;
        } else {
            speed = AppContext.getPreference().getInt(Preference.MONITOR_SPEED, ProfileFragment.MONITOR_CUSTOM_MODE_DEFAULT_SPEED);
        }
        initView();
        refreshContent();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        mDrawerLayout = (DrawerLayout) View.inflate(this, R.layout.activity_monitor_history, null);

        Toolbar mToolbar = (Toolbar) mDrawerLayout.findViewById(R.id.monitor_history_toolbar);
        mToolbar.setTitle(R.string.monitor_history_title);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mStatusTextView = (TextView) mDrawerLayout.findViewById(R.id.monitor_history_status_chart_date);
        mVibrationDateTextView = (TextView) mDrawerLayout.findViewById(R.id.monitor_history_accelerate_chart_date);
        mScreenDataTextView = (TextView) mDrawerLayout.findViewById(R.id.monitor_history_screen_chart_date);

        // 状态统计图
        mStatusChart = (LineChart) mDrawerLayout.findViewById(R.id.monitor_history_status_chart);
        mStatusChart.setXWidth(AppContext.dp2px(50));
        mStatusChart.selfAdaptive = false;
        ArrayList<Float> statusYLabels = new ArrayList<>();
        for (int i = 0; i <= STATUS_MAX; i++) {
            statusYLabels.add((float) i);
        }
        mStatusChart.setYLabels(statusYLabels);

        mStatusArrayList = new ArrayList<>();
        mStatusAdapter = new SimpleChartAdapter() {
            @Override
            public int getLineCount() {
                return 1;
            }

            @Override
            public ArrayList<Float> getLineData(int index) {
                return mStatusArrayList;
            }

            @Override
            public int getLineColorId(int index) {
                return R.color.colorPrimary;
            }

            @Override
            public String getXLabel(int position) {
                if (date != null) {
                    String d = TimeUtils.add(date, speed * position);
                    d = TimeUtils.convertTimeFormat(d, "yyyyMMddHHmmss", "yyyyMMddHH:mm:ss");
                    return d.substring(8);
                }
                return super.getXLabel(position);
            }
        };
        mStatusChart.setAdapter(mStatusAdapter);

        // 振动数据图
        mAccelerateChart = (LineChart) mDrawerLayout.findViewById(R.id.monitor_history_accelerate_chart);
        mAccelerateChart.setYAxisValuesFormatter(new Chart.YAxisValueFormatter() {
            @Override
            public String yValuesString(float v) {
                return formatter.format(v);
            }
        });

        // 屏幕控制图
        mSrcChart = (LineChart) mDrawerLayout.findViewById(R.id.monitor_history_screen_chart);
        mSrcChart.selfAdaptive = false;
        ArrayList<Float> srcYLabels = new ArrayList<>();
        srcYLabels.add(0.f);
        srcYLabels.add(1.f);
        srcYLabels.add(2.f);
        mSrcChart.setYLabels(srcYLabels);
        mSrcChart.setYAxisValuesFormatter(new Chart.YAxisValueFormatter() {
            @Override
            public String yValuesString(float v) {
                if (v == 0.f) return "off";
                if (v == 1.f) return "on";
                else return "in";
            }
        });
        mSrcChart.setXWidth(AppContext.dp2px(60));

        mAccelerateDataArrayList = new ArrayList<>();
        mScreenDataArrayList = new ArrayList<>();
        mScreenXLabels = new ArrayList<>();

        // 振动数据源
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
            public int getXLabelsCount() {
                return mAccelerateDataArrayList.size()>ACCELERATE_DATA_COUNT ? mAccelerateDataArrayList.size() : ACCELERATE_DATA_COUNT;
            }

            @Override
            public String getXLabel(int position) {
                return (position+1)+"";
            }
        };
        mAccelerateChart.setAdapter(mAccelerateAdapter);

        // 屏幕控制数据源
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
            public int getLineColorId(int index) {
                return R.color.colorPrimary;
            }

            @Override
            public int getXLabelsCount() {
                return mScreenXLabels.size()>SCREEN_DATA_COUNT ? mScreenXLabels.size() : SCREEN_DATA_COUNT;
            }

            @Override
            public String getXLabel(int position) {
                if (position < mScreenXLabels.size()) {
                    String text = TimeUtils.convertTimeFormat(mScreenXLabels.get(position), "yyyyMMddHHmmss", "yyyy年MM月dd日HH:mm:ss");
                    return text.substring(11, text.length());
                }
                return "";
            }
        };
        mSrcChart.setAdapter(mSrcAdapter);

        // drawer
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.main_drawer_open, R.string.main_drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                mTimeAdapter.notifyDataSetChanged();
            }
        };
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mTimeArrayList = new ArrayList<>();
        ListView mTimeListView = (ListView) mDrawerLayout.findViewById(R.id.monitor_history_time_listView);
        mTimeAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return mTimeArrayList.size();
            }

            @Override
            public Object getItem(int position) {
                return mTimeArrayList.get(position);
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView tv;
                if (convertView != null) {
                    tv = (TextView) convertView;
                } else {
                    tv = new TextView(MonitorHistoryActivity.this);
                    tv.setGravity(Gravity.CENTER);
                    tv.setTextSize(16);
                    tv.setTextColor(Compat.getColor(MonitorHistoryActivity.this, R.color.textColorPrimary));
                    tv.setHeight(getResources().getDimensionPixelSize(R.dimen.single_line_list_item_height));
                }
                String text = TimeUtils.convertTimeFormat(mTimeArrayList.get(position), "yyyyMMddHHmmss", "yyyy年MM月dd日HH:mm:ss");
                tv.setText(text);
                if (mTimeArrayList.get(position).equals(date)) {
                    tv.setBackgroundResource(R.color.colorPrimaryLight);
                } else {
                    tv.setBackgroundResource(R.color.pureWindowBackground);
                }
                return tv;
            }
        };
        mTimeListView.setAdapter(mTimeAdapter);
        mTimeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                date = mTimeArrayList.get(position);
                refreshContent();
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        formatter = new DecimalFormat("##.##");

        setContentView(mDrawerLayout);
    }

    private void refreshContent() {

        mTimeArrayList = DataAccess.getHistoryMonitorVibrationTime();
        mTimeAdapter.notifyDataSetChanged();

        if (mTimeArrayList.size() == 0) {
            mStatusChart.setVisibility(View.INVISIBLE);
            mAccelerateChart.setVisibility(View.INVISIBLE);
            mSrcChart.setVisibility(View.INVISIBLE);
            return;
        }

        mStatusChart.setVisibility(View.VISIBLE);
        mAccelerateChart.setVisibility(View.VISIBLE);
        mSrcChart.setVisibility(View.VISIBLE);

        if (date == null) {
            date = mTimeArrayList.get(0);
        }

//        mAccelerateChart.reset();

        String title = TimeUtils.convertTimeFormat(date, "yyyyMMddHHmmss", "yyyy年MM月dd日HH:mm:ss");

        // 获取振动数据
        mVibrationDateTextView.setText(title);
        mAccelerateDataArrayList = DataAccess.getVibrationData(date);
        mAccelerateAdapter.notifyDataSetChanged();

        // 获取屏幕控制数据
        mScreenDataTextView.setText(title);
        ArrayList<DataAccess.SrcDataUnit> srcData = DataAccess.getSrcData(date);
        mScreenDataArrayList.clear();
        mScreenXLabels.clear();
        for (DataAccess.SrcDataUnit data : srcData) {
            mScreenDataArrayList.add((float) data.srcOn);
            mScreenXLabels.add(data.recTime);
        }
        mSrcAdapter.notifyDataSetChanged();

        // 计算监控状态
        mStatusTextView.setText(title);
        float std = SensorManager.GRAVITY_EARTH;
        String srcTime = null;
        String nextTime = null;
        int next = 2;
        int srcOn = 2;
        if (srcData.size() > 1) {
            srcTime = srcData.get(0).recTime;
            nextTime = srcData.get(1).recTime;
        }
        for (int i = 0; i < mAccelerateDataArrayList.size(); i++) {
            float acc = mAccelerateDataArrayList.get(i);
            String time = TimeUtils.add(date, speed * i);
            if (srcTime == null || (nextTime != null && time.compareTo(nextTime) < 0 && srcOn != 2)) {
                // 无屏幕亮灭数据或者time时间以后的屏幕都不是已解锁状态，
                // 则按照振动数据计算状态
                if (acc >= std-2 && acc <= std+2) {
                    mStatusArrayList.add(STATUS_MAX*0.2f);
                } else if (acc >= std-6 && acc <= std+6) {
                    mStatusArrayList.add(STATUS_MAX*0.5f);
                } else {
                    mStatusArrayList.add(STATUS_MAX*0.7f);
                }
            } else if (nextTime != null && time.compareTo(nextTime) < 0 && srcOn == 2) {
                // 屏幕此时处于已解锁状态，
                mStatusArrayList.add((float) STATUS_MAX);
            } else if (nextTime != null && time.compareTo(nextTime) >= 0) {
                // time时候已越过下一个屏幕控制时间，
                // 则更新当前屏幕控制时间和下一个屏幕控制时间
                srcTime = nextTime;
                srcOn = srcData.get(next-1).srcOn;
                if (next < srcData.size()) {
                    nextTime = srcData.get(next).recTime;
                    next++;
                } else {
                    nextTime = null;
                }
                // 如果当前屏幕控制状态不是已解锁，则按照振动数据计算状态
                if (srcOn != 2) {
                    if (acc >= std-2 && acc <= std+2) {
                        mStatusArrayList.add(STATUS_MAX*0.2f);
                    } else if (acc >= std-6 && acc <= std+6) {
                        mStatusArrayList.add(STATUS_MAX*0.5f);
                    } else {
                        mStatusArrayList.add(STATUS_MAX*0.7f);
                    }
                } else {
                    mStatusArrayList.add((float) STATUS_MAX);
                }
            }
        }
        mStatusAdapter.notifyDataSetChanged();
    }
}
