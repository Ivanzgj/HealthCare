package com.ivan.healthcare.healthcare_android.view.chart.provider;

public abstract class SimpleChartAdapter extends LineChartAdapter {
        @Override
        public int getXLabelsCount() {
            return getLineData(0).size();
        }

        @Override
        public String getXLabel(int position) {
            return null;
        }

        public int getLegendCount() {
            return 0;
        }

        /**
         * 获得图例
         * @return 图例
         */
        public String getLegend(int position) {
            return null;
        }

        /**
         * 获得图例的颜色
         * @return 图例的颜色
         */
        public int getLegendColorId(int position) {
            return 0;
        }
    }