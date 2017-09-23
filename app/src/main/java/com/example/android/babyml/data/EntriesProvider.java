package com.example.android.babyml.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class EntriesProvider extends ContentProvider {
    public static final String AUTHORITY = "com.example.android.babyml.provider";
    public static final String SCHEME = "content://";

    // URIs
    public static final String PATH_ENTRY = "entry";
    public static final int CODE_ENTRY = 100;
    public static final int CODE_ENTRY_WITH_ID = 101;
    public static final String ENTRIES = SCHEME + AUTHORITY + "/entry";
    public static final Uri URI_ENTRIES = Uri.parse(ENTRIES);
    public static final String ENTRY_BASE = ENTRIES + "/"; // This is for one ENTRY;
    public static final String PATH_FEED = "feed";

    public static final int CODE_FEED = 200;
    public static final int CODE_FEED_WITH_ID = 201;

    public static final String FEEDS = SCHEME + AUTHORITY + "/feed";
    public static final Uri URI_FEEDS = Uri.parse(FEEDS);
    public static final String FEED_BASE = FEEDS + "/"; // This is for one FEED;

    public static final String PATH_NAPPY = "nappy";
    public static final int CODE_NAPPY = 300;
    public static final int CODE_NAPPY_WITH_ID = 301;
    public static final String NAPPIES = SCHEME + AUTHORITY + "/nappy";
    public static final Uri URI_NAPPIES = Uri.parse(NAPPIES);
    public static final String NAPPY_BASE = NAPPIES + "/"; // This is for one NAPPY.

    // TODO: SLEEP
    public static final String PATH_SLEEP = "sleep";
    public static final int CODE_SLEEP = 400;
    public static final int CODE_SLEEP_WITH_ID = 401;
    public static final String SLEEPS = SCHEME + AUTHORITY + "/sleep";
    public static final Uri URI_SLEEPS = Uri.parse(SLEEPS);

    public static final String NOTES = SCHEME + AUTHORITY + "/note";
    public static final Uri URI_NOTES = Uri.parse(NOTES);
    public static final String NOTE_BASE = NOTES + "/"; // This is for one NAPPY.
    private static final String TAG = EntriesProvider.class.getSimpleName();

    // URI Matchers
    public static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(AUTHORITY, PATH_ENTRY, CODE_ENTRY);
        uriMatcher.addURI(AUTHORITY, PATH_ENTRY + "/#", CODE_ENTRY_WITH_ID); // This is unhandled probably.

        uriMatcher.addURI(AUTHORITY, PATH_FEED, CODE_FEED);
        uriMatcher.addURI(AUTHORITY, PATH_FEED + "/#", CODE_FEED_WITH_ID);

        uriMatcher.addURI(AUTHORITY, PATH_NAPPY, CODE_NAPPY);
        uriMatcher.addURI(AUTHORITY, PATH_NAPPY + "/#", CODE_NAPPY_WITH_ID);

        uriMatcher.addURI(AUTHORITY, PATH_SLEEP, CODE_SLEEP);
        uriMatcher.addURI(AUTHORITY, PATH_SLEEP + "/#", CODE_SLEEP_WITH_ID);

        return uriMatcher;
    }

    // TODO: Notes URIs
    // TODO: Nappy URIs
    // TODO: Add UriMatcher for retrieving individual entries

//    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public EntriesProvider() {
    }

