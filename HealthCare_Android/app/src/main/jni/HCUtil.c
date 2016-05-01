//
// Created by 朱广健 on 16/5/1.
//

#include <stdio.h>
#include <stdlib.h>
#include "com_ivan_healthcare_healthcare_android_util_crypt_Crypter.h"
#include <android/log.h>
#define TAG "jni"
#define LOGV(...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)

JNIEXPORT jstring JNICALL Java_com_ivan_healthcare_healthcare_1android_util_crypt_Crypter_encrypt
        (JNIEnv *env, jclass cls, jstring str) {

        LOGV("log from native");
        return (*env)->NewStringUTF(env, "Hello from JNI");
}

JNIEXPORT jstring JNICALL Java_com_ivan_healthcare_healthcare_1android_util_crypt_Crypter_decrypt
        (JNIEnv *env, jclass cls, jstring str) {

        LOGV("log from native");
        return (*env)->NewStringUTF(env, "Hello from JNI");
}