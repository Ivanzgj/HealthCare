package com.ivan.healthcare.healthcare_android.settings;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.andexert.library.RippleView;
import com.ivan.healthcare.healthcare_android.AppContext;
import com.ivan.healthcare.healthcare_android.Configurations;
import com.ivan.healthcare.healthcare_android.R;
import com.ivan.healthcare.healthcare_android.database.DataAccess;
import com.ivan.healthcare.healthcare_android.local.Preference;
import com.ivan.healthcare.healthcare_android.local.User;
import com.ivan.healthcare.healthcare_android.network.BaseStringRequest;
import com.ivan.healthcare.healthcare_android.network.bean.UserInfoBean;
import com.ivan.healthcare.healthcare_android.settings.dialog.LoginDialog;
import com.ivan.healthcare.healthcare_android.util.DialogBuilder;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 个人中心
 * Created by Ivan on 16/2/3.
 */
public class ProfileFragment extends Fragment implements RippleView.OnRippleCompleteListener {

    private final int REQUEST_PERSONAL_ACTIVITY = 0x31;

    private final int BLOOD_MODE_AUTO = 0x31;
    private final int BLOOD_MODE_CUSTOM = 0x32;
    public static final int MONITOR_MODE_AUTO = 0x33;
    public static final int MONITOR_MODE_CUSTOM = 0x34;
    public static final int MONITOR_CUSTOM_MODE_DEFAULT_SPEED  = 500;

    private RippleView mPersonCell;
    private RippleView mTimerCell;
    private RippleView mSyncCell;
    private RippleView mResetCell;
    private RippleView mLoginCell;
    private RippleView mBloodModeCell;
    private RippleView mMonitorModeCell;
    private RippleView mMonitorSpeedCell;

    private ImageView mAvatarImageView;
    private TextView mUserNameTextView;
    private TextView mBloodModeTextView;
    private TextView mMonitorModeTextView;
    private TextView mMonitorSpeedTextView;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        initView(rootView);
        refreshContents();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initView(View rootView) {
        mPersonCell = (RippleView) rootView.findViewById(R.id.profile_person_rel);
        mTimerCell = (RippleView) rootView.findViewById(R.id.profile_timer_rel);
        mSyncCell = (RippleView) rootView.findViewById(R.id.profile_upload_rel);
        mResetCell = (RippleView) rootView.findViewById(R.id.profile_reset_rel);
        mLoginCell = (RippleView) rootView.findViewById(R.id.profile_login_rel);
        mBloodModeCell = (RippleView) rootView.findViewById(R.id.profile_blood_mode_rel);
        mMonitorModeCell = (RippleView) rootView.findViewById(R.id.profile_monitor_mode_rel);
        mMonitorSpeedCell = (RippleView) rootView.findViewById(R.id.profile_monitor_gap_rel);
        mAvatarImageView = (ImageView) rootView.findViewById(R.id.profile_avatar_imageview);
        mUserNameTextView = (TextView) rootView.findViewById(R.id.profile_person_name);
        mBloodModeTextView = (TextView) rootView.findViewById(R.id.profile_blood_mode_textview);
        mMonitorModeTextView = (TextView) rootView.findViewById(R.id.profile_monitor_mode_textview);
        mMonitorSpeedTextView = (TextView) rootView.findViewById(R.id.profile_monitor_gap_textview);

        mPersonCell.setOnRippleCompleteListener(this);
        mTimerCell.setOnRippleCompleteListener(this);
        mSyncCell.setOnRippleCompleteListener(this);
        mResetCell.setOnRippleCompleteListener(this);
        mLoginCell.setOnRippleCompleteListener(this);
        mBloodModeCell.setOnRippleCompleteListener(this);
        mMonitorModeCell.setOnRippleCompleteListener(this);
        mMonitorSpeedCell.setOnRippleCompleteListener(this);

        int bloodMode = AppContext.getPreference().getInt(Preference.BLOOD_MODE, BLOOD_MODE_AUTO);
        if (bloodMode == BLOOD_MODE_AUTO) {
            mBloodModeTextView.setText(R.string.profile_blood_mode_auto);
        } else if (bloodMode == BLOOD_MODE_CUSTOM) {
            mBloodModeTextView.setText(R.string.profile_blood_mode_custom);
        }

        int monitorMode = AppContext.getPreference().getInt(Preference.MONITOR_MODE, MONITOR_MODE_AUTO);
        if (monitorMode == MONITOR_MODE_AUTO) {
            mMonitorModeTextView.setText(R.string.profile_monitor_mode_auto);
            mMonitorSpeedTextView.setText("");
            mMonitorSpeedCell.setEnabled(false);
            return;
        } else if (monitorMode == MONITOR_MODE_CUSTOM) {
            mMonitorModeTextView.setText(R.string.profile_monitor_mode_custom);
            mMonitorSpeedCell.setEnabled(true);
        }

        int monitorSpeed = AppContext.getPreference().getInt(Preference.MONITOR_SPEED, MONITOR_CUSTOM_MODE_DEFAULT_SPEED);
        mMonitorSpeedTextView.setText(String.format(getResources().getString(R.string.profile_monitor_auto_speed), monitorSpeed));
    }

    private void refreshContents() {

        mUserNameTextView.setText(User.userName);

        String home = getActivity().getFilesDir().getAbsolutePath();
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

        if (User.uid == -1) {
            mLoginCell.setVisibility(View.VISIBLE);
        } else {
            mLoginCell.setVisibility(View.GONE);
        }
    }

    private void jumpToPersonal() {
        Intent intent = new Intent();
        intent.setClass(getActivity(), PersonalInfoActivity.class);
        startActivityForResult(intent, REQUEST_PERSONAL_ACTIVITY);
    }

