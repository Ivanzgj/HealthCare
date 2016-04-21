package com.ivan.healthcare.healthcare_android.charts;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.ivan.healthcare.healthcare_android.AppContext;
import com.ivan.healthcare.healthcare_android.R;
import com.ivan.healthcare.healthcare_android.ui.BaseActivity;
import com.ivan.healthcare.healthcare_android.view.chart.LineChart;
import com.ivan.healthcare.healthcare_android.view.chart.StackedColumnChart;
import com.ivan.healthcare.healthcare_android.view.chart.provider.ColumnChartAdapter;
import com.ivan.healthcare.healthcare_android.view.chart.provider.LineChartAdapter;
import com.ivan.healthcare.healthcare_android.database.DataAccess;
import com.ivan.healthcare.healthcare_android.util.Compat;
import com.ivan.healthcare.healthcare_android.util.TimeUtils;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * 显示各种图表的activity
 * Created by Ivan on 16/4/2.
 */
public class ChartActivity extends BaseActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {

    public static final String CHART_YEAR = "CHART_YEAR";
    public static final String CHART_MONTH = "CHART_MONTH";
    public static final String CHART_DAYOFMONTH = "CHART_DAYOFMONTH";

    private static final int SLIDE_MARGIN = AppContext.dp2px(10);

    private ViewPager mViewPager;
    private View slideView;
    private RelativeLayout tabbar;
    private TextView mBloodTextView;
    private TextView mBeepTextView;
    private TextView mAssessTextView;
    private TextView tab1;
    private TextView tab2;
    private TextView tab3;
    private ArrayList<TextView> tabArrayList;

    private float[] originColorHSV;
    private float[] newColorHSV;

    private float tabItemWidth = 0;
    private boolean alreadySet = false;

    private LineChart latestDataChart;
    private LineChart todayDataChart;
    private StackedColumnChart todayStatusChart;

    private TextView mMask1;
    private TextView mMask2;
    private TextView mMask3;

