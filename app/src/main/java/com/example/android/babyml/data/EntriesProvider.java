package com.example.android.babyml.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class EntriesProvider extends ContentProvider {
    public static final String AUTHORITY = "com.example.android.babyml.provider";
    public static final String SCHEME = "content://";

    // URIs
    public static final String ENTRIES = SCHEME + AUTHORITY + "/entry";
    public static final Uri URI_ENTRIES = Uri.parse(ENTRIES);
    public static final String ENTRY_BASE = ENTRIES + "/"; // This is for one ENTRY;

    public static final String FEEDS = SCHEME + AUTHORITY + "/feed";
    public static final Uri URI_FEEDS = Uri.parse(FEEDS);
    public static final String FEED_BASE = FEEDS + "/"; // This is for one FEED;

    public static final String NAPPIES = SCHEME + AUTHORITY + "/nappy";
    public static final Uri URI_NAPPIES = Uri.parse(NAPPIES);
    public static final String NAPPY_BASE = NAPPIES + "/"; // This is for one NAPPY.

    public static final String NOTES = SCHEME + AUTHORITY + "/note";
    public static final Uri URI_NOTES = Uri.parse(NOTES);
    public static final String NOTE_BASE = NOTES + "/"; // This is for one NAPPY.
    private static final String TAG = EntriesProvider.class.getSimpleName();

    // TODO: Notes URIs
    // TODO: Nappy URIs
    // TODO: Add UriMatcher for retrieving individual entries

    public EntriesProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onCreate() {
        return true;
    }



    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        if (sortOrder == null) {
            throw new IllegalArgumentException("No sortOrder specified");
            //sortOrder = EntriesDbHandler.COLUMN_TS  + " DESC";
//            Log.w(TAG, "potentially invalid call/query: no sort order provided; "
//                    + " expecting, e.g. EntriesDbHandler.COLUMN_TS  + \" DESC\"");
        }

        // TODO: Implement this to handle query requests from clients.
        if (URI_ENTRIES.equals(uri)) {
            String limit = null;
            Cursor cursor = EntriesDbHandler
                    .getInstance(getContext())
                        .getAllEntriesCursor(
                            projection,
                            selection,
                            selectionArgs,
                            sortOrder,
                            limit);

            cursor.setNotificationUri(getContext().getContentResolver(), URI_ENTRIES);
            return cursor;
        } else if (URI_FEEDS.equals(uri)) {
            String limit = null;
            Cursor cursor = EntriesDbHandler
                    .getInstance(getContext())
                        .getAllFeedingsCursor(
                            projection,
                            selection,
                            selectionArgs,
                            sortOrder,
                            limit);
            cursor.setNotificationUri(getContext().getContentResolver(), URI_FEEDS);
            return cursor;
        } else {
            throw new UnsupportedOperationException("Not yet implemented");
        }


    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {


        // TODO: Implement this to handle requests to update one or more rows.

        throw new UnsupportedOperationException("Not yet implemented");
    }
}
