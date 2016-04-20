package com.ivan.healthcare.healthcare_android.viewcontrollers;

import java.util.ArrayList;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.view.LayoutInflater;
import android.view.View;
import com.ivan.healthcare.healthcare_android.R;

/**
 * 实现ios TabViewController功能的管理多个个fragments的控制器
 * @author Ivan
 */
public class TabViewController extends RelativeLayout {

	/**
	 * 底部tabbar的引用
	 */
	private Tabbar tabbar;
	/**
	 * 管理各个fragment的viewpager的引用
	 */
	private CustomViewPager viewPager;
	/**
	 * 管理各个fragment的viewpager的adapter
	 */
	private FragmentStatePagerAdapter mAdapter;
	/**
	 * 使用该类的activity的句柄
	 */
	private Context context;
	/**
	 * 包含各个fragment的列表
	 */
	private ArrayList<Fragment> fragmentList;
	/**
	 * 包含底部tabbar对应各个fragment的icon的列表
	 */
	private ArrayList<Integer> iconIDList;
	/**
	 * 标题列表
	 */
	private ArrayList<String> titleList;
	/**
	 *
	 */
	private FragmentManager fm;

	public TabViewController(Context context, ArrayList<Fragment> fragmentList, ArrayList<Integer> iconIDList, ArrayList<String> titleList) {
		super(context);
		this.context = context;
		this.fragmentList = fragmentList;
		this.iconIDList = iconIDList;
		this.titleList = titleList;
		fm = ((FragmentActivity)context).getSupportFragmentManager();
		initView();
	}

	public TabViewController(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		fm = ((FragmentActivity)context).getSupportFragmentManager();
	}

	private void initView() {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View root = inflater.inflate(R.layout.layout_tabview_controller, this, true);
		
		tabbar = (Tabbar) root.findViewById(R.id.multipager_tabbar);
		viewPager = (CustomViewPager) root.findViewById(R.id.multipager_viewpager);
		viewPager.setOffscreenPageLimit(fragmentList.size()-1);
		
		tabbar.setViewPager(viewPager);
		tabbar.setIcons(iconIDList);
		tabbar.setTitles(titleList);
		tabbar.initView();
		
		mAdapter = new FragmentStatePagerAdapter(fm) {
			
			@Override
			public int getCount() {
				return fragmentList.size();
			}
			
			@Override
			public Fragment getItem(int index) {
				return fragmentList.get(index);
			}
		};
		viewPager.setAdapter(mAdapter);

		viewPager.setHorizontalScrollBarEnabled(false);

		mAdapter.notifyDataSetChanged();
	}

	/**
	 * 指定viewpager是否可以滑动
	 * @param scrollable 是否可以滑动
	 */
	public void setScrollable(boolean scrollable) {
		viewPager.setScrollable(scrollable);
	}

	public void hideTabbar() {
		tabbar.setVisibility(View.GONE);
	}

	public void showTabbar() {
		tabbar.setVisibility(View.VISIBLE);
	}

	public boolean isTabbarVisible() {
		return tabbar.getVisibility() == View.VISIBLE;
	}

}
