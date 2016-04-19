package com.ivan.healthcare.healthcare_android.view.chart;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import com.ivan.healthcare.healthcare_android.AppContext;
import com.ivan.healthcare.healthcare_android.view.chart.provider.ChartAdapter;
import com.ivan.healthcare.healthcare_android.view.chart.theme.ChartTheme;
import com.ivan.healthcare.healthcare_android.util.Compat;
import java.util.ArrayList;

/**
 * <h3>图表抽象类</h3>
 * <p>Created by Ivan on 16/3/19.</p>
 * <p>该抽象类已经实现了绘制图表坐标系，图例，x轴和y轴的标签的工作，实现类只需要绘制数据即可。</p>
 * <p>绘制数据时，实现类只需要调用Chart类提供的api绘制数据即可，不用关心转换过程。</p>
 * <p>绘制数据有一些这样的api：<p/>
 * <p>{@link #drawLine(Canvas, Paint, float, float, float, float, boolean)}</p>
 * <p>{@link #drawPoint(Canvas, Paint, float, float, float)}</p>
 * <p>{@link #drawRect(Canvas, Paint, float, float, float, float)}</p>
 * <P>{@link #drawRect(Canvas, Paint, float, float, float)}</P>
 * <p/>
 * <p>实现该图表类的时候，需要注意一些预定义的规则。</p>
 * <p>1. 图表的横坐标从编程意义上来说，其步进为1，从0开始，只有正坐标；</p>
 * <p>2. Chart类提供了两个变量及其set与get方法
 *    {@link #getAnimateRate()}，{@link #getAnimateType()}，{@link #setAnimateRate(float)}，{@link #setAnimateType(int)}
 *    来作为动画绘图的依据。做动画时调用者可以自行定义其规则并调用之，也可以自己实现一套变量。
 *    实现动画应该实现Chart类的抽象动画方法。
 *    动画类型预定义有两种：{@link #ANIMATE_X_FLAG}和{@link #ANIMATE_Y_FLAG};</p>
 *
 * @see LineChart
 * @see StackedColumnChart
 * @see ParallelColumnChart
 */
public abstract class Chart extends View {

    protected final int ANIMATE_X_FLAG = 1;
    protected final int ANIMATE_Y_FLAG = 0;
    protected final int ANIMATE_NON_FLAG = -1;

    protected int animateType = ANIMATE_NON_FLAG;
    /**
     * 动画的计时器
     */
    protected float animateRate = 1.f;
    protected ValueAnimator valueAnimator;

    private final int X_LABEL_TEXT_SIZE = AppContext.dp2px(11);
    private final int Y_LABEL_TEXT_SIZE = AppContext.dp2px(13);
    private final int X_LABEL_LINE_SPACE = AppContext.dp2px(2);

    public final int MODE_LINE_CHART = 0x31;
    public final int MODE_COLUMN_CHART = 0x32;

    /**
     * 图表种类
     */
    private int mode;
    /**
     * 图表默认间距
     */
    private int gridGap = AppContext.dp2px(30);
    /**
     * x轴标签高度
     */
    private final int xLabelHeight = AppContext.dp2px(35);
    /**
     * y轴标签宽度
     */
    private final int yLabelWidth = AppContext.dp2px(43);
    /**
     * x轴标签上页距
     */
    private final int xLabelMargin = AppContext.dp2px(20);
    /**
     * 图表上页距
     */
    private final int topMargin = AppContext.dp2px(10);
    /**
     * 高亮标签边距
     */
    private final int highlightMargin = AppContext.dp2px(3);
    /**
     * 高亮标签圆角弧度
     */
    private final int circleRadius = AppContext.dp2px(2.5f);
    /**
     * y轴右侧与图表坐标系的水平距离
     */
    private final int yLabelDistanceToChart = AppContext.dp2px(5);

    /**
     * 图例的属性
     */
    private final float LINE_WIDTH = AppContext.dp2px(2.5f);
    private final float LINE_LENGTH = AppContext.dp2px(25);
    private final float CIRCLE_RADIUS = AppContext.dp2px(2.5f);
    private final float RECT_WIDTH = AppContext.dp2px(25);
    private final float RECT_HEIGHT = AppContext.dp2px(10);
    private final float TEXT_SIZE = AppContext.dp2px(13);
    private final float OFF_SET = AppContext.dp2px(10);
    private final static int LEGEND_HEIGHT = AppContext.dp2px(25);
    private final int LEFT_MARGIN = AppContext.dp2px(10);

