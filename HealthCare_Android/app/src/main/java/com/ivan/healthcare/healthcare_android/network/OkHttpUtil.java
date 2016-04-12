package com.ivan.healthcare.healthcare_android.network;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 基于okhttp的网络请求实现类
 * Created by Ivan on 16/1/30.
 */
public class OkHttpUtil {

    private static final int CONNECTION_TIME_OUT_IN_SECONDS = 30;
    private static final String CHARSET_NAME = "UTF-8";
    public static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");

    private static OkHttpClient mOkHttpClient = new OkHttpClient();

    static {
        mOkHttpClient.setConnectTimeout(CONNECTION_TIME_OUT_IN_SECONDS, TimeUnit.SECONDS);
    }

    /**
     * 构造post请求request对象
     * @param url 请求的url地址
     * @param map 请求的post参数
     * @return 根据请求url和请求参数够构造的request对象
     */
    private static Request makeRequest(String url, Map<String,Object> map) {
        FormEncodingBuilder builder = new FormEncodingBuilder();
        for (String key : map.keySet()) {
            builder.add(key, map.get(key).toString());
        }
        RequestBody requestBody = builder.build();
        return new Request.Builder()
                                    .url(url)
                                    .post(requestBody)
                                    .build();
    }

    /**
     * 构造表单传输文件（或与键值对混合）
     * @param url 请求的url地址
     * @param map 请求的参数
     * @param fileType 请求上传的文件类型
     * @return 根据请求url和请求参数构造的表单request对象
     */
    private static Request buildMultiPart(String url, Map<String, Object> map, Map<String, MediaType> fileType) {
        MultipartBuilder builder = new MultipartBuilder()
                                        .type(MultipartBuilder.FORM);
        for (String key : map.keySet()) {
            Object value = map.get(key);
            if (value instanceof File) {
                File file = (File) value;
                builder.addPart(Headers.of("Content-Disposition", "form-data; filename=\""+ file.getName() +"\"; name=\"" + key + "\""),
                                RequestBody.create(fileType.get(key), file));
            } else {
                builder.addFormDataPart(key, value.toString());
            }
        }
        RequestBody requestBody = builder.build();
        return new Request.Builder()
                                .url(url)
                                .post(requestBody)
                                .build();
    }

    /**
     * 同步请求
     * @param call 请求的call
     * @return 请求结果的字符串，请求失败返回null
     * @throws IOException
     */
    public static String execute(Call call) throws IOException {
        Response response = call.execute();
        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            return null;
        }
    }

    /**
     * 同步请求
     * @param call 请求的call
     * @return 请求结果的字节流，请求失败返回null
     * @throws IOException
     */
    public static byte[] executeForBytes(Call call) throws IOException {
        Response response = call.execute();
        if (response.isSuccessful()) {
            return response.body().bytes();
        } else {
            return null;
        }
    }

    /**
     * 同步请求
     * @param call 请求的call
     * @return 请求结果的input stream，请求失败返回null
     * @throws IOException
     */
    public static InputStream executeForInputStream(Call call) throws IOException {
        Response response = call.execute();
        if (response.isSuccessful()) {
            return response.body().byteStream();
        } else {
            return null;
        }
    }

    /**
     * 异步请求
     * @param call 请求的call对象
     * @param responseCallback 异步请求get结果的callback
     * @throws IOException
     */
    public static void enqueue(Call call, Callback responseCallback) throws IOException {
        call.enqueue(responseCallback);
    }

    /**
     * get，有get参数
     * @param url 请求的url地址
     * @param map get的请求参数
     * @return 返回请求的call对象
     * @throws IOException
     */
    public static Call get(String url, Map<String,Object> map) throws IOException {
        url = attachEncodedParams(url, map);
        Request request = new Request.Builder()
                                    .url(url)
                                    .build();
        return mOkHttpClient.newCall(request);
    }

    /**
     * get，无get参数
     * @param url 请求的url地址
     */
    public static Call get(String url) throws IOException {
        Request request = new Request.Builder()
                                    .url(url)
                                    .build();
        return mOkHttpClient.newCall(request);
    }

    /**
     * 将键值对整理成url get形式
     * @param map 键值对
     * @return url encodeing的字符串
     */
    private static String formatParams(Map<String,Object> map) {
        StringBuilder sb = new StringBuilder("");
        boolean first = true;
        for (String key : map.keySet()) {
            if (first) {
                first = false;
            } else {
                sb.append("&");
            }
            sb.append(key).append("=").append(map.get(key).toString());
        }
        String s;
        try {
            s = URLEncoder.encode(sb.toString(), CHARSET_NAME);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            s = "";
        }
        return s;
    }

    /**
     * 将请求参数附加到url上
     * @param url 请求url
     * @param map 请求参数
     * @return get请求的url字符串
     */
    private static String attachEncodedParams(String url, Map<String,Object> map) {
        return url+"?"+formatParams(map);
    }

    /**
     * 同步post
     * @param url 请求的url地址
     * @param map post参数的键值对
     * @return 返回请求的call对象
     * @throws IOException
     */
    public static Call post(String url, Map<String,Object> map) throws IOException {
        Request request = makeRequest(url, map);
        return mOkHttpClient.newCall(request);
    }

    /**
     * 构建表单上传数据，一般为文件上传服务
     * @param url 请求的url地址
     * @param map post参数的键值对
     * @param fileType 请求上传的文件类型
     * @return 返回请求的call对象
     * @throws IOException
     */
    public static Call postMultiPart(String url, Map<String,Object> map, Map<String, MediaType> fileType) throws IOException {
        Request request = buildMultiPart(url, map, fileType);
        return mOkHttpClient.newCall(request);
    }

}
