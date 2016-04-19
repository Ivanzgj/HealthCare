package com.ivan.healthcare.healthcare_android.settings;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.ivan.healthcare.healthcare_android.R;
import com.ivan.healthcare.healthcare_android.local.User;
import com.ivan.healthcare.healthcare_android.view.material.ButtonFlat;

/**
 * 登陆/注册对话框
 * Created by Ivan on 16/4/19.
 */
public class LoginDialog extends Dialog implements View.OnClickListener {

    private Context context;

    private TextView mLoginHeader;
    private TextView mRegisterHeader;
    private View mLoginMask;
    private View mRegisterMask;
    private EditText mUserNameEdit;
    private EditText mPwdEdit;
    private EditText mPwdConfirmEdit;
    private ButtonFlat mLoginButton;
    private LinearLayout mConfirmLayout;

    private boolean isLogin;

    private OnLoginRegisterCompleteListener onLoginRegisterCompleteListener = new OnLoginRegisterCompleteListener() {
        @Override
        public void onLoginRegisterComplete(boolean isLogin) {

        }

        @Override
        public void onFail(boolean isLogin, int errorFlag) {

        }
    };

    public LoginDialog(Context context, boolean isLogin) {
        super(context);
        this.context = context;
        this.isLogin = isLogin;
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.layout_login_dialog);
        initView();
        super.onCreate(savedInstanceState);
    }

    private void initView() {

        mLoginHeader = (TextView) findViewById(R.id.login_dialog_login_header);
        mRegisterHeader = (TextView) findViewById(R.id.login_dialog_register_header);
        mLoginMask = findViewById(R.id.login_dialog_login_header_mask);
        mRegisterMask = findViewById(R.id.login_dialog_register_header_mask);
        mUserNameEdit = (EditText) findViewById(R.id.login_dialog_name_input);
        mPwdEdit = (EditText) findViewById(R.id.login_dialog_pwd_input);
        mPwdConfirmEdit = (EditText) findViewById(R.id.login_dialog_pwd_confirm_input);
        mLoginButton = (ButtonFlat) findViewById(R.id.login_dialog_login_btn);
        mConfirmLayout = (LinearLayout) findViewById(R.id.login_dialog_confirm_rel);

        mLoginButton.setOnClickListener(this);
        mLoginHeader.setOnClickListener(this);
        mRegisterHeader.setOnClickListener(this);

        if (isLogin) {
            mLoginMask.setVisibility(View.VISIBLE);
            mRegisterMask.setVisibility(View.INVISIBLE);
            mConfirmLayout.setVisibility(View.GONE);
            mLoginButton.setText(context.getResources().getString(R.string.login_dialog_header_login));
        } else {
            mLoginMask.setVisibility(View.INVISIBLE);
            mRegisterMask.setVisibility(View.VISIBLE);
            mConfirmLayout.setVisibility(View.VISIBLE);
            mLoginButton.setText(context.getResources().getString(R.string.login_dialog_header_register));
        }
    }

    @Override
    public void onClick(View v) {
        if (mLoginButton.equals(v)) {
            if (isLogin)    login();
            else            register();
        } else if (mLoginHeader.equals(v)) {
            isLogin = true;
            mLoginMask.setVisibility(View.VISIBLE);
            mRegisterMask.setVisibility(View.INVISIBLE);
            mConfirmLayout.setVisibility(View.GONE);
            mLoginButton.setText(context.getResources().getString(R.string.login_dialog_header_login));
        } else if (mRegisterHeader.equals(v)) {
            isLogin = false;
            mLoginMask.setVisibility(View.INVISIBLE);
            mRegisterMask.setVisibility(View.VISIBLE);
            mConfirmLayout.setVisibility(View.VISIBLE);
            mLoginButton.setText(context.getResources().getString(R.string.login_dialog_header_register));
        }
    }

    private void login() {
        User.edit().setUid(10001).setUserName("User_10001").commit();
        onLoginRegisterCompleteListener.onLoginRegisterComplete(true);
        dismiss();
    }

    private void register() {
        dismiss();
    }

    public void setOnLoginRegisterCompleteListener(OnLoginRegisterCompleteListener l) {
        onLoginRegisterCompleteListener = l;
    }

    public interface OnLoginRegisterCompleteListener {
        void onLoginRegisterComplete(boolean isLogin);
        void onFail(boolean isLogin, int errorFlag);
    }
}