    /**
     * 默认y轴分格
     */
    private int yStep = 10;

    /**
     * 是否自适应调整图表Y轴最大值和分格
     */
    public Boolean selfAdaptive = true;

    private Paint paint;

    /**
     * x轴分割线数量
     */
    private int xGridLineCount;
    /**
     * y轴分割线数量
     */
    private int yGridLineCount;
    /**
     * 图表宽度
     */
    private int chartWidth = -1;
    /**
     * y轴标签
     */
    private ArrayList<Float> yLabels;
    /**
     * y轴最大/最小值
     */
    private float maxYValue;
    private float minYValue;
    /**
     * y轴标签格式化工具
     */
    private YAxisValueFormatter yf = new YAxisValueFormatter(){

        @Override
        public String yValuesString(float v) {

            return v+"";
        }

    };
    /**
     * 列宽
     */
    private float rectWidth;
    /**
     * view宽度
      */
    private float viewWidth;
    /**
     * view高度
     */
    private float viewHeight;
    /**
     * 所有图例占据的空间高度
     */
    private float allLegendsHeight;
    /**
     * 图表高度
     */
    private float chartHeight;
    /**
     * 列高
     */
    private float rectHeight;
    /**
     * 行宽
     */
    private float yLineSep;

    /**
     * 图表主题
     */
    private ChartTheme theme;

    /**
     * 背景颜色，优先于theme
     */
    private int backgroundColor = 0;

    /**
     * 数据源适配器
     */
    private ChartAdapter mAdapter;

    /**
     * 检测滑动手势的监听器
     */
    private GestureDetector gestureDetector;

    /**
     * 滑动的偏移
     */
    private float scrollOffset;
    /**
     * 当前图表偏移
     */
    private float curOffset;

    private Context context;

    public Chart(Context context) {
        super(context);
        init(context);
    }

