package com.rubylicious.climbingtracker;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {
	
	// All Static variables
    // Database Version
	private static final int DATABASE_VERSION = 1;
 
    // Database Name
	private static final String DATABASE_NAME = "climbingRecord.db";

	public static final String COLUMN_ID = "id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_COMMENTS = "comments";
	
	public static final String TABLE_LOCATION = "location";
	public static final String COLUMN_LOCATION = "location_id";
	public static final String COLUMN_ADDRESS = "address";
	public static final String COLUMN_GPS = "gps";
	
	public static final String TABLE_AREA = "area";
	public static final String COLUMN_AREA = "area_id";
	
	public static final String TABLE_CLIMB = "climb";
	public static final String COLUMN_SLOPE = "slope";
	public static final String COLUMN_TYPE = "type";	
	public static final String COLUMN_STATUS = "status";
	public static final String COLUMN_INJURY = "injury";
	public static final String COLUMN_TERRAIN = "terrain";
	public static final String COLUMN_GRADE = "grade";
	public static final String COLUMN_MOVES = "moves";
	public static final String COLUMN_CAL = "cal";
	public static final String COLUMN_COLOR = "color";
	public static final String COLUMN_SEND_DATE = "send_date";
	public static final String COLUMN_SEND_TOTAL = "send_total";
	public static final String COLUMN_FIRST_ATTEMPT_DATE = "first_attempt_date";
	public static final String COLUMN_FIRST_ATTEMPT_TOTAL = "first_attempt_total";
	public static final String COLUMN_PHOTO = "photo";
	public static final String COLUMN_PHOTO_COMMENTS = "photo_comments";

	public static final String TABLE_HISTORY = "history";
	public static final String COLUMN_CLIMB_ID = "climb_id";
	public static final String COLUMN_DATE = "date";
	public static final String COLUMN_CLIMB_STATUS = "climb_status";
	public static final String COLUMN_ATTEMPTS = "attempts";
	public static final String COLUMN_SENDS = "sends";
	public static final String COLUMN_RATING = "rating";	

	// Database creation sql statement
	private static final String CLIMB_CREATE = "create table "
			+ TABLE_CLIMB + "(" + COLUMN_ID + " integer primary key autoincrement, " 
			+ COLUMN_NAME + " text not null, "
			+ COLUMN_LOCATION + " integer, "
			+ COLUMN_AREA + " integer, "
			+ COLUMN_GRADE + " integer, " 
			+ COLUMN_MOVES + " integer, " 
			+ COLUMN_CAL + " integer, " 
			+ COLUMN_SLOPE + " integer, " 
			+ COLUMN_TYPE + " integer, " 
			+ COLUMN_COLOR + " integer, " 
			+ COLUMN_INJURY + " integer, " 
			+ COLUMN_STATUS + " integer, " 		
			+ COLUMN_TERRAIN + " text, " 
			+ COLUMN_FIRST_ATTEMPT_DATE + " integer, " 
			+ COLUMN_FIRST_ATTEMPT_TOTAL + " integer, " 		
			+ COLUMN_SEND_DATE + " integer, " 
			+ COLUMN_SEND_TOTAL + " integer, " 
			+ COLUMN_PHOTO + " blob, " 			
			+ COLUMN_PHOTO_COMMENTS + " text,"
			+ COLUMN_RATING + " real);";
	
	private static final String LOCATION_CREATE = "create table "
			+ TABLE_LOCATION + "(" + COLUMN_ID + " integer primary key autoincrement, " 
			+ COLUMN_NAME + " text not null, "	 
			+ COLUMN_ADDRESS + " text, " 
			+ COLUMN_GPS + " text,"
			+ COLUMN_COMMENTS + " text);";
	
	private static final String AREA_CREATE = "create table "
			+ TABLE_AREA + "(" + COLUMN_ID + " integer primary key autoincrement, " 
			+ COLUMN_LOCATION + " integer, "
			+ COLUMN_NAME + " text not null, " 
			+ COLUMN_COMMENTS + " text);";

	private static final String HISTORY_CREATE = "create table "
			+ TABLE_HISTORY + "(" + COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_CLIMB_ID + " integer not null, "
			+ COLUMN_DATE + " long not null, "
			+ COLUMN_MOVES + " integer)";

	public MySQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(CLIMB_CREATE);
		database.execSQL(LOCATION_CREATE);
		database.execSQL(AREA_CREATE);
		database.execSQL(HISTORY_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(MySQLiteHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLIMB);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_AREA);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
		onCreate(db);
	}

}
