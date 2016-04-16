package com.ivan.healthcare.healthcare_android.settings;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.ivan.healthcare.healthcare_android.AppContext;
import com.ivan.healthcare.healthcare_android.Configurations;
import com.ivan.healthcare.healthcare_android.R;
import com.ivan.healthcare.healthcare_android.local.Constellation;
import com.ivan.healthcare.healthcare_android.local.User;
import com.ivan.healthcare.healthcare_android.ui.BaseActivity;
import com.ivan.healthcare.healthcare_android.util.Compat;
import com.ivan.healthcare.healthcare_android.util.DialogBuilder;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 个人资料页面
 * Created by Ivan on 16/4/7.
 */
public class PersonalInfoActivity extends BaseActivity implements View.OnClickListener, AppBarLayout.OnOffsetChangedListener, SwipeRefreshLayout.OnRefreshListener {

    private static final int REQUEST_GALLERY_PICK = 0x31;
    private static final int REQUEST_IMAGE_CROP = 0x32;

    private View rootView;
    private AppBarLayout mAppbar;
    private CollapsingToolbarLayout mToolbarLayout;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private CircleImageView mAvatarImageView;
    private TextView mTodayTimesTextView;
    private TextView mTotalTimesTextView;
    private TextView mAssessTextView;
    private TextView mUidTextView;
    private EditText mNameEdit;
    private TextView mSexTextView;
    private TextView mBirthTextView;
    private TextView mAgeTextView;
    private TextView mConstellationTextView;
    private EditText mEmailEdit;
    private EditText mLocationEdit;
    private EditText mIntroEdit;
    private TextView mLogoutView;
    private TextView mChangePwdView;
    private View clickMask;
    private RelativeLayout mLogoutRel;

    private MenuItem uploadMenuItem;
    private final static int UPLOAD_MENU_ITEM_ID = 0x01;

