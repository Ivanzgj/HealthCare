package com.ivan.healthcare.healthcare_android.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import com.ivan.healthcare.healthcare_android.log.L;

/**
 * sql查询语句构造器类
 * @author Ivan
 */

public class QueryBuilder {

	private final String SQL_TAG = "com.ivan.healthcare.healthcare_android.database";
	public static final Object NIL = new Object();
	/**
	 * 数据库实例的引用
	 */
	private SQLiteDatabase db;
	/**
	 * 表名
	 */
	private String tableName;
	/**
	 * 要执行的sql语句
	 */
	private StringBuilder execString;
	/**
	 * 保存执行插入，更新操作时通过 {@link #add(String, Object)} 方法添加的键值对
	 */
	private Map<String, Object> contentValues;
	/**
	 * 保存执行更新，选择，删除语句时通过 {@link #where(String)} 或者 {@link #whereOr(String)} 方法添加的condition
	 * 使用上述方法时，需要配套使用以下方法
	 * {@link #equal(Object)}
	 * {@link #largeThan(Object)}
	 * {@link #lessThan(Object)}
	 * {@link #largeOrEqual(Object)}
	 * {@link #lessOrEqual(Object)}
	 */
	private StringBuilder whereString;
	/**
	 * 保存执行选择操作时通过 {@link #field(String)} 方法添加的所要检索的字段，以","分割
	 */
	private StringBuilder fieldsString;
	/**
	 * 保存执行选择操作时通过 {@link #limit(int)} 方法提供的限制检索的条数
	 */
	private int limit = 0;
	/**
	 * 执行排序的操作
	 */
	private String orderString;
	/**
	 * 执行分组的操作
	 */
	private String groupString;
	/**
	 * 修改指定表结构时，添加字段所用的字符串
	 */
	private StringBuilder addColumnString;
	
	public QueryBuilder(SQLiteDatabase db) {
		this.db = db;
		execString = new StringBuilder("");
		whereString = null;
		contentValues = new HashMap<>();
		fieldsString = new StringBuilder("");
		orderString = null;
		groupString = null;
		addColumnString = null;
	}
	
	/**
	 * 为构建sqlite查询提供一个表名
	 * @param name 表名
	 * @return QueryBuilder构造器
	 */
	public QueryBuilder table(String name) {
		tableName = name;
		return this;
	}
	/**
	 * 为构建sqlite查询提供键值对
	 * @param field 表的字段名
	 * @param value 该字段的值
	 * @return QueryBuilder构造器
	 */
	public QueryBuilder add(String field, Object value) { 
		contentValues.put(field, value);
		return this;
	}
	/**
	 * 为构建sqlite查询添加一个where...and子句
	 * @param field 字段名
	 * @return QueryBuilder构造器
	 */
	public QueryBuilder where(String field) {
		if (whereString == null) {
			whereString = new StringBuilder("");
		}
		if (whereString.length() == 0) {
			whereString.append("where ");
		} else {
			whereString.append(" and ");
		}
		whereString.append(field);
		return this;
	}
	/**
	 * 为构建sqlite查询添加一个where...or子句
	 * @param field 字段名
	 * @return QueryBuilder构造器
	 */
	public QueryBuilder whereOr(String field) {
		if (whereString == null) {
			return this;
		}
		if (whereString.length() == 0) {
			whereString.append("where ");
		} else {
			whereString.append(" or ");
		}
		whereString.append(field);
		return this;
	}

	/**
	 * 为sqlite查询的where子句添加一个like操作
	 * @param like like子句的匹配字符串
	 * @return QueryBuilder构造器
	 */
	public QueryBuilder like(String like) {
		whereString.append(" like ");
		whereString.append(like);
		return this;
	}
	/**
	 * 为构建sqlite查询的where子句提供一个等于判断值，
	 * 必须在{@link #where(String)}
	 * 或者 {@link #whereOr(String)}方法后调用
	 * @param value 判断值
	 * @return QueryBuilder构造器
	 */
	public QueryBuilder equal(Object value) {
		whereString.append("==");
		whereString.append(value);
		return this;
	}
	/**
	 * 为构建sqlite查询的where子句提供一个大于判断值，
	 * 必须在{@link #where(String)}
	 * 或者 {@link #whereOr(String)}方法后调用
	 * @param value 判断值
	 * @return QueryBuilder构造器
	 */
	public QueryBuilder largeThan(Object value) {
		whereString.append(">");
		whereString.append(value);
		return this;
	}
	/**
	 * 为构建sqlite查询的where子句提供一个小于判断值，
	 * 必须在{@link #where(String)}
	 * 或者 {@link #whereOr(String)}方法后调用
	 * @param value 判断值
	 * @return QueryBuilder构造器
	 */
	public QueryBuilder lessThan(Object value) {
		whereString.append("<");
		whereString.append(value);
		return this;
	}
	/**
	 * 为构建sqlite查询的where子句提供一个大于等于判断值，
	 * 必须在{@link #where(String)}
	 * 或者 {@link #whereOr(String)}方法后调用
	 * @param value 判断值
	 * @return QueryBuilder构造器
	 */
	public QueryBuilder largeOrEqual(Object value) {
		whereString.append(">=");
		whereString.append(value);
		return this;
	}
	/**
	 * 为构建sqlite查询的where子句提供一个小于等于判断值，
	 * 必须在{@link #where(String)}
	 * 或者 {@link #whereOr(String)}方法后调用
	 * @param value 判断值
	 * @return QueryBuilder构造器
	 */
	public QueryBuilder lessOrEqual(Object value) {
		whereString.append("<=");
		whereString.append(value);
		return this;
	}
	
	/**
	 * 执行插入
	 * @return 返回受影响的结果数
	 */
	public int insert() {
		execString.append("insert into ");
		execString.append(tableName);
		execString.append(" ");
		String fields = getFieldsString(contentValues.keySet().toArray());
		String values = getValuesString(contentValues.values().toArray());
		execString.append(fields);
		execString.append(" values ");
		execString.append(values);
		
		return executeInsert(execString.toString());
	}
	
	/**
	 * 为查询数据库提供所要查询的列名
	 * @param f 字段名
	 * @return QueryBuilder构造器
	 */
	public QueryBuilder field(String f) {
		if (fieldsString.length() > 0) {
			fieldsString.append(",");
		}
		fieldsString.append(f);
		return this;
	}
	
	/**
	 * 为select语句提供查询条数限制
	 * @param limit 限制条数
	 * @return QueryBuilder构造器
	 */
	public QueryBuilder limit(int limit) {
		this.limit = limit;
		return this;
	}

	/**
	 * 为select语句提供排序操作
	 * @param order 要排序的字段/操作，可根据要求附加ASC或者DESC
	 * @return QueryBuilder构造器
	 */
	public QueryBuilder order(String order) {
		orderString = order;
		return this;
	}

	/**
	 * 为select语句提供分组操作
	 * @param group 要分组的字段
	 * @return QueryBuilder构造器
	 */
	public QueryBuilder group(String group) {
		groupString = group;
		return this;
	}
	
	/**
	 * 查询数据库返回结果集
	 * 如果只需要查询一条结果可以使用{@link #first()}
	 * @return 结果列表
	 * @see #first()
	 */
	public List<Result> list() {
		execString.append("select ");
		String[] fields = fieldsString.toString().split(",");
		for (String f : fields) {
			execString.append(f);
			execString.append(",");
		}
		execString.delete(execString.length() - 1, execString.length());
		execString.append(" from ");
		execString.append(tableName);
		if (whereString!=null) {
			execString.append(" ");
			execString.append(whereString);
		}
		if (limit!=0) {
			execString.append(" limit ");
			execString.append(limit);
		}
		if (groupString!=null) {
			execString.append(" ").append("group by ").append(groupString);
		}
		if (orderString!=null) {
			execString.append(" ").append("order by ").append(orderString);
		}

		L.d(SQL_TAG, execString.toString());

		Cursor cursor = db.rawQuery(execString.toString(), null);
		int columns = cursor.getColumnCount();

		List<Result> rl = new ArrayList<>();
		while (cursor.moveToNext()) {
			for (int i=0;i<columns;i++) {
				Result r = new Result();
				switch (cursor.getType(i)) {
					case Cursor.FIELD_TYPE_INTEGER:
						r.put(cursor.getColumnName(i), cursor.getInt(i));
						break;
					case Cursor.FIELD_TYPE_FLOAT:
						r.put(cursor.getColumnName(i), cursor.getFloat(i));
						break;
					case Cursor.FIELD_TYPE_STRING:
						r.put(cursor.getColumnName(i), cursor.getString(i));
						break;
					default:
						break;
				}
				rl.add(r);
			}
		}
		cursor.close();
		return rl;
	}
	
	/**
	 * 查询数据库返回第一条结果
	 * 如果要查询所有结果可以使用{@link #list()}
	 * @return 单条结果
	 */
	public Result first() {
		execString.append("select ");
		String[] fields = fieldsString.toString().split(",");
		for (String f : fields) {
			execString.append(f);
			execString.append(",");
		}
		execString.delete(execString.length() - 1, execString.length());
		execString.append(" from ");
		execString.append(tableName);
		if (whereString!=null) {
			execString.append(" ");
			execString.append(whereString);
		}
		execString.append(" limit 1");
		if (groupString!=null) {
			execString.append(" ").append("group by ").append(groupString);
		}
		if (orderString!=null) {
			execString.append(" ").append("order by ").append(orderString);
		}

		L.d(SQL_TAG, execString.toString());

		Cursor cursor = db.rawQuery(execString.toString(), null);
		int columns = cursor.getColumnCount();

		if (cursor.moveToNext()) {
			Result r = new Result();
			for (int i=0;i<columns;i++) {
				switch (cursor.getType(i)) {
					case Cursor.FIELD_TYPE_INTEGER:
						r.put(cursor.getColumnName(i), cursor.getInt(i));
						break;
					case Cursor.FIELD_TYPE_FLOAT:
						r.put(cursor.getColumnName(i), cursor.getFloat(i));
						break;
					case Cursor.FIELD_TYPE_STRING:
						r.put(cursor.getColumnName(i), cursor.getString(i));
						break;
					default:
						break;
				}
			}
			cursor.close();
			return r;
		} else {
			cursor.close();
			return null;
		}
	}
	