//    private static UriMatcher buildUriMatcher() {
//        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
//        //final String authority = AUTHORITY;
//
//        ENTRIES ->
//        matcher.addURI(AUTHORITY, ENTRIES, );
//
//    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        switch (sUriMatcher.match(uri)) {
            //case CODE_ENTRY_WITH_ID: { break; }
            case CODE_FEED: {
                return EntriesDbHandler.getInstance(getContext()).deleteAllFeeds();
            }
            case CODE_FEED_WITH_ID: {
                long id = Long.valueOf(uri.getLastPathSegment());
                return EntriesDbHandler.getInstance(getContext()).deleteFeedById(id);
            }
            case CODE_NAPPY_WITH_ID: {
                long id = Long.valueOf(uri.getLastPathSegment());
                return EntriesDbHandler.getInstance(getContext()).deleteNappyById(id);
            }
            case CODE_SLEEP_WITH_ID: {
                long id = Long.valueOf(uri.getLastPathSegment());
                return EntriesDbHandler.getInstance(getContext()).deleteSleepById(id);
            }
            default:
                throw new IllegalArgumentException("Unhandled query; sUriMatcher=" + sUriMatcher.match(uri));
        }
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        switch (sUriMatcher.match(uri)) {
            case CODE_ENTRY:
                throw new IllegalArgumentException("Cannot insert an ENTRY; Can insert Feed or other ones...");

            case CODE_FEED:
//                int amount = values.getAsInteger("FEED_AMOUNT");
//                long ts = values.getAsLong("FEED_TS");
                Log.d(TAG, "values:" + values.keySet());

                long _id = EntriesDbHandler.getInstance(getContext())
                        .insertFeed(Feed.fromContentValues(values));

                return URI_FEEDS.buildUpon().appendPath(String.valueOf(_id)).build();

            case CODE_NAPPY:
                _id = EntriesDbHandler.getInstance(getContext())
                        .insertNappy(Nappy.fromContentValues(values));

                return URI_NAPPIES.buildUpon().appendPath(String.valueOf(_id)).build();

            case CODE_SLEEP:
                _id = EntriesDbHandler.getInstance(getContext())
                        .insertSleep(Sleep.fromContentValues(values));
                return URI_SLEEPS.buildUpon().appendPath(String.valueOf(_id)).build();

            default:
                // TODO: Implement this to handle requests to insert a new row.
                throw new IllegalArgumentException("Unhandled query; sUriMatcher=" + sUriMatcher.match(uri));
        }

//        if (URI_ENTRIES.equals(uri)) {
//            throw new IllegalArgumentException("Cannot insert an ENTRY; Can insert Feed or other ones...");
//        } else if (URI_FEEDS.equals(uri)) {
//
//            int amount = values.getAsInteger("FEED_AMOUNT");
//            long ts = values.getAsLong("FEED_TS");
//            long _id = EntriesDbHandler.getInstance(getContext())
//                    .insertFeed(new Feed(values));
//
//            return URI_FEEDS.buildUpon().appendPath(String.valueOf(_id)).build();
//        }
    }

    @Override
    public boolean onCreate() {
        return true;
    }



    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor cursor;

        if (sortOrder == null) {
            throw new IllegalArgumentException("No sortOrder specified");
        }

        switch (sUriMatcher.match(uri)) {
            case CODE_ENTRY:
                cursor = EntriesDbHandler
                        .getInstance(getContext())
                        .getAllEntriesCursor(
                                projection,
                                selection,
                                selectionArgs,
                                sortOrder,
                                null); // limit = null

                cursor.setNotificationUri(getContext().getContentResolver(), URI_ENTRIES);
                return cursor;
            case CODE_ENTRY_WITH_ID:
                throw new UnsupportedOperationException("FUNCTION TO BE IMPLEMENTED");
            case CODE_FEED:
                cursor = EntriesDbHandler
                        .getInstance(getContext())
                        .getAllFeedingsCursor(
                                projection,
                                selection,
                                selectionArgs,
                                sortOrder,
                                null); // limit = null;
                cursor.setNotificationUri(getContext().getContentResolver(), URI_FEEDS);
                return cursor;
            case CODE_FEED_WITH_ID:
                throw new UnsupportedOperationException("FUNCTION TO BE IMPLEMENTED");
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }

        // TODO: Implement this to handle query requests from clients.
//        if (URI_ENTRIES.equals(uri)) {
//            String limit = null;
//            Cursor cursor = EntriesDbHandler
//                    .getInstance(getContext())
//                        .getAllEntriesCursor(
//                            projection,
//                            selection,
//                            selectionArgs,
//                            sortOrder,
//                            limit);
//
//            cursor.setNotificationUri(getContext().getContentResolver(), URI_ENTRIES);
//            return cursor;
//        } else if (URI_FEEDS.equals(uri)) {
//            String limit = null;
//            Cursor cursor = EntriesDbHandler
//                    .getInstance(getContext())
//                        .getAllFeedingsCursor(
//                            projection,
//                            selection,
//                            selectionArgs,
//                            sortOrder,
//                            limit);
//            cursor.setNotificationUri(getContext().getContentResolver(), URI_FEEDS);
//            return cursor;
//        } else {
//            throw new UnsupportedOperationException("Not yet implemented");
//        }


    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {


        // TODO: Implement this to handle requests to update one or more rows.

        throw new UnsupportedOperationException("Not yet implemented");
    }
}
