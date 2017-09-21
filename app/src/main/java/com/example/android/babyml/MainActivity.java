package com.example.android.babyml;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import org.joda.time.*;

import com.example.android.babyml.data.EntriesDbHandler;
import com.example.android.babyml.data.EntriesProvider;
import com.example.android.babyml.data.EntriesUtils;
import com.example.android.babyml.data.Entry;
import com.example.android.babyml.data.Feed;
//import com.example.android.babyml.data.FeedContract;
import com.example.android.babyml.data.Nappy;
import com.example.android.babyml.utils.DateUtils;
import com.example.android.babyml.utils.DebugUtils;

import static com.example.android.babyml.LogAdapter.DATE_VIEW_HOLDER;
import static com.example.android.babyml.LogAdapter.FEEDING_VIEW_HOLDER;
import static com.example.android.babyml.LogAdapter.NAPPY_VIEW_HOLDER;

// TODO: Review these ones for sliding tabs:
//   https://developer.android.com/training/implementing-navigation/lateral.html
//   https://developer.android.com/samples/SlidingTabsBasic/src/com.example.android.common/view/SlidingTabLayout.html
//

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener, SharedPreferences.OnSharedPreferenceChangeListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    // FIXME: Do we need mDb here (it has been already added to AddEntryActivity.

    public static String TAG = MainActivity.class.getSimpleName();
    private static final int ID_ENTRIES_LOADER = 1;
    private static final int ID_LATEST_FEED_LOADER = 2;

    // Database:
    EntriesDbHandler mDb; // Add closing;
    RecyclerView logRecyclerView;
    FloatingActionButton fab;

    // FRAGMENTS:
    TimeElapsedFragment timeElapsedFrag;
    public static final String TIME_ELAPSED_FRAG_TAG = "TIME_ELAPSED_FRAG";


    // Log-related items:
    LogAdapter mAdapter;
    LogListItemClickListener mLogListItemClickListener;
    private int mPosition = RecyclerView.NO_POSITION;

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case ID_ENTRIES_LOADER:
                Uri entriesQueryUri = EntriesProvider.URI_ENTRIES; // FIXME: This should come from a separate class (decoupled from EntriesProvider)
                String[] projection = null; // not used by the content provider
                String selection = null; // not used by the content provider
                String[] selectionArgs = null; // not used by the content provider
                String sortOrder = null; // not used by the content provider

                return new CursorLoader(this,
                        entriesQueryUri,
                        projection,
                        selection,
                        selectionArgs,
                        Entry.COLUMN_ENTRY_TS + " DESC");
            case ID_LATEST_FEED_LOADER:
                Uri feedsUri = EntriesProvider.URI_FEEDS;
                return new CursorLoader(this,
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
        DebugUtils.printAllEntries(this, TAG);
        DebugUtils.printAllFeeds(this, TAG);

        switch (loader.getId()) {
            case ID_ENTRIES_LOADER:
                mAdapter.swapCursor(data);
                if (mPosition == RecyclerView.NO_POSITION)
                    mPosition = 0;
                logRecyclerView.smoothScrollToPosition(mPosition);
                break;

            case ID_LATEST_FEED_LOADER:
                updateTimeElapsed(data);
                break;


            default: break;
        }

        // Optionally: HERE -> remove Progression Icon if disaplyed
        // TODO: Update time.
    }


    // TODO: Remove it or move to loader!
    private void updateTimeElapsed(Cursor cursor) {
        Log.d(TAG,
                "updateTimeElpased called; cursorCount=" + cursor.getCount() + "; pos=" + cursor.getPosition() + "\n" +
                "timeElapsedFrag: isNull?=" + (timeElapsedFrag == null));

        if (timeElapsedFrag == null)
            return;

        //Cursor cursor = mDb.getLatestFeeding();
        if (cursor.getCount() == 0) {
            timeElapsedFrag.setTimeZero(-1);
        } else {
            cursor.moveToFirst();
            Feed f = LogAdapter.cursorToFeed(cursor);

            timeElapsedFrag.setTimeZero(f.getTs());
        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
        updateTimeElapsed(null);
    }

//    /**
//     * Function gets current date time and applies particular time to it.
//     * If hour/min are ahead of current time - a day earlier is returned.
//     *
//     * TODO: Move this to Utils class.
//     *
//     * @return
//     */
//    public static LocalDateTime applyTimeToCurrentLocalDate(int hour, int min) {
//        LocalDateTime ldtNow = LocalDateTime.now();
//        LocalTime ltNow = ldtNow.toLocalTime();
//
//        LocalDate ld; // date to be used (either today or yesterday)
//        LocalTime lt = new LocalTime(hour, min, 0); // time to be used
//
//        if (lt.isAfter(ltNow)) {
//            // Use yesterday's date as hour/time specifies time before midnight.
//            ld = ldtNow.toLocalDate().minusDays(1);
//        } else {
//            // Use yesterday's date as hour/time specifies today's time during.
//            ld = ldtNow.toLocalDate();
//        }
//
//        return new LocalDateTime(
//                ld.getYear(),
//                ld.getMonthOfYear(),
//                ld.getDayOfMonth(),
//                lt.getHourOfDay(),
//                lt.getMinuteOfHour());
//    }

    public static class LogListItemClickListener implements LogAdapter.ListItemClickListener {
        Context context;
        Toast mToast;

        public LogListItemClickListener(Context context) {
            this.context = context;
        }

        @Override
        public void onListItemClick(int clickItemIndex) {
            // get from cursor + output as Toast.
            if (mToast != null) {
                mToast.cancel();
            }
            mToast = Toast.makeText(context, "Item #" + clickItemIndex + " clicked", Toast.LENGTH_LONG);
            mToast.show();
        }
    }

    /**
     * The following is a textWatcher for milkTimeEditText
     *
     * TODO: Implement something similar to this one;
     * TODO: Make it as a separate class/file related to resources/UI; this class is used for Nappies
     *   as well as for Milk and is likely to be used elsewhere
     * http://www.techrepublic.com/article/pro-tip-write-a-validate-as-you-go-android-textwatcher-for-date-entry-fields/
     */
    public static class TimeTextWatcher implements TextWatcher {
        EditText et;

        public TimeTextWatcher(EditText et) {
            this.et = et;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void setDefaultTimeIfEntryInvalid() {
            String entry = et.getText().toString();
            int[] hhmm = DateUtils.toHoursMinArray(entry);
            boolean isEntryValid = (hhmm != null);
            if (!isEntryValid) {
                setDefaultTime();
            } else {
                // empty.
            }
        }

        public int[] getHoursMins() {
            String entry = et.getText().toString();
            return DateUtils.toHoursMinArray(entry);
        }

        public long getTimeMilis() {
            int[] hhmm = getHoursMins();
            if (hhmm == null) {
                throw new NullPointerException("getHoursMins returned null; field contains invalid time. Try using setDefaultTimeIfEntryInvalid before using getHoursMins");
            }

            LocalDateTime ldt = DateUtils.applyTimeToCurrentLocalDate(hhmm[0], hhmm[1]);
            return ldt.toDateTime().getMillis();
        }

        public void setDefaultTime() {
            //android.text.format.DateFormat df = new android.text.format.DateFormat();
            long timeInMillis = System.currentTimeMillis();

            CharSequence csTime = DateFormat.format("hh:mm", timeInMillis); // was: df.
            et.setText(csTime);
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            int[] hhmm = DateUtils.toHoursMinArray(s.toString());
            boolean isEntryValid = (hhmm != null);
            if (!isEntryValid) {
                Log.d(TAG, "Invalid time: " + s.toString());
                et.setError("Invalid time");
            } else {
                Log.d(TAG, "Time ok: " + s.toString());
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        this.updateLogRecyclerView();
//        this.updateTimeElapsed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Getting database instance:
        mDb = EntriesDbHandler.getInstance(this);
        EntriesUtils.tryUpgradeFromOld(this); // Upgrading from OldDb to the new one.

        // RecyclerView:
        logRecyclerView = (RecyclerView) findViewById(R.id.log_rv);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this); //  to display stuff linearly.
        logRecyclerView.setLayoutManager(layoutManager);
        logRecyclerView.setHasFixedSize(true);

        mLogListItemClickListener = new LogListItemClickListener(this);
        mAdapter = new LogAdapter(mLogListItemClickListener);
        logRecyclerView.setAdapter(mAdapter);

        // add Time Elapsed Fragment
        //addTimeElapsedFrag();

        timeElapsedFrag = (TimeElapsedFragment) getSupportFragmentManager().findFragmentByTag(TIME_ELAPSED_FRAG_TAG);

        setupSharedPreferences();


        // https://stackoverflow.com/questions/42792984/how-to-swipe-to-delete-on-recyclerview
        // Handling of swiping left/right:
        // TODO: Add this one to the top or somewhere else.
        ItemTouchHelper.SimpleCallback simpleTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                // From: https://stackoverflow.com/questions/30713121/disable-swipe-for-position-in-recyclerview-using-itemtouchhelper-simplecallback
                int dragFlags = 0;
                int swipeFlags = 0;
                int itemViewType = viewHolder.getItemViewType();
                switch (itemViewType) {
                    case FEEDING_VIEW_HOLDER: // fall-through
                    case NAPPY_VIEW_HOLDER:
                        swipeFlags = ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT;
                        break;
                    default:
                        break;
                }
                return makeMovementFlags(dragFlags, swipeFlags);
                //return super.getMovementFlags(recyclerView, viewHolder);
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                if (direction == ItemTouchHelper.RIGHT || direction == ItemTouchHelper.LEFT) {
                    int itemViewType = viewHolder.getItemViewType();

                    if (itemViewType == FEEDING_VIEW_HOLDER) { // TODO: Change 0 to use FINAL STATIC INT!!
                        LogAdapter.FeedingViewHolder logViewHolder = (LogAdapter.FeedingViewHolder) viewHolder;

                        long lid = logViewHolder.getAdapterPosition();
                        Feed feed = logViewHolder.getFeed();
                            //mDb.deleteFeeding(feed.getId());

                        Uri deleteFeedUri = EntriesProvider.URI_FEEDS
                                .buildUpon()
                                .appendPath(String.valueOf(feed.get_id()))
                                .build();

                        getContentResolver().delete(deleteFeedUri, null, null);
                    } else if (itemViewType == NAPPY_VIEW_HOLDER) {
                        LogAdapter.NappyViewHolder nappyViewHolder = (LogAdapter.NappyViewHolder) viewHolder;
                        long lid = nappyViewHolder.getAdapterPosition();
                        Nappy nappy = nappyViewHolder.getNappy();
                        //mDb.deleteNappy(nappy.getId());
                        Uri deleteUri = EntriesProvider.URI_NAPPIES.buildUpon()
                                        .appendPath(String.valueOf(nappy.get_id()))
                                        .build();
                        getContentResolver().delete(deleteUri, null, null);
                    } else {

                        return;
                        // DO NOTHING. This can be: DATE_VIEW_HOLDER or SUMMARY_VIEW_HOLDER or other.
                        //throw new IllegalArgumentException("ItemViewType = " + itemViewType);
                    }
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleTouchCallback);
        itemTouchHelper.attachToRecyclerView(logRecyclerView);

        // Getting FloatingActionButton:
        fab = (FloatingActionButton) findViewById(R.id.fab_add);
        fab.setOnClickListener(this);

        // Two loaders:
        getSupportLoaderManager().initLoader(ID_ENTRIES_LOADER, null, this);
        getSupportLoaderManager().initLoader(ID_LATEST_FEED_LOADER, null, this);
    }


    private void setupSharedPreferences() {
        // Shared preference listening;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.registerOnSharedPreferenceChangeListener(this);
        updateTimeElapsedFrag(sp);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.unregisterOnSharedPreferenceChangeListener(this);
    }

    // FIXME: Deal with this one here:
//    private void drawTimeElapsed() {
//        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
//
//    }

    public void safeAddTimeElapsedFrag() {
        if (timeElapsedFrag == null) {
            timeElapsedFrag = new TimeElapsedFragment();
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.top_main_fragment, timeElapsedFrag, TIME_ELAPSED_FRAG_TAG);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }

    public void safeDeleteTimeElapsedFrag() {
        //getFragmentManager().findFragmentByTag(TIME_ELAPSED_FRAG_TAG);
        if (timeElapsedFrag == null) {
            return;
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.remove(timeElapsedFrag);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        ft.commit();
        timeElapsedFrag = null;
    }

    // TODO: rework this function.
    public void updateTimeElapsedFrag(SharedPreferences sharedPreferences) {
        boolean showTimeElapsed = sharedPreferences.getBoolean("show_time_passed", true);

        if (showTimeElapsed) {
            safeAddTimeElapsedFrag();
        } else {
            safeDeleteTimeElapsedFrag();
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("show_time_passed")) {
            // FIXME: This cannot be here, as this function will be called when activity is not active, what will lead to crash (java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState)
            //updateTimeElapsedFrag(sharedPreferences);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        // FIXME: Think where to put this.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        updateTimeElapsedFrag(sp);

//        updateLogRecyclerView();
//        updateTimeElapsed();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }




/*    private void updateLogRecyclerView() {
        // FIXME: This updateLogRecyclerView is not greatest lets put it that way.
        getSupportLoaderManager().<Cursor>restartLoader(ID_ENTRIES_LOADER, null, this);

    }*/

    @Override
    public void onClick(View v) {
        if (v == null) {
            throw new UnsupportedOperationException("Onclick provided with null value");
        }

        if (v.equals(logRecyclerView)) {
            Toast.makeText(this, "logRecyclerView to be handled yet", Toast.LENGTH_LONG).show();
        } else if (v.equals(fab)) {
            Intent intent = new Intent(this, AddEntryActivity.class);
            // TODO: putExtra parameters here
            startActivity(intent);
        } else {
            Log.d(TAG, "Unknown item clicked: " + v);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        outState.putInt(MILK_AMOUNT_VALUE_KEY, milkAmountValue);
//        Log.d(TAG, "Saved milkAmountValue: " + milkAmountValue);
    }

}
