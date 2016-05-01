LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := HCUtil
LOCAL_SRC_FILES := HCUtil.c
# for logging
LOCAL_LDLIBS    += -llog

include $(BUILD_SHARED_LIBRARY)