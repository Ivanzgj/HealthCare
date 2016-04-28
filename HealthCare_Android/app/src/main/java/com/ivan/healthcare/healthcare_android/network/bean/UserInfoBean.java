package com.ivan.healthcare.healthcare_android.network.bean;

/**
 * 用于Gson解析的用户请求登陆返回数据
 * Created by Ivan on 16/4/24.
 */
public class UserInfoBean extends BaseBean {

    private String uid;
    private String name;
    private int age;
    private int sex;
    private String birth;
    private int constellation;
    private String email;
    private String address;
    private String introduction;
    private int measure_today_times;
    private int measure_total_times;
    private int measure_total_assessment;

    private String avatar;

    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public int getSex() {
        return sex;
    }

    public String getBirth() {
        return birth;
    }

    public int getConstellation() {
        return constellation;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public String getIntroduction() {
        return introduction;
    }

    public int getMeasure_today_times() {
        return measure_today_times;
    }

    public int getMeasure_total_times() {
        return measure_total_times;
    }

    public int getMeasure_total_assessment() {
        return measure_total_assessment;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public void setConstellation(int constellation) {
        this.constellation = constellation;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public void setMeasure_today_times(int measure_today_times) {
        this.measure_today_times = measure_today_times;
    }

    public void setMeasure_total_times(int measure_total_times) {
        this.measure_total_times = measure_total_times;
    }

    public void setMeasure_total_assessment(int measure_total_assessment) {
        this.measure_total_assessment = measure_total_assessment;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
