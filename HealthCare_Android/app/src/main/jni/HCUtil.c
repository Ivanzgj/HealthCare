//
// Created by 朱广健 on 16/5/1.
//

#include <stdio.h>
#include <stdlib.h>
#include "com_ivan_healthcare_healthcare_android_util_crypt_Crypter.h"
#include <android/log.h>
#include "base64.h"
#define TAG "jni"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)

#define baseLen 65
#define GROUP_LEN 16

static const char e1[] = "QW/47SDv935=ERIhjklzxc0XCVTYUBNMqwerbnm1pasdotyuiOPA82+fgFGHJKLZ6";

static const char e2[] = "!$%wTG+)qazBsMxedcW(YHmi@#kolpPOhnIKUJuQAZ=VEDC-SNvt^&LR*gbyXrfjF";

static const char e3[] = "X1piOxCsdKLZ62l7Q93tyumc0SVTY5=aDv+fgFGHJIhjkwerbERNMqoPA84nzUBW/";

int findPos(char c, const char *map)
{
        int i;
        for (i = 0; i < baseLen; i++)
        {
                if (map[i] == c)
                {
                        return i;
                }
        }
        return -1;
}

char *ee(const char *data)
{
        char *result = (char*)malloc(sizeof(char) * GROUP_LEN);
        int i;
        for (i = 0; i < GROUP_LEN / 2; i++)
        {
                char c = data[i];
                int pos = findPos(c, e1);
                result[i] = e2[pos];
                c = data[GROUP_LEN - 1 - i];
                pos = findPos(c, e3);
                result[GROUP_LEN - 1 - i] = e2[pos];
        }
        return result;
}

char *e(const char *data, int length)
{
        int group = length / GROUP_LEN;
        int mod = length % GROUP_LEN;
        char *result = (char*)malloc(sizeof(char) * length + 1);

        char *f = result;
        int i, j;
        for (i = 0; i < group; i++)
        {
                char *enc = ee(data + i * GROUP_LEN);
                for (j = 0; j < GROUP_LEN; j++)
                {
                        *f = enc[j];
                        f++;
                }
                free(enc);
        }
        for (i = length - 1; i >= length - mod; i--)
        {
                char c = data[i];
                int pos = findPos(c, e1);
                *f = e2[pos];
                f++;
        }
        *f = '\0';

        return result;
}

char *dd(const char *data)
{
        char *result = (char*)malloc(sizeof(char) * GROUP_LEN);
        int i;
        for (i = 0; i < GROUP_LEN / 2; i++)
        {
                char c = data[i];
                int pos = findPos(c, e2);
                result[i] = e1[pos];
                c = data[GROUP_LEN - 1 - i];
                pos = findPos(c, e2);
                result[GROUP_LEN - 1 - i] = e3[pos];
        }
        return result;
}

char *d(const char *data, int length)
{
        int group = length / GROUP_LEN;
        int mod = length % GROUP_LEN;
        char *result = (char*)malloc(sizeof(char) * length + 1);

        char *f = result;
        int i, j;
        for (i = 0; i < group; i++)
        {
                char *dec = dd(data + i * GROUP_LEN);
                for (j = 0; j < GROUP_LEN; j++)
                {
                        *f = dec[j];
                        f++;
                }
                free(dec);
        }
        for (i = length - 1; i >= length - mod; i--)
        {
                char c = data[i];
                int pos = findPos(c, e2);
                *f = e1[pos];
                f++;
        }
        *f = '\0';

        return result;
}

JNIEXPORT jstring JNICALL Java_com_ivan_healthcare_healthcare_1android_util_crypt_Crypter_encrypt
        (JNIEnv *env, jclass cls, jstring str)
{
        const char *data = (*env)->GetStringUTFChars(env, str, NULL);
        char *enc = base64_encode(data, strlen(data));
        char *result = e(enc, strlen(enc));
        free(enc);
        LOGD("log in native encrypt >>>");
        LOGD(result);
        return (*env)->NewStringUTF(env, result);
}

JNIEXPORT jstring JNICALL Java_com_ivan_healthcare_healthcare_1android_util_crypt_Crypter_decrypt
        (JNIEnv *env, jclass cls, jstring str)
{
        const char *data = (*env)->GetStringUTFChars(env, str, NULL);
        char *result = d(data, strlen(data));
        char *dec= base64_decode(result, strlen(result));
        free(result);
        LOGD("log in native decrypt >>>");
        LOGD(dec);
        return (*env)->NewStringUTF(env, dec);
}