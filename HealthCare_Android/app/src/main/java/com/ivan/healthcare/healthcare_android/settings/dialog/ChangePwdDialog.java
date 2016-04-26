package com.ivan.healthcare.healthcare_android.settings.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import com.ivan.healthcare.healthcare_android.Configurations;
import com.ivan.healthcare.healthcare_android.R;
import com.ivan.healthcare.healthcare_android.local.User;
import com.ivan.healthcare.healthcare_android.network.AbsBaseRequest;
import com.ivan.healthcare.healthcare_android.network.BaseStringRequest;
import com.ivan.healthcare.healthcare_android.util.DialogBuilder;

/**
 * change password dialog
 * Created by Ivan on 16/4/23.
 */
public class ChangePwdDialog extends Dialog implements View.OnClickListener {

    private EditText mOldPwdEdit;
    private EditText mNewPwdEdit;
    private EditText mPwdConfirmEdit;
    private TextView mOkButton;
    private TextView mCancelButton;

    private Context context;

    private OnChangeListener onChangeListener;

    public void setOnChangeListener(OnChangeListener l) {
        onChangeListener = l;
    }

    public ChangePwdDialog(Context context) {
        super(context);
        this.context = context;
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.layout_change_pwd_dialog);
        initView();
        super.onCreate(savedInstanceState);
    }

    private void initView() {

        mOldPwdEdit = (EditText) findViewById(R.id.change_pwd_dialog_initial_pwd_input);
        mNewPwdEdit = (EditText) findViewById(R.id.change_pwd_dialog_new_pwd_input);
        mPwdConfirmEdit = (EditText) findViewById(R.id.change_pwd_dialog_pwd_confirm_input);
        mOkButton = (TextView) findViewById(R.id.change_pwd_dialog_ok_btn);
        mCancelButton = (TextView) findViewById(R.id.change_pwd_dialog_cancel_btn);

        mOkButton.setOnClickListener(this);
        mCancelButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (mOkButton.equals(v)) {
            changePwd();
        } else if (mCancelButton.equals(v)) {
            dismiss();
        }
    }

    private void changePwd() {
        String oldPwd = mOldPwdEdit.getText().toString();
        String newPwd = mNewPwdEdit.getText().toString();
        String confirmPwd = mPwdConfirmEdit.getText().toString();

        if (oldPwd.length() == 0 || newPwd.length() == 0 || confirmPwd.length() == 0) {
            return;
        } else if (!newPwd.equals(confirmPwd)) {
            mOldPwdEdit.setText("");
            mNewPwdEdit.setText("");
            mPwdConfirmEdit.setText("");
            return;
        }

        final ProgressDialog dialog = new DialogBuilder(context)
                .createProgress(R.string.tips, context.getResources().getString(R.string.change_pwd_dialog_ing_message), false);
        dismiss();
        dialog.show();

        BaseStringRequest.Builder builder  = new BaseStringRequest.Builder();
        builder.url(Configurations.REQUEST_URL)
                .add("uid", User.uid)
                .add("old_pwd", oldPwd)
                .add("new_pwd", newPwd)
                .build()
                .post(new AbsBaseRequest.Callback() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        onChangeListener.onSuccess();
                    }

                    @Override
                    public void onFailure(int errorFlag, String error) {
                        onChangeListener.onFail(errorFlag, error);
                    }
                });
    }

    public interface OnChangeListener {
        void onSuccess();
        void onFail(int errorFlag, String error);
    }
}
