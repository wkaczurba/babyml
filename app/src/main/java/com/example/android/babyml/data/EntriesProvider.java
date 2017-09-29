package com.example.android.babyml.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

// TODO: Consider adding Strategy pattern to make handling easier.

public class EntriesProvider extends ContentProvider {
    public static final String AUTHORITY = "com.example.android.babyml.provider";
    public static final String SCHEME = "content://";

    // URIs
    public static final String PATH_ENTRY = "entry";
    public static final int CODE_ENTRY = 100;
    public static final int CODE_ENTRY_WITH_ID = 101;
    public static final String ENTRIES = SCHEME + AUTHORITY + "/entry";
    public static final Uri URI_ENTRIES = Uri.parse(ENTRIES);
//    public static final String ENTRY_BASE = ENTRIES + "/"; // This is for one ENTRY;
    public static final String PATH_FEED = "feed";

    public static final int CODE_FEED = 200;
    public static final int CODE_FEED_WITH_ID = 201;

    public static final String FEEDS = SCHEME + AUTHORITY + "/feed";
    public static final Uri URI_FEEDS = Uri.parse(FEEDS);
//    public static final String FEED_BASE = FEEDS + "/"; // This is for one FEED;

    public static final String PATH_NAPPY = "nappy";
    public static final int CODE_NAPPY = 300;
    public static final int CODE_NAPPY_WITH_ID = 301;
    public static final String NAPPIES = SCHEME + AUTHORITY + "/nappy";
    public static final Uri URI_NAPPIES = Uri.parse(NAPPIES);
//    public static final String NAPPY_BASE = NAPPIES + "/"; // This is for one NAPPY.

    public static final String PATH_SLEEP = "sleep";
    public static final int CODE_SLEEP = 400;
    public static final int CODE_SLEEP_WITH_ID = 401;
    public static final String SLEEPS = SCHEME + AUTHORITY + "/sleep";
    public static final Uri URI_SLEEPS = Uri.parse(SLEEPS);

    public static final String PATH_NOTE = "note";
    public static final int CODE_NOTE = 500;
    public static final int CODE_NOTE_WITH_ID = 501;
    public static final String NOTES = SCHEME + AUTHORITY + "/note";
    public static final Uri URI_NOTES = Uri.parse(NOTES);
//    public static final String NOTE_BASE = NOTES + "/"; // This is for one NAPPY.
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

        uriMatcher.addURI(AUTHORITY, PATH_NOTE, CODE_NOTE);
        uriMatcher.addURI(AUTHORITY, PATH_NOTE + "/#", CODE_NOTE_WITH_ID);

        return uriMatcher;
    }

