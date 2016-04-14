package com.ivan.healthcare.healthcare_android.local;

/**
 * 星座管理类
 * Created by Ivan on 16/4/14.
 */
public class Constellation {

    public enum ConstellationEnum {
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

    public static int getConstellationInt(ConstellationEnum constellation) {
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

    public static ConstellationEnum getConstellationEnum(int constellation) {
        switch (constellation) {
            case 1:
                return ConstellationEnum.Capricorn;
            case 2:
                return ConstellationEnum.Aquarius;
            case 3:
                return ConstellationEnum.Pisces;
            case 4:
                return ConstellationEnum.Aries;
            case 5:
                return ConstellationEnum.Taurus;
            case 6:
                return ConstellationEnum.Gemini;
            case 7:
                return ConstellationEnum.Cancer;
            case 8:
                return ConstellationEnum.Leo;
            case 9:
                return ConstellationEnum.Virgo;
            case 10:
                return ConstellationEnum.Libra;
            case 11:
                return ConstellationEnum.Scorpio;
            case 12:
                return ConstellationEnum.Sagittarius;
            default:
                return ConstellationEnum.Undefine;
        }
    }

    public static String getConstellationString(ConstellationEnum constellation) {
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

    /**
     * 根据月份和日计算星座, 月份从1开始
     */
    public static ConstellationEnum getConstellation(int month, int dayOfMonth) {
        ConstellationEnum constell = ConstellationEnum.Undefine;
        switch(month) {
            case 1:
                constell = (dayOfMonth < 21) ? ConstellationEnum.Capricorn : ConstellationEnum.Aquarius;
                break;
            case 2:
                constell = (dayOfMonth < 19) ? ConstellationEnum.Aquarius : ConstellationEnum.Pisces;
                break;
            case 3:
                constell = (dayOfMonth < 21) ? ConstellationEnum.Pisces : ConstellationEnum.Aries;
                break;
            case 4:
                constell = (dayOfMonth < 21) ? ConstellationEnum.Aries : ConstellationEnum.Taurus;
                break;
            case 5:
                constell = (dayOfMonth < 22) ? ConstellationEnum.Taurus : ConstellationEnum.Gemini;
                break;
            case 6:
                constell = (dayOfMonth < 22) ? ConstellationEnum.Gemini : ConstellationEnum.Cancer;
                break;
            case 7:
                constell = (dayOfMonth < 23) ? ConstellationEnum.Cancer : ConstellationEnum.Leo;
                break;
            case 8:
                constell = (dayOfMonth < 24) ? ConstellationEnum.Leo : ConstellationEnum.Virgo;
                break;
            case 9:
                constell = (dayOfMonth < 23) ? ConstellationEnum.Virgo : ConstellationEnum.Libra;
                break;
            case 10:
                constell = (dayOfMonth < 24) ? ConstellationEnum.Libra : ConstellationEnum.Scorpio;
                break;
            case 11:
                constell = (dayOfMonth < 23) ? ConstellationEnum.Scorpio : ConstellationEnum.Sagittarius;
                break;
            case 12:
                constell = (dayOfMonth < 22) ? ConstellationEnum.Sagittarius : ConstellationEnum.Capricorn;
                break;
        }
        return constell;
    }

}
