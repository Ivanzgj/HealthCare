package com.ivan.healthcare.healthcare_android.view;

import android.content.Context;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.andexert.library.RippleView;
import com.ivan.healthcare.healthcare_android.R;
import com.ivan.healthcare.healthcare_android.customobj.Time;

/**
 * 闹钟时间以及开启的view
 * Created by Ivan on 16/4/9.
 */
public class AlarmView extends RelativeLayout {

    private Time mTime;
    private TextView mTimeTextView;
    private SwitchCompat mSwitch;
    private RippleView mRippleView;

    public AlarmView(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        View rootView = View.inflate(getContext(), R.layout.layout_alarm_view, this);
        mTimeTextView = (TextView) rootView.findViewById(R.id.alarm_time_textview);
        mSwitch = (SwitchCompat) rootView.findViewById(R.id.alarm_switch);
        mRippleView = (RippleView) rootView.findViewById(R.id.alarm_ripple_view);
    }

    public Time getTime() {
        return mTime;
    }

    public void setTime(Time time) {
        this.mTime = time;
        mTimeTextView.setText(time.toString());
        mSwitch.setChecked(time.isOn());
    }

    public void setSwitch(boolean on) {
        mSwitch.setChecked(on);
    }

    public boolean getSwitch() {
        return mSwitch.isChecked();
    }

    @Override
    public void setOnClickListener(final OnClickListener l) {
        mRippleView.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                l.onClick(rippleView);
            }
        });
    }
}