//    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public EntriesProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        switch (sUriMatcher.match(uri)) {
            // TODO: Review all of this:
            case CODE_ENTRY: // fall-through
            case CODE_ENTRY_WITH_ID:
                throw new IllegalArgumentException("Unhandled query; sUriMatcher=" + sUriMatcher.match(uri));
            case CODE_FEED:
                return EntriesDbHandler.getInstance(getContext()).deleteAllFeeds();
            case CODE_FEED_WITH_ID:
                long id = Long.valueOf(uri.getLastPathSegment());
                return EntriesDbHandler.getInstance(getContext()).deleteFeedById(id);
            case CODE_NAPPY:
                throw new IllegalArgumentException("Unhandled query; sUriMatcher=" + sUriMatcher.match(uri));
            case CODE_NAPPY_WITH_ID:
                id = Long.valueOf(uri.getLastPathSegment());
                return EntriesDbHandler.getInstance(getContext()).deleteNappyById(id);
            case CODE_SLEEP:
                throw new IllegalArgumentException("Unhandled query; sUriMatcher=" + sUriMatcher.match(uri));
            case CODE_SLEEP_WITH_ID:
                id = Long.valueOf(uri.getLastPathSegment());
                return EntriesDbHandler.getInstance(getContext()).deleteSleepById(id);
            case CODE_NOTE:
                throw new IllegalArgumentException("Unhandled query; sUriMatcher=" + sUriMatcher.match(uri));
            case CODE_NOTE_WITH_ID: {
                id = Long.valueOf(uri.getLastPathSegment());
                return EntriesDbHandler.getInstance(getContext()).deleteNoteById(id);
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
            case CODE_ENTRY: // fall-through logic
            case CODE_ENTRY_WITH_ID:
                throw new IllegalArgumentException("Cannot insert an ENTRY; Can insert Feed or other ones...");

            case CODE_FEED:
                Log.d(TAG, "values:" + values.keySet() + ";" + values.toString() );
                long _id = EntriesDbHandler.getInstance(getContext())
                        .insertFeed(Feed.fromContentValues(values));
                if (_id == -1) {
                    return null;
                }
                return URI_FEEDS.buildUpon().appendPath(String.valueOf(_id)).build();
            case CODE_FEED_WITH_ID:
                throw new IllegalArgumentException("Cannot insert an ENTRY; Can insert Feed or other ones...");

            case CODE_NAPPY:
                _id = EntriesDbHandler.getInstance(getContext())
                        .insertNappy(Nappy.fromContentValues(values));
                if (_id == -1) {
                    return null;
                }
                return URI_NAPPIES.buildUpon().appendPath(String.valueOf(_id)).build();
            case CODE_NAPPY_WITH_ID:
                throw new IllegalArgumentException("Cannot insert an ENTRY; Can insert Feed or other ones...");

            case CODE_SLEEP:
                _id = EntriesDbHandler.getInstance(getContext())
                        .insertSleep(Sleep.fromContentValues(values));
                if (_id == -1) {
                    return null;
                }
                return URI_SLEEPS.buildUpon().appendPath(String.valueOf(_id)).build();
            case CODE_SLEEP_WITH_ID:
                throw new IllegalArgumentException("Cannot insert an ENTRY; Can insert Feed or other ones...");
            case CODE_NOTE:
                _id = EntriesDbHandler.getInstance(getContext())
                        .insertNote(Note.fromContentValues(values));
                if (_id == -1) {
                    return null;
                }
                return URI_NOTES.buildUpon().appendPath(String.valueOf(_id)).build();
            case CODE_NOTE_WITH_ID:
                throw new IllegalArgumentException("Cannot insert an ENTRY; Can insert Feed or other ones...");

            default:
                throw new UnsupportedOperationException("Unhandled query; sUriMatcher=" + sUriMatcher.match(uri));
        }
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
        }

        switch (sUriMatcher.match(uri)) {
            case CODE_ENTRY:
                Cursor cursor = EntriesDbHandler
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
                // TODO: Implement
                throw new UnsupportedOperationException("FUNCTION TO BE IMPLEMENTED");
            case CODE_NAPPY:
                // TODO: Implement
                throw new UnsupportedOperationException("FUNCTION TO BE IMPLEMENTED");
            case CODE_NAPPY_WITH_ID:
                // TODO: Implement
                throw new UnsupportedOperationException("FUNCTION TO BE IMPLEMENTED");
            case CODE_SLEEP:
                // TODO: Implement
                throw new UnsupportedOperationException("FUNCTION TO BE IMPLEMENTED");
            case CODE_SLEEP_WITH_ID:
                // TODO: Implement
                throw new UnsupportedOperationException("FUNCTION TO BE IMPLEMENTED");
            case CODE_NOTE:
                // TODO: Implement
                throw new UnsupportedOperationException("FUNCTION TO BE IMPLEMENTED");
            case CODE_NOTE_WITH_ID:
                // TODO: Implement
                throw new UnsupportedOperationException("FUNCTION TO BE IMPLEMENTED");
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        switch (sUriMatcher.match(uri)) { // fall-through logic
            case CODE_ENTRY:
            case CODE_ENTRY_WITH_ID:
            case CODE_FEED:
            case CODE_FEED_WITH_ID:
            case CODE_NAPPY:
            case CODE_NAPPY_WITH_ID:
            case CODE_SLEEP:
            case CODE_SLEEP_WITH_ID:
            case CODE_NOTE:
            case CODE_NOTE_WITH_ID:
            default:
                throw new UnsupportedOperationException("No support for update");
        }
    }
}
