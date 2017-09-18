package com.example.android.babyml;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.babyml.data.EntriesDbHelper;
import com.example.android.babyml.data.EntriesMap;
import com.example.android.babyml.data.Feed;
import com.example.android.babyml.data.FeedContract;
import com.example.android.babyml.data.Nappy;
import com.example.android.babyml.data.Note;
import com.example.android.babyml.data.Summary;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.TimeZone;

// FIXME: On changing TimeZones items in text moxes are not updated.

// TODO: Add different viewHolders as per: https://stackoverflow.com/questions/26245139/how-to-create-recyclerview-with-multiple-view-type
// TODO: C:\git\android\ud851-Exercises\Lesson07-Waitlist\T07.04-Exercise-UpdateTheAdapter
// READ: https://developer.android.com/training/material/lists-cards.html

public class LogAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = LogAdapter.class.getSimpleName();
    public final static int FEEDING_VIEW_HOLDER = 0;
    public final static int DATE_VIEW_HOLDER = 1;
    public static final int NAPPY_VIEW_HOLDER = 2;
    public static final int SUMMARY_VIEW_HOLDER = 3;

    public interface ListItemClickListener {
        public void onListItemClick(int clickItemIndex);
    }

    static abstract class Item {};
    private EntriesMap entriesMap;

    /**
     * Carries:
     *   Feed for Feeds
     *   LocalDate objects for separators
     *
     */
