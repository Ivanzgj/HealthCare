package com.ivan.healthcare.healthcare_android.settings;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import com.andexert.library.RippleView;
import com.ivan.healthcare.healthcare_android.Configurations;
import com.ivan.healthcare.healthcare_android.R;
import com.ivan.healthcare.healthcare_android.database.DataAccess;
import com.ivan.healthcare.healthcare_android.network.AbsBaseRequest;
import com.ivan.healthcare.healthcare_android.network.BaseStringRequest;
import com.ivan.healthcare.healthcare_android.ui.BaseActivity;
import com.ivan.healthcare.healthcare_android.util.DialogBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * 数据上传和同步的页面
 * Created by Ivan on 16/4/11.
 */
public class BackupActivity extends BaseActivity implements RippleView.OnRippleCompleteListener {

    private View rootView;
    private Toolbar mToolBar;
    private RippleView mUploadView;
    private RippleView mSyncView;
    private RippleView mClearView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = View.inflate(this, R.layout.activity_backup, null);
        setContentView(rootView);
        initView();
    }

    private void initView() {
        mToolBar = (Toolbar) findViewById(R.id.backup_toolbar);
        mUploadView = (RippleView) findViewById(R.id.backup_backup_item);
        mSyncView = (RippleView) findViewById(R.id.backup_sync_item);
        mClearView = (RippleView) findViewById(R.id.backup_clear_item);

        mUploadView.setOnRippleCompleteListener(this);
        mSyncView.setOnRippleCompleteListener(this);
        mClearView.setOnRippleCompleteListener(this);

        mToolBar.setTitle(R.string.backup_title);
        setSupportActionBar(mToolBar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onComplete(RippleView rippleView) {
        if (mUploadView.equals(rippleView)) {
            upload();
        } else if (mSyncView.equals(rippleView)) {
            Snackbar.make(rootView, "sync", Snackbar.LENGTH_SHORT).show();
        } else if (mClearView.equals(rippleView)) {
            Snackbar.make(rootView, "clear", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void upload() {
        final ProgressDialog dialog = new DialogBuilder(this)
                .createProgress(R.string.login_dialog_header_register,
                        getResources().getString(R.string.backup_ing_message),
                        false);
        dialog.show();

        ArrayList<String> timeList = DataAccess.getHistoryMonitorVibrationTime();
        final ArrayList<Map<String, String>> uploadList = new ArrayList<>();

        for (String time : timeList) {
            ArrayList<Float> accData = DataAccess.getVibrationData(time);
            ArrayList<DataAccess.SrcDataUnit> srcData = DataAccess.getSrcData(time);
            StringBuilder accString = new StringBuilder();
            StringBuilder srcTimeString = new StringBuilder();
            StringBuilder srcString = new StringBuilder();
            for (float data : accData) {
                accString.append(data+"|");
            }
            accString.deleteCharAt(accString.length() - 1);
            for (DataAccess.SrcDataUnit unit : srcData) {
                srcTimeString.append(unit.recTime+"|");
                srcString.append(unit.srcOn+"|");
            }
            srcTimeString.deleteCharAt(srcTimeString.length() - 1);
            srcString.deleteCharAt(srcString.length() - 1);

            HashMap<String, String> map = new HashMap<>();
            map.put("time", time);
            map.put("acc_data", accString.toString());
            map.put("src_time", srcTimeString.toString());
            map.put("src_status", srcString.toString());
            uploadList.add(map);
        }

        new BaseStringRequest.Builder()
                .url(Configurations.SYNC_URL)
                .add("action", "upload")
                .add("data", uploadList)
                .build()
                .post(new AbsBaseRequest.Callback() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        Snackbar.make(rootView, R.string.upload_success_message, Snackbar.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int errorFlag, String error) {
                        dialog.dismiss();
                        Snackbar.make(rootView, R.string.upload_fail_message, Snackbar.LENGTH_SHORT).show();
                    }
                });
    }

}
