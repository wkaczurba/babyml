package com.example.android.babyml.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import static com.example.android.babyml.data.EntriesProviderContract.*;

import org.jetbrains.annotations.NotNull;

// TODO: Consider adding Strategy pattern to make handling easier.

public class EntriesProvider extends ContentProvider {


//    public static final String NOTE_BASE = NOTES + "/"; // This is for one NAPPY.
    private static final String TAG = EntriesProvider.class.getSimpleName();
    private EntriesDbHandler dbHandler;

    // URI Matchers
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
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

    public EntriesProvider() {
    }

    @Override
    public int delete(@NotNull Uri uri, String selection, String[] selectionArgs) {
        switch (sUriMatcher.match(uri)) {
            case CODE_FEED_WITH_ID:
                long id = Long.valueOf(uri.getLastPathSegment());
                return dbHandler.deleteFeedById(id);
            case CODE_NAPPY_WITH_ID:
                id = Long.valueOf(uri.getLastPathSegment());
                return dbHandler.deleteNappyById(id);
            case CODE_SLEEP_WITH_ID:
                id = Long.valueOf(uri.getLastPathSegment());
                return dbHandler.deleteSleepById(id);
            case CODE_NOTE_WITH_ID:
                id = Long.valueOf(uri.getLastPathSegment());
                return dbHandler.deleteNoteById(id);

            case CODE_FEED: // Uncomment if needed to support the function.
                //return EntriesDbHandler.getInstance(getContext()).deleteAllFeeds();
            case CODE_ENTRY: // fall-through
            case CODE_ENTRY_WITH_ID:
            case CODE_NAPPY:
            case CODE_SLEEP:
            case CODE_NOTE:
            default:
                throw new IllegalArgumentException("Unhandled query; sUriMatcher=" + sUriMatcher.match(uri));
        }
    }

    @Override
    public String getType(@NotNull Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(@NotNull Uri uri, ContentValues values) {
        switch (sUriMatcher.match(uri)) {
            case CODE_ENTRY: // fall-through logic
            case CODE_ENTRY_WITH_ID:
                throw new IllegalArgumentException("Cannot insert an ENTRY; Can insert Feed or other ones...");

            case CODE_FEED:
                Log.d(TAG, "values:" + values.keySet() + ";" + values.toString() );
                long _id = dbHandler.insertFeed(Feed.fromContentValues(values));
                if (_id == -1) {
                    return null;
                }
                return URI_FEEDS.buildUpon().appendPath(String.valueOf(_id)).build();
            case CODE_FEED_WITH_ID:
                throw new IllegalArgumentException("Cannot insert an ENTRY; Can insert Feed or other ones...");

            case CODE_NAPPY:
                _id = dbHandler.insertNappy(Nappy.fromContentValues(values));
                if (_id == -1) {
                    return null;
                }
                return URI_NAPPIES.buildUpon().appendPath(String.valueOf(_id)).build();
            case CODE_NAPPY_WITH_ID:
                throw new IllegalArgumentException("Cannot insert an ENTRY; Can insert Feed or other ones...");

            case CODE_SLEEP:
                _id = dbHandler.insertSleep(Sleep.fromContentValues(values));
                if (_id == -1) {
                    return null;
                }
                return URI_SLEEPS.buildUpon().appendPath(String.valueOf(_id)).build();
            case CODE_SLEEP_WITH_ID:
                throw new IllegalArgumentException("Cannot insert an ENTRY; Can insert Feed or other ones...");
            case CODE_NOTE:
                _id = dbHandler.insertNote(Note.fromContentValues(values));
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
        // getContext must not be called from constructor
        // as context is not set there yet.
        Context context = this.getContext();
        if (context == null) {
            throw new IllegalArgumentException("Context invalid");
        }

        dbHandler = new EntriesDbHandler(context);
        return true;
    }

    @Override
    public Cursor query(@NotNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Context context = getContext();
        if (context == null) {
            throw new NullPointerException("Context is null;");
        }

        if (sortOrder == null) {
            throw new IllegalArgumentException("No sortOrder specified");
        }

        switch (sUriMatcher.match(uri)) {
            case CODE_ENTRY:
                Cursor cursor = dbHandler.getAllEntriesCursor(
                                projection,
                                selection,
                                selectionArgs,
                                sortOrder,
                                null); // limit = null

                cursor.setNotificationUri(context.getContentResolver(), URI_ENTRIES);
                return cursor;
            case CODE_ENTRY_WITH_ID:
                throw new UnsupportedOperationException("FUNCTION TO BE IMPLEMENTED");
            case CODE_FEED:
                cursor = dbHandler.getAllFeedingsCursor(
                                projection,
                                selection,
                                selectionArgs,
                                sortOrder,
                                null); // limit = null;
                cursor.setNotificationUri(context.getContentResolver(), URI_FEEDS);
                return cursor;

            // TODO: Implement the following cases CODE_FEED_WITH_ID, CODE_NAPPY, CODE_NAPPY_WITH_ID, CODE_SLEEP, CODE_SLEEP_WITH_ID, CODE_NOTE, CODE_NOTE_WITH_ID:
            case CODE_FEED_WITH_ID:
            case CODE_NAPPY:
            case CODE_NAPPY_WITH_ID:
            case CODE_SLEEP:
            case CODE_SLEEP_WITH_ID:
            case CODE_NOTE:
            case CODE_NOTE_WITH_ID:
                throw new UnsupportedOperationException("FUNCTION TO BE IMPLEMENTED");

            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }
    }

    @Override
    public int update(@NotNull Uri uri, ContentValues values, String selection,
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
