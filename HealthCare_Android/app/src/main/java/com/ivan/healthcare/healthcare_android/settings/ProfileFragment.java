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
import android.widget.TextView;
import com.andexert.library.RippleView;
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
public class ProfileFragment extends Fragment implements RippleView.OnRippleCompleteListener {

    private RippleView mPersonCell;
    private RippleView mTimerCell;
    private RippleView mSyncCell;
    private RippleView mResetCell;
    private RippleView mLoginCell;

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
        mPersonCell = (RippleView) rootView.findViewById(R.id.profile_person_rel);
        mTimerCell = (RippleView) rootView.findViewById(R.id.profile_timer_rel);
        mSyncCell = (RippleView) rootView.findViewById(R.id.profile_upload_rel);
        mResetCell = (RippleView) rootView.findViewById(R.id.profile_reset_rel);
        mLoginCell = (RippleView) rootView.findViewById(R.id.profile_login_rel);
        mAvatarImageView = (ImageView) rootView.findViewById(R.id.profile_avatar_imageview);
        mUserNameTextView = (TextView) rootView.findViewById(R.id.profile_person_name);

        mPersonCell.setOnRippleCompleteListener(this);
        mTimerCell.setOnRippleCompleteListener(this);
        mSyncCell.setOnRippleCompleteListener(this);
        mResetCell.setOnRippleCompleteListener(this);
        mLoginCell.setOnRippleCompleteListener(this);
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
            User.edit().setUid(10001).setUserName("User_10001").commit();
            refreshContents();
        }
    }
}
