package com.ivan.healthcare.healthcare_android.network.bean;

import java.util.List;

/**
 * 从服务器下载测量数据的 Gson bean
 * Created by Ivan on 16/4/27.
 */
public class MeasureDataBean extends BaseBean{

    public static class DataUnit {

        private String acc_data;
        private String src_time;
        private StringBuilder src_status;

        public String getAcc_data() {
            return acc_data;
        }

        public String getSrc_time() {
            return src_time;
        }

        public StringBuilder getSrc_status() {
            return src_status;
        }

        public void setAcc_data(String acc_data) {
            this.acc_data = acc_data;
        }

        public void setSrc_time(String src_time) {
            this.src_time = src_time;
        }

        public void setSrc_status(StringBuilder src_status) {
            this.src_status = src_status;
        }
    }

    private List<DataUnit> data;

    public List<DataUnit> getData() {
        return data;
    }

    public void setData(List<DataUnit> data) {
        this.data = data;
    }

}
