//package com.example.android.babyml.data;
//
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteException;
//import android.util.Log;
//import android.widget.Toast;
//
////import com.example.android.babyml.legacy.FeedingContract;
////import com.example.android.babyml.legacy.FeedingDbHelper;
////import com.example.android.babyml.legacy.FeedingUtils;
//
//import java.io.File;
//
///**
// * Created by WKaczurb on 8/6/2017.
// *
// * File for Entries functions.
// * Contains conversion from old to new DB version.
// */
//public class EntriesUtils {
//
//    private static final String TAG = EntriesUtils.class.getSimpleName();
//
//    public static void tryUpgradeFromOld(Context ctx) {
//        File dbFile = ctx.getDatabasePath(FeedingDbHelper.DB_NAME);
//        boolean oldDbExists = dbFile.exists();
//
//        FeedingDbHelper oldFeedingDbHelper = new FeedingDbHelper(ctx);
//
//        if (!oldDbExists) {
//            Toast.makeText(ctx, "Old DB does not exist;", Toast.LENGTH_LONG).show();
//        } else {
//            Toast.makeText(ctx, "FeedingDbHelper existed - upgrading", Toast.LENGTH_LONG).show();
//
//            // If exists
//            try {
//                int cnt = 0;
//                SQLiteDatabase oldDb = oldFeedingDbHelper.getReadableDatabase();
//                Cursor cursor = FeedingUtils.getAllFeedingsCursor(oldDb);
//                while (cursor.moveToNext()) {
//                    int feedAmount = cursor.getInt(cursor.getColumnIndex(FeedingContract.FeedingEntry.COLUMN_FEED_AMOUNT));
//                    long feedTimestamp = cursor.getLong(cursor.getColumnIndex(FeedingContract.FeedingEntry.COLUMN_FEED_TIMESTAMP));
//                    long id = cursor.getLong(cursor.getColumnIndex(FeedingContract.FeedingEntry._ID));
//                    Log.d(TAG, String.format("ALL FEEDINGS [%d]: %d ml; ts=%d", id, feedAmount, feedTimestamp));
//
//                    ContentValues cv = (new Feed(-1L, Feed.TABLE_NAME, feedTimestamp, feedAmount, null)).asContentValues();
//                    ctx.getContentResolver().insert(EntriesProvider.URI_FEEDS, cv);
//
//                    Log.d(TAG, "Inserted into new table Feed");
//                    cnt += 1;
//                }
//
//                try {
//                    ctx.deleteDatabase(FeedingDbHelper.DB_NAME);
//                    Toast.makeText(ctx, "Inserted " + cnt + " items into new DB; Old one deleted ok.", Toast.LENGTH_LONG).show();
//                } catch (Exception e){
//                    //Log.d(TAG, "DELETING DB -> CAUGHT AN EXCEPTION");
//                    Toast.makeText(ctx, "Inserted " + cnt + " items into new DB; FAILED WIHLE DELETING OLD DB.", Toast.LENGTH_LONG).show();
//                }
//            } catch (SQLiteException e) {
//                Log.d(TAG, "Could not read TABLE. Aborting.");
//            }
//
//        }
//    }
//}
//
//
//
