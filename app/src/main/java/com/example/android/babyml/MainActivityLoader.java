package com.example.android.babyml;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.support.v7.app.AppCompatActivity;

import com.example.android.babyml.data.EntriesProvider;
import com.example.android.babyml.data.EntriesProviderContract;
import com.example.android.babyml.data.Entry;
import com.example.android.babyml.data.Feed;
import com.example.android.babyml.ui.LogAdapter;
import com.example.android.babyml.utils.DebugUtils;

/**
 * Created by WKaczurb on 10/4/2017.
 *
 * Loader for the MainActivity class, decoupled for the rest for testing.
 */

class MainActivityLoader implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = MainActivityLoader.class.getSimpleName();
    private RecyclerView mLogRecyclerView;
    private Activity activity;
    private LogAdapter mLogAdatper;
    private OnUpdateTimeElapsed mOnUpdateTimeElapsed;
    private static final int ID_ENTRIES_LOADER     = 1; // This loader is used for loading all entries
    private static final int ID_LATEST_FEED_LOADER = 2; // This loader is used only to retreive latest feed and to calculate time since the last feed.
    private int mPosition = RecyclerView.NO_POSITION;

    MainActivityLoader(AppCompatActivity activity, RecyclerView logRecyclerView, LogAdapter logAdapter, OnUpdateTimeElapsed onUpdateTimeElapsed) {
        this.activity = activity;
        this.mLogRecyclerView = logRecyclerView;
        this.mLogAdatper = logAdapter;
        this.mOnUpdateTimeElapsed = onUpdateTimeElapsed;

        activity.getSupportLoaderManager().initLoader(ID_ENTRIES_LOADER, null, this);
        activity.getSupportLoaderManager().initLoader(ID_LATEST_FEED_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case ID_ENTRIES_LOADER:
                Uri entriesQueryUri = EntriesProviderContract.URI_ENTRIES; // FIXME: This should come from a separate class (decoupled from EntriesProvider)
                //String sortOrder = null; not used by the content provider

                return new CursorLoader(activity,
                        entriesQueryUri,
                        null, // String[] projection
                        null, // String selection
                        null, // String[] selectionArgs
                        Entry.COLUMN_ENTRY_TS + " DESC");
            case ID_LATEST_FEED_LOADER:
                Uri feedsUri = EntriesProviderContract.URI_FEEDS;
                return new CursorLoader(activity,
                        feedsUri,
                        null,
                        null,
                        null,
                        Feed.COLUMN_FEED_TS + " DESC");
            default:
                throw new IllegalArgumentException("Invalid id supplied: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        DebugUtils.printAllEntries(activity, TAG);
        DebugUtils.printAllFeeds(activity, TAG);

        switch (loader.getId()) {
            case ID_ENTRIES_LOADER:
                mLogAdatper.swapCursor(data);
                if (mPosition == RecyclerView.NO_POSITION)
                    mPosition = 0;
                mLogRecyclerView.smoothScrollToPosition(mPosition); // TODO: Decouple it? Add iface?
                break;

            case ID_LATEST_FEED_LOADER:
                mOnUpdateTimeElapsed.onUpdateTimeElapsed(data);
                break;

            default: break;
        }
        // Optionally: Progression Icon if disaplyed
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mLogAdatper.swapCursor(null);
        mOnUpdateTimeElapsed.onUpdateTimeElapsed(null);
    }
}
