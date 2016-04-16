package com.ivan.healthcare.healthcare_android.settings;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.ivan.healthcare.healthcare_android.Configurations;
import com.ivan.healthcare.healthcare_android.R;
import com.ivan.healthcare.healthcare_android.local.User;
import com.ivan.healthcare.healthcare_android.util.DialogBuilder;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 个人中心
 * Created by Ivan on 16/2/3.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener {

    private RelativeLayout mPersonCell;
    private RelativeLayout mTimerCell;
    private RelativeLayout mSyncCell;
    private TextView mResetCell;
    private TextView mLoginCell;

    private ImageView mAvatarImageView;
    private TextView mUserNameTextView;

    private View rootView;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        initView(rootView);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshContents();
    }

    private void initView(View rootView) {
        mPersonCell = (RelativeLayout) rootView.findViewById(R.id.profile_person_rel);
        mTimerCell = (RelativeLayout) rootView.findViewById(R.id.profile_timer_rel);
        mSyncCell = (RelativeLayout) rootView.findViewById(R.id.profile_upload_rel);
        mResetCell = (TextView) rootView.findViewById(R.id.profile_reset_rel);
        mLoginCell = (TextView) rootView.findViewById(R.id.profile_login_rel);
        mAvatarImageView = (ImageView) rootView.findViewById(R.id.profile_avatar_imageview);
        mUserNameTextView = (TextView) rootView.findViewById(R.id.profile_person_name);

        mPersonCell.setOnClickListener(this);
        mTimerCell.setOnClickListener(this);
        mSyncCell.setOnClickListener(this);
        mResetCell.setOnClickListener(this);
        mLoginCell.setOnClickListener(this);
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
            mLoginCell.setVisibility(View.GONE);
        } else {
            mLoginCell.setVisibility(View.GONE);
        }
    }

    private void jumpToPersonal() {
        Intent intent = new Intent();
        intent.setClass(getActivity(), PersonalInfoActivity.class);
        startActivity(intent);
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
                        Snackbar.make(rootView, "reset data", Snackbar.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                })
                .setNegative(R.string.cancel)
                .setOnNegaitiveClickListener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @Override
    public void onClick(View v) {
        if (mPersonCell.equals(v)) {
            jumpToPersonal();
        } else if (mTimerCell.equals(v)) {
            jumpToTimer();
        } else if (mSyncCell.equals(v)) {
            jumpToSync();
        } else if (mResetCell.equals(v)) {
            reset();
        } else if (mLoginCell.equals(v)) {
            User.edit().setUid(10001).setUserName("User_10001").commit();
            refreshContents();
        }
    }
}
