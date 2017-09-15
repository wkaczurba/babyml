package com.example.android.babyml.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WKaczurb on 8/6/2017.
 */
public class EntriesUtils {

    public static void insertFeeding(SQLiteDatabase db, int amount, long timestampMilis) {
        // TODO: Move this stuff into DbUtils class
        ContentValues values = new ContentValues();
        values.put(FeedContract.FeedingEntry.COLUMN_FEED_AMOUNT, amount); //10);
        values.put(FeedContract.FeedingEntry.COLUMN_FEED_TS, timestampMilis); //System.currentTimeMillis());
        db.insert(FeedContract.FeedingEntry.TABLE_NAME, null, values);
    }

    /**
     * Delet
     * @param db
     * @param id
     */
    public static void deleteFeeding(SQLiteDatabase db, long id) {
        db.delete(FeedContract.FeedingEntry.TABLE_NAME, "_ID=" + id, null ); // equivalent to "_ID =" + id
    }

    public static List<String> cursorAsStringList(Cursor cursor) {
        List<String> list = new ArrayList<>();

        if (!cursor.moveToFirst()) {
            return list; // empty list;
        }

        do {
            int feedAmount = cursor.getInt(cursor.getColumnIndex(FeedContract.FeedingEntry.COLUMN_FEED_AMOUNT));
            long feedTimestamp = cursor.getLong(cursor.getColumnIndex(FeedContract.FeedingEntry.COLUMN_FEED_TS));


            android.text.format.DateFormat df = new android.text.format.DateFormat();
            CharSequence csTime = df.format("E yyyy-MM-dd HH:mm", feedTimestamp); // TODO: Consider adding option to select 12 vs 24 hours.

            StringBuilder sb = new StringBuilder();
            sb.append(csTime).append(" : milk= ").append(feedAmount);
            list.add(sb.toString());

        } while (cursor.moveToNext());
        //return sb.toString();
        return list;
    }

    public static String cursorAsString(Cursor cursor) {
        StringBuilder sb = new StringBuilder();
        List<String> list = cursorAsStringList(cursor);
        for (String s : list) {
            sb.append(s).append("\n");
        }
        return sb.toString();
    }

    public static Cursor getLatestFeeding(SQLiteDatabase db) {
        return db.query(
                FeedContract.FeedingEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                FeedContract.FeedingEntry.COLUMN_FEED_TS + " DESC", "1");
    }

    public static Cursor getAllFeedingsCursor(SQLiteDatabase db) {
        return db.query(
                FeedContract.FeedingEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                FeedContract.FeedingEntry.COLUMN_FEED_TS + " DESC");
    }

    public static Cursor getAllEntriesCursor(SQLiteDatabase db) {
        return db.query(
                EntriesDbHelper.ENTRIES_ALL_VIEW,
                null,
                null,
                null,
                null,
                null,
                null); // The View is already sorted.
    }

    /**
     * Deletes all entries in the Feedings table.
     * @param db - writeable database
     * @return number of rows affected.
     */
    public static int deleteAllFeedings(SQLiteDatabase db) {
        return db.delete(FeedContract.FeedingEntry.TABLE_NAME, null, null);
    }
}

