package com.ivan.healthcare.healthcare_android.view.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import com.ivan.healthcare.healthcare_android.AppContext;
import com.ivan.healthcare.healthcare_android.util.Compat;
import com.ivan.healthcare.healthcare_android.view.chart.provider.LineChartAdapter;
import java.util.ArrayList;

/**
 * 自定义简单曲线图，没有坐标系，只显示曲线
 * Created by Ivan on 16/4/1.
 */
public class ShadowLineChart extends Chart {

    private static final int LINE_WIDTH = AppContext.dp2px(2);

    private LineChartAdapter mAdapter = new LineChartAdapter() {
        @Override
        public int getLineCount() {
            return 0;
        }

        @Override
        public ArrayList<Float> getLineData(int index) {
            return null;
        }

        @Override
        public int getLineColor(int index) {
            return 0;
        }

        @Override
        public int getXLabelsCount() {
            return 0;
        }

        @Override
        public String getXLabel(int position) {
            return null;
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

    private int xCount = 10;
    private float yLength = 0;
    private float minValue = Integer.MAX_VALUE;
    private float maxValue = Integer.MIN_VALUE;
    private float xStepWidth;

    private Path path;

    public ShadowLineChart(Context context) {
        super(context);
        path = new Path();
        setAdapter(mAdapter);
        setMode(MODE_LINE_CHART);
    }

    public ShadowLineChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        path = new Path();
        setAdapter(mAdapter);
        setMode(MODE_LINE_CHART);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        float chartWidth = getWidth() - getyLabelWidth();
        if (mAdapter != null) {
            xCount = mAdapter.getXLabelsCount();
        }
        xStepWidth = chartWidth / xCount;
        setXWidth((int) xStepWidth);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDrawData(Canvas canvas, Paint paint) {

        if (mAdapter == null || mAdapter.getLineCount() == 0) {
            return;
        }

        paint.setAntiAlias(true);
        paint.setStrokeWidth(LINE_WIDTH);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Compat.getColor(getContext(), mAdapter.getLineColor(0)));
        path.reset();

        ArrayList<Float> data = mAdapter.getLineData(0);

        float chartHeight = getChartHeight();
        float topMargin = getTopMargin();
        float startX;
        float startY;
        float endX = 0;
        float endY;
        for (int i = 0;i < data.size()-1; i++) {
            float y1 = data.get(i);
            float y2 = data.get(i+1);
            startX = i*xStepWidth;
            startY = (1-(y1-minValue)/yLength)*chartHeight+topMargin;
            endX = (i+1)*xStepWidth;
            endY = (1-(y2-minValue)/yLength)*chartHeight+topMargin;
            float cx = (startX + endX) / 2;
            if (i == 0) {
                path.moveTo(startX, startY);
            }
            path.cubicTo(cx, startY, cx, endY, endX, endY);
        }
        path.lineTo(endX, chartHeight+topMargin);
        path.lineTo(0, chartHeight+topMargin);
        path.close();
        canvas.drawPath(path, paint);
    }

    @Override
    public void animateY(long delay, long millis) {
        showWithoutAnimation();
    }

    @Override
    public void animateX(long delay, long millis) {
        showWithoutAnimation();
    }

    public void setAdapter(@NonNull LineChartAdapter adapter) {
        super.setAdapter(adapter);

        mAdapter = adapter;

        for (int i=0;i<mAdapter.getLineCount();i++) {
            ArrayList<Float> data = mAdapter.getLineData(i);
            for (int j=0;j<mAdapter.getLineData(i).size();j++) {
                float value = data.get(j);
                if (maxValue < value)	maxValue = (int) value;
                if (minValue > value)	minValue = (int) value;
            }
        }

        int yStep = getYStep();
        if (selfAdaptive) {
            setYStep(10);
            yStep = 10;
            this.yLength = maxValue - minValue;
            ArrayList<Float> yLabels = new ArrayList<>();
            for (int i=0;i<=yStep;i++){
                yLabels.add(minValue + i*yLength/yStep);
            }
            setYlabels(yLabels);
            return;
        }

        yLength = maxValue - minValue;
        ArrayList<Float> yLabels = new ArrayList<>();
        for (int i = 0; i <= yStep; i++){
            yLabels.add(minValue + i*yLength/yStep);
        }
        setYlabels(yLabels);
    }
}
