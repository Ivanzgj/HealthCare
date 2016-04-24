package com.ivan.healthcare.healthcare_android.network.bean;

/**
 * 用于Gson解析的基础Bean
 * Created by Ivan on 16/4/24.
 */
public class BaseBean {

    private int errorCode;
    private String error;

    public int getErrorCode() {
        return errorCode;
    }

    public String getError() {
        return error;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public void setError(String error) {
        this.error = error;
    }
}