	/**
	 * 执行数据库更新操作
	 * @return 返回受影响的条数
	 */
	public int update() {
		execString.append("update ");
		execString.append(tableName);
		execString.append(" set ");
		for (String key : contentValues.keySet()) {
			Object v = contentValues.get(key);
			execString.append(key);
			execString.append("=");
			if (v instanceof Integer || v instanceof Short || v instanceof Long
					|| v instanceof Float) {
				execString.append((v.equals(NIL))?"NULL":v);
			} else if (v instanceof String){
				execString.append((v.equals(NIL))?"NULL":("\'"+v+"\'"));
			}
			execString.append(",");
		}
		execString.replace(execString.length() - 1, execString.length(), " ");
		execString.append(whereString);
		
		return executeUpdateOrDelete(execString.toString());
	}
	
	/**
	 * 执行数据库删除操作
	 * @return 返回受影响的条数
	 */
	public int delete() {
		execString.append("delete from ");
		execString.append(tableName);
		execString.append(" ");
		execString.append(whereString);
		return executeUpdateOrDelete(execString.toString());
	}
	
	/**
	 * 删除表
	 * @return 是否修改成功
	 */
	public boolean drop() {
		execString.append("drop table ");
		execString.append(tableName);

		L.d(SQL_TAG, execString.toString());

		try {
			db.execSQL(execString.toString());
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 为表结构添加一个字段
	 * @param field 字段名
	 * @param attr 字段属性
	 * @return QueryBuilder构造器
	 */
	public QueryBuilder addColumn(String field, String attr) {
		if (addColumnString == null) {
			addColumnString = new StringBuilder("");
			addColumnString.append("add column ");
		} else {
			addColumnString.append(",");
		}
		addColumnString.append(field)
				.append(" ")
				.append(attr);
		return this;
	}

	/**
	 * 修改指定表结构
	 * @return 是否修改成功
	 */
	public boolean alter() {
		execString.append("alter table ")
				.append(tableName)
				.append(" ");
		if (addColumnString!=null) {
			execString.append(addColumnString);
		}
		try {
			db.execSQL(execString.toString());
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 修改指定表的表名
	 * @param newName 新表名
	 * @return 是否修改成功
	 */
	public boolean rename(String newName) {
		execString.append("alter table ")
				.append(tableName)
				.append(" rename to ")
				.append(newName);
		try {
			db.execSQL(execString.toString());
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 将字段名数组转化为以","分割，以"()"包括的字符串
	 * @param fields 字段名数组
	 * @return 以","分割，以"()"包括的字符串
	 */
	private String getFieldsString(Object[] fields) {
		StringBuilder sb = new StringBuilder("(");
		for (Object f : fields) {
			sb.append(f);
			sb.append(",");
		}
		sb.replace(sb.length()-1, sb.length(), ")");
		return sb.toString();
	}
	/**
	 * 将值数组转化为以","分割，以"()"包括的字符串
	 * @param values 值数组
	 * @return 以","分割，以"()"包括的字符串
	 */
	private String getValuesString(Object[] values) {
		StringBuilder sb = new StringBuilder("(");
		for (Object v : values) {
			if (v instanceof Integer || v instanceof Short || v instanceof Long
					|| v instanceof Float) {
				sb.append((v.equals(NIL))?"NULL":v);
			} else if (v instanceof String){
				sb.append((v.equals(NIL))?"NULL":("\'"+v+"\'"));
			}
			sb.append(",");
		}
		sb.replace(sb.length() - 1, sb.length(), ")");
		return sb.toString();
	}
	
	/**
	 * 执行sql插入
	 * @param sql sql语句
	 * @return 返回受影响的结果数
	 */
	private int executeInsert(String sql) {
		L.d(SQL_TAG, execString.toString());
		try {
			SQLiteStatement statement = db.compileStatement(sql);
			return (int) statement.executeInsert();
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	/**
	 * 执行sql更新或删除操作
	 * @param sql sql语句
	 * @return 返回受影响的结果数
	 */
	private int executeUpdateOrDelete(String sql) {
		L.d(SQL_TAG, execString.toString());
		try {
			SQLiteStatement statement = db.compileStatement(sql);
			return statement.executeUpdateDelete();
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}
}
