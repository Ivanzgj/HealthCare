package com.ivan.healthcare.healthcare_android.database;

import android.content.Context;
import android.content.SharedPreferences;

import com.ivan.healthcare.healthcare_android.Configurations;

import java.util.HashMap;
import java.util.Map;

/**
 * SharedPrefernce的自定义操作类
 * Created by Ivan on 16/2/6.
 */
public class Preference {

    public static final String BOND_DEVICE_ADDRESS = "BOND_DEVICE_ADDRESS";

    private static Map<String, Object> cache;

    private static Editor editor;
    private static SharedPreferences sp;

    public Preference(Context context) {
        synchronized (this) {
            if (sp == null) {
                sp = context.getSharedPreferences(Configurations.PREFERENCE_NAME, Context.MODE_PRIVATE);
            }
            if (editor == null) {
                editor = new Editor(sp);
            }
            if (cache == null) {
                cache = new HashMap<>();
            }
        }
    }

    public int getInt(String key, int defValue) {
        if (cache.containsKey(key)) {
            return (int) cache.get(key);
        }
        Integer result = sp.getInt(key, defValue);
        cache.put(key, result);
        return result;
    }

    public float getFloat(String key, float defValue) {
        if (cache.containsKey(key)) {
            return (float) cache.get(key);
        }
        Float result = sp.getFloat(key, defValue);
        cache.put(key, result);
        return result;
    }

    public long getLong(String key, long defValue) {
        if (cache.containsKey(key)) {
            return (long) cache.get(key);
        }
        Long result = sp.getLong(key, defValue);
        cache.put(key, result);
        return result;
    }

    public boolean getBoolean(String key, boolean defValue) {
        if (cache.containsKey(key)) {
            return (boolean) cache.get(key);
        }
        Boolean result = sp.getBoolean(key, defValue);
        cache.put(key, result);
        return result;
    }

    public String getString(String key, String defValue) {
        if (cache.containsKey(key)) {
            return (String) cache.get(key);
        }
        String result = sp.getString(key, defValue);
        cache.put(key, result);
        return result;
    }

    public Editor editor() {
        return editor;
    }

    public class Editor {
        private SharedPreferences.Editor edit;

        public Editor(SharedPreferences sp) {
            this.edit = sp.edit();
        }

        public Editor putInt(String key, int value) {
            cache.put(key, value);
            edit.putInt(key, value);
            return this;
        }

        public Editor putFloat(String key, float value) {
            cache.put(key, value);
            edit.putFloat(key, value);
            return this;
        }

        public Editor putLong(String key, long value) {
            cache.put(key, value);
            edit.putLong(key, value);
            return this;
        }

        public Editor putBoolean(String key, boolean value) {
            cache.put(key, value);
            edit.putBoolean(key, value);
            return this;
        }

        public Editor putString(String key, String value) {
            cache.put(key, value);
            edit.putString(key, value);
            return this;
        }

        public Editor remove(String key) {
            cache.remove(key);
            edit.remove(key);
            return this;
        }

        public Editor clearAll() {
            cache.clear();
            edit.clear();
            return this;
        }

        /**
         * 同步提交
         */
        public void commit() {
            edit.commit();
        }

        /**
         * 异步提交
         */
        public void apply() {
            edit.apply();
        }
    }
}
