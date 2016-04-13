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

    public enum Constellation {
        Capricorn,      //摩羯座
        Aquarius,       //水瓶座
        Pisces,         //双鱼座
        Aries,          //白羊座
        Taurus,         //金牛座
        Gemini,         //双子座
        Cancer,         //巨蟹座
        Leo,            //狮子座
        Virgo,          //处女座
        Libra,          //天秤座
        Scorpio,        //天蝎座
        Sagittarius,    //射手座
        Undefine        //未定义
    }

    private static final String DEFAULT_USER_NAME_PREFIX = "User_";

    public static int uid;
    public static String userName;
    public static int age;
    public static UserSex sex;
    public static String birthday;
    public static Constellation constellation;
    public static String email;
    public static String address;
    public static String introduction;
    public static int todayMeasureTimes;
    public static int totalMeasureTimes;
    public static int totalMeasureAssessment;

    public static void initUserInfo() {
        uid = DataAccess.getUid();
        if (uid == -1) {
            userName = DEFAULT_USER_NAME_PREFIX + uid;
            age = -1;
            sex = UserSex.Undefine;
            constellation = Constellation.Undefine;
            birthday = "";
            email = "";
            address = "";
            introduction = "";
            todayMeasureTimes = 0;
            totalMeasureTimes = 0;
            totalMeasureAssessment = 0;
        } else {
            DataAccess.initUserInfo();
            if (userName == null) {
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

    public static int getConstellationInt() {
        switch (constellation) {
            case Capricorn:
                return 1;
            case Aquarius:
                return 2;
            case Pisces:
                return 3;
            case Aries:
                return 4;
            case Taurus:
                return 5;
            case Gemini:
                return 6;
            case Cancer:
                return 7;
            case Leo:
                return 8;
            case Virgo:
                return 9;
            case Libra:
                return 10;
            case Scorpio:
                return 11;
            case Sagittarius:
                return 12;
            default:
                return -1;
        }
    }

    public static String getConstellationString() {
        switch (constellation) {
            case Capricorn:
                return "摩羯座";
            case Aquarius:
                return "水瓶座";
            case Pisces:
                return "双鱼座";
            case Aries:
                return "白羊座";
            case Taurus:
                return "金牛座";
            case Gemini:
                return "双子座";
            case Cancer:
                return "巨蟹座";
            case Leo:
                return "狮子座";
            case Virgo:
                return "处女座";
            case Libra:
                return "天秤座";
            case Scorpio:
                return "天蝎座";
            case Sagittarius:
                return "射手座";
            default:
                return "";
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
        switch (constellationInt) {
            case 1:
                constellation = Constellation.Capricorn;
                break;
            case 2:
                constellation = Constellation.Aquarius;
                break;
            case 3:
                constellation = Constellation.Pisces;
                break;
            case 4:
                constellation = Constellation.Aries;
                break;
            case 5:
                constellation = Constellation.Taurus;
                break;
            case 6:
                constellation = Constellation.Gemini;
                break;
            case 7:
                constellation = Constellation.Cancer;
                break;
            case 8:
                constellation = Constellation.Leo;
                break;
            case 9:
                constellation = Constellation.Virgo;
                break;
            case 10:
                constellation = Constellation.Libra;
                break;
            case 11:
                constellation = Constellation.Scorpio;
                break;
            case 12:
                constellation = Constellation.Sagittarius;
                break;
            default:
                constellation = Constellation.Undefine;
                break;
        }
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
        private Constellation constellation;
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

        public Editor setConstellation(Constellation constellation) {
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
