package com.ivan.healthcare.healthcare_android.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.ivan.healthcare.healthcare_android.AppContext;
import com.ivan.healthcare.healthcare_android.R;
import com.ivan.healthcare.healthcare_android.chart.LineChart;
import com.ivan.healthcare.healthcare_android.chart.provider.LineChartAdapter;
import com.ivan.healthcare.healthcare_android.util.Compat;
import java.util.ArrayList;

/**
 * 显示各种图表的activity
 * Created by Ivan on 16/4/2.
 */
public class ChartActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {

    public static final String CHART_DATE = "CHART_DATE";
    private static final int SLIDE_MARGIN = AppContext.dp2px(10);

    private Toolbar mToolbar;
    private ViewPager mViewPager;
    private PagerAdapter mAdapter;
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

    /**
     * 查看数据的日期
     */
    private String mDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = LayoutInflater.from(this);
        View rootView = inflater.inflate(R.layout.activity_chart, null, false);
        initView(rootView);
        setContentView(rootView);
    }

    private void initView(View rootView) {

        mToolbar = (Toolbar) rootView.findViewById(R.id.chart_toolbar);
        mToolbar.setTitle(R.string.chart_title);
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

        Intent intent = getIntent();
        mDate = intent.getStringExtra(CHART_DATE);

        final LineChart chart1 = buildChart();
        final LineChart chart2 = buildChart();
        final LineChart chart3 = buildChart();

        mAdapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return 3;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                if (position == 0) {
                    container.addView(chart1);
                    return chart1;
                } else if (position == 1) {
                    container.addView(chart2);
                    return chart2;
                } else {
                    container.addView(chart3);
                    return chart3;
                }
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                if (position == 0) {
                    container.removeView(chart1);
                } else if (position == 1) {
                    container.removeView(chart2);
                } else {
                    container.removeView(chart3);
                }
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }
        };

        mViewPager.setAdapter(mAdapter);

        chart1.postDelayed(new Runnable() {
            @Override
            public void run() {
                chart1.animateX(0, 3000);
            }
        }, 2000);
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

    // for test
    private LineChart buildChart() {
        final ArrayList<Float> data1 = new ArrayList<>();
        final ArrayList<Float> data2 = new ArrayList<>();
        final ArrayList<Float> data3 = new ArrayList<>();
        for (int i=0;i<20;i++) {
            if (i % 2 == 0) data1.add((float) 10);
            else            data1.add((float) 100);
            data2.add(data1.get(i)*2);
            data3.add(data1.get(i)*3);
        }
        LineChartAdapter adapter = new LineChartAdapter() {
            @Override
            public int getLineCount() {
                return 3;
            }

            @Override
            public ArrayList<Float> getLineData(int index) {
                if (index == 0) return data1;
                if (index == 1) return data2;
                return data3;
            }

            @Override
            public int getLineColor(int index) {
                if (index == 0) return R.color.chart_cyan;
                if (index == 1) return R.color.chart_green;
                return R.color.chart_amber;
            }

            @Override
            public int getXLabelsCount() {
                return data1.size();
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
                return "test";
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

    private void pageUp() {

    }

    private void pageDown() {

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