//    private List<Object> items;
//
    public final ListItemClickListener mOnClickListener;
    public LogAdapter(ListItemClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }


    /**
     * Created by WKaczurb on 8/6/2017.
     */

    public class FeedingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Feed feed;
        public TextView logTextView;

        public Feed getFeed() {
            return feed;
        }

        public FeedingViewHolder(View itemView) {
            super(itemView);
            logTextView = (TextView) itemView.findViewById(R.id.log_item_tv);
            itemView.setOnClickListener(this);
        }

        private void bind(int position) {
            Object o;
            if (position < 0 || position >= entriesMap.size())
                return;

            o = entriesMap.get(position);

            if (o instanceof Feed) {
                feed = (Feed) o;

                // Format the stuff: (TODO: Move to formatting or somewhere)
                DateTimeZone currentTz = DateTimeZone.forTimeZone(TimeZone.getDefault());
                DateTime dt = new DateTime(feed.getTimestamp(), currentTz);
                DateTimeFormatter dtf = DateTimeFormat.forPattern("HH:mm");
                CharSequence time = dtf.print(dt);

                String s = String.format(time + " milk = %d", feed.getAmount());

                logTextView.setText(s);
            } else { //if (o instanceof DateItem) { // DateItem
                throw new IllegalArgumentException("Invalid object; expected FeedingItem; got: " + o.getClass());
            }
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }

    public class DateViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private LocalDate date;
        public TextView dateTextView;

        public LocalDate getDate() {
            return date;
        }

        public DateViewHolder(View itemView) {
            super(itemView);
            dateTextView = (TextView) itemView.findViewById(R.id.date_item_tv);
            itemView.setOnClickListener(this);
        }

        private void bind(int position) {

            if (position < 0 || position >= entriesMap.size())
                return;

            Log.d(TAG, "Binding LocalDaate: " + position);
            Object o = entriesMap.get(position);
            if (o instanceof LocalDate) {
                date = ((LocalDate) o);

                DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd");
                dateTextView.setText(dtf.print(date));
            } else {
                throw new IllegalArgumentException("Invalid object; expected LocalDate; got: " + o.getClass());
            }
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }

    public class NappyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView logListNappyTv;
        private Nappy nappy;

        public NappyViewHolder(View itemView) {
            super(itemView);
            logListNappyTv = (TextView) itemView.findViewById(R.id.log_list_nappy_tv);
            itemView.setOnClickListener(this);
        }

        public Nappy getNappy() {
            return nappy;
        }

        private void bind(int position) {
            if (position < 0 || position >= entriesMap.size()) {
                return;
            }

            Log.d(TAG, "Binding dateTime (nappy): " + position);
            Object o = entriesMap.get(position);
            if (o instanceof Nappy) {
                nappy = (Nappy) o;

                // Format the stuff: (TODO: Move to formatting or somewhere); it is common with FeedingViewHolder.bind(...)
                DateTimeZone currentTz = DateTimeZone.forTimeZone(TimeZone.getDefault());
                DateTime dt = new DateTime(nappy.getTimestamp(), currentTz);
                DateTimeFormatter dtf = DateTimeFormat.forPattern("HH:mm");
                CharSequence time = dtf.print(dt);

                String s = String.format(time + " dirty nappy"); // can be "nappy=" + n.getDirrty();
                logListNappyTv.setText(s);
            } else { //if (o instanceof DateItem) { // DateItem
                throw new IllegalArgumentException("Invalid object; expected Nappy; got: " + o.getClass());
            }

        }

            @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
                mOnClickListener.onListItemClick(clickedPosition);
        }
    }

    public class SummaryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Summary summary; // FIXME: This should probabyl contain something different (?)
        public TextView summaryTextView;

        public Summary getSummary() {
            return summary;
        }

        public SummaryViewHolder(View itemView) {
            super(itemView);
            summaryTextView = (TextView) itemView.findViewById(R.id.summary_tv);
            itemView.setOnClickListener(this);
        }

        private void bind(int position) {
            if (position < 0 || position >= entriesMap.size())
                return;

            Log.d(TAG, "Binding summary: " + position);
            Object o = entriesMap.get(position);
            if (o instanceof Summary) {
                summary = (Summary) o;
                //summaryTextView.setText("<SUMMARY TODO>" + summary.toString());
                String s = String.format("Total %d ml (%d %s); %d dirty %s",
                        summary.getFeedAmount(),
                        summary.getFeedCounts(),
                        summary.getFeedCounts() == 1 ? "feed" : "feeds",
                        summary.getDirtyNappies(),
                        summary.getDirtyNappies() == 1 ? "nappy" : "nappies");
                summaryTextView.setText(s);
            } else {
                throw new IllegalArgumentException("Invalid object; expected String; got: " + o.getClass());
            }
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }

    // Helper:
    public static Feed cursorToFeed(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndex(FeedContract.FeedingEntry._ID));
        int amt = cursor.getInt(cursor.getColumnIndex(FeedContract.FeedingEntry.COLUMN_FEED_AMOUNT));
        long ts = cursor.getLong(cursor.getColumnIndex(FeedContract.FeedingEntry.COLUMN_FEED_TS));

        return new Feed(id, ts, amt);
    }

    public static EntriesDbHelper.EntryType getEntryType(Cursor cursor) {
        EntriesDbHelper.EntryType e;
        String tb = cursor.getString(cursor.getColumnIndex(EntriesDbHelper.COLUMN_TB));


        e = EntriesDbHelper.EntryType.getEntryType(tb);

        return e;
    }

    public void swapCursor(Cursor cursor) {
        LocalDate oldDate = null;
        entriesMap = new EntriesMap();

        // Updating all items;
        while (cursor.moveToNext()) {

            EntriesDbHelper.EntryType e = getEntryType(cursor);

            long id = cursor.getLong(cursor.getColumnIndex(EntriesDbHelper.COLUMN_ID));
            long timestamp = cursor.getLong(cursor.getColumnIndex(EntriesDbHelper.COLUMN_TS));

            // TODO: Consider changing to switch.case...:
            if (e.equals(EntriesDbHelper.EntryType.Feed)) {
                int feedAmount = cursor.getInt(cursor.getColumnIndex(EntriesDbHelper.COLUMN_FEED_AMOUNT));
                Feed f = new Feed(id, timestamp, feedAmount);
                Log.d(TAG, "GOT: " + f);
                entriesMap.addSerializable(f);
            } else if (e.equals(EntriesDbHelper.EntryType.Nappy)) {
                int nappyDirty = cursor.getInt(cursor.getColumnIndex(EntriesDbHelper.COLUMN_NAPPY_DIRTY));
                Nappy n = new Nappy(id, timestamp, nappyDirty);
                Log.d(TAG, "GOT NAPPY -> TODO");
                entriesMap.addSerializable(n);
            } else if (e.equals(EntriesDbHelper.EntryType.Note)) {
                String noteValue = cursor.getString(cursor.getColumnIndex(EntriesDbHelper.COLUMN_NOTE_VALUE));
                Note n = new Note(id, timestamp, noteValue);
                Log.d(TAG, "GOT NOTE -> TODO");
                entriesMap.addSerializable(n);
            } else {
                throw new IllegalArgumentException("Invalid enum: " + e.name());
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        boolean shouldAttachParentImmediately = false;
        RecyclerView.ViewHolder vh = null;
        View view;

        switch (viewType) {
            case FEEDING_VIEW_HOLDER:
                view = layoutInflater.inflate(R.layout.log_list_item,
                        parent,
                        shouldAttachParentImmediately);

                vh = new FeedingViewHolder(view);
                break;

            case DATE_VIEW_HOLDER:
                view = layoutInflater.inflate(R.layout.log_list_date,
                        parent,
                        shouldAttachParentImmediately);

                vh = new DateViewHolder(view);
                break;

            case NAPPY_VIEW_HOLDER:
                view = layoutInflater.inflate(R.layout.log_list_nappy,
                        parent,
                        shouldAttachParentImmediately);
                vh = new NappyViewHolder(view);
                break;

            case SUMMARY_VIEW_HOLDER:
                view = layoutInflater.inflate(R.layout.log_list_summary,
                        parent,
                        shouldAttachParentImmediately);
                vh = new SummaryViewHolder(view);
                break;

            default: throw new IllegalArgumentException("Invalid viewType:" + vh);
        }
        return vh;
    }

    @Override
    public int getItemViewType(int position) {
        if (entriesMap.get(position) instanceof Feed) {
            return FEEDING_VIEW_HOLDER;
        } else if (entriesMap.get(position) instanceof LocalDate) {
            return DATE_VIEW_HOLDER;
        } else if (entriesMap.get(position) instanceof Nappy) {
            return NAPPY_VIEW_HOLDER;
        } else if (entriesMap.get(position) instanceof Summary) {
            return SUMMARY_VIEW_HOLDER;
        } else {
            throw new IllegalArgumentException("Item at position(" + position +") is neither FeedingItem or DateItem");
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case FEEDING_VIEW_HOLDER: ((FeedingViewHolder) holder).bind(position); break;
            case DATE_VIEW_HOLDER: ((DateViewHolder) holder).bind(position); break;
            case NAPPY_VIEW_HOLDER: ((NappyViewHolder) holder).bind(position); break;
            case SUMMARY_VIEW_HOLDER: ((SummaryViewHolder) holder).bind(position); break;
// TODO: Add another view_holder for summary of feedings.
            default: break;
        }
    }

    @Override
    public int getItemCount() {
        if (entriesMap == null)
            return 0;
        return entriesMap.size();
    }

}
