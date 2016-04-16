package com.ivan.healthcare.healthcare_android.settings;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.ivan.healthcare.healthcare_android.R;
import com.ivan.healthcare.healthcare_android.ui.BaseActivity;

/**
 * 数据上传和同步的页面
 * Created by Ivan on 16/4/11.
 */
public class BackupActivity extends BaseActivity implements View.OnClickListener {

    private View rootView;
    private TextView mUploadView;
    private TextView mSyncView;
    private TextView mClearView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = View.inflate(this, R.layout.activity_backup, null);
        setContentView(rootView);
        initView();
    }

    private void initView() {
        Toolbar mToolBar = (Toolbar) findViewById(R.id.backup_toolbar);
        mUploadView = (TextView) findViewById(R.id.backup_backup_item);
        mSyncView = (TextView) findViewById(R.id.backup_sync_item);
        mClearView = (TextView) findViewById(R.id.backup_clear_item);

        mUploadView.setOnClickListener(this);
        mSyncView.setOnClickListener(this);
        mClearView.setOnClickListener(this);

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
    public void onClick(View v) {
        if (mUploadView.equals(v)) {
            Snackbar.make(rootView, "upload", Snackbar.LENGTH_SHORT).show();
        } else if (mSyncView.equals(v)) {
            Snackbar.make(rootView, "sync", Snackbar.LENGTH_SHORT).show();
        } else if (mClearView.equals(v)) {
            Snackbar.make(rootView, "clear", Snackbar.LENGTH_SHORT).show();
        }
    }
}
