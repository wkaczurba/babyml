package com.example.android.babyml.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.example.android.babyml.data.EntriesProviderContract;
import com.example.android.babyml.data.Entry;
import com.example.android.babyml.data.Feed;

import java.util.Locale;

/**
 * Created by wkaczurb on 9/20/2017.
 *
 * Methods for debugging.
 */

public class DebugUtils {
    public static void printAllEntries(Context ctx, String tag) {
        String s = "printAllEntries():\n";
        Uri uri = EntriesProviderContract.URI_ENTRIES;

        Cursor cursor = ctx.getContentResolver()
                .query(uri,  null, null, null, Entry.COLUMN_ENTRY_TS  + " DESC");

        if (cursor == null) {
            throw new NullPointerException("content resolver did not produce valid cursor for a query.");
        }

        s += "printAllEntries() -> cursor.getCount() == " + cursor.getCount() + "\n";
        if (cursor.getCount() == 0) {
            s = "cursor.getCount() == 0";
            Log.d(tag, s);
            return;
        } else {
            while (cursor.moveToNext()) {
                Locale locale = Locale.getDefault();
                Entry entry = Entry.fromCursor(cursor);

                s += String.format(locale,
                        "[cursor at %d]; %s\n", cursor.getPosition(), entry.toString());
                        //_id=%d, tb=%s, ts=%d\n", cursor.getPosition(), _id, tb, ts);
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

        if (cursor == null) {
            throw new NullPointerException("content resolver did not produce valid cursor for a query.");
        }

        s += "cursor.getCount() == " + cursor.getCount() + "\n";
        if (cursor.getCount() == 0) {
            return;
        } else {
            while (cursor.moveToNext()) {
                Feed feed = Feed.fromCursor(cursor);
                s += "cursor.position=" + cursor.getPosition() + "; " + feed.toString() + "\n";
            }
        }
        s += "---------------------------\n";
        Log.d(tag, s);
    }


}
