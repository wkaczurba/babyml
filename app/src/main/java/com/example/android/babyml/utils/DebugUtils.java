package com.example.android.babyml.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.example.android.babyml.data.EntriesDbHandler;
import com.example.android.babyml.data.EntriesProvider;
import com.example.android.babyml.data.EntriesProviderContract;
import com.example.android.babyml.data.Entry;
import com.example.android.babyml.data.Feed;

/**
 * Created by wkaczurb on 9/20/2017.
 */

public class DebugUtils {
    public static void printAllEntries(Context ctx, String tag) {
        String s = "printAllEntries():\n";
        Uri uri = EntriesProviderContract.URI_ENTRIES;

        Cursor cursor = ctx.getContentResolver()
                .query(uri,  null, null, null, Entry.COLUMN_ENTRY_TS  + " DESC");


        s += "printAllEntries() -> cursor.getCount() == " + cursor.getCount() + "\n";
        if (cursor.getCount() == 0) {
            s = "cursor.getCount() == 0";
            Log.d(tag, s);
            return;
        } else {
            while (cursor.moveToNext()) {
                Entry entry = Entry.fromCursor(cursor);

                s += String.format("[cursor at %d]; %s", cursor.getPosition(), entry.toString() + "\n"); //_id=%d, tb=%s, ts=%d\n", cursor.getPosition(), _id, tb, ts);
            }
        }
        s += "---------------------------\n";
        Log.d(tag, s);
    }

    public static void printAllFeeds(Context ctx, String tag) {
        String s = "printAllFeeds():\n";
        Uri uri = EntriesProviderContract.URI_FEEDS;

        Cursor cursor = ctx.getContentResolver()
                .query(uri,  null, null, null, Feed.COLUMN_FEED_TS + " DESC");

        s += "cursor.getCount() == " + cursor.getCount() + "\n";
        if (cursor.getCount() == 0) {
            return;
        } else {
            while (cursor.moveToNext()) {
                String colNames[] = cursor.getColumnNames();
                Feed feed = Feed.fromCursor(cursor);
                s += "cursor.position=" + cursor.getPosition() + "; " + feed.toString() + "\n";
            }
        }
        s += "---------------------------\n";
        Log.d(tag, s);
    }


}
