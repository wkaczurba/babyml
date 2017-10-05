package com.example.android.babyml;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.babyml.data.EntriesProviderContract;
import com.example.android.babyml.data.Feed;
import com.example.android.babyml.data.Nappy;
import com.example.android.babyml.data.Note;
import com.example.android.babyml.data.Sleep;
import com.example.android.babyml.ui.LogAdapter;
import com.example.android.babyml.ui.ToastListItemClickListener;

import java.util.HashMap;
import java.util.Map;

import static com.example.android.babyml.ui.LogAdapter.FEEDING_VIEW_HOLDER;
import static com.example.android.babyml.ui.LogAdapter.NAPPY_VIEW_HOLDER;
import static com.example.android.babyml.ui.LogAdapter.SLEEP_VIEW_HOLDER;
import static com.example.android.babyml.ui.LogAdapter.NOTE_VIEW_HOLDER;

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private static String TAG = MainActivity.class.getSimpleName();
    private RecyclerView logRecyclerView;
    private TextView bottomBarMilkTv;
    private TextView bottomBarNappyTv;
    private TextView bottomBarNoteTv;
    private TextView bottomBarSleepTv;

    private static final String TIME_ELAPSED_FRAG_TAG = "TIME_ELAPSED_FRAG";
    private static final String MILK_ADDER_FRAG_TAG = "MILK_ADDER_FRAG";
    private static final String NAPPY_ADDER_FRAG_TAG = "NAPPY_ADDER_FRAG";
    private static final String SLEEP_ADDER_FRAG_TAG = "SLEEP_ADDER_FRAG";
    private static final String NOTE_ADDER_FRAG_TAG = "NOTE_ADDER_FRAG";

    // Loaders:
    private MainActivityLoader loader;

    // Log-related items:
    private LogAdapter mAdapter;
    private ToastListItemClickListener mToastListItemClickListener;

    // TODO: Remove it or move to loader!
    private void updateTimeElapsed(Cursor cursor) {
        TimeElapsedFragment frag = (TimeElapsedFragment) fragments.get(TIME_ELAPSED_FRAG_TAG);

        if (frag == null) {
            return;
        }

        if (cursor == null || cursor.getCount() == 0) {
            frag.setTimeZero(-1);
        } else {
            cursor.moveToFirst();
            Feed f = LogAdapter.cursorToFeed(cursor);

            frag.setTimeZero(f.getTs());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        this.updateLogRecyclerView();
//        this.updateTimeElapsed();
    }

    private void populateFragmentsMap() {
        String[] tags = new String[] {
            TIME_ELAPSED_FRAG_TAG,
            MILK_ADDER_FRAG_TAG,
            NAPPY_ADDER_FRAG_TAG,
            SLEEP_ADDER_FRAG_TAG,
            NOTE_ADDER_FRAG_TAG
        };

        for (String tag : tags) {
            Fragment frag = getSupportFragmentManager().findFragmentByTag(tag);
            if (frag != null)
                fragments.put(tag, frag);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Getting database instance:
//        mDb = EntriesDbHandler.getInstance(this);
//        EntriesUtils.tryUpgradeFromOld(this); // Upgrading from OldDb to the new one.

        // RecyclerView:
        logRecyclerView = (RecyclerView) findViewById(R.id.log_rv);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this); //  to display stuff linearly.
        logRecyclerView.setLayoutManager(layoutManager);
        logRecyclerView.setHasFixedSize(true);


        mToastListItemClickListener = new ToastListItemClickListener(this);
        mAdapter = new LogAdapter(mToastListItemClickListener);
        mToastListItemClickListener.setAdapter(mAdapter);
        logRecyclerView.setAdapter(mAdapter);

        // add Time Elapsed Fragment
        //addTimeElapsedFrag();

        // Is this needed?:
        populateFragmentsMap();

        Log.d(TAG, "fragments; timeElapsedFrag =" + fragments.get(TIME_ELAPSED_FRAG_TAG));
        Log.d(TAG, "fragments; milkAdderFragment =" + fragments.get(MILK_ADDER_FRAG_TAG));
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
                    case SLEEP_VIEW_HOLDER:
                    case NOTE_VIEW_HOLDER:
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

                    switch (itemViewType) {
                        case FEEDING_VIEW_HOLDER:
                            LogAdapter.FeedingViewHolder logViewHolder = (LogAdapter.FeedingViewHolder) viewHolder;

                            Feed feed = logViewHolder.getFeed();

                            Uri deleteUri = EntriesProviderContract.URI_FEEDS
                                    .buildUpon()
                                    .appendPath(String.valueOf(feed.get_id()))
                                    .build();

                            getContentResolver().delete(deleteUri, null, null);
                            break;
                        case NAPPY_VIEW_HOLDER:
                            LogAdapter.NappyViewHolder nappyViewHolder = (LogAdapter.NappyViewHolder) viewHolder;
                            Nappy nappy = nappyViewHolder.getNappy();

                            deleteUri = EntriesProviderContract.URI_NAPPIES.buildUpon()
                                    .appendPath(String.valueOf(nappy.get_id()))
                                    .build();
                            getContentResolver().delete(deleteUri, null, null);

                            break;
                        case SLEEP_VIEW_HOLDER:
                            LogAdapter.SleepViewHolder sleepViewHolder = (LogAdapter.SleepViewHolder) viewHolder;
                            Sleep sleep = sleepViewHolder.getSleep();

                            deleteUri = EntriesProviderContract.URI_SLEEPS.buildUpon()
                                    .appendPath(String.valueOf(sleep.get_id()))
                                    .build();
                            getContentResolver().delete(deleteUri, null, null);
//                            // TODO: Implement
//                            throw new UnsupportedOperationException("Not implemented yet;");
                            break;
                        case NOTE_VIEW_HOLDER:
                            LogAdapter.NoteViewHolder noteViewHolder = (LogAdapter.NoteViewHolder) viewHolder;
                            Note note = noteViewHolder.getNote();

                            if (note == null) {
                                throw new NullPointerException("note var is not supposed to be null!");
                            }


                            deleteUri = EntriesProviderContract.URI_NOTES.buildUpon()
                                    .appendPath(String.valueOf(note.get_id()))
                                    .build();
                            getContentResolver().delete(deleteUri, null, null);

//                            throw new UnsupportedOperationException("Not implemented yet;");
                            break;
                        default:
                            break;
                    }

//                    if (itemViewType == FEEDING_VIEW_HOLDER) { // TODO: Change 0 to use FINAL STATIC INT!!
//                        LogAdapter.FeedingViewHolder logViewHolder = (LogAdapter.FeedingViewHolder) viewHolder;
//
//                        long lid = logViewHolder.getAdapterPosition();
//                        Feed feed = logViewHolder.getFeed();
//                            //mDb.deleteFeeding(feed.getId());
//
//                        Uri deleteFeedUri = EntriesProviderContract.URI_FEEDS
//                                .buildUpon()
//                                .appendPath(String.valueOf(feed.get_id()))
//                                .build();
//
//                        getContentResolver().delete(deleteFeedUri, null, null);
//                    } else if (itemViewType == NAPPY_VIEW_HOLDER) {
//                        LogAdapter.NappyViewHolder nappyViewHolder = (LogAdapter.NappyViewHolder) viewHolder;
//                        long lid = nappyViewHolder.getAdapterPosition();
//                        Nappy nappy = nappyViewHolder.getNappy();
//                        //mDb.deleteNappy(nappy.getId());
//                        Uri deleteUri = EntriesProviderContract.URI_NAPPIES.buildUpon()
//                                .appendPath(String.valueOf(nappy.get_id()))
//                                .build();
//                        getContentResolver().delete(deleteUri, null, null);
//                    } else {
//
//                        return;
//                        // DO NOTHING. This can be: DATE_VIEW_HOLDER or SUMMARY_VIEW_HOLDER or other.
//                        //throw new IllegalArgumentException("ItemViewType = " + itemViewType);
//                    }
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleTouchCallback);
        itemTouchHelper.attachToRecyclerView(logRecyclerView);

        // Getting FloatingActionButton:
//        fab = (FloatingActionButton) findViewById(R.id.fab_add);
//        fab.setOnClickListener(this);

        // Adding bottom-bar items:
        bottomBarMilkTv = (TextView) findViewById(R.id.bottom_bar_milk_tv);
        bottomBarNappyTv = (TextView) findViewById(R.id.bottom_bar_nappy_tv);
        bottomBarNoteTv = (TextView) findViewById(R.id.bottom_bar_note_tv);
        bottomBarSleepTv = (TextView) findViewById(R.id.bottom_bar_sleep_tv);

        for(TextView tv : new TextView[] { bottomBarMilkTv, bottomBarNappyTv, bottomBarNoteTv, bottomBarSleepTv }) {
            tv.setOnClickListener(this);
        }


        // Two loaders:
//        getSupportLoaderManager().initLoader(ID_ENTRIES_LOADER, null, this);
//        getSupportLoaderManager().initLoader(ID_LATEST_FEED_LOADER, null, this);
        loader = new MainActivityLoader(this, logRecyclerView, mAdapter, new OnUpdateTimeElapsed() {
            @Override
            public void onUpdateTimeElapsed(Cursor data) {
                updateTimeElapsed(data);
            }
        });
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


/*    public void safeAddTimeElapsedFrag() {
        if (timeElapsedFrag == null) {
            timeElapsedFrag = new TimeElapsedFragment();
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.top_main_fragment, timeElapsedFrag, TIME_ELAPSED_FRAG_TAG);
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    } */

// FRAGMENTS.
    private Map<String, Fragment> fragments = new HashMap<>();

    private OnCloseListener backButton = new OnCloseListener() {
        @Override
        public void close() {
            onBackPressed();
        }
    };

    private void safeAddWhatever(String fragment_id) {
        Fragment frag;

        if (!fragments.containsKey(fragment_id)) {

            // TODO: Replece the stuff here with a fragment factory:
            switch (fragment_id) {
                case MILK_ADDER_FRAG_TAG: {
                    frag = new MilkAdderFragment();
                    ((MilkAdderFragment) frag).setOnCloseListener(backButton);
                    break;
                }
                case TIME_ELAPSED_FRAG_TAG: {
                    frag = new TimeElapsedFragment();
                    break;
                }
                case NAPPY_ADDER_FRAG_TAG: {
                    frag = new NappyAdderFragment();
                    ((NappyAdderFragment) frag).setOnCloseListener(backButton);
                    break;
                }
                case SLEEP_ADDER_FRAG_TAG: {
                    frag = new SleepAdderFragment();
                    ((SleepAdderFragment) frag).setOnCloseListener(backButton);
                    break;
                }
                case NOTE_ADDER_FRAG_TAG: {
                    frag = new NoteAdderFragment();
                    ((NoteAdderFragment) frag).setOnCloseListener(backButton);
                    break;
                }
                default: {
                    throw new IllegalArgumentException("Invalid FRAGMENT_ID number");
                }
            }
            fragments.put(fragment_id, frag);
        } else {
            frag = fragments.get(fragment_id);
        }

        // Do not add fragment if it is already visible.
        if (frag.isVisible()) {
            return;
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        ft.replace(R.id.top_main_fragment, frag, fragment_id);
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }

//    public void safeDeleteTimeElapsedFrag() {
//        // TODO: Rewrite to support other fragments.
//
//        //getFragmentManager().findFragmentByTag(TIME_ELAPSED_FRAG_TAG);
//        if (timeElapsedFrag != null) {
//            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//            ft.remove(timeElapsedFrag);
//            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
//            ft.commit();
//            timeElapsedFrag = null;
//            fragments.remove(TIME_ELAPSED_FRAG_TAG);
//        } else if (milkAdderFragment != null) {
//            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//            ft.remove(milkAdderFragment);
//            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
//            ft.commit();
//            milkAdderFragment= null;
//            fragments.remove(MILK_ADDER_FRAG_TAG);
//        }
//    }

    private void safeDeleteFragment(String tag) {
        Fragment frag;
        // TODO: Rewrite to support other fragments.

        //Fragment frag = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragments.containsKey(tag)) {
            frag = fragments.get(tag);
        } else {
            return;
        }

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.remove(frag);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
            ft.commit();
            fragments.remove(tag);
//            timeElapsedFrag = null;
            fragments.remove(TIME_ELAPSED_FRAG_TAG);
//        } else if (milkAdderFragment != null) {
//            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//            ft.remove(milkAdderFragment);
//            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
//            ft.commit();
//            milkAdderFragment= null;
//            fragments.remove(MILK_ADDER_FRAG_TAG);
//        }
    }

    // TODO: rework this function.
    private void updateTimeElapsedFrag(SharedPreferences sharedPreferences) {
        boolean showTimeElapsed = sharedPreferences.getBoolean("show_time_passed", true);

        if (showTimeElapsed) {
            //safeAddTimeElapsedFrag();
            safeAddWhatever(TIME_ELAPSED_FRAG_TAG);
        } else {
            //safeDeleteTimeElapsedFrag();
            safeDeleteFragment(TIME_ELAPSED_FRAG_TAG);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
//        if (key.equals("show_time_passed")) {
//            // FIXME: This cannot be here, as this function will be called when activity is not active, what will lead to crash (java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState)
//            //updateTimeElapsedFrag(sharedPreferences);
//        }
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

    @Override
    public void onClick(View v) {
        if (v == null) {
            throw new UnsupportedOperationException("Onclick provided with null value");
        }

        if (v.equals(logRecyclerView)) {
            Toast.makeText(this, "logRecyclerView to be handled yet", Toast.LENGTH_LONG).show();
        }
//        else if (v.equals(fab)) {
//            Intent intent = new Intent(this, AddEntryActivity.class);
//            // TODO: putExtra parameters here
//            startActivity(intent);
//
//        }
        else if (v.equals(bottomBarMilkTv)) {
            safeAddWhatever(MILK_ADDER_FRAG_TAG);
            //safeAddMilkAdderFrag();
        } else if (v.equals(bottomBarNappyTv)) {
            safeAddWhatever(NAPPY_ADDER_FRAG_TAG);
        } else if (v.equals(bottomBarNoteTv)) {
            safeAddWhatever(NOTE_ADDER_FRAG_TAG);
        } else if (v.equals(bottomBarSleepTv)) {
            safeAddWhatever(SLEEP_ADDER_FRAG_TAG);
        } else {
            throw new UnsupportedOperationException(
                    "Unhandled item clicked: " + v.getClass() + "[" + v +" ]");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        outState.putInt(MILK_AMOUNT_VALUE_KEY, milkAmountValue);
//        Log.d(TAG, "Saved milkAmountValue: " + milkAmountValue);
    }

}
