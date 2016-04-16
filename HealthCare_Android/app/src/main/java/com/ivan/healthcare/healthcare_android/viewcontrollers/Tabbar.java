package com.ivan.healthcare.healthcare_android.viewcontrollers;

import java.util.ArrayList;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import com.ivan.healthcare.healthcare_android.R;
import com.ivan.healthcare.healthcare_android.util.Compat;

/**
 * 自定义底部tabbar
 * @author Ivan
 */
class Tabbar extends RelativeLayout implements View.OnClickListener, ViewPager.OnPageChangeListener {

	private Context context;
	/**
	 * 图标列表
	 */
	private ArrayList<Integer> iconIDList;
	/**
	 * 标题列表
	 */
	private ArrayList<String> titleList;

	private ViewPager viewPager;

	private ArrayList<ImageView> iconImageViewList;
	private ArrayList<TextView> titleTextViewList;

	private int prePosition = 0;

	public Tabbar(Context context) {
		super(context);
		this.context = context;
		iconIDList = new ArrayList<>();
		titleList = new ArrayList<>();
	}
	
	public Tabbar(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		iconIDList = new ArrayList<>();
		titleList = new ArrayList<>();
	}

	/**
	 * 设置图标
	 * @param icons 图标列表
	 */
	public void setIcons(ArrayList<Integer> icons) {
		iconIDList = icons;
	}

	/**
	 * 设置tabbar控制的viewPager
	 * @param viewPager 一般是一个fragment view pager
	 */
	public void setViewPager(ViewPager viewPager) {
		this.viewPager = viewPager;
		viewPager.addOnPageChangeListener(this);
	}

	/**
	 * 设置标题
	 * @param titles 标题列表
	 */
	public void setTitles(ArrayList<String> titles) {
		titleList = titles;
	}
	
	public void initView() {

		setBackgroundColor(Color.WHITE);

		iconImageViewList = new ArrayList<>();
		titleTextViewList = new ArrayList<>();
		
		View line = new View(context);
		line.setBackgroundColor(Compat.getColor(context, R.color.WHTabbarLineColor));
		LayoutParams lineParams = new LayoutParams(LayoutParams.MATCH_PARENT, context.getResources().getDimensionPixelSize(R.dimen.WHTabbarLineHeight));
		lineParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		line.setId(R.id.tabbarLine);
		addView(line, lineParams);
		
		final LinearLayout iconsLayout = new LinearLayout(context);
		LayoutParams linearParams = new LayoutParams(LayoutParams.MATCH_PARENT, 
													LayoutParams.MATCH_PARENT);
		linearParams.addRule(RelativeLayout.BELOW, line.getId());
		iconsLayout.setOrientation(LinearLayout.HORIZONTAL);
		addView(iconsLayout, linearParams);

		int count = titleList.size();
		for (int i = 0; i < count; i++) {
			RelativeLayout rel = new RelativeLayout(context);
			ImageView iconImageView = new ImageView(context);
			int iconID = iconIDList.get(i);
			iconImageView.setImageResource(iconID);
			iconImageView.setScaleType(ScaleType.FIT_CENTER);
			iconImageView.setAdjustViewBounds(true);
			LayoutParams iconParams = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
			iconParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
			iconParams.bottomMargin = dp2px(20);
			iconParams.topMargin = dp2px(6);

			TextView title = new TextView(context);
			title.setTextSize(12);
			title.setText(titleList.get(i));
			title.setTextColor(Compat.getColor(context, R.color.textColorSecondaryPressed));
			LayoutParams titleParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			titleParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			titleParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
			titleParams.bottomMargin = dp2px(3);

			rel.addView(iconImageView, iconParams);
			rel.addView(title, titleParams);

			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
																			LinearLayout.LayoutParams.MATCH_PARENT);
			params.weight = 1;
			iconsLayout.addView(rel, params);

			rel.setTag(i);
			rel.setOnClickListener(Tabbar.this);

			iconImageViewList.add(iconImageView);
			titleTextViewList.add(title);
		}
	}

	@Override
	public void onClick(View v) {
		if (v instanceof RelativeLayout) {
			try {
				Integer index = (Integer) v.getTag();
				viewPager.setCurrentItem(index, false);
			} catch (UnsupportedOperationException e) {
				e.printStackTrace();
			}
		}
	}

	private int dp2px(float dpvalue) {
		return (int) (dpvalue * context.getResources().getDisplayMetrics().density + 0.5f);
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		if (positionOffset == 0) {
			iconImageViewList.get(prePosition).setImageResource(iconIDList.get(prePosition));
			titleTextViewList.get(prePosition).setTextColor(Compat.getColor(context, R.color.textColorSecondaryPressed));
			iconImageViewList.get(position).setImageResource(iconIDList.get(position + titleList.size()));
			titleTextViewList.get(position).setTextColor(Compat.getColor(context, R.color.colorPrimary));
			prePosition = position;
		}
	}

	@Override
	public void onPageSelected(int position) {

	}

	@Override
	public void onPageScrollStateChanged(int state) {

	}
}
