package com.rubylicious.climbingtracker;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DataSource {

	// Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] allClimbColumns = { MySQLiteHelper.COLUMN_ID,
			MySQLiteHelper.COLUMN_NAME, MySQLiteHelper.COLUMN_LOCATION,
			MySQLiteHelper.COLUMN_AREA, MySQLiteHelper.COLUMN_GRADE,
			MySQLiteHelper.COLUMN_MOVES, MySQLiteHelper.COLUMN_CAL,
			MySQLiteHelper.COLUMN_SLOPE, MySQLiteHelper.COLUMN_TYPE,
			MySQLiteHelper.COLUMN_COLOR, MySQLiteHelper.COLUMN_INJURY,
			MySQLiteHelper.COLUMN_STATUS, MySQLiteHelper.COLUMN_TERRAIN,
			MySQLiteHelper.COLUMN_FIRST_ATTEMPT_DATE,
			MySQLiteHelper.COLUMN_FIRST_ATTEMPT_TOTAL,
			MySQLiteHelper.COLUMN_SEND_DATE, MySQLiteHelper.COLUMN_SEND_TOTAL,
			MySQLiteHelper.COLUMN_PHOTO, MySQLiteHelper.COLUMN_PHOTO_COMMENTS,			
			MySQLiteHelper.COLUMN_RATING };

	private String[] allLocationColumns = { MySQLiteHelper.COLUMN_ID,
			MySQLiteHelper.COLUMN_NAME, MySQLiteHelper.COLUMN_ADDRESS,
			MySQLiteHelper.COLUMN_GPS, MySQLiteHelper.COLUMN_COMMENTS };

	private String[] allAreaColumns = { MySQLiteHelper.COLUMN_ID,
			MySQLiteHelper.COLUMN_NAME, MySQLiteHelper.COLUMN_LOCATION,
			MySQLiteHelper.COLUMN_COMMENTS };
	
	private String[] allHistoryColumns = { MySQLiteHelper.COLUMN_ID,
			MySQLiteHelper.COLUMN_CLIMB_ID, MySQLiteHelper.COLUMN_DATE,
			MySQLiteHelper.COLUMN_MOVES };

	public DataSource(Context context) {
		dbHelper = new MySQLiteHelper(context);
	}

	public void openWrite() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void openRead() throws SQLException {
		database = dbHelper.getReadableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public void addClimb(Climb climb) {
		openWrite();
		ContentValues values = createClimbValues(climb);
		database.insert(MySQLiteHelper.TABLE_CLIMB, null, values);
		close();
	}

	public void addLocation(Location loc) {
		openWrite();
		ContentValues values = createLocationValues(loc);
		database.insert(MySQLiteHelper.TABLE_LOCATION, null, values);
		close();
	}

	public void addArea(Area area) {
		openWrite();
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_NAME, area.getName());
		values.put(MySQLiteHelper.COLUMN_LOCATION, area.getLocation());
		values.put(MySQLiteHelper.COLUMN_COMMENTS, area.getComments());
		database.insert(MySQLiteHelper.TABLE_AREA, null, values);
		close();
	}
	
	public void addHistory(History history){
		openWrite();
		ContentValues values = createHistoryValues(history);
		database.insert(MySQLiteHelper.TABLE_HISTORY, null, values);
		close();
	}

	public void deleteClimb(int id) {
		openWrite();
		database.delete(MySQLiteHelper.TABLE_CLIMB, MySQLiteHelper.COLUMN_ID
				+ " = ?", new String[] { String.valueOf(id) });
		close();
	}

	public void deleteLocation(int id) {
		List<Area> areas = getAllAreasPerLocation(id);
		openWrite();

		for (Area area : areas) {
			deleteArea(area.getId());
		}

		database.delete(MySQLiteHelper.TABLE_AREA,
				MySQLiteHelper.COLUMN_LOCATION + " = ?",
				new String[] { String.valueOf(id) });

		database.delete(MySQLiteHelper.TABLE_LOCATION, MySQLiteHelper.COLUMN_ID
				+ " = ?", new String[] { String.valueOf(id) });
		close();
	}

	public void deleteArea(int id) {
		openWrite();

		database.delete(MySQLiteHelper.TABLE_AREA, MySQLiteHelper.COLUMN_ID
				+ " = ?", new String[] { String.valueOf(id) });
		close();
	}
	
	public void deleteHistory(int historyId){
		openWrite();
		
		database.delete(MySQLiteHelper.TABLE_HISTORY, MySQLiteHelper.COLUMN_ID
				+ " = ?", new String[] { String.valueOf(historyId) });
		close();
	}

	public Climb getClimb(int id) {
		openRead();
		Climb climb = null;
		Cursor cursor = database.query(MySQLiteHelper.TABLE_CLIMB,
				allClimbColumns, MySQLiteHelper.COLUMN_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);
		if (cursor.getCount() != 0) {
			cursor.moveToFirst();
			climb = cursorToClimb(cursor);
		}
		close();
		return climb;
	}

	public Location getLocation(int id) {
		openRead();
		Location loc = null;
		Cursor cursor = database.query(MySQLiteHelper.TABLE_LOCATION,
				allLocationColumns, MySQLiteHelper.COLUMN_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);
		if (cursor.getCount() != 0) {
			cursor.moveToFirst();
			loc = cursorToLocation(cursor);
		}

		close();
		return loc;
	}

	public Area getArea(int id, int locationId) {
		openRead();
		Area area = null;
		Cursor cursor = database.query(MySQLiteHelper.TABLE_AREA,
				allAreaColumns, MySQLiteHelper.COLUMN_ID + "=? AND "
						+ MySQLiteHelper.COLUMN_LOCATION + "=?", new String[] {
						String.valueOf(id), String.valueOf(locationId) }, null,
				null, null, null);
		if (cursor.getCount() != 0) {
			cursor.moveToFirst();
			area = cursorToArea(cursor);
		}
		close();
		return area;
	}

	public List<Climb> getAllClimbs() {
		List<Climb> climbs = new ArrayList<Climb>();
		openRead();
		Cursor cursor = database.query(MySQLiteHelper.TABLE_CLIMB,
				allClimbColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Climb climb = (Climb) cursorToClimb(cursor);
			climbs.add(climb);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		close();
		return climbs;
	}
	
	public List<History> getAllHistoryPerClimb(Integer climbId) {
		List<History> histories = new ArrayList<History>();
		openRead();
		Cursor cursor = database.query(MySQLiteHelper.TABLE_HISTORY,
				allHistoryColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			History history = cursorToHistory(cursor);
			histories.add(history);
			
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		close();
		return histories;
	}

	public List<Location> getAllLocations() {
		List<Location> locations = new ArrayList<Location>();
		openRead();
		Cursor cursor = database.query(MySQLiteHelper.TABLE_LOCATION,
				allLocationColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Location location = cursorToLocation(cursor);
			locations.add(location);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		close();
		return locations;
	}

	public List<Area> getAllAreasPerLocation(Integer locationId) {
		List<Area> areas = new ArrayList<Area>();
		openRead();
		Cursor cursor = database.query(MySQLiteHelper.TABLE_AREA,
				allAreaColumns, MySQLiteHelper.COLUMN_LOCATION + "=?",
				new String[] { String.valueOf(locationId) }, null, null, null,
				null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Area area = cursorToArea(cursor);
			areas.add(area);
			
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		close();
		return areas;
	}

	public void updateClimb(Climb climb) {
		openWrite();
		ContentValues values = createClimbValues(climb);
		String whereClause = MySQLiteHelper.COLUMN_ID + " = ?";
		String[] whereArgs = new String[] { String.valueOf(climb.getId()) };
		database.update(MySQLiteHelper.TABLE_CLIMB, values, whereClause,
				whereArgs);
		close();
	}
	
	public void updateClimbFromAttempt(Climb climb) {
		openWrite();
		ContentValues values = createClimbAttemptValues(climb);
		String whereClause = MySQLiteHelper.COLUMN_ID + " = ?";
		String[] whereArgs = new String[] { String.valueOf(climb.getId()) };
		database.update(MySQLiteHelper.TABLE_CLIMB, values, whereClause,
				whereArgs);
		close();
	}

	private ContentValues createClimbAttemptValues(Climb climb) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_NAME, climb.getName());
		values.put(MySQLiteHelper.COLUMN_SLOPE, climb.getSlopeId());
		values.put(MySQLiteHelper.COLUMN_TYPE, climb.getTypeId());
		values.put(MySQLiteHelper.COLUMN_STATUS, climb.getStatus());
		values.put(MySQLiteHelper.COLUMN_INJURY, climb.getInjury());
		values.put(MySQLiteHelper.COLUMN_TERRAIN, String.valueOf(climb.isGym()));
		values.put(MySQLiteHelper.COLUMN_GRADE, climb.getGradeId());
		values.put(MySQLiteHelper.COLUMN_MOVES, climb.getMoves());
		values.put(MySQLiteHelper.COLUMN_CAL, climb.getCal());
		values.put(MySQLiteHelper.COLUMN_COLOR, climb.getColor());
		values.put(MySQLiteHelper.COLUMN_SEND_DATE,
				climb.getSendDate() == null ? null : climb.getSendDate()
						.toString());
		values.put(MySQLiteHelper.COLUMN_SEND_TOTAL, climb.getSendTotal());
		values.put(MySQLiteHelper.COLUMN_FIRST_ATTEMPT_DATE, climb
				.getFirstAttemptDate() == null ? null : climb
				.getFirstAttemptDate().toString());
		values.put(MySQLiteHelper.COLUMN_FIRST_ATTEMPT_TOTAL,
				climb.getFirstAttemptTotal());
		values.put(MySQLiteHelper.COLUMN_PHOTO, climb.getPhoto());
		values.put(MySQLiteHelper.COLUMN_PHOTO_COMMENTS, climb.getComments());
		values.put(MySQLiteHelper.COLUMN_RATING, climb.getRating());
		return values;
	}

	public void updateLocation(Location loc) {
		openWrite();
		ContentValues values = createLocationValues(loc);
		String whereClause = MySQLiteHelper.COLUMN_ID + " = ?";
		String[] whereArgs = new String[] { String.valueOf(loc.getId()) };
		database.update(MySQLiteHelper.TABLE_LOCATION, values, whereClause,
				whereArgs);
		close();
	}

	public void updateArea(Area area) {
		openWrite();
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_NAME, area.getName());
		values.put(MySQLiteHelper.COLUMN_LOCATION, area.getLocation());
		values.put(MySQLiteHelper.COLUMN_COMMENTS, area.getComments());
		String whereClause = MySQLiteHelper.COLUMN_ID + " = ?";
		String[] whereArgs = new String[] { String.valueOf(area.getId()) };
		database.update(MySQLiteHelper.TABLE_AREA, values, whereClause,
				whereArgs);
		close();
	}

	private Climb cursorToClimb(Cursor cursor) {
		Climb climb = new Climb(cursor.getInt(0), cursor.getString(1),
				cursor.getInt(2), cursor.getInt(3), cursor.getInt(4),
				cursor.getInt(5), cursor.getInt(6), cursor.getInt(7),
				cursor.getInt(8), cursor.getInt(9), cursor.getInt(10),
				cursor.getInt(11), Boolean.valueOf(cursor.getString(12)),
				(cursor.getString(13) == null) ? null : LocalDate.parse(
						cursor.getString(13),
						DateTimeFormat.forPattern("yyyy-MM-dd")),
				cursor.getInt(14), (cursor.getString(15) == null) ? null
						: LocalDate.parse(cursor.getString(15),
								DateTimeFormat.forPattern("yyyy-MM-dd")),
				cursor.getInt(16), cursor.getBlob(17), cursor.getString(18), cursor.getDouble
				(19));

		return climb;
	}

	private Location cursorToLocation(Cursor cursor) {
		Location location = new Location(cursor.getInt(0), cursor.getString(1),
				cursor.getString(2), cursor.getString(3), cursor.getString(4));
		return location;
	}
	
	private History cursorToHistory(Cursor cursor) {
		History history = new History(cursor.getInt(0), cursor.getInt(1), (cursor.getString(2) == null) ? null : 
			new DateTime(cursor.getLong(2)), cursor.getInt(3));
		return history;
	}
	private Area cursorToArea(Cursor cursor) {
		Area area = new Area(cursor.getInt(0), cursor.getString(1),
				cursor.getInt(2), cursor.getString(3));
		return area;
	}

	private ContentValues createClimbValues(Climb climb) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_NAME, climb.getName());
		values.put(MySQLiteHelper.COLUMN_LOCATION, climb.getLocationId());
		values.put(MySQLiteHelper.COLUMN_AREA, climb.getAreaId());
		values.put(MySQLiteHelper.COLUMN_SLOPE, climb.getSlopeId());
		values.put(MySQLiteHelper.COLUMN_TYPE, climb.getTypeId());
		values.put(MySQLiteHelper.COLUMN_STATUS, climb.getStatus());
		values.put(MySQLiteHelper.COLUMN_INJURY, climb.getInjury());
		values.put(MySQLiteHelper.COLUMN_TERRAIN, String.valueOf(climb.isGym()));
		values.put(MySQLiteHelper.COLUMN_GRADE, climb.getGradeId());
		values.put(MySQLiteHelper.COLUMN_MOVES, climb.getMoves());
		values.put(MySQLiteHelper.COLUMN_CAL, climb.getCal());
		values.put(MySQLiteHelper.COLUMN_COLOR, climb.getColor());
		values.put(MySQLiteHelper.COLUMN_SEND_DATE,
				climb.getSendDate() == null ? null : climb.getSendDate()
						.toString());
		values.put(MySQLiteHelper.COLUMN_SEND_TOTAL, climb.getSendTotal());
		values.put(MySQLiteHelper.COLUMN_FIRST_ATTEMPT_DATE, climb
				.getFirstAttemptDate() == null ? null : climb
				.getFirstAttemptDate().toString());
		values.put(MySQLiteHelper.COLUMN_FIRST_ATTEMPT_TOTAL,
				climb.getFirstAttemptTotal());
		values.put(MySQLiteHelper.COLUMN_PHOTO, climb.getPhoto());
		values.put(MySQLiteHelper.COLUMN_PHOTO_COMMENTS, climb.getComments());
		values.put(MySQLiteHelper.COLUMN_RATING, climb.getRating());
		return values;
	}

	ContentValues createLocationValues(Location loc) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_NAME, loc.getName());
		values.put(MySQLiteHelper.COLUMN_ADDRESS, loc.getAddress());
		values.put(MySQLiteHelper.COLUMN_GPS, loc.getGpsCoordinates());
		values.put(MySQLiteHelper.COLUMN_COMMENTS, loc.getComments());
		return values;
	}
	
	ContentValues createHistoryValues(History history) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_CLIMB_ID, history.getClimbId());
		values.put(MySQLiteHelper.COLUMN_DATE, System.currentTimeMillis());
		values.put(MySQLiteHelper.COLUMN_MOVES, history.getMoves());
		return values;
	}

	public int getClimbId() {
		String countQuery = "SELECT " + MySQLiteHelper.COLUMN_ID + " FROM "
				+ MySQLiteHelper.TABLE_CLIMB;
		return getLastIndex(countQuery);
	}

	public int getLocationId() {
		String countQuery = "SELECT " + MySQLiteHelper.COLUMN_ID + " FROM "
				+ MySQLiteHelper.TABLE_LOCATION;
		return getLastIndex(countQuery);
	}

	public int getAreaId() {
		String countQuery = "SELECT " + MySQLiteHelper.COLUMN_ID + " FROM "
				+ MySQLiteHelper.TABLE_AREA;
		return getLastIndex(countQuery);
	}

	private int getLastIndex(String countQuery) {
		openRead();
		Cursor cursor = database.rawQuery(countQuery, null);
		// return count
		int count = cursor.getCount();
		cursor.close();
		close();
		return count;
	}

	public boolean canDeleteArea(int id) {
		List<Climb> climbs = getAllClimbs();
		for(Climb climb : climbs){
			if(id == climb.getAreaId()){
				return false;
			}
		}
		return true;
	}

	public boolean canDeleteLocation(int id) {
		List<Climb> climbs = getAllClimbs();
		for(Climb climb : climbs){
			if(id == climb.getLocationId()){
				return false;
			}
		}
		return true;
	}

	public History getHistory(int id) {
		openRead();
		History history = null;
		Cursor cursor = database.query(MySQLiteHelper.TABLE_HISTORY,
				allHistoryColumns, MySQLiteHelper.COLUMN_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);
		if (cursor.getCount() != 0) {
			cursor.moveToFirst();
			history = cursorToHistory(cursor);
		}
		close();
		return history;
	}
}