    /**
     * 查看数据的日期
     */
    private int year;
    private int month;
    private int dayOfMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = View.inflate(this, R.layout.activity_chart, null);
        initView(rootView);
        setContentView(rootView);
    }

    private void initView(View rootView) {

        Intent intent = getIntent();
        year = intent.getIntExtra(CHART_YEAR, 0);
        month = intent.getIntExtra(CHART_MONTH, 0);
        dayOfMonth = intent.getIntExtra(CHART_DAYOFMONTH, 0);

        Calendar cal = Calendar.getInstance();
        cal.set(year, month, dayOfMonth);
        Toolbar mToolbar = (Toolbar) rootView.findViewById(R.id.chart_toolbar);
        mToolbar.setTitle(TimeUtils.getDateString(cal.getTime(), "yyyy年MM月dd日"));
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        slideView = rootView.findViewById(R.id.slide_tabbar_slide_view);
        tabbar = (RelativeLayout) rootView.findViewById(R.id.slide_tabbar_rel);
        tab1 = (TextView) rootView.findViewById(R.id.slide_tabbar_tv1);
        tab1.setOnClickListener(this);
        tab2 = (TextView) rootView.findViewById(R.id.slide_tabbar_tv2);
        tab2.setOnClickListener(this);
        tab3 = (TextView) rootView.findViewById(R.id.slide_tabbar_tv3);
        tab3.setOnClickListener(this);
        tabArrayList = new ArrayList<>();
        tabArrayList.add(tab1);
        tabArrayList.add(tab2);
        tabArrayList.add(tab3);

        mBloodTextView = (TextView) rootView.findViewById(R.id.chart_detail_blood_tv);
        mBeepTextView = (TextView) rootView.findViewById(R.id.chart_detail_beep_tv);
        mAssessTextView = (TextView) rootView.findViewById(R.id.chart_detail_assess_tv);

        // 配置tab文字颜色
        originColorHSV = new float[3];
        newColorHSV = new float[3];
        int[] originColorRGB = new int[3];
        int[] newColorRGB = new int[3];
        String oColor = Integer.toHexString(Compat.getColor(this, R.color.textColorSecondary));
        String nColor = Integer.toHexString(Compat.getColor(this, R.color.default_main_color));
        originColorRGB[0] = Integer.valueOf(oColor.substring(2, 4), 16);
        originColorRGB[1] = Integer.valueOf(oColor.substring(4, 6), 16);
        originColorRGB[2] = Integer.valueOf(oColor.substring(6, 8), 16);
        newColorRGB[0] = Integer.valueOf(nColor.substring(2, 4), 16);
        newColorRGB[1] = Integer.valueOf(nColor.substring(4, 6), 16);
        newColorRGB[2] = Integer.valueOf(nColor.substring(6, 8), 16);
        Color.RGBToHSV(originColorRGB[0], originColorRGB[1], originColorRGB[2], originColorHSV);
        Color.RGBToHSV(newColorRGB[0], newColorRGB[1], newColorRGB[2], newColorHSV);

        mViewPager = (ViewPager) rootView.findViewById(R.id.chart_viewpager);
        mViewPager.addOnPageChangeListener(this);

        latestDataChart = buildLatestDataChart();
        todayDataChart = buildTodayDataChart();
        todayStatusChart = buildTodayAssessmentChart();

        PagerAdapter mAdapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return 3;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                if (position == 0) {
                    if (latestDataChart != null) {
                        container.addView(latestDataChart);
                        return latestDataChart;
                    } else {
                        if (mMask1 == null) {
                            mMask1 = makeMask();
                        }
                        container.addView(mMask1);
                        return mMask1;
                    }
                } else if (position == 1) {
                    if (todayDataChart != null) {
                        container.addView(todayDataChart);
                        return todayDataChart;
                    } else {
                        if (mMask2 == null) {
                            mMask2 = makeMask();
                        }
                        container.addView(mMask2);
                        return mMask2;
                    }
                } else {
                    if (todayStatusChart != null) {
                        container.addView(todayStatusChart);
                        return todayStatusChart;
                    } else {
                        if (mMask3 == null) {
                            mMask3 = makeMask();
                        }
                        container.addView(mMask3);
                        return mMask3;
                    }
                }
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                if (position == 0) {
                    if (latestDataChart != null) {
                        container.removeView(latestDataChart);
                    } else {
                        container.removeView(mMask1);
                    }
                } else if (position == 1) {
                    if (todayDataChart != null) {
                        container.removeView(todayDataChart);
                    } else {
                        container.removeView(mMask2);
                    }
                } else {
                    if (todayStatusChart != null) {
                        container.removeView(todayStatusChart);
                    } else {
                        container.removeView(mMask3);
                    }
                }
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }
        };

        mViewPager.setAdapter(mAdapter);
    }

    private TextView makeMask() {
        TextView mask = new TextView(this);
        mask.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mask.setGravity(Gravity.CENTER);
        mask.setText(R.string.chart_no_data);
        return mask;
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

    /**
     * 构建最近一次测量的数据折线图
     */
    private LineChart buildLatestDataChart() {
        final DataAccess.MeasuredDataUnit dataUnit = DataAccess.getLatestMeasuredData();
        if (dataUnit == null) {
            return null;
        }

        mBeepTextView.setText(String.valueOf(dataUnit.beepRate));
        mBloodTextView.setText(String.valueOf(dataUnit.pressureHigh + "/" + dataUnit.pressureLow));
        mAssessTextView.setText(String.valueOf(dataUnit.assessment));

        final ArrayList<Float> data = dataUnit.data;

        LineChartAdapter adapter = new LineChartAdapter() {
            @Override
            public int getLineCount() {
                return 1;
            }

            @Override
            public ArrayList<Float> getLineData(int index) {
                return data;
            }

            @Override
            public int getLineColor(int index) {
                return R.color.chart_cyan;
            }

            @Override
            public int getXLabelsCount() {
                return data.size();
            }

            @Override
            public String getXLabel(int position) {
                return String.valueOf(position);
            }

            @Override
            public int getLegendCount() {
                return 1;
            }

            @Override
            public String getLegend(int position) {
                return getResources().getString(R.string.chart_latest_measure_data_legend) + dataUnit.date;
            }

            @Override
            public int getLegendColorId(int position) {
                return R.color.chart_cyan;
            }
        };
        LineChart chart = new LineChart(this);
        chart.setAdapter(adapter);
        chart.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        chart.setBackgroundColor(Compat.getColor(this, R.color.pureWindowBackground));

        return chart;
    }

    /**
     * 构造今日测量数据折线图
     */
    private LineChart buildTodayDataChart() {

        ArrayList<DataAccess.MeasuredDataUnit> dataList = DataAccess.getMeasuredData(year, month, dayOfMonth);
        if (dataList == null) {
            return null;
        }

        final ArrayList<Float> pressHighData = new ArrayList<>();
        final ArrayList<Float> pressLowData = new ArrayList<>();
        final ArrayList<Float> beepRateData = new ArrayList<>();

        for (DataAccess.MeasuredDataUnit dataUnit : dataList) {
            pressHighData.add((float) dataUnit.pressureHigh);
            pressLowData.add((float) dataUnit.pressureLow);
            beepRateData.add((float) dataUnit.beepRate);
        }

        LineChartAdapter adapter = new LineChartAdapter() {
            @Override
            public int getLineCount() {
                return 3;
            }

            @Override
            public ArrayList<Float> getLineData(int index) {
                if (index == 0) return pressHighData;
                if (index == 1) return pressLowData;
                return beepRateData;
            }

            @Override
            public int getLineColor(int index) {
                if (index == 0) return R.color.chart_cyan;
                if (index == 1) return R.color.chart_green;
                return R.color.chart_amber;
            }

            @Override
            public int getXLabelsCount() {
                return pressHighData.size();
            }

            @Override
            public String getXLabel(int position) {
                return String.valueOf(position);
            }

            @Override
            public int getLegendCount() {
                return 3;
            }

            @Override
            public String getLegend(int position) {
                if (position == 0) return getResources().getString(R.string.chart_today_pressure_high_legend);
                if (position == 1) return getResources().getString(R.string.chart_today_pressure_low_legend);
                return getResources().getString(R.string.chart_today_beep_rate_legend);
            }

            @Override
            public int getLegendColorId(int position) {
                if (position == 0) return R.color.chart_cyan;
                if (position == 1) return R.color.chart_green;
                return R.color.chart_amber;
            }
        };
        LineChart chart = new LineChart(this);
        chart.setAdapter(adapter);
        chart.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        chart.setBackgroundColor(Compat.getColor(this, R.color.pureWindowBackground));

        return chart;
    }

    /**
     * 构建今日测量评估的柱状图
     */
    private StackedColumnChart buildTodayAssessmentChart() {
        final ArrayList<Float> data = DataAccess.getMeasuredAssessment(year, month, dayOfMonth);
        if (data == null) {
            return null;
        }

        ColumnChartAdapter adapter = new ColumnChartAdapter() {
            @Override
            public int getColumnCount() {
                return 1;
            }

            @Override
            public ArrayList<Float> getColumnData(int index) {
                return data;
            }

            @Override
            public int getColumnColor(int index) {
                return R.color.chart_cyan;
            }

            @Override
            public int getXLabelsCount() {
                return data.size();
            }

            @Override
            public String getXLabel(int position) {
                return String.valueOf(position);
            }

            @Override
            public int getLegendCount() {
                return 1;
            }

            @Override
            public String getLegend(int position) {
                return "test";
            }

            @Override
            public int getLegendColorId(int position) {
                return R.color.chart_cyan;
            }
        };
        StackedColumnChart chart = new StackedColumnChart(this);
        chart.setAdapter(adapter);
        chart.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        chart.setBackgroundColor(Compat.getColor(this, R.color.pureWindowBackground));

        return chart;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (!alreadySet) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) slideView.getLayoutParams();
            tabItemWidth = tabbar.getWidth() / 3;
            params.width = (int) (tabItemWidth - SLIDE_MARGIN * 2);
            slideView.setLayoutParams(params);
            slideView.setX(SLIDE_MARGIN);
            alreadySet = true;
        }

        slideView.setX((position + positionOffset) * tabItemWidth + SLIDE_MARGIN);

        if(position == 0) {
            setGradualColor(1, positionOffset);
            setGradualColor(0, 1.0f - positionOffset);
        } else if(position == 1) {
            setGradualColor(2, positionOffset);
            setGradualColor(1, 1.0f - positionOffset);
        }
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View v) {
        if (tab1.equals(v)) {
            mViewPager.setCurrentItem(0);
        } else if (tab2.equals(v)) {
            mViewPager.setCurrentItem(1);
        } else if (tab3.equals(v)) {
            mViewPager.setCurrentItem(2);
        }
    }

    private void setGradualColor(int tab, float percent) {
        float[] tHSV = new float[]{
                newColorHSV[0],
                originColorHSV[1] + (newColorHSV[1] - originColorHSV[1]) * percent,
                originColorHSV[2] - (originColorHSV[2] - newColorHSV[2]) * percent
            };
        setTabTextColor(tab, Color.HSVToColor(tHSV));
    }

    private void setTabTextColor(int tab, int color) {
        tabArrayList.get(tab).setTextColor(color);
    }
}
