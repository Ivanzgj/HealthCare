package com.ivan.healthcare.healthcare_android.settings.sync;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.view.View;
import com.google.gson.Gson;
import com.ivan.healthcare.healthcare_android.Configurations;
import com.ivan.healthcare.healthcare_android.R;
import com.ivan.healthcare.healthcare_android.database.DataAccess;
import com.ivan.healthcare.healthcare_android.network.AbsBaseRequest;
import com.ivan.healthcare.healthcare_android.network.BaseStringRequest;
import com.ivan.healthcare.healthcare_android.network.bean.MeasureDataBean;
import com.ivan.healthcare.healthcare_android.util.DialogBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 与服务器同步数据的工具类
 * Created by Ivan on 16/4/28.
 */
public class Sync {

    /**
     * 上传本地测量数据到服务器
     */
    public static void upload(Context context, final View rootView) {
        final ProgressDialog dialog = new DialogBuilder(context)
                .createProgress(R.string.tips,
                        context.getResources().getString(R.string.backup_backup_ing_message),
                        false);
        dialog.show();

        ArrayList<String> timeList = DataAccess.getHistoryMonitorVibrationTime();
        final ArrayList<Map<String, String>> uploadList = new ArrayList<>();

        for (String time : timeList) {
            ArrayList<Float> accData = DataAccess.getVibrationData(time);
            ArrayList<DataAccess.SrcDataUnit> srcData = DataAccess.getSrcData(time);

            StringBuilder accString = new StringBuilder();
            StringBuilder srcTimeString = new StringBuilder();
            StringBuilder srcString = new StringBuilder();

            for (float data : accData) {
                accString.append(data).append("|");
            }
            accString.deleteCharAt(accString.length() - 1);
            for (DataAccess.SrcDataUnit unit : srcData) {
                srcTimeString.append(unit.recTime).append("|");
                srcString.append(unit.srcOn).append("|");
            }
            srcTimeString.deleteCharAt(srcTimeString.length() - 1);
            srcString.deleteCharAt(srcString.length() - 1);

            HashMap<String, String> map = new HashMap<>();
            map.put("time", time);
            map.put("acc_data", accString.toString());
            map.put("src_time", srcTimeString.toString());
            map.put("src_status", srcString.toString());
            uploadList.add(map);
        }

        new BaseStringRequest.Builder()
                .url(Configurations.SYNC_URL)
                .add("action", "upload")
                .add("data", uploadList)
                .build()
                .post(new AbsBaseRequest.Callback() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        if (rootView != null) {
                            Snackbar.make(rootView, R.string.backup_upload_success_message, Snackbar.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(int errorFlag, String error) {
                        dialog.dismiss();
                        if (rootView != null) {
                            Snackbar.make(rootView, R.string.backup_upload_fail_message, Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * 下载服务器测量数据到本地
     */
    public static void sync(Context context, final View rootView) {
        final ProgressDialog dialog = new DialogBuilder(context)
                .createProgress(R.string.tips,
                        context.getResources().getString(R.string.backup_sync_ing_message),
                        false);
        dialog.show();

        new BaseStringRequest.Builder()
                .url(Configurations.SYNC_URL)
                .add("action", "download")
                .add("time", "0")
                .build()
                .post(new AbsBaseRequest.Callback() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        MeasureDataBean bean = gson.fromJson(response, MeasureDataBean.class);
                        ArrayList<MeasureDataBean.DataUnit> dataList = (ArrayList<MeasureDataBean.DataUnit>) bean.getData();
                        sync(dataList);
                        dialog.dismiss();
                        if (rootView != null) {
                            Snackbar.make(rootView, R.string.backup_sync_success_message, Snackbar.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(int errorFlag, String error) {
                        dialog.dismiss();
                        if (rootView != null) {
                            Snackbar.make(rootView, R.string.backup_sync_fail_message, Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private static void sync(ArrayList<MeasureDataBean.DataUnit> dataList) {
        for (MeasureDataBean.DataUnit unit : dataList) {
            String time = unit.getTime();
            String accString = unit.getAcc_data();
            String srcTimeString = unit.getSrc_time();
            String srcString = unit.getSrc_status();
            String[] accData = accString.split("\\|");
            String[] srcTime = srcTimeString.split("\\|");
            String[] srcData = srcString.split("\\|");
            for (int i = 0; i < accData.length; i++) {
                float data = Float.valueOf(accData[i]);
                DataAccess.writeVibrationData(time, i, data);
            }
            for (int i = 0; i < srcData.length; i++) {
                int status = Integer.valueOf(srcData[i]);
                String recTime = srcTime[i];
                DataAccess.writeSrcData(time, recTime, status);
            }
        }
    }

    /**
     * 清空本地和服务器数据
     */
    public static void clear(final Context context, final View rootView) {
        new DialogBuilder(context).create()
                .setTitle(R.string.tips)
                .setContent(R.string.backup_clear_message)
                .setPositive(R.string.ok)
                .setNegative(R.string.cancel)
                .setOnPositiveClickListener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        _clear(context, rootView);
                    }
                })
                .show();
    }

    private static void _clear(Context context, final View rootView) {
        final ProgressDialog dialog = new DialogBuilder(context)
                .createProgress(R.string.tips,
                        context.getResources().getString(R.string.backup_clear_ing_message),
                        false);
        dialog.show();

        new BaseStringRequest.Builder()
                .url(Configurations.SYNC_URL)
                .add("action", "clear")
                .build()
                .post(new AbsBaseRequest.Callback() {
                    @Override
                    public void onResponse(String response) {
                        DataAccess.clearMeasureTable();
                        DataAccess.clearSrcTable();
                        DataAccess.clearVibrationTable();
                        dialog.dismiss();
                        if (rootView != null) {
                            Snackbar.make(rootView, R.string.backup_clear_success_message, Snackbar.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(int errorFlag, String error) {
                        dialog.dismiss();
                        if (rootView != null) {
                            Snackbar.make(rootView, R.string.backup_clear_fail_message, Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
