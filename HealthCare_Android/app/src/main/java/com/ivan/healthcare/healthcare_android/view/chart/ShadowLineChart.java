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

    private final int LINE_WIDTH = AppContext.dp2px(2);

    private boolean isPointMiddle = false;

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
        public int getLineColorId(int index) {
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

    private float yLength = 0;
    private float minValue = Float.MAX_VALUE;
    private float maxValue = Float.MIN_VALUE;
    private float xStepWidth = -1;

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
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDrawData(Canvas canvas, Paint paint) {

        if (mAdapter == null || mAdapter.getLineCount() == 0) {
            return;
        }

        ArrayList<Float> data = mAdapter.getLineData(0);
        if (data.size() <= 1) {
            return;
        }

        if (xStepWidth <= 0) {
            float chartWidth = getWidth() - getyLabelWidth();
            int xCount = mAdapter.getXLabelsCount();
            xStepWidth = chartWidth / xCount;
            super.setXWidth((int) xStepWidth);
        }

        float pointOffset = 0.f;
        if (isPointMiddle)  pointOffset = getPointMiddleOffset();

        paint.setAntiAlias(true);
        path.reset();

        float chartHeight = getChartHeight();
        float topMargin = getTopMargin();

        float startX;
        float startY;
        float endX = 0;
        float endY;
        for (int i = 0;i < data.size()-1; i++) {
            float y1 = data.get(i);
            float y2 = data.get(i+1);
            startX = pointOffset + i*xStepWidth;
            startY = (1-(y1-minValue)/yLength)*chartHeight+topMargin;
            endX = pointOffset + (i+1)*xStepWidth;
            endY = (1-(y2-minValue)/yLength)*chartHeight+topMargin;
            float cx = (startX + endX) / 2;
            if (i == 0) {
                path.moveTo(startX, startY);
            }
            path.cubicTo(cx, startY, cx, endY, endX, endY);
//            drawLine(canvas, paint, i, data.get(i), i+1, data.get(i+1), true);
        }

        int lineColor = mAdapter.getLineColorId(0);
        int shadowColor = mAdapter.getShadowColor(0);

        paint.setStrokeWidth(LINE_WIDTH);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Compat.getColor(getContext(), lineColor));
        canvas.drawPath(path, paint);

        path.lineTo(endX, chartHeight + topMargin);
        path.lineTo(pointOffset, chartHeight + topMargin);
        path.close();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Compat.getColor(getContext(), shadowColor));
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

        if (mAdapter.getLineCount() == 0) {
            return;
        }

        if (selfAdaptive) {

            for (int i=0;i<mAdapter.getLineCount();i++) {
                ArrayList<Float> data = mAdapter.getLineData(i);
                for (int j=0;j<mAdapter.getLineData(i).size();j++) {
                    float value = data.get(j);
                    if (maxValue < value)	maxValue = (float) Math.ceil(value);
                    if (minValue > value)	minValue = (float) Math.floor(value);
                }
            }

            int yStep = 10;
            yLength = maxValue - minValue;
            ArrayList<Float> yLabels = new ArrayList<>();
            for (int i=0;i<=yStep;i++){
                yLabels.add(minValue + i*yLength/yStep);
            }
            super.setYLabels(yLabels);
        }
    }

    @Override
    public void setYLabels(ArrayList<Float> yLabels) {
        super.setYLabels(yLabels);
        maxValue = yLabels.get(yLabels.size() - 1);
        minValue = yLabels.get(0);
        yLength = maxValue - minValue;
    }

    public void setDrawPointMiddle(boolean isMiddle) {
        isPointMiddle = isMiddle;
    }

    public void setXWidth(int gridGap) {
        this.xStepWidth = gridGap;
        super.setXWidth(gridGap);
    }

    public void reset() {
        yLength = 0;
        minValue = Integer.MAX_VALUE;
        maxValue = Integer.MIN_VALUE;
    }
}
