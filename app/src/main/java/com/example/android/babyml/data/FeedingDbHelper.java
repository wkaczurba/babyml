package com.example.android.babyml.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by WKaczurb on 8/5/2017.
 */

public class FeedingDbHelper extends SQLiteOpenHelper {

    static final String TAG = FeedingDbHelper.class.getSimpleName();
    static final String DB_NAME = "feeds.db"; // TODO: Rename it.
    static final int DB_VERSION = 1; // TODO: Rename it.

    //public FeedingDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
    public FeedingDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_FEEDING_TABLE = "CREATE TABLE " + FeedingContract.FeedingEntry.TABLE_NAME + " (" +
                FeedingContract.FeedingEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                FeedingContract.FeedingEntry.COLUMN_FEED_AMOUNT + " NUMBER(3, 0)," +
                FeedingContract.FeedingEntry.COLUMN_FEED_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";
        db.execSQL(SQL_CREATE_FEEDING_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //if (oldVersion != newVersion) {
        Log.d(TAG, String.format("Upgrading of DB from %0d to %0d should take place here...; for now - old version will be dropped.", oldVersion, newVersion));
        String SQL_DROP_OLD_TABLE = "DROP TABLE IF EXISTS " + FeedingContract.FeedingEntry.TABLE_NAME;
        db.execSQL(SQL_DROP_OLD_TABLE);
        onCreate(db);
        //}
    }
}
