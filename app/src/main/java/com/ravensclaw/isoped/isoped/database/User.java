package com.ravensclaw.isoped.isoped.database;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Created by CAD Station on 12/20/2016.
 */

public class User {

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + Columns.TABLE_NAME + " (" +
                    Columns._ID + " INTEGER PRIMARY KEY," +
                    Columns.COLUMN_NAME_FIRST + " TEXT," +
                    Columns.COLUMN_NAME_LAST + " TEXT," +
                    Columns.COLUMN_NOTES + " TEXT," +
                    Columns.COLUMN_PHONE + " TEXT)";
    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + Columns.TABLE_NAME;
    private Cursor userInfo;
    private DbHelper dbHelper;
    private Activity activity;
    public User(Activity activity, long uid) {
        this.activity = activity;
        dbHelper = DbHelper.getInstance(activity);

        String[] projection = {
                Columns._ID,
                Columns.COLUMN_NAME_FIRST,
                Columns.COLUMN_NAME_LAST,
                Columns.COLUMN_PHONE,
                Columns.COLUMN_NOTES
        };

        // Selection parameters
        String selection = Columns._ID + "=?";
        String[] selectionArgs = {Long.toString(uid)};

        userInfo = dbHelper.getReadableDatabase().query(
                Columns.TABLE_NAME,                         // The table to query
                projection,                                 // The columns to return
                selection,                                  // The columns for the WHERE clause
                selectionArgs,                              // The values for the WHERE clause
                null,                                       // don't group the rows
                null,                                       // don't filter by row groups
                null,                                       // The sort order
                "1"                                         // limit to 1
        );
    }

    static public Cursor getList(Activity activity) {
        DbHelper dbHelper = DbHelper.getInstance(activity);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String[] projection = {
                User.Columns._ID,
                Columns.COLUMN_NAME_FIRST,
                Columns.COLUMN_NAME_LAST
        };

        // Selection parameters
        String selection = "";
        String[] selectionArgs = {};

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                Columns.COLUMN_NAME_FIRST + " ASC";

        Cursor cursor = dbHelper.getReadableDatabase().query(
                User.Columns.TABLE_NAME,                  // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        return cursor;
    }

    static public long insert(Activity activity, ContentValues values) {
        DbHelper dbHelper = DbHelper.getInstance(activity);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // Insert the new row, returning the primary key value of the new row
        long insert_id = db.insert(User.Columns.TABLE_NAME, null, values);
        return insert_id;
    }

    public void delete() {
        DbHelper dbHelper = DbHelper.getInstance(activity);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String selection = User.Columns._ID + "=?";
        String[] selectionArgs = {Long.toString(getUID())};

        db.delete(User.Columns.TABLE_NAME, selection, selectionArgs);
    }

    public void update(ContentValues values) {
        DbHelper dbHelper = DbHelper.getInstance(activity);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String selection = User.Columns._ID + "=?";
        String[] selectionArgs = {Long.toString(getUID())};

        db.update(User.Columns.TABLE_NAME, values, selection, selectionArgs);
    }

    public long getUID() {
        long uid = -1;
        if (userInfo.moveToFirst()) {
            if (userInfo.getCount() > 0 && userInfo.getColumnIndex(Columns._ID) != -1) {
                uid = userInfo.getLong(userInfo.getColumnIndex(Columns._ID));
            }
        }
        return uid;
    }

    public String getFirstName() {
        String name = "";
        if (userInfo.moveToFirst()) {
            if (userInfo.getCount() > 0 && userInfo.getColumnIndex(Columns.COLUMN_NAME_FIRST) != -1) {
                name = userInfo.getString(userInfo.getColumnIndex(Columns.COLUMN_NAME_FIRST));
            }
        }
        return name;
    }

    public String getLastName() {
        String name = "";
        if (userInfo.moveToFirst()) {
            if (userInfo.getCount() > 0 && userInfo.getColumnIndex(Columns.COLUMN_NAME_LAST) != -1) {
                name = userInfo.getString(userInfo.getColumnIndex(Columns.COLUMN_NAME_LAST));
            }
        }
        return name;
    }

    public String getFullName() {
        return getFirstName() + " " + getLastName();
    }

    public String getPhone() {
        String phone = "";
        if (userInfo.moveToFirst()) {
            if (userInfo.getCount() > 0 && userInfo.getColumnIndex(Columns.COLUMN_PHONE) != -1) {
                phone = userInfo.getString(userInfo.getColumnIndex(Columns.COLUMN_PHONE));
            }
        }
        return phone;
    }

    public String getNotes() {
        String notes = "";
        if (userInfo.moveToFirst()) {
            if (userInfo.getCount() > 0 && userInfo.getColumnIndex(Columns.COLUMN_NOTES) != -1) {
                notes = userInfo.getString(userInfo.getColumnIndex(Columns.COLUMN_NOTES));
            }
        }
        return notes;
    }

    public static class Columns implements BaseColumns {
        public static final String TABLE_NAME = "users";
        public static final String COLUMN_NAME_FIRST = "name_first";
        public static final String COLUMN_NAME_LAST = "name_last";
        public static final String COLUMN_PHONE = "name";
        public static final String COLUMN_NOTES = "notes";
    }
}