    private View currentFocusedView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        refreshContents();
    }


    @Override
    protected void onResume() {
        super.onResume();
        mAppbar.addOnOffsetChangedListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAppbar.removeOnOffsetChangedListener(this);
    }

    private void initView() {

        rootView = View.inflate(this, R.layout.activity_personalinfo, null);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.personal_PtrFrameLayout);

        Toolbar mToolbar = (Toolbar) mSwipeRefreshLayout.findViewById(R.id.personal_toolbar);
        mToolbarLayout = (CollapsingToolbarLayout) mSwipeRefreshLayout.findViewById(R.id.personal_collapsing_toolbar);
        mToolbarLayout.setTitle("Ivan");
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mAppbar = (AppBarLayout) mSwipeRefreshLayout.findViewById(R.id.personal_appbar);

        mSwipeRefreshLayout.setColorSchemeResources(R.color.red,
                R.color.green,
                R.color.blue);
        mSwipeRefreshLayout.setSize(SwipeRefreshLayout.LARGE);
        mSwipeRefreshLayout.setProgressBackgroundColorSchemeColor(Compat.getColor(this, R.color.default_main_color));
        mSwipeRefreshLayout.setDistanceToTriggerSync(AppContext.dp2px(35));
        mSwipeRefreshLayout.setProgressViewEndTarget(true, AppContext.dp2px(100));
        mSwipeRefreshLayout.setOnRefreshListener(this);

        View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    PersonalInfoActivity.this.onClick(v);
                }
            }
        };

        mAvatarImageView = (CircleImageView) mSwipeRefreshLayout.findViewById(R.id.personal_info_avatar_imageview);
        mAvatarImageView.setOnClickListener(this);
        mTodayTimesTextView = (TextView) mSwipeRefreshLayout.findViewById(R.id.personal_info_today_times);
        mTotalTimesTextView = (TextView) mSwipeRefreshLayout.findViewById(R.id.personal_info_total_times);
        mAssessTextView = (TextView) mSwipeRefreshLayout.findViewById(R.id.personal_info_health_assess);
        mUidTextView = (TextView) mSwipeRefreshLayout.findViewById(R.id.personal_info_uid_edit_text);
        mUidTextView.setOnFocusChangeListener(onFocusChangeListener);
        mNameEdit = (EditText) mSwipeRefreshLayout.findViewById(R.id.personal_info_name_edit_text);
        mNameEdit.setOnClickListener(this);
        mNameEdit.setOnFocusChangeListener(onFocusChangeListener);
        mSexTextView = (TextView) mSwipeRefreshLayout.findViewById(R.id.personal_info_sex_tv);
        mSexTextView.setOnClickListener(this);
        mBirthTextView = (TextView) mSwipeRefreshLayout.findViewById(R.id.personal_info_birth_tv);
        mBirthTextView.setOnClickListener(this);
        mAgeTextView = (TextView) mSwipeRefreshLayout.findViewById(R.id.personal_info_age_tv);
        mConstellationTextView = (TextView) mSwipeRefreshLayout.findViewById(R.id.personal_info_constellation_tv);
        mEmailEdit = (EditText) mSwipeRefreshLayout.findViewById(R.id.personal_email_edit_text);
        mEmailEdit.setOnClickListener(this);
        mEmailEdit.setOnFocusChangeListener(onFocusChangeListener);
        mLocationEdit = (EditText) mSwipeRefreshLayout.findViewById(R.id.personal_info_location_edit_text);
        mLocationEdit.setOnClickListener(this);
        mLocationEdit.setOnFocusChangeListener(onFocusChangeListener);
        mIntroEdit = (EditText) mSwipeRefreshLayout.findViewById(R.id.personal_info_intro_edit_text);
        mIntroEdit.setOnClickListener(this);
        mIntroEdit.setOnFocusChangeListener(onFocusChangeListener);
        mLogoutView = (TextView) mSwipeRefreshLayout.findViewById(R.id.personal_logout_tv);
        mLogoutView.setOnClickListener(this);
        mChangePwdView = (TextView) mSwipeRefreshLayout.findViewById(R.id.personal_change_pwd_tv);
        mChangePwdView.setOnClickListener(this);

        clickMask = mSwipeRefreshLayout.findViewById(R.id.personal_click_mask);
        clickMask.setOnClickListener(this);
        clickMask.setVisibility(View.INVISIBLE);

        mLogoutRel = (RelativeLayout) mSwipeRefreshLayout.findViewById(R.id.personal_info_logout_rel);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        setContentView(rootView);
    }

    private void refreshContents() {

        mToolbarLayout.setTitle(User.userName);

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
        mAssessTextView.setText(String.valueOf(User.totalMeasureAssessment));
        mUidTextView.setText(String.valueOf(User.uid));
        mNameEdit.setText(User.userName);
        mBirthTextView.setText(User.birthday);
        mSexTextView.setTag(User.sex);
        if (User.sex == User.UserSex.USER_MALE) mSexTextView.setText(getResources().getString(R.string.personal_sex_male));
        else if (User.sex == User.UserSex.USER_FEMALE) mSexTextView.setText(getResources().getString(R.string.personal_sex_female));
        else if (User.sex == User.UserSex.USER_ALIEN) mSexTextView.setText(getResources().getString(R.string.personal_sex_alien));
        mConstellationTextView.setTag(User.constellation);
        mConstellationTextView.setText(Constellation.getConstellationString(User.constellation));
        if (User.age >= 0)  mAgeTextView.setText(String.valueOf(User.age));
        mEmailEdit.setText(User.email);
        mLocationEdit.setText(User.address);
        mIntroEdit.setText(User.introduction);

        if (User.uid == -1) {
            mLogoutRel.setVisibility(View.GONE);
        } else {
            mLogoutRel.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        uploadMenuItem = menu.add(0, UPLOAD_MENU_ITEM_ID, 0, R.string.personal_save);
//        uploadMenuItem.setEnabled(false);
        uploadMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case UPLOAD_MENU_ITEM_ID:
                upload();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (currentFocusedView != null) {

            clickMask.setVisibility(View.INVISIBLE);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(currentFocusedView.getWindowToken(), 0);
            currentFocusedView = null;

        } else if (mAvatarImageView.equals(v)) {

            Intent intent = new Intent(Intent.ACTION_PICK ,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_GALLERY_PICK);

        } else if (mSexTextView.equals(v)) {

            final String[] array = getResources().getStringArray(R.array.sex_array);
            ListView listView = new ListView(this);
            listView.setAdapter(new ArrayAdapter<>(this, R.layout.layout_simple_list_item, array));
            final Dialog dialog = new DialogBuilder(this).create()
                                        .setCustomView(listView)
                                        .setPositive(null)
                                        .show();
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    mSexTextView.setText(array[position]);
                    if (position == 0)          mSexTextView.setTag(User.UserSex.USER_MALE);
                    else if (position == 1)     mSexTextView.setTag(User.UserSex.USER_FEMALE);
                    else if (position == 2)     mSexTextView.setTag(User.UserSex.USER_ALIEN);
                    dialog.dismiss();
                }
            });

        } else if (mBirthTextView.equals(v)) {

            final Calendar cal = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                    String date = year+"-"+(monthOfYear+1)+"-"+dayOfMonth;
                    mBirthTextView.setText(date);
                    int age = cal.get(Calendar.YEAR) - year;
                    if (cal.get(Calendar.MONTH) < monthOfYear) {
                        age -= 1;
                    } else if (cal.get(Calendar.MONTH) == monthOfYear) {
                        if (cal.get(Calendar.DAY_OF_MONTH) < dayOfMonth) {
                            age -= 1;
                        }
                    }
                    mAgeTextView.setText(String.valueOf(age));
                    Constellation.ConstellationEnum constellation = Constellation.getConstellation(monthOfYear + 1, dayOfMonth);
                    mConstellationTextView.setTag(constellation);
                    mConstellationTextView.setText(Constellation.getConstellationString(constellation));

                }
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
            Compat.fixDialogStyle(datePickerDialog);

        } else if (mLogoutView.equals(v)) {
            logout();

        } else if (mChangePwdView.equals(v)) {
            changePwd();

        } else if (v instanceof EditText) {

            clickMask.setVisibility(View.VISIBLE);
            currentFocusedView = v;
        }
    }

    private void upload() {
        User.edit()
                .setUserName(mNameEdit.getText().toString())
                .setAge(mAgeTextView.getText().length()==0?0:Integer.valueOf(mAgeTextView.getText().toString()))
                .setBirthday(mBirthTextView.getText().toString())
                .setSex((User.UserSex) mSexTextView.getTag())
                .setConstellation((Constellation.ConstellationEnum) mConstellationTextView.getTag())
                .setEmail(mEmailEdit.getText().toString())
                .setAddress(mLocationEdit.getText().toString())
                .setIntroduction(mIntroEdit.getText().toString())
                .commit();
    }

    private void logout() {

    }

    private void changePwd() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GALLERY_PICK) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Intent intent = new Intent();
                    intent.setClass(this, CropImageActivity.class);
                    intent.setData(data.getData());
                    startActivityForResult(intent, REQUEST_IMAGE_CROP);
                }
            }
        } else if (requestCode == REQUEST_IMAGE_CROP) {
            if (resultCode == RESULT_OK) {
                Bitmap bm = data.getParcelableExtra(CropImageActivity.CROPPED_BITMAP);
                mAvatarImageView.setImageBitmap(bm);
            }
        }
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        mSwipeRefreshLayout.setEnabled(verticalOffset == 0);
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 5000);
    }

}
