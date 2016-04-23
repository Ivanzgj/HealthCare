package com.ivan.healthcare.healthcare_android.monitor;

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

    private final int ACCELERATE_DATA_COUNT = 15;
    private final int SCREEN_DATA_COUNT = 6;

    private TextView mVibrationDateTextView;
    private TextView mScreenDataTextView;

    private LineChart mAccelerateChart;
    private LineChart mSrcChart;

    private ListView mTimeListView;

    private LineChartAdapter mAccelerateAdapter;
    private LineChartAdapter mSrcAdapter;

    private ArrayList<String> mTimeArrayList;
    private BaseAdapter mTimeAdapter;
    private ArrayList<Float> mAccelerateDataArrayList;
    private ArrayList<Float> mScreenDataArrayList;
    private ArrayList<String> mScreenXLabels;

    private DrawerLayout mDrawerLayout;

    private DecimalFormat formatter;

    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        mVibrationDateTextView = (TextView) mDrawerLayout.findViewById(R.id.monitor_history_accelerate_chart_date);
        mScreenDataTextView = (TextView) mDrawerLayout.findViewById(R.id.monitor_history_screen_chart_date);

        mAccelerateChart = (LineChart) mDrawerLayout.findViewById(R.id.monitor_history_accelerate_chart);
        mAccelerateChart.setYAxisValuesFormatter(new Chart.YAxisValueFormatter() {
            @Override
            public String yValuesString(float v) {
                return formatter.format(v);
            }
        });

        mSrcChart = (LineChart) mDrawerLayout.findViewById(R.id.monitor_history_screen_chart);
        mSrcChart.selfAdaptive = false;
        ArrayList<Float> yLabels = new ArrayList<>();
        yLabels.add(0.f);
        yLabels.add(1.f);
        yLabels.add(2.f);
        mSrcChart.setYLabels(yLabels);
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
                return R.color.colorPrimary;
            }

            @Override
            public int getShadowColor(int position) {
                return R.color.colorPrimaryLight;
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
                    String text = TimeUtils.convertTimeFormat(mScreenXLabels.get(position), "yyyyMMddHHmmss", "yyyy年MM月dd日HH:mm:ss");
                    return text.substring(11, text.length());
                }
                return "";
            }
        };
        mSrcChart.setAdapter(mSrcAdapter);

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
        mTimeListView = (ListView) mDrawerLayout.findViewById(R.id.monitor_history_time_listView);
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
            mAccelerateChart.setVisibility(View.INVISIBLE);
            mSrcChart.setVisibility(View.INVISIBLE);
            return;
        }

        mAccelerateChart.setVisibility(View.VISIBLE);
        mSrcChart.setVisibility(View.VISIBLE);

        if (date == null) {
            date = mTimeArrayList.get(0);
        }

//        mAccelerateChart.reset();

        String title = TimeUtils.convertTimeFormat(date, "yyyyMMddHHmmss", "yyyy年MM月dd日HH:mm:ss");
        mVibrationDateTextView.setText(title);
        mAccelerateDataArrayList = DataAccess.getVibrationData(date);
        mAccelerateAdapter.notifyDataSetChanged();

        mScreenDataTextView.setText(title);
        ArrayList<DataAccess.SrcDataUnit> srcData = DataAccess.getSrcData(date);
        mScreenDataArrayList.clear();
        mScreenXLabels.clear();
        for (DataAccess.SrcDataUnit data : srcData) {
            mScreenDataArrayList.add((float) data.srcOn);
            mScreenXLabels.add(data.recTime);
        }
        mSrcAdapter.notifyDataSetChanged();
    }
}
