package com.ivan.healthcare.healthcare_android.network.bean;

/**
 * 用于Gson解析的用户请求登陆返回数据
 * Created by Ivan on 16/4/24.
 */
public class LoginBean extends BaseBean {

    private String uid;
    private String name;

    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setName(String name) {
        this.name = name;
    }
}
