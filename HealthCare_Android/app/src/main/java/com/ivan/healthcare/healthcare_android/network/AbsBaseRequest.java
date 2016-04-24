package com.ivan.healthcare.healthcare_android.network;

import com.squareup.okhttp.MediaType;
import java.io.IOException;
import java.util.Map;

/**
 * 基本网络请求类
 * Created by Ivan on 16/1/30.
 */
public abstract class AbsBaseRequest {

    public static final int UNKNOWN_ERROR = 0x31;

    private Map<String, Object> params;
    private String url;
    private Map<String, MediaType> fileType;

    public AbsBaseRequest() {
        params = null;
        url = "";
        fileType = null;
    }

    /**
     * 异步get请求
     */
    public abstract void get(final AbsBaseRequest.Callback callback);
    /**
     * 异步post请求
     */
    public abstract void post(final AbsBaseRequest.Callback callback);

    /**
     * 同步post请求
     */
    public abstract String sync() throws IOException;

    /**
     * 取消请求
     */
    public abstract void cancel() throws IOException;

    /**
     * 获取请求地址字符串
     * @return url字符串
     */
    public String getURL() {
        return url;
    }

    /**
     * 获取请求参数键值对
     * @return 参数map
     */
    public Map<String,Object> getParams() {
        return params;
    }

    /**
     * 获取文件类型
     * @return 文件类型键值对
     */
    public Map<String, MediaType> getFileType() {
        return fileType;
    }

    /**
     * 设置请求参数
     * @param params 请求参数键值对
     */
    public void setRequestParams(Map<String,Object> params) {
        this.params = params;
    }

    /**
     * 设置请求url地址
     * @param url 请求地址字符串
     */
    public void setRequestUrl(String url) {
        this.url = url;
    }

    /**
     * 设置文件类型
     * @param fileType 上传的文件的类型的键值对
     */
    public void setFileType(Map<String, MediaType> fileType) {
        this.fileType = fileType;
    }

    /**
     * 处理网络请求的callback
     * Created by Ivan on 16/1/30.
     */
    public static abstract class Callback {

        /**
         * 响应请求结果
         * @param response okHttp网络请求的结果
         */
        public abstract void onResponse(final String response);

        /**
         * 失败处理
         * @param errorFlag 失败标识
         */
        public abstract void onFailure(final int errorFlag, final String error);
    }

}
