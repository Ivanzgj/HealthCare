package com.ivan.healthcare.healthcare_android;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import com.ivan.healthcare.healthcare_android.ui.MeasureFragment;
import com.ivan.healthcare.healthcare_android.ui.CalendarFragment;
import com.ivan.healthcare.healthcare_android.ui.ProfileFragment;
import com.ivan.healthcare.healthcare_android.viewcontrollers.TabViewController;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TabViewController tabViewController;
    private MeasureFragment bluetoothCommFragment;
    private CalendarFragment chartFragment;
    private ProfileFragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        setContentView(tabViewController);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
