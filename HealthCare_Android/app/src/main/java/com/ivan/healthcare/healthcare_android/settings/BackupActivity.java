package com.ivan.healthcare.healthcare_android.settings;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import com.andexert.library.RippleView;
import com.ivan.healthcare.healthcare_android.R;
import com.ivan.healthcare.healthcare_android.ui.BaseActivity;

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
            Snackbar.make(rootView, "upload", Snackbar.LENGTH_SHORT).show();
        } else if (mSyncView.equals(rippleView)) {
            Snackbar.make(rootView, "sync", Snackbar.LENGTH_SHORT).show();
        } else if (mClearView.equals(rippleView)) {
            Snackbar.make(rootView, "clear", Snackbar.LENGTH_SHORT).show();
        }
    }
}