    private void jumpToTimer() {
        Intent intent = new Intent();
        intent.setClass(getActivity(), AlarmActivity.class);
        startActivity(intent);
    }

    private void jumpToSync() {
        Intent intent = new Intent();
        intent.setClass(getActivity(), BackupActivity.class);
        startActivity(intent);
    }

    private void reset() {
        new DialogBuilder(getActivity()).create()
                .setTitle(R.string.tips)
                .setContent(R.string.personal_reset_message)
                .setPositive(R.string.ok)
                .setOnPositiveClickListener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DataAccess.clearVibrationTable();
                        DataAccess.clearSrcTable();
                        dialog.dismiss();
                        refreshContents();
                    }
                })
                .setNegative(R.string.cancel)
                .show();
    }

    @Override
    public void onComplete(RippleView rippleView) {
        if (mPersonCell.equals(rippleView)) {

            jumpToPersonal();

        } else if (mTimerCell.equals(rippleView)) {

            jumpToTimer();

        } else if (mSyncCell.equals(rippleView)) {

            jumpToSync();

        } else if (mResetCell.equals(rippleView)) {

            reset();

        } else if (mLoginCell.equals(rippleView)) {

            login();

        } else if (mBloodModeCell.equals(rippleView)) {

            setBloodMode();

        } else if (mMonitorModeCell.equals(rippleView)) {

            setMonitorMode();

        } else if (mMonitorSpeedCell.equals(rippleView)) {

            setMonitorSpeed();

        }
    }

    private void login() {
        LoginDialog loginDialog = new LoginDialog(getActivity(), true);
        loginDialog.setOnLoginRegisterCompleteListener(new LoginDialog.OnLoginRegisterCompleteListener() {
            @Override
            public void onLoginRegisterComplete(boolean isLogin, UserInfoBean bean) {
                retrieveAvatar(bean.getAvatar());
            }

            @Override
            public void onFail(boolean isLogin, int errorFlag) {

            }
        });
        loginDialog.show();
    }

    private void retrieveAvatar(String url) {
        if (url == null || url.length() == 0) {
            return;
        }
        new BaseStringRequest.Builder()
                .url(url)
                .build()
                .input(new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                refreshContents();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        final InputStream is = response.body().byteStream();
                        String home = getActivity().getFilesDir().getAbsolutePath();
                        File avatarFile = new File(home + Configurations.AVATAR_FILE_PATH);
                        if (!avatarFile.exists()) {
                            if (!avatarFile.createNewFile()) {
                                return;
                            }
                        }
                        Bitmap bm = BitmapFactory.decodeStream(is);
                        BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(avatarFile));
                        bm.compress(Bitmap.CompressFormat.PNG, 100, os);  //图片存成png格式。
                        os.close();
                        is.close();
                        bm.recycle();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                refreshContents();
                            }
                        });
                    }
                });
    }

    private void setBloodMode() {
        ListView lv = new ListView(getActivity());
        String[] array = getResources().getStringArray(R.array.blood_mode_array);
        lv.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.layout_simple_list_item, array));
        final Dialog d = new DialogBuilder(getActivity()).create()
                .setCustomView(lv)
                .show();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AppContext.getPreference().editor()
                        .putInt(Preference.BLOOD_MODE, position==0?BLOOD_MODE_AUTO:BLOOD_MODE_CUSTOM)
                        .commit();
                if (position == 0) {
                    mBloodModeTextView.setText(R.string.profile_blood_mode_auto);
                } else {
                    mBloodModeTextView.setText(R.string.profile_blood_mode_custom);
                }
                d.dismiss();
            }
        });
    }

    private void setMonitorMode() {
        ListView lv = new ListView(getActivity());
        String[] array = getResources().getStringArray(R.array.monitor_mode_array);
        lv.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.layout_simple_list_item, array));
        final Dialog d = new DialogBuilder(getActivity()).create()
                .setCustomView(lv)
                .show();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AppContext.getPreference().editor()
                        .putInt(Preference.MONITOR_MODE, position==0?MONITOR_MODE_AUTO:MONITOR_MODE_CUSTOM)
                        .commit();
                if (position == 0) {
                    mMonitorModeTextView.setText(R.string.profile_monitor_mode_auto);
                    mMonitorSpeedTextView.setText("");
                    mMonitorSpeedCell.setEnabled(false);
                } else {
                    mMonitorModeTextView.setText(R.string.profile_monitor_mode_custom);
                    int monitorSpeed = AppContext.getPreference().getInt(Preference.MONITOR_SPEED, MONITOR_CUSTOM_MODE_DEFAULT_SPEED);
                    mMonitorSpeedTextView.setText(String.format(getResources().getString(R.string.profile_monitor_auto_speed), monitorSpeed));
                    mMonitorSpeedCell.setEnabled(true);
                }
                d.dismiss();
            }
        });
    }

    private void setMonitorSpeed() {
        ListView lv = new ListView(getActivity());
        String[] array = getResources().getStringArray(R.array.monitor_speed_array);
        final int[] speeds = getResources().getIntArray(R.array.monitor_speed_int_array);
        lv.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.layout_simple_list_item, array));
        final Dialog d = new DialogBuilder(getActivity()).create()
                .setCustomView(lv)
                .show();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AppContext.getPreference().editor()
                        .putInt(Preference.MONITOR_SPEED, speeds[position])
                        .commit();
                mMonitorSpeedTextView.setText(String.format(getResources().getString(R.string.profile_monitor_auto_speed), speeds[position]));
                d.dismiss();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PERSONAL_ACTIVITY) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                refreshContents();
            }
        }
    }
}