    public Chart(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        paint = new Paint();
        theme = new ChartTheme(context, ChartTheme.THEME_LIGHT);

        gestureDetector = new GestureDetector(context, new OnGestureDetector());
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                getParent().requestDisallowInterceptTouchEvent(true);
                return gestureDetector.onTouchEvent(event);
            }
        });

        scrollOffset = 0;
        curOffset = 0;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        xGridLineCount = mAdapter.getXLabelsCount();
        chartWidth = gridGap * xGridLineCount;
        rectWidth = gridGap;
        // view宽度
        viewWidth = getWidth();
        // view高度
        viewHeight = getHeight();
        // 图例占据的空间高度
        allLegendsHeight = LEGEND_HEIGHT * mAdapter.getLegendCount();
        // 图表高度
        chartHeight = viewHeight - (mAdapter.drawXLabels()?(xLabelHeight):0) - topMargin - allLegendsHeight - xLabelMargin/2;
        // 列高
        rectHeight = chartHeight;
        // 行宽
        yLineSep = (chartHeight /((float)yGridLineCount-1.f));

        drawChart(canvas);

        drawYAxis(canvas);

        drawLegends(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (e.getActionMasked() == MotionEvent.ACTION_UP && e.getPointerCount() == 1) {
            curOffset += scrollOffset;
            scrollOffset = 0;
            return true;
        }
        return false;
    }




    /**
     * 将数据画在有图表背景的canvas上
     * @param canvas 画布
     */
    protected abstract void onDrawData(Canvas canvas, Paint paint);

    /**
     * Y轴动画
     *
     * @param delay  延迟，ms单位
     * @param millis 动画总时间，ms单位
     */
    public abstract void animateY(long delay, final long millis);

    /**
     * X轴动画
     *
     * @param delay  延迟，ms单位
     * @param millis 动画总时间，ms单位
     */
    public abstract void animateX(long delay, final long millis);



    /**
     * 无动画
     */
    public void showWithoutAnimation() {
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        setAnimateType(ANIMATE_NON_FLAG);
        setAnimateRate(1.f);
        invalideChartData();
    }





    /**
     * 设置y轴标签格式化工具
     */
    public void setYAxisValuesFormatter(YAxisValueFormatter yf){
        this.yf = yf;
    }

    /**
     * 设置图表主题
     *
     * @param theme 图表主题常量
     * @see ChartTheme
     */
    public void setTheme(int theme) {
        setAnimateType(-1);
        getChartTheme().setChartTheme(theme);
        invalidate();
    }

    /**
     * 设置y轴标签
     * @param yLabels y轴标签列表
     */
    public void setYLabels(ArrayList<Float> yLabels) {
        yGridLineCount = yLabels.size();
        this.yLabels = yLabels;
        maxYValue = yLabels.get(yLabels.size() - 1);
        minYValue = yLabels.get(0);
    }

    /**
     * 清空所有状态
     */
    public void clearAll() {
        if (valueAnimator != null) {
            valueAnimator.cancel();
            valueAnimator = null;
        }
        curOffset = 0;
        scrollOffset = 0;
        setAnimateRate(1.f);
        setAnimateType(ANIMATE_NON_FLAG);
    }

    /**
     * 设置y轴分多少格
     * @param step 格数
     */
    public void setYStep(int step) {
        this.yStep = step;
    }

    public float getChartHeight() {
        return chartHeight;
    }

    public int getyLabelWidth() {
        return yLabelWidth;
    }

    public int getTopMargin() {
        return topMargin;
    }

    public ChartAdapter getAdapter() {
        return mAdapter;
    }

    public void setBackgroundColor(int color) {
        backgroundColor = color;
    }

    public float getPointMiddleOffset() {
        return rectWidth / 2;
    }



    /**
     * 设置图标模式
     * @see #MODE_LINE_CHART
     * @see #MODE_COLUMN_CHART
     */
    protected void setMode(int mode) {
        this.mode = mode;
    }

    /**
     * 设置数据适配器
     * @param adapter 数据源适配器
     */
    protected void setAdapter(ChartAdapter adapter) {
        mAdapter = adapter;
        mAdapter.chart = this;
    }

    protected void setXWidth(int gridGap) {
        this.gridGap = gridGap;
    }




    /**
     * 绘制图表坐标系（包括x坐标）以及数据
     */
    private void drawChart(Canvas canvas) {

        paint.setAntiAlias(true);

        paint.setStyle(Paint.Style.FILL);
        if (backgroundColor != 0) {
            paint.setColor(backgroundColor);
            canvas.drawRect(yLabelWidth, 0, viewWidth, topMargin + chartHeight + xLabelHeight + xLabelMargin, paint);
        } else if (theme.chartBGColor != 0) {
            paint.setColor(theme.chartBGColor);
            canvas.drawRect(yLabelWidth, 0, viewWidth, topMargin + chartHeight + xLabelHeight + xLabelMargin, paint);
        }

        canvas.save();
        canvas.translate(curOffset + scrollOffset + yLabelWidth, 0);

        paint.setTextAlign(Paint.Align.CENTER);

        // 画x轴label
        if (mAdapter.drawXLabels()) {
            paint.setTextSize(X_LABEL_TEXT_SIZE);
//        paint.setTypeface(Typeface.DEFAULT_BOLD);
            for (int i = 0; i <= xGridLineCount; i++) {

                if (i < xGridLineCount) {
                    paint.setColor(theme.xLabelTextColor);
                    paint.setStyle(Paint.Style.FILL);
//                String xLabel = xLabels.get(i);
                    String xLabel = mAdapter.getXLabel(i);
                    String[] labels = xLabel.split(",");    // 分割行

                    // 画xLabel标签
                    if (xLabel.equals("今,天")) {
                        // 今天加上绿色背景
                        paint.setColor(theme.todayXLabelBGColor);
                        Compat.drawRoundRect(i * rectWidth + rectWidth / 2 - X_LABEL_TEXT_SIZE / 2 - highlightMargin,
                                rectHeight + topMargin + xLabelMargin - X_LABEL_TEXT_SIZE - highlightMargin,
                                i * rectWidth + rectWidth / 2 + X_LABEL_TEXT_SIZE / 2 + highlightMargin,
                                rectHeight + topMargin + xLabelMargin + X_LABEL_TEXT_SIZE * labels.length - X_LABEL_TEXT_SIZE * 0.7f + X_LABEL_LINE_SPACE * (labels.length - 1) + highlightMargin,
                                circleRadius, circleRadius, canvas, paint);

                        paint.setColor(theme.todayXLabelTextColor);
                        for (int k = 0; k < labels.length; k++) {
                            canvas.drawText(labels[k],
                                    i * rectWidth + rectWidth / 2,
                                    rectHeight + xLabelMargin + topMargin + (X_LABEL_TEXT_SIZE + X_LABEL_LINE_SPACE) * k,
                                    paint);
                        }
                        paint.setColor(theme.xLabelTextColor);
                    } else {
                        for (int k = 0; k < labels.length; k++) {
                            canvas.drawText(labels[k],
                                    i * rectWidth + rectWidth / 2,
                                    rectHeight + xLabelMargin + topMargin + (X_LABEL_TEXT_SIZE + X_LABEL_LINE_SPACE) * k,
                                    paint);
                        }
                    }
                }
            }
        }

        // 画水平方向的gridLine
        paint.setColor(theme.gridLineColor);
        for (int i=0;i<yGridLineCount;i++){
            canvas.drawLine(0, i* yLineSep +topMargin, chartWidth, i* yLineSep +topMargin, paint);
        }

        onDrawData(canvas, paint);

        canvas.restore();
    }


    /**
     * 绘制y轴坐标
     */
    private void drawYAxis(Canvas canvas) {

        paint.setStyle(Paint.Style.FILL);

        if (backgroundColor != 0) {
            paint.setColor(backgroundColor);
            canvas.drawRect(0, 0, yLabelWidth, topMargin + chartHeight + xLabelHeight + xLabelMargin, paint);
        } else if (theme.chartBGColor != 0) {
            paint.setColor(theme.chartBGColor );
            canvas.drawRect(0, 0, yLabelWidth, topMargin + chartHeight + xLabelHeight + xLabelMargin, paint);
        }

        paint.setAlpha(0);
        paint.setColor(theme.yLabelTextColor);
        paint.setTextAlign(Paint.Align.RIGHT);
        paint.setStrokeWidth(2);
        float textSize = yLineSep * 0.8f;
        paint.setTextSize(textSize>Y_LABEL_TEXT_SIZE?Y_LABEL_TEXT_SIZE:textSize);
//        paint.setTypeface(Typeface.DEFAULT_BOLD);

        Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
        int offset = (-fontMetrics.bottom - fontMetrics.top)/2;

        // 画y轴label
        for (int i=0;i<yGridLineCount; i++) {
            canvas.drawText(yf.yValuesString(yLabels.get((yGridLineCount - 1 - i))),
                    yLabelWidth - yLabelDistanceToChart,
                    i * yLineSep + topMargin + offset,
                    paint);
        }
    }


    /**
     * 绘制图例
     */
    private void drawLegends(Canvas canvas) {

        paint.setAntiAlias(true);
        paint.setTextSize(TEXT_SIZE);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextAlign(Paint.Align.LEFT);

        if (backgroundColor != 0) {
            paint.setColor(backgroundColor);
            canvas.drawRect(0, viewHeight - allLegendsHeight, viewWidth, viewHeight, paint);
        } else if (theme.chartBGColor != 0) {
            paint.setColor(theme.chartBGColor);
            canvas.drawRect(0, viewHeight - allLegendsHeight, viewWidth, viewHeight, paint);
        }

        int legendCount = mAdapter.getLegendCount();
        for (int i = 0; i < legendCount; i++) {
            drawLegend(canvas, Compat.getColor(context, mAdapter.getLegendColorId(i)), mAdapter.getLegend(i), mode, i);
        }
    }

    /**
     * 绘制单个图例
     */
    private  void drawLegend(Canvas canvas, int color, String text, int mode, int position) {

        paint.setColor(color);

        Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
        int baseline = (LEGEND_HEIGHT - fontMetrics.bottom - fontMetrics.top) / 2;
        float start = viewHeight - allLegendsHeight + position * LEGEND_HEIGHT;
        float y;

        switch (mode) {
            case MODE_LINE_CHART:
                paint.setStrokeWidth(LINE_WIDTH);
                y = start + LEGEND_HEIGHT / 2;
                canvas.drawLine(LEFT_MARGIN, y, LEFT_MARGIN+LINE_LENGTH, y, paint);
                canvas.drawCircle(LEFT_MARGIN+LINE_LENGTH / 2, y, CIRCLE_RADIUS, paint);
                paint.setStrokeWidth(AppContext.dp2px(1));
                paint.setColor(theme.legendTextColor);
                canvas.drawText(text, LEFT_MARGIN+LINE_LENGTH+OFF_SET, start + baseline, paint);
                break;
            case MODE_COLUMN_CHART:
                y = start + (LEGEND_HEIGHT + RECT_HEIGHT) / 2;
                canvas.drawRect(LEFT_MARGIN, start + (LEGEND_HEIGHT -RECT_HEIGHT)/2, LEFT_MARGIN+RECT_WIDTH, y, paint);
                paint.setStrokeWidth(AppContext.dp2px(1));
                paint.setColor(theme.legendTextColor);
                canvas.drawText(text, LEFT_MARGIN+RECT_WIDTH+OFF_SET, start + baseline, paint);
                break;
            default:
                break;
        }
    }





    /**
     * 重画数据，UI线程调用
     */
    protected void invalideChartData() {
        invalidate(yLabelWidth, 0, (int) viewWidth, (int) (viewHeight - allLegendsHeight));
    }

    /**
     * 重画数据，非UI线程调用
     */
    protected void postInvalidateChartData() {
        postInvalidate(yLabelWidth, 0, (int) viewWidth, (int) (viewHeight - allLegendsHeight));
    }

    /**
     * 继承自该Chart类的具体实现类可以通过该api在指定的坐标上绘制点，
     * 该方法需要在{@link #onDrawData(Canvas, Paint)}方法中调用
     * @param canvas 该Chart类的canvas，来自于{@link #onDrawData(Canvas, Paint)}方法
     * @param paint 画笔实例
     * @param x 坐标x，对应于已设定好的坐标系
     * @param y 坐标y，对应于已设定好的坐标系
     *
     * @see #drawRect(Canvas, Paint, float, float, float, float)
     * @see #drawLine(Canvas, Paint, float, float, float, float, boolean)
     * @see #drawPoint(Canvas, Paint, float, float, float)
     * @see #drawRect(Canvas, Paint, float, float, float)
     */
    protected void drawPoint(Canvas canvas, Paint paint, float x, float y, float circleRadius) {
        canvas.drawCircle(rectWidth / 2 + x * rectWidth, (1 - (y-minYValue) / (maxYValue-minYValue)) * chartHeight + topMargin, circleRadius, paint);
    }

    /**
     * 继承自该Chart类的具体实现类可以通过该api在指定的坐标上绘制线，
     * 该方法需要在{@link #onDrawData(Canvas, Paint)}方法中调用
     * @param canvas 该Chart类的canvas，来自于{@link #onDrawData(Canvas, Paint)}方法
     * @param paint 画笔实例
     * @param x1 起点坐标x1，对应于已设定好的坐标系
     * @param y1 起点坐标y1，对应于已设定好的坐标系
     * @param x2 终点坐标x2，对应于已设定好的坐标系
     * @param y2 终点坐标y2，对应于已设定好的坐标系
     * @param cubic 是否画曲线.true画曲线，false画直线
     *
     * @see #drawRect(Canvas, Paint, float, float, float, float)
     * @see #drawLine(Canvas, Paint, float, float, float, float, boolean)
     * @see #drawPoint(Canvas, Paint, float, float, float)
     * @see #drawRect(Canvas, Paint, float, float, float)
     */
    protected void drawLine(Canvas canvas, Paint paint, float x1, float y1, float x2, float y2, boolean cubic) {
        if (!cubic) {
            canvas.drawLine(rectWidth / 2 + x1 * rectWidth, (1 - (y1-minYValue) / (maxYValue-minYValue)) * chartHeight + topMargin,
                    rectWidth / 2 + (x2) * rectWidth, (1 - (y2-minYValue) / (maxYValue-minYValue)) * chartHeight + topMargin,
                    paint);
        } else {
            float startX = rectWidth / 2 + x1 * rectWidth;
            float startY = (1 - (y1-minYValue) / (maxYValue-minYValue)) * chartHeight + topMargin;
            float endX = rectWidth / 2 + (x2) * rectWidth;
            float endY = (1 - (y2-minYValue) / (maxYValue-minYValue)) * chartHeight + topMargin;
            Path path = new Path();
            float cx = (startX + endX) / 2;
            path.moveTo(startX, startY);
            path.cubicTo(cx, startY, cx, endY, endX, endY);
            canvas.drawPath(path, paint);
        }
    }

    /**
     * 继承自该Chart类的具体实现类可以通过该api在指定的坐标上绘制堆叠条形图的矩形，
     * 该方法需要在{@link #onDrawData(Canvas, Paint)}方法中调用
     * @param canvas 该Chart类的canvas，来自于{@link #onDrawData(Canvas, Paint)}方法
     * @param paint 画笔实例
     * @param l 矩形左边界
     * @param t 矩形上边界
     * @param r 矩形右边界
     * @param b 矩形下边界
     *
     * @see #drawRect(Canvas, Paint, float, float, float, float)
     * @see #drawLine(Canvas, Paint, float, float, float, float, boolean)
     * @see #drawPoint(Canvas, Paint, float, float, float)
     * @see #drawRect(Canvas, Paint, float, float, float)
     */
    protected void drawRect(Canvas canvas, Paint paint, float l, float t, float r, float b) {
        canvas.drawRect(rectWidth * l,
                        (1 - t / maxYValue) * chartHeight + topMargin,
                        rectWidth * r,
                        (1 - b / maxYValue) * chartHeight + topMargin,
                        paint);
    }

    /**
     * 继承自该Chart类的具体实现类可以通过该api在指定的坐标上绘制平行条形图的矩形，
     * 该方法需要在{@link #onDrawData(Canvas, Paint)}方法中调用
     * @param canvas 该Chart类的canvas，来自于{@link #onDrawData(Canvas, Paint)}方法
     * @param paint 画笔实例
     * @param l 矩形左边界
     * @param t 矩形上边界
     * @param width 矩形宽度
     *
     * @see #drawRect(Canvas, Paint, float, float, float, float)
     * @see #drawLine(Canvas, Paint, float, float, float, float, boolean)
     * @see #drawPoint(Canvas, Paint, float, float, float)
     * @see #drawRect(Canvas, Paint, float, float, float)
     */
    protected void drawRect(Canvas canvas, Paint paint, float l, float t, float width) {
        canvas.drawRect(rectWidth * l,
                        (1 - t / maxYValue) * chartHeight + topMargin,
                        rectWidth * (l + width),
                        chartHeight + topMargin,
                        paint);
    }


    /**
     * 设置动画进度
     * @param rate 动画进度
     */
    protected void setAnimateRate(float rate) {
        animateRate = rate;
    }

    /**
     * 获取动画进度
     */
    protected float getAnimateRate() {
        return animateRate;
    }

    /**
     * 设置动画类型
     */
    protected void setAnimateType(int type) {
        animateType = type;
    }

    /**
     * 获取动画类型
     * @see #ANIMATE_NON_FLAG
     * @see #ANIMATE_X_FLAG
     * @see #ANIMATE_Y_FLAG
     */
    protected int getAnimateType() {
        return animateType;
    }

    /**
     * 获取图表主题
     * @return 图表主题
     */
    protected ChartTheme getChartTheme() {
        return theme;
    }





    /**
     * 检测滑动手势
     */
    private class OnGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (e1.getY() > viewHeight - allLegendsHeight || e1.getY() < topMargin) {
                getParent().requestDisallowInterceptTouchEvent(false);
                return false;
            }
            if (e1.getX() < yLabelWidth) {
                getParent().requestDisallowInterceptTouchEvent(false);
                return false;
            }

            scrollOffset = e2.getX() - e1.getX();
            if (-(curOffset + scrollOffset) < 0) {
                scrollOffset = -curOffset;
                e1.setLocation(e2.getX(), e1.getY());
                curOffset += scrollOffset;
                scrollOffset = 0;
                getParent().requestDisallowInterceptTouchEvent(false);
            } else if (-(curOffset + scrollOffset) > chartWidth - viewWidth +yLabelWidth) {
                scrollOffset = viewWidth - chartWidth - yLabelWidth - curOffset;
                e1.setLocation(e2.getX(), e1.getY());
                curOffset += scrollOffset;
                scrollOffset = 0;
                getParent().requestDisallowInterceptTouchEvent(false);
            }
            postInvalidate(yLabelWidth, 0, (int) viewWidth, (int) (viewHeight - allLegendsHeight));
            return false;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
    }





    /**
     * y轴label的formatter
     * @author Ivan
     */
    public interface YAxisValueFormatter {
        String yValuesString(float v);
    }


}
