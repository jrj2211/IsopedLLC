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
                    Columns.COLUMN_PHONE + " INT)";
    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + Columns.TABLE_NAME;
    Cursor userInfo;
    private DbHelper dbHelper;

    public User(Activity activity, long uid) {

        dbHelper = DbHelper.getInstance(activity);

        String[] projection = {
                Columns._ID,
                Columns.COLUMN_NAME_FIRST,
                Columns.COLUMN_NAME_LAST,
                Columns.COLUMN_PHONE
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

    static public long insert(Activity activity, ContentValues values) {
        DbHelper dbHelper = DbHelper.getInstance(activity);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // Insert the new row, returning the primary key value of the new row
        long insert_id = db.insert(User.Columns.TABLE_NAME, null, values);
        return insert_id;
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

    public static class Columns implements BaseColumns {
        public static final String TABLE_NAME = "users";
        public static final String COLUMN_NAME_FIRST = "name_first";
        public static final String COLUMN_NAME_LAST = "name_last";
        public static final String COLUMN_PHONE = "name";
        public static final String COLUMN_NOTES = "notes";
    }
}

