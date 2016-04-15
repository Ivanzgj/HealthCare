package com.ivan.healthcare.healthcare_android.ui;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.ivan.healthcare.healthcare_android.AppContext;
import com.ivan.healthcare.healthcare_android.R;
import com.ivan.healthcare.healthcare_android.customobj.Time;
import com.ivan.healthcare.healthcare_android.local.Alarm;
import com.ivan.healthcare.healthcare_android.util.Compat;
import com.ivan.healthcare.healthcare_android.view.AlarmView;
import java.util.ArrayList;

/**
 * 闹钟设置页面
 * Created by Ivan on 16/4/9.
 */
public class AlarmActivity extends AppCompatActivity {

    private static final int ADD_ALARM_ITEM_ID = 0x31;

    private CoordinatorLayout mCoordinatorLayout;
    private BaseAdapter mAlarmAdapter;
    private ArrayList<Time> mAlarmArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, ADD_ALARM_ITEM_ID, 0, R.string.alarm_add)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case ADD_ALARM_ITEM_ID:
                addAlarm();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {

        refreshAlarms();

        View rootView = View.inflate(this, R.layout.activity_alarm, null);

        mCoordinatorLayout = (CoordinatorLayout) rootView.findViewById(R.id.alarm_CoordinatorLayout);
        Toolbar mToolbar = (Toolbar) rootView.findViewById(R.id.alarm_toolbar);
        mToolbar.setTitle(R.string.alarm_title);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ListView mListView = (ListView) rootView.findViewById(R.id.alarm_listView);
        mAlarmAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return mAlarmArrayList.size();
            }

            @Override
            public Object getItem(int position) {
                return mAlarmArrayList.get(position);
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                AlarmView alarmView;
                if (convertView != null) {
                    alarmView = (AlarmView) convertView;
                } else {
                    alarmView = new AlarmView(AlarmActivity.this);
                    alarmView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, AppContext.dp2px(60)));
                }
                alarmView.setTime(mAlarmArrayList.get(position));

                final int pos = position;
                alarmView.setOnAlarmSwitchListener(new AlarmView.OnAlarmSwitchListener() {
                    @Override
                    public void onSwitch(Time time, boolean on) {
                        switchAlarm(pos, on);
                    }
                });
                alarmView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        changeAlarm(pos);
                    }
                });
                return alarmView;
            }
        };
        mListView.setAdapter(mAlarmAdapter);

        setContentView(rootView);
    }

    private void refreshAlarms() {
        mAlarmArrayList = Alarm.readAlarms(true);
    }

    private void addAlarm() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Time alarm = new Time(0, 0, Alarm.getMaxAlarmId(false) + 1, true);
                alarm.setHour(hourOfDay);
                alarm.setMinute(minute);
                if (Alarm.addAlarm(alarm)) {
                    mAlarmAdapter.notifyDataSetChanged();
                    Snackbar.make(mCoordinatorLayout, alarm + "", Snackbar.LENGTH_SHORT).show();
                }
            }
        }, 8, 0, true);
        timePickerDialog.show();
        Compat.fixDialogStyle(timePickerDialog);
    }

    private void changeAlarm(int position) {
        final Time time = mAlarmArrayList.get(position);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                time.setHour(hourOfDay);
                time.setMinute(minute);
                time.setOn(true);
                Alarm.updateAlarm(time);
                mAlarmAdapter.notifyDataSetChanged();
                Snackbar.make(mCoordinatorLayout, time + "", Snackbar.LENGTH_SHORT).show();
            }
        }, time.getHour(), time.getMinute(), true);
        timePickerDialog.show();
        Compat.fixDialogStyle(timePickerDialog);
    }

    private void switchAlarm(int position, boolean isOn) {
        Time time = mAlarmArrayList.get(position);
        time.setOn(isOn);
        Alarm.updateAlarm(time);
        Toast.makeText(this, time.toString(), Toast.LENGTH_SHORT).show();
    }

    private void deleteAlarm(int position) {
        if (Alarm.deleteAlarm(mAlarmArrayList.get(position))) {
            mAlarmAdapter.notifyDataSetChanged();
        }
    }
}
