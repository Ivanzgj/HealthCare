package com.ivan.healthcare.healthcare_android.database;

import android.database.sqlite.SQLiteDatabase;

/**
 * 数据库操作类
 * @author Ivan
 */

public class Database {
	
	private SQLiteDatabase db;

	public Database(SQLiteDatabase db) {
		this.db = db;
	}
	
	public QueryBuilder query() {
		return new QueryBuilder(db);
	}
	
	public SQLiteDatabase getSQLDataBase() {
		return db;
	}
}
