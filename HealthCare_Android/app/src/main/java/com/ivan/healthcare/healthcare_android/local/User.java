package com.ivan.healthcare.healthcare_android.local;

import com.ivan.healthcare.healthcare_android.database.DataAccess;

/**
 * 用户信息类
 * Created by Ivan on 16/4/13.
 */
public class User {

    public enum UserSex {
        USER_MALE,
        USER_FEMALE,
        USER_ALIEN,
        Undefine
    }

    public static final int UID_UNDEFINE = 1002;

    private static final String DEFAULT_USER_NAME_PREFIX = "User_";

    public static int uid;
    public static String userName;
    public static int age;
    public static UserSex sex;
    public static String birthday;
    public static Constellation.ConstellationEnum constellation;
    public static String email;
    public static String address;
    public static String introduction;
    public static int todayMeasureTimes;
    public static int totalMeasureTimes;
    public static int totalMeasureAssessment;

    public static void initUserInfo() {
        uid = DataAccess.getUid();
        if (uid == UID_UNDEFINE) {
            uid = -1;
            userName = DEFAULT_USER_NAME_PREFIX + uid;
            age = -1;
            sex = UserSex.Undefine;
            constellation = Constellation.ConstellationEnum.Undefine;
            birthday = "";
            email = "";
            address = "";
            introduction = "";
            todayMeasureTimes = 0;
            totalMeasureTimes = 0;
            totalMeasureAssessment = 0;
        } else {
            DataAccess.initUserInfo();
            if (userName == null || userName.length() == 0) {
                userName = DEFAULT_USER_NAME_PREFIX + uid;
            }
        }
    }

    public static Editor edit() {
        return new Editor();
    }

    public static int getSexInt() {
        switch (sex) {
            case USER_MALE:
                return 0;
            case USER_FEMALE:
                return 1;
            case USER_ALIEN:
                return 2;
            default:
                return -1;
        }
    }

    public static void setSexInt(int sexInt) {
        switch (sexInt) {
            case 0:
                sex = UserSex.USER_MALE;
                break;
            case 1:
                sex = UserSex.USER_FEMALE;
                break;
            case 2:
                sex = UserSex.USER_ALIEN;
                break;
            default:
                sex = UserSex.Undefine;
                break;
        }
    }

    public static void setConstellationInt(int constellationInt) {
        constellation = Constellation.getConstellationEnum(constellationInt);
    }

    public static void login(String account, String pwd, UserLogListener l) {

    }

    public static void logout(UserLogListener l) {

    }

    public static interface UserLogListener {
        void onSuccess();
        void onFail(int errorFlag);
    }

    /**
     * User编辑类
     */
    public static class Editor {

        private int uid;
        private String userName;
        private int age;
        private UserSex sex;
        private String birthday;
        private Constellation.ConstellationEnum constellation;
        private String email;
        private String address;
        private String introduction;
        private int todayMeasureTimes;
        private int totalMeasureTimes;
        private int totalMeasureAssessment;

        private boolean uidChanged = false;
        private boolean userNameChanged = false;
        private boolean ageChanged = false;
        private boolean sexChanged = false;
        private boolean birthdayChanged = false;
        private boolean constellationChanged = false;
        private boolean emailChanged = false;
        private boolean addressChanged = false;
        private boolean introductionChanged = false;
        private boolean todayMeasureTimesChanged = false;
        private boolean totalMeasureTimesChanged = false;
        private boolean totalMeasureAssessmentChanged = false;

        public Editor setUid(int uid) {
            this.uid = uid;
            uidChanged = true;
            return this;
        }

        public Editor setUserName(String userName) {
            this.userName = userName;
            userNameChanged = true;
            return this;
        }

        public Editor setAge(int age) {
            this.age = age;
            ageChanged = true;
            return this;
        }

        public Editor setSex(UserSex sex) {
            this.sex = sex;
            sexChanged = true;
            return this;
        }

        public Editor setBirthday(String birthday) {
            this.birthday = birthday;
            birthdayChanged = true;
            return this;
        }

        public Editor setConstellation(Constellation.ConstellationEnum constellation) {
            this.constellation = constellation;
            constellationChanged = true;
            return this;
        }

        public Editor setEmail(String email) {
            this.email = email;
            emailChanged = true;
            return this;
        }

        public Editor setAddress(String address) {
            this.address = address;
            addressChanged = true;
            return this;
        }

        public Editor setIntroduction(String introduction) {
            this.introduction = introduction;
            introductionChanged = true;
            return this;
        }

        public Editor setTodayMeasureTimes(int todayMeasureTimes) {
            this.todayMeasureTimes = todayMeasureTimes;
            todayMeasureTimesChanged = true;
            return this;
        }

        public Editor setTotalMeasureTimes(int totalMeasureTimes) {
            this.totalMeasureTimes = totalMeasureTimes;
            totalMeasureTimesChanged = true;
            return this;
        }

        public Editor setTotalMeasureAssessment(int totalMeasureAssessment) {
            this.totalMeasureAssessment = totalMeasureAssessment;
            totalMeasureAssessmentChanged = true;
            return this;
        }

        public boolean commit() {
            if (uidChanged)                     User.uid = this.uid;
            if (userNameChanged)                User.userName = this.userName;
            if (sexChanged)                     User.sex = this.sex;
            if (ageChanged)                     User.age = this.age;
            if (birthdayChanged)                User.birthday = this.birthday;
            if (constellationChanged)           User.constellation = this.constellation;
            if (emailChanged)                   User.email = this.email;
            if (addressChanged)                 User.address = this.address;
            if (introductionChanged)            User.introduction = this.introduction;
            if (todayMeasureTimesChanged)       User.todayMeasureTimes = this.todayMeasureTimes;
            if (totalMeasureTimesChanged)       User.totalMeasureTimes = this.totalMeasureTimes;
            if (totalMeasureAssessmentChanged)  User.totalMeasureAssessment = this.totalMeasureAssessment;
            if (!DataAccess.updateUserInfo()) {
                initUserInfo();
                return false;
            }
            return true;
        }
    }
}
