package com.piliguerra.android.hbpm.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseAdapter {

	private static final String DATABASE_NAME = "data"; 
	private static final String DATABASE_TABLE = "songs";
	private static final int DATABASE_VERSION = 1;
	public static final String KEY_URI = "uri";
	public static final String KEY_BPM = "bpm";
	public static final String KEY_ROWID = "_id";
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;

	private static final String DATABASE_CREATE =
			"create table " + DATABASE_TABLE + " ("
			+ KEY_ROWID + " integer primary key autoincrement, "
			+ KEY_URI + " text not null, "
			+ KEY_BPM + " text not null);";
	
	private final Context mCtx; 
	
	public DatabaseAdapter(Context ctx) {
		this.mCtx = ctx;
	}
	
	public DatabaseAdapter open() throws android.database.SQLException {
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}
	
	public long createEntry(String path, int bpm) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_URI, path);
		initialValues.put(KEY_BPM, bpm);
		return mDb.insert(DATABASE_TABLE, null, initialValues);
	}
	
	public Cursor fetchEntriesByBPM(int bpm) throws SQLException {
		Cursor mCursor = mDb.query(true, DATABASE_TABLE, new String[] {KEY_URI , KEY_BPM}, KEY_BPM + "=" + bpm, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	
	public void close() {
		mDbHelper.close();
	}
	
	private static class DatabaseHelper extends SQLiteOpenHelper { 
		DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION); 
		}
		@Override
		public void onCreate(SQLiteDatabase db) { 
		db.execSQL(DATABASE_CREATE); 
		}
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion,int newVersion) { 
		// Not used, but you could upgrade the database with ALTER
		// Scripts
		}
	}
}
