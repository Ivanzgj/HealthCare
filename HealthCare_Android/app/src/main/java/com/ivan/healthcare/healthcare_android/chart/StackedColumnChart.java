package com.ivan.healthcare.healthcare_android.chart;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.util.AttributeSet;

import com.ivan.healthcare.healthcare_android.chart.provider.ColumnChartAdapter;
import com.ivan.healthcare.healthcare_android.util.Compat;

import java.util.ArrayList;

/**
 * 堆叠柱状图
 * @author Ivan
 */
public class StackedColumnChart extends Chart {

	/**
	 * 柱状图柱距
	 */
	private static final float columnSpace = 0.2f;

	private ColumnChartAdapter mAdapter = new ColumnChartAdapter() {

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

		@Override
		public int getColumnCount() {
			return 0;
		}

		@Override
		public ArrayList<Float> getColumnData(int index) {
			return null;
		}

		@Override
		public int getColumnColor(int index) {
			return 0;
		}
	};

	private Context context;

	public StackedColumnChart(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		setMode(MODE_COLUMN_CHART);
		setAdapter(mAdapter);
	}
	
	public StackedColumnChart(Context context) {
		super(context);
		this.context = context;
		setMode(MODE_COLUMN_CHART);
		setAdapter(mAdapter);
	}

	@Override
	public void onDrawData(Canvas canvas, Paint paint) {

		if (mAdapter.getColumnCount() == 0)	return;

		int animateType = getAnimateType();
		float animateRate = getAnimateRate();

		float y;
		int xCount = mAdapter.getColumnData(0).size();
		int stackCount = mAdapter.getColumnCount();

		paint.setStyle(Paint.Style.FILL);

		if (animateType == ANIMATE_X_FLAG || animateType == ANIMATE_NON_FLAG)	animateRate = 1.f;	// 柱状图没有x轴方向的动画

		if (animateRate > 1) {
			animateRate = 1.f;
		}
		for (int k=0;k<xCount;k++){
			// 画矩形条
			y = 0;
			for (int i=0;i<stackCount;i++){
				ArrayList<Float> data = mAdapter.getColumnData(i);
				paint.setColor(Compat.getColor(context, mAdapter.getColumnColor(i)));
				float top = y + data.get(k);
				drawRect(canvas, paint, k + columnSpace, top * animateRate, k + 1 - columnSpace, y * animateRate);
				y = top;
			}
		}
	}

	/**
	 * 设置数据适配器
	 * @param adapter 数据适配器
	 */
	public void setAdapter(@NonNull ColumnChartAdapter adapter) {
		super.setAdapter(adapter);
		mAdapter = adapter;
		setColumnDataSet();
	}

	private void setColumnDataSet(){

		if (mAdapter.getColumnCount() == 0)	{
			return;
		}

		int maxY = 0;

		for (int i = 0; i < mAdapter.getColumnData(0).size(); i++) {
			float value = 0;
			for (int j = 0; j < mAdapter.getColumnCount(); j++) {
				value += mAdapter.getColumnData(j).get(i);
			}
			if (maxY < value) {
				maxY = (int) Math.ceil(value);
			}
		}

		int yStep = getYStep();
		if (selfAdaptive) {
			setYStep(10);
			yStep = 10;
			if (maxY <= 100) maxY = 100;
			else if (maxY > 100 && maxY <= 1000) maxY = (int) (Math.ceil((float)maxY/100.f)*100);
			else if (maxY > 1000 && maxY < 10000) maxY = (int) (Math.ceil((float)maxY/1000.f)*1000);
			ArrayList<Float> yLabels = new ArrayList<>();
			for (int i=0;i<=yStep;i++){
				yLabels.add((float) (i*maxY/yStep));
			}
			setYlabels(yLabels);
			return;
		}

		if (maxY % yStep != 0)		maxY = (int) (Math.ceil(maxY/yStep))*yStep;
		ArrayList<Float> yLabels = new ArrayList<>();
		for (int i=0;i<=yStep;i++){
			yLabels.add((float) (i*maxY/yStep));
		}
		setYlabels(yLabels);
	}

	/**
	 * Y轴动画
	 *
	 * @param delay  延迟，ms单位
	 * @param millis 动画总时间，ms单位
	 */
	@Override
	public void animateY(long delay, final long millis) {

		valueAnimator = ValueAnimator.ofFloat(0.f, 1.f);
		valueAnimator.setDuration(millis);
		valueAnimator.setStartDelay(delay);
		valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				setAnimateRate(animation.getAnimatedFraction());
				postInvalidateChartData();
			}
		});
		valueAnimator.addListener(new Animator.AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animation) {
				setAnimateType(ANIMATE_Y_FALG);
				setAnimateRate(0.f);
			}

			@Override
			public void onAnimationEnd(Animator animation) {

			}

			@Override
			public void onAnimationCancel(Animator animation) {
				setAnimateType(ANIMATE_NON_FLAG);
			}

			@Override
			public void onAnimationRepeat(Animator animation) {
				setAnimateType(ANIMATE_Y_FALG);
				setAnimateRate(0.f);
			}
		});
		valueAnimator.start();
	}

	@Override
	public void animateX(long delay, long millis) {
		showWithoutAnimation();
	}
}
