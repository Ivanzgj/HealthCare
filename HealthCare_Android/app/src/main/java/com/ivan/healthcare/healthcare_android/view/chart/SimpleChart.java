package com.ivan.healthcare.healthcare_android.view.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.ivan.healthcare.healthcare_android.AppContext;
import com.ivan.healthcare.healthcare_android.util.Compat;
import com.ivan.healthcare.healthcare_android.view.chart.provider.LineChartAdapter;

import java.util.ArrayList;

/**
 * 自定义简单曲线图，没有坐标系，只显示曲线
 * Created by Ivan on 16/4/1.
 */
public class SimpleChart extends View {

    private static final int LINE_WIDTH = AppContext.dp2px(2);

    private SimpleChartAdapter mAdapter = null;

    private float yLength = 0;
    private float minValue = 0;
    private float xStepWidth = AppContext.dp2px(30);
    private float chartHeight = 0;
    private float chartWidth = 0;

    private Paint paint;
    private Path path;

    private float mScrollX = 0;

    public SimpleChart(Context context) {
        super(context);
        paint = new Paint();
        path = new Path();
    }

    public SimpleChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        path = new Path();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        chartHeight = getHeight();
        chartWidth = getWidth();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mAdapter == null) {
            return;
        }

        canvas.save();
        canvas.translate(mScrollX, 0);

        paint.setAntiAlias(true);
        paint.setStrokeWidth(LINE_WIDTH);
        paint.setStyle(Paint.Style.STROKE);

        for (int i=0;i<mAdapter.getLineCount();i++) {
            ArrayList<Float> data = mAdapter.getLineData(i);
            for (int j=0;j<data.size()-1;j++) {
                paint.setColor(Compat.getColor(getContext(), mAdapter.getLineColor(j)));
                float y1 = data.get(j);
                float y2 = data.get(j+1);
                float startX = j*xStepWidth;
                float startY = (1-(y1-minValue)/yLength)*chartHeight;
                float endX = (j+1)*xStepWidth;
                float endY = (1-(y2-minValue)/yLength)*chartHeight;
                path.reset();
                float cx = (startX + endX) / 2;
                path.moveTo(startX, startY);
                path.cubicTo(cx, startY, cx, endY, endX, endY);
                canvas.drawPath(path, paint);
            }
        }

        canvas.restore();
    }

    public void setAdapter(SimpleChartAdapter adapter) {
        this.mAdapter = adapter;

        int maxY = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        for (int i=0;i<mAdapter.getLineCount();i++) {
            ArrayList<Float> data = mAdapter.getLineData(i);
            for (int j=0;j<mAdapter.getLineData(i).size();j++) {
                float value = data.get(j);
                if (maxY < value)	maxY = (int) value;
                if (minY > value)	minY = (int) value;
            }
        }

        this.minValue = minY - 10;
        this.yLength = maxY + 10 - this.minValue;
    }

    public void scrollTo(float x) {
        mScrollX = x;
        invalidate();
    }

    public static abstract class SimpleChartAdapter extends LineChartAdapter {
        @Override
        public int getXLabelsCount() {
            return getLineData(0).size();
        }

        @Override
        public String getXLabel(int position) {
            return null;
        }

        @Override
        public boolean drawXLabels() {
            return false;
        }

        public int getLegendCount() {
            return 0;
        }

        /**
         * 获得图例
         * @return 图例
         */
        public String getLegend(int position) {
            return null;
        }

        /**
         * 获得图例的颜色
         * @return 图例的颜色
         */
        public int getLegendColorId(int position) {
            return 0;
        }
    }
}
