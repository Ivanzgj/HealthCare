package com.ivan.healthcare.healthcare_android.settings.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ivan.healthcare.healthcare_android.Configurations;
import com.ivan.healthcare.healthcare_android.R;
import com.ivan.healthcare.healthcare_android.local.User;
import com.ivan.healthcare.healthcare_android.network.AbsBaseRequest;
import com.ivan.healthcare.healthcare_android.network.BaseStringRequest;
import com.ivan.healthcare.healthcare_android.network.bean.UserInfoBean;
import com.ivan.healthcare.healthcare_android.settings.sync.Sync;
import com.ivan.healthcare.healthcare_android.util.DialogBuilder;
import com.ivan.healthcare.healthcare_android.util.L;


/**
 * 登陆/注册对话框
 * Created by Ivan on 16/4/19.
 */
@SuppressWarnings("unchecked")
public class LoginDialog extends Dialog implements View.OnClickListener {

    private Context context;

    private TextView mLoginHeader;
    private TextView mRegisterHeader;
    private View mLoginMask;
    private View mRegisterMask;
    private EditText mUserNameEdit;
    private EditText mPwdEdit;
    private EditText mPwdConfirmEdit;
    private TextView mLoginButton;
    private LinearLayout mConfirmLayout;

    private boolean isLogin;

    private OnLoginRegisterCompleteListener onLoginRegisterCompleteListener = new OnLoginRegisterCompleteListener() {
        @Override
        public void onLoginRegisterComplete(boolean isLogin, UserInfoBean bean) {

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
        mLoginButton = (TextView) findViewById(R.id.login_dialog_login_btn);
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

        String email = mUserNameEdit.getText().toString();
        String pwd = mPwdEdit.getText().toString();
        if (email.length() == 0 || pwd.length() == 0) {
            mPwdEdit.setText("");
            return;
        }

        final ProgressDialog dialog = new DialogBuilder(context)
                .createProgress(R.string.login_dialog_header_login,
                        context.getResources().getString(R.string.login_ing_message),
                        false);
        dismiss();
        dialog.show();

        BaseStringRequest.Builder builder = new BaseStringRequest.Builder();
        builder.url(Configurations.USER_URL)
                .add("action", "login")
                .add("account", email)
                .add("pwd", pwd)
                .build()
                .post(new AbsBaseRequest.Callback() {
                    @Override
                    public void onResponse(final String response) {
                        try {
                            Gson gson = new Gson();
                            UserInfoBean bean = gson.fromJson(response, UserInfoBean.class);
                            User.syncUserInfo(bean, context);
                            Sync.sync(context, null);
                            onLoginRegisterCompleteListener.onLoginRegisterComplete(true, bean);
                            dialog.dismiss();
                        } catch (ClassCastException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(final int errorFlag, String error) {
                        L.d("test", errorFlag + "");
                        onLoginRegisterCompleteListener.onFail(true, errorFlag);
                        dialog.dismiss();
                        new DialogBuilder(context).create()
                                .setTitle(R.string.tips)
                                .setContent(error)
                                .setPositive(R.string.ok)
                                .show();
                    }
                });
    }

    private void register() {
        String email = mUserNameEdit.getText().toString();
        String pwd1 = mPwdEdit.getText().toString();
        String pwd2 = mPwdConfirmEdit.getText().toString();
        if(email.length() == 0 || pwd1.length() == 0 || pwd2.length() == 0 || !pwd1.equals(pwd2)) {
            mPwdEdit.setText("");
            mPwdConfirmEdit.setText("");
            return;
        }

        final ProgressDialog dialog = new DialogBuilder(context)
                .createProgress(R.string.login_dialog_header_register,
                        context.getResources().getString(R.string.register_ing_message),
                        false);
        dismiss();
        dialog.show();

        BaseStringRequest.Builder builder = new BaseStringRequest.Builder();
        builder.url(Configurations.USER_URL)
                .add("action", "register")
                .add("account", email)
                .add("pwd", pwd1)
                .build()
                .post(new AbsBaseRequest.Callback() {
                    @Override
                    public void onResponse(final String response) {
                        try {
                            Gson gson = new Gson();
                            UserInfoBean bean = gson.fromJson(response, UserInfoBean.class);
                            User.syncUserInfo(bean, context);
                            onLoginRegisterCompleteListener.onLoginRegisterComplete(false, bean);
                            dialog.dismiss();
                        } catch (ClassCastException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(final int errorFlag, String error) {
                        L.d("test", errorFlag + "");
                        onLoginRegisterCompleteListener.onFail(false, errorFlag);
                        dialog.dismiss();
                        new DialogBuilder(context).create()
                                .setTitle(R.string.tips)
                                .setContent(error)
                                .setPositive(R.string.ok)
                                .show();
                    }
                });
    }

    public void setOnLoginRegisterCompleteListener(OnLoginRegisterCompleteListener l) {
        onLoginRegisterCompleteListener = l;
    }

    public interface OnLoginRegisterCompleteListener {
        void onLoginRegisterComplete(boolean isLogin, UserInfoBean bean);
        void onFail(boolean isLogin, int errorFlag);
    }
}
