package com.ivan.healthcare.healthcare_android;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

import com.ivan.healthcare.healthcare_android.local.User;
import com.ivan.healthcare.healthcare_android.measure.MeasureFragment;
import com.ivan.healthcare.healthcare_android.charts.CalendarFragment;
import com.ivan.healthcare.healthcare_android.settings.ProfileFragment;
import com.ivan.healthcare.healthcare_android.viewcontrollers.TabViewController;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TabViewController tabViewController;

    private MeasureFragment bluetoothCommFragment;
    private CalendarFragment chartFragment;
    private ProfileFragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();

        User.initUserInfo();
    }

    private void initView() {
        bluetoothCommFragment = new MeasureFragment();
        chartFragment = new CalendarFragment();
        profileFragment = new ProfileFragment();

        ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
        fragmentArrayList.add(bluetoothCommFragment);
        fragmentArrayList.add(chartFragment);
        fragmentArrayList.add(profileFragment);
        ArrayList<Integer> iconArrayList = new ArrayList<>();
        iconArrayList.add(R.mipmap.tab_review_unselected);
        iconArrayList.add(R.mipmap.tab_graph_unselected);
        iconArrayList.add(R.mipmap.tab_settings_unselected);
        iconArrayList.add(R.mipmap.tab_review_selected);
        iconArrayList.add(R.mipmap.tab_graph_selected);
        iconArrayList.add(R.mipmap.tab_settings_selected);
        ArrayList<String> titles = new ArrayList<>();
        titles.add(getResources().getString(R.string.main_measure_title));
        titles.add(getResources().getString(R.string.main_data_title));
        titles.add(getResources().getString(R.string.main_profile_title));
        tabViewController = new TabViewController(this, fragmentArrayList, iconArrayList, titles);
        tabViewController.setScrollable(true);

        LinearLayout rootView = new LinearLayout(this);
        rootView.setOrientation(LinearLayout.VERTICAL);
        rootView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        mToolbar = (Toolbar) View.inflate(this, R.layout.layout_toolbar, null);
        mToolbar.setTitle(R.string.app_name);

        rootView.addView(mToolbar);
        rootView.addView(tabViewController);

        setContentView(rootView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
