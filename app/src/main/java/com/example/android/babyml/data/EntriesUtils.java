package com.example.android.babyml.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import android.widget.Toast;

import com.example.android.babyml.legacy.FeedingContract;
import com.example.android.babyml.legacy.FeedingDbHelper;
import com.example.android.babyml.legacy.FeedingUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by WKaczurb on 8/6/2017.
 */
public class EntriesUtils {

    private static final String TAG = EntriesUtils.class.getSimpleName();

//    public static long insertFeeding(SQLiteDatabase db, int amount, long timestampMilis) {
//        ContentValues values = new ContentValues();
//        values.put(FeedContract.FeedingEntry.COLUMN_FEED_AMOUNT, amount); //10);
//        values.put(FeedContract.FeedingEntry.COLUMN_FEED_TS, timestampMilis); //System.currentTimeMillis());
//        return db.insert(FeedContract.FeedingEntry.TABLE_NAME, null, values);
//    }
//
//    /**
//     * Delet
//     * @param db
//     * @param id
//     */
//    public static void deleteFeeding(SQLiteDatabase db, long id) {
//        db.delete(FeedContract.FeedingEntry.TABLE_NAME, "_ID=" + id, null ); // equivalent to "_ID =" + id
//    }

//    public static List<String> cursorAsStringList(Cursor cursor) {
//        List<String> list = new ArrayList<>();
//
//        if (!cursor.moveToFirst()) {
//            return list; // empty list;
//        }
//
//        do {
//            int feedAmount = cursor.getInt(cursor.getColumnIndex(FeedContract.FeedingEntry.COLUMN_FEED_AMOUNT));
//            long feedTimestamp = cursor.getLong(cursor.getColumnIndex(FeedContract.FeedingEntry.COLUMN_FEED_TS));
//
//
//            android.text.format.DateFormat df = new android.text.format.DateFormat();
//            CharSequence csTime = df.format("E yyyy-MM-dd HH:mm", feedTimestamp); // TODO: Consider adding option to select 12 vs 24 hours.
//
//            StringBuilder sb = new StringBuilder();
//            sb.append(csTime).append(" : milk= ").append(feedAmount);
//            list.add(sb.toString());
//
//        } while (cursor.moveToNext());
//        //return sb.toString();
//        return list;
//    }

//    public static String cursorAsString(Cursor cursor) {
//        StringBuilder sb = new StringBuilder();
//        List<String> list = cursorAsStringList(cursor);
//        for (String s : list) {
//            sb.append(s).append("\n");
//        }
//        return sb.toString();
//    }

//    public static Cursor getLatestFeeding(SQLiteDatabase db) {
//        return db.query(
//                FeedContract.FeedingEntry.TABLE_NAME,
//                null,
//                null,
//                null,
//                null,
//                null,
//                FeedContract.FeedingEntry.COLUMN_FEED_TS + " DESC", "1");
//    }
//
//    public static Cursor getAllFeedingsCursor(SQLiteDatabase db) {
//        return db.query(
//                FeedContract.FeedingEntry.TABLE_NAME,
//                null,
//                null,
//                null,
//                null,
//                null,
//                FeedContract.FeedingEntry.COLUMN_FEED_TS + " DESC");
//    }
//
//    public static Cursor getAllEntriesCursor(SQLiteDatabase db) {
//        return db.query(
//                EntriesDbHandler.ENTRIES_ALL_VIEW,
//                null,
//                null,
//                null,
//                null,
//                null,
//                null); // The View is already sorted.
//    }
//
//    /**
//     * Deletes all entries in the Feedings table.
//     * @param db - writeable database
//     * @return number of rows affected.
//     */
//    public static int deleteAllFeedings(SQLiteDatabase db) {
//        return db.delete(FeedContract.FeedingEntry.TABLE_NAME, null, null);
//    }
//
//    public static long insertNappy(SQLiteDatabase db, int dirty, long timeStampMilis) {
//        ContentValues values = new ContentValues();
//        values.put(NappyContract.NappyEntry.COLUMN_NAPPY_TS, timeStampMilis);
//        values.put(NappyContract.NappyEntry.COLUMN_NAPPY_DIRTY, dirty);
//        return db.insert(NappyContract.NappyEntry.TABLE_NAME, null, values);
//    }
//
//    public static void deleteNappy(SQLiteDatabase db, long id) {
//        db.delete(NappyContract.NappyEntry.TABLE_NAME, "_ID=" + id, null);
//    }

    public static void tryUpgradeFromOld(Context ctx) {
        File dbFile = ctx.getDatabasePath(FeedingDbHelper.DB_NAME);
        boolean oldDbExists = dbFile.exists();

        FeedingDbHelper oldFeedingDbHelper = new FeedingDbHelper(ctx);

        if (!oldDbExists) {
            //Log.d(TAG, "FeedingDbHelper did not exits - creating now...");
            Toast.makeText(ctx, "Old DB does not exist;", Toast.LENGTH_LONG).show();
            return;
        } else {
            Toast.makeText(ctx, "FeedingDbHelper existed - upgrading", Toast.LENGTH_LONG).show();

            // If exists
            try {
                int cnt = 0;
                SQLiteDatabase oldDb = oldFeedingDbHelper.getReadableDatabase();
                Cursor cursor = FeedingUtils.getAllFeedingsCursor(oldDb);
                while (cursor.moveToNext()) {
                    int feedAmount = cursor.getInt(cursor.getColumnIndex(FeedingContract.FeedingEntry.COLUMN_FEED_AMOUNT));
                    long feedTimestamp = cursor.getLong(cursor.getColumnIndex(FeedingContract.FeedingEntry.COLUMN_FEED_TIMESTAMP));
                    long id = cursor.getLong(cursor.getColumnIndex(FeedingContract.FeedingEntry._ID));
                    Log.d(TAG, String.format("ALL FEEDINGS [%d]: %d ml; ts=%d", id, feedAmount, feedTimestamp));

                    //long newId = EntriesUtils.insertFeeding(newDb, feedAmount, feedTimestamp);
                    long newId = EntriesDbHandler
                            .getInstance(ctx)
                            .insertFeeding(feedAmount, feedTimestamp);

                    Log.d(TAG, "Inserted into new table Feed with id=" + newId);
                    cnt += 1;
                }

                try {
                    ctx.deleteDatabase(FeedingDbHelper.DB_NAME);
                    Toast.makeText(ctx, "Inserted " + cnt + " items into new DB; Old one deleted ok.", Toast.LENGTH_LONG).show();
                } catch (Exception e){
                    //Log.d(TAG, "DELETING DB -> CAUGHT AN EXCEPTION");
                    Toast.makeText(ctx, "Inserted " + cnt + " items into new DB; FAILED WIHLE DELETING OLD DB.", Toast.LENGTH_LONG).show();
                }
            } catch (SQLiteException e) {
                Log.d(TAG, "Could not read TABLE. Aborting.");
            }

        }
    }
}



