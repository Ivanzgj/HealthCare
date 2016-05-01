package com.ivan.healthcare.healthcare_android.util.crypt;

/**
 * 数据加解密工具
 * Created by Ivan on 16/5/1.
 */
public class Crypter {
    static {
        System.loadLibrary("HCUtil");
    }
    public static native String encrypt(String str);
    public static native String decrypt(String str);
}
