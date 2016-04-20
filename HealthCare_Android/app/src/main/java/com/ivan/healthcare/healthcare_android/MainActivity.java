package com.ivan.healthcare.healthcare_android;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ivan.healthcare.healthcare_android.local.Constellation;
import com.ivan.healthcare.healthcare_android.local.User;
import com.ivan.healthcare.healthcare_android.measure.MeasureFragment;
import com.ivan.healthcare.healthcare_android.charts.CalendarFragment;
import com.ivan.healthcare.healthcare_android.monitor.MonitorFragment;
import com.ivan.healthcare.healthcare_android.settings.ProfileFragment;
import com.ivan.healthcare.healthcare_android.ui.BaseActivity;
import com.ivan.healthcare.healthcare_android.viewcontrollers.TabViewController;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends BaseActivity {

    private CircleImageView mAvatarImageView;
    private TextView mUidTextView;
    private TextView mNameTextView;
    private TextView mIntroTextView;
    private TextView mSexTextView;
    private TextView mAgeTextView;
    private TextView mConstellationTextView;
    private TextView mBirthTextView;
    private TextView mEmailTextView;
    private TextView mLocationTextView;
    private TextView mTodayTimesTextView;
    private TextView mTotalTimesTextView;
    private TextView mTotalStatusTextView;

    private TabViewController tabViewController;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        User.initUserInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshUserDetails();
    }

    private void initView() {
        MeasureFragment bluetoothCommFragment = new MeasureFragment();
        MonitorFragment monitorFragment = new MonitorFragment();
        CalendarFragment chartFragment = new CalendarFragment();
        ProfileFragment profileFragment = new ProfileFragment();

        ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
        fragmentArrayList.add(bluetoothCommFragment);
        fragmentArrayList.add(monitorFragment);
        fragmentArrayList.add(chartFragment);
        fragmentArrayList.add(profileFragment);
        ArrayList<Integer> iconArrayList = new ArrayList<>();
        iconArrayList.add(R.mipmap.tab_measure_unselected);
        iconArrayList.add(R.mipmap.tab_monitor_unselected);
        iconArrayList.add(R.mipmap.tab_graph_unselected);
        iconArrayList.add(R.mipmap.tab_settings_unselected);
        iconArrayList.add(R.mipmap.tab_measure_selected);
        iconArrayList.add(R.mipmap.tab_monitor_selected);
        iconArrayList.add(R.mipmap.tab_graph_selected);
        iconArrayList.add(R.mipmap.tab_settings_selected);
        ArrayList<String> titles = new ArrayList<>();
        titles.add(getResources().getString(R.string.main_measure_title));
        titles.add(getResources().getString(R.string.main_monitor_title));
        titles.add(getResources().getString(R.string.main_data_title));
        titles.add(getResources().getString(R.string.main_profile_title));

        DrawerLayout mDrawerLayout = (DrawerLayout) View.inflate(this, R.layout.activity_main, null);

        tabViewController = new TabViewController(this, fragmentArrayList, iconArrayList, titles);
        tabViewController.setScrollable(true);

        LinearLayout contentLayout = new LinearLayout(this);
        contentLayout.setOrientation(LinearLayout.VERTICAL);
        contentLayout.setLayoutParams(
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT));

        mToolbar = (Toolbar) View.inflate(this, R.layout.layout_toolbar, null);
        mToolbar.setTitle(R.string.app_name);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        contentLayout.addView(mToolbar);
        contentLayout.addView(tabViewController);

        mDrawerLayout.addView(contentLayout);

        ActionBarDrawerToggle mDrawerToggle =
                new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.main_drawer_open, R.string.main_drawer_close) {
                    @Override
                    public void onDrawerClosed(View drawerView) {
                        super.onDrawerClosed(drawerView);
                    }

                    @Override
                    public void onDrawerOpened(View drawerView) {
                        super.onDrawerOpened(drawerView);
                    }
                };
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mAvatarImageView = (CircleImageView) mDrawerLayout.findViewById(R.id.user_detail_avatar);
        mUidTextView = (TextView) mDrawerLayout.findViewById(R.id.user_detail_uid);
        mNameTextView = (TextView) mDrawerLayout.findViewById(R.id.user_detail_name);
        mIntroTextView = (TextView) mDrawerLayout.findViewById(R.id.user_detail_intro);
        mAgeTextView = (TextView) mDrawerLayout.findViewById(R.id.user_detail_age);
        mSexTextView = (TextView) mDrawerLayout.findViewById(R.id.user_detail_sex);
        mBirthTextView = (TextView) mDrawerLayout.findViewById(R.id.user_detail_birth);
        mConstellationTextView = (TextView) mDrawerLayout.findViewById(R.id.user_detail_constellation);
        mEmailTextView = (TextView) mDrawerLayout.findViewById(R.id.user_detail_email);
        mLocationTextView = (TextView) mDrawerLayout.findViewById(R.id.user_detail_location);
        mTodayTimesTextView = (TextView) mDrawerLayout.findViewById(R.id.user_detail_today_times);
        mTotalTimesTextView = (TextView) mDrawerLayout.findViewById(R.id.user_detail_total_times);
        mTotalStatusTextView = (TextView) mDrawerLayout.findViewById(R.id.user_detail_health_assess);

        setContentView(mDrawerLayout);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void refreshUserDetails() {

        String home = getFilesDir().getAbsolutePath();
        File avatarFile = new File(home + Configurations.AVATAR_FILE_PATH);
        if (avatarFile.exists()) {
            try {
                InputStream is = new FileInputStream(avatarFile);
                mAvatarImageView.setImageBitmap(BitmapFactory.decodeStream(is));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            mAvatarImageView.setImageResource(R.drawable.default_avatar);
        }

        mTodayTimesTextView.setText(String.valueOf(User.todayMeasureTimes));
        mTotalTimesTextView.setText(String.valueOf(User.totalMeasureTimes));
        mTotalStatusTextView.setText(String.valueOf(User.totalMeasureAssessment));
        mUidTextView.setText(String.valueOf(User.uid));
        mNameTextView.setText(User.userName);
        mBirthTextView.setText(User.birthday);
        mSexTextView.setTag(User.sex);
        if (User.sex == User.UserSex.USER_MALE) mSexTextView.setText(getResources().getString(R.string.personal_sex_male));
        else if (User.sex == User.UserSex.USER_FEMALE) mSexTextView.setText(getResources().getString(R.string.personal_sex_female));
        else if (User.sex == User.UserSex.USER_ALIEN) mSexTextView.setText(getResources().getString(R.string.personal_sex_alien));
        mConstellationTextView.setTag(User.constellation);
        mConstellationTextView.setText(Constellation.getConstellationString(User.constellation));
        if (User.age >= 0)  mAgeTextView.setText(String.valueOf(User.age));
        mEmailTextView.setText(User.email);
        mLocationTextView.setText(User.address);
        mIntroTextView.setText(User.introduction);
    }

    public TabViewController getTabViewController() {
        return tabViewController;
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return tabViewController.isTabbarVisible() && super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        return tabViewController.isTabbarVisible() && super.onKeyLongPress(keyCode, event);
    }
}
