package com.ivan.healthcare.healthcare_android.database;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据库查询结果类
 * @author Ivan
 */

public class Result {
	
	private Map<String, Object> resultMap;
	
	public Result() {
		resultMap = new HashMap<>();
	}
	
	public void put(String key, Object value) {
		resultMap.put(key, value);
	}
	
	public int getInt(String key) {
		return (int) resultMap.get(key);
	}
	
	public long getLong(String key) {
		return (long) resultMap.get(key);
	}
	
	public short getShort(String key) {
		return (short) resultMap.get(key);
	}
	
	public float getFloat(String key) {
		return (float) resultMap.get(key);
	}
	
	public String getString(String key) {
		return (String) resultMap.get(key);
	}
}
