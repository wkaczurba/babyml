package com.example.android.babyml;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.babyml.data.EntriesDbHandler;
import com.example.android.babyml.data.EntriesMap;
import com.example.android.babyml.data.Entry;
import com.example.android.babyml.data.Feed;
import com.example.android.babyml.data.Nappy;
import com.example.android.babyml.data.Note;
import com.example.android.babyml.data.Sleep;
import com.example.android.babyml.data.Summary;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;
import java.util.TimeZone;

import lombok.Getter;
import lombok.Setter;

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
    public static final int SLEEP_VIEW_HOLDER = 4;
    public static final int NOTE_VIEW_HOLDER = 5;

    public interface ListItemClickListener {
        public void onListItemClick(int clickItemIndex);
    }

//    static abstract class Item {};
    private EntriesMap entriesMap;

    /**
     * Carries:
     *   Feed for Feeds
     *   LocalDate objects for separators
     *
     */
    public final ListItemClickListener mOnClickListener;
    public LogAdapter(ListItemClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    public Object get(int position) {
        return entriesMap.get(position);
    }

    /**
     * Created by WKaczurb on 8/6/2017.
     */

    public class FeedingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Getter
        private Feed feed;
        public TextView logTextView;

//        public Feed getFeed() {
//            return feed;
//        }

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
                DateTime dt = new DateTime(feed.getTs(), currentTz);
                DateTimeFormatter dtf = DateTimeFormat.forPattern("HH:mm");
                CharSequence time = dtf.print(dt);

// Format note
//                StringBuilder noteSb = new StringBuilder();
//                if (feed.getNote() != null && feed.getNote().length() > 0) {
//                    noteSb.append("; ").append(feed.getNote());
//                }
// End
                String s = String.format("%s milk = %d%s", time, feed.getAmount(), formatNote(feed.getNote()));

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

//        public LocalDate getDate() {
//            return date;
//        }

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
        @Getter
        private Nappy nappy;

        public NappyViewHolder(View itemView) {
            super(itemView);
            logListNappyTv = (TextView) itemView.findViewById(R.id.log_list_nappy_tv);
            itemView.setOnClickListener(this);
        }

//        public Nappy getNappy() {
//            return nappy;
//        }

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
                DateTime dt = new DateTime(nappy.getTs(), currentTz);
                DateTimeFormatter dtf = DateTimeFormat.forPattern("HH:mm");
                CharSequence time = dtf.print(dt);
// Format note
//                StringBuilder noteSb = new StringBuilder();
//                if (nappy.getNote() != null && nappy.getNote().length() > 0) {
//                    noteSb.append("; ").append(nappy.getNote());
//
//
//                }
// End
                String s = String.format("%s dirty nappy%s", time, formatNote(nappy.getNote())); // can be "nappy=" + n.getDirrty();
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

    public String formatNote(String note) {
        if (note == null) {
            return "";
        } else {
            StringBuilder sb = new StringBuilder("; ");
            if (note.length() > 20) {
                sb.append(note.substring(0, 20) + "...");
            } else {
                sb.append(note);
            }
            return sb.toString();
        }
    }

    public class SummaryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Getter
        private Summary summary; // FIXME: This should probabyl contain something different (?)
        public TextView summaryTextView;

//        public Summary getSummary() {
//            return summary;
//        }

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

    public class SleepViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView logListSleepTv;

        @Getter
        @Setter
        Context ctx;

        @Getter
        private Sleep sleep;

        public SleepViewHolder(View itemView) {
            super(itemView);
            logListSleepTv = (TextView) itemView.findViewById(R.id.log_list_sleep_tv);
            itemView.setOnClickListener(this);
        }

        private void bind(int position) {
            if (position < 0 || position >= entriesMap.size()) {
                return;
            }

            Log.d(TAG, "Binding (sleep): " + position);
            Object o = entriesMap.get(position);
            if (o instanceof Sleep) {
                sleep = (Sleep) o;
                long start = sleep.getTs();
                long end = sleep.getEndTs();
//                String note = sleep.getNote();

                // Format the stuff: (TODO: Move to formatting or somewhere); it is common with FeedingViewHolder.bind(...)
                DateTimeZone currentTz = DateTimeZone.forTimeZone(TimeZone.getDefault());
                DateTime dt = new DateTime(start, currentTz);
                DateTimeFormatter dtf = DateTimeFormat.forPattern("HH:mm");
                CharSequence startTime = dtf.print(dt);

                DateTime endTs = new DateTime(end, currentTz);
                CharSequence endTime = dtf.print(endTs);

// FIXME: Simplify once DB is correct.
                String lengthS = "?";
                if (end < start) {
                    Toast.makeText(ctx, "Error in DB: end < start", Toast.LENGTH_LONG).show();
                } else {
                    long lengthMillis = (end - start);
                    LocalTime lengthLt = new LocalTime((int) (lengthMillis / 3600_000L), (int) (lengthMillis / 60_000L));
                    lengthS = dtf.print(lengthLt);
                }

                // Process note:
// Note formatting.
//                if (note == null) {
//                    note = "";
//                } else {
//                    StringBuilder sb = new StringBuilder("; ");
//                    if (note.length() > 20) {
//                        sb.append(note.substring(0, 20) + "...");
//                    } else {
//                        sb.append(note);
//                    }
//                    note = sb.toString();
//                }
// Note formating

                // TODO: Add note.!
                String s = String.format(startTime + "-" + endTime + " sleep (" + lengthS + " long)" + formatNote(sleep.getNote())); // can be "sleep=" + n.getDirrty();
                logListSleepTv.setText(s);
            } else { //if (o instanceof DateItem) { // DateItem
                throw new IllegalArgumentException("Invalid object; expected Sleep; got: " + o.getClass());
            }
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView logListNoteTv;

        @Getter
        private Note note;

        public NoteViewHolder(View itemView) {
            super(itemView);
            logListNoteTv = (TextView) itemView.findViewById(R.id.log_list_note_tv);
            itemView.setOnClickListener(this);
        }

        private void bind(int position) {
            Log.d(TAG, "In binding note.");
            if (position < 0 || position >= entriesMap.size()) {
                Log.d(TAG, "Binding (note) skipped.");
                return;
            }

            Log.d(TAG, "Binding (note): " + position);
            Object o = entriesMap.get(position);
            if (o instanceof Note) {
                note = (Note) o;
                long ts = note.getTs(); // timestamp;
                String value = note.getValue();

                // Format the stuff: (TODO: Move to formatting or somewhere); it is common with FeedingViewHolder.bind(...)
                DateTimeZone currentTz = DateTimeZone.forTimeZone(TimeZone.getDefault());
                DateTime dt = new DateTime(ts, currentTz);
                DateTimeFormatter dtf = DateTimeFormat.forPattern("HH:mm");
                CharSequence time = dtf.print(dt);

                String s = new StringBuilder(time).append(" ").append(value).toString(); // can be "note=" + n.getDirrty();
                logListNoteTv.setText(s);
            } else { //if (o instanceof DateItem) { // DateItem
                throw new IllegalArgumentException("Invalid object; expected Note; got: " + o.getClass());
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
        long id = cursor.getLong(cursor.getColumnIndex(Feed.COLUMN_ID));
        int amt = cursor.getInt(cursor.getColumnIndex(Feed.COLUMN_FEED_AMOUNT));
        long ts = cursor.getLong(cursor.getColumnIndex(Feed.COLUMN_FEED_TS));
        String note = cursor.getString(cursor.getColumnIndex(Feed.COLUMN_FEED_NOTE));

        //return new Feed(id, ts, amt);
        return new Feed(id, Feed.COLUMN_FEED_TB, ts, amt, note);
    }

    public static EntriesDbHandler.EntryType getEntryType(Cursor cursor) {
        EntriesDbHandler.EntryType e;
        String tb = cursor.getString(cursor.getColumnIndex(Entry.COLUMN_ENTRY_TB)); // WAS: EntriesDbHandler.COLUMN_TB));


        e = EntriesDbHandler.EntryType.getEntryType(tb);

        return e;
    }

    public void swapCursor(Cursor cursor) {
        entriesMap = new EntriesMap();

        if (cursor == null) {
            Log.d(TAG, "swapCursor.cursor == null");
            notifyDataSetChanged();
            return;
        }
        Log.d(TAG, "swapCursor.cursor.getCount()= " + cursor.getCount());

        // Updating all items;
        cursor.moveToPosition(-1); // This is necessary as same cursor will be reused for reloading on device rotation.
        while (cursor.moveToNext()) {

            EntriesDbHandler.EntryType e = getEntryType(cursor);

            long id = cursor.getLong(cursor.getColumnIndex(Entry.COLUMN_ID)); // EntriesDbHandler.COLUMN_ID_FOREIGN));
            long timestamp = cursor.getLong(cursor.getColumnIndex(Entry.COLUMN_ENTRY_TS)); //EntriesDbHandler.COLUMN_TS));

            // TODO: Consider changing to switch.case...:
            if (e.equals(EntriesDbHandler.EntryType.Feed)) {
                int feedAmount = cursor.getInt(cursor.getColumnIndex(Feed.COLUMN_FEED_AMOUNT)); // EntriesDbHandler.COLUMN_FEED_AMOUNT));
                String feedNote = cursor.getString(cursor.getColumnIndex(Feed.COLUMN_FEED_NOTE));
                // TODO: Consider using Feed.fromCursor.
                Feed f = new Feed(id, Feed.TABLE_NAME, timestamp, feedAmount, feedNote);
                entriesMap.addSummarizable(f);
            } else if (e.equals(EntriesDbHandler.EntryType.Nappy)) {
                int nappyDirty = cursor.getInt(cursor.getColumnIndex(Nappy.COLUMN_NAPPY_DIRTY)); //EntriesDbHandler.COLUMN_NAPPY_DIRTY));
                int nappyWet = cursor.getInt(cursor.getColumnIndex(Nappy.COLUMN_NAPPY_WET)); //EntriesDbHandler.COLUMN_NAPPY_DIRTY));
                String nappyNote = cursor.getString(cursor.getColumnIndex(Nappy.COLUMN_NAPPY_NOTE)); //EntriesDbHandler.COLUMN_NAPPY_DIRTY));
                // TODO: Consider using Nappy.fromCursor.
                Nappy n = new Nappy(id, Nappy.TABLE_NAME, timestamp, nappyDirty, nappyWet, nappyNote);
                entriesMap.addSummarizable(n);
            } else if (e.equals(EntriesDbHandler.EntryType.Note)) {
                String noteValue = cursor.getString(cursor.getColumnIndex(Note.COLUMN_NOTE_VALUE)); //EntriesDbHandler.COLUMN_NOTE_VALUE));
                Note n = new Note(id, Note.TABLE_NAME, timestamp, noteValue);
                // TODO: Consider using Note.fromCursor.
                entriesMap.addSummarizable(n);
            } else if (e.equals(EntriesDbHandler.EntryType.Sleep)) {
                String columns[] = cursor.getColumnNames();
                for (String column : columns) {
                    Log.d(TAG, "COLUMN IN SLEEP: " + column);
                }
                String note = cursor.getString(cursor.getColumnIndex(Sleep.COLUMN_SLEEP_NOTE)); //EntriesDbHandler.COLUMN_NOTE_VALUE));
                Long entTimestamp = cursor.getLong(cursor.getColumnIndex(Sleep.COLUMN_SLEEP_END_TS)); //EntriesDbHandler.COLUMN_NOTE_VALUE));
                Sleep s = new Sleep(id, Sleep.TABLE_NAME, timestamp, entTimestamp, note);
                // TODO: Consider using Sleep.fromCursor.
                entriesMap.addSummarizable(s);
            } else {
                throw new IllegalArgumentException("Invalid enum: " + e.name());
            }
        }
        notifyDataSetChanged();
        Log.d(TAG, "Cursor swapped; EntryMap.size(): " + entriesMap.size());
    }


    // TODO: Use strategy pattern for this!.
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

            case SLEEP_VIEW_HOLDER:
                view = layoutInflater.inflate(R.layout.log_list_sleep,
                        parent,
                        shouldAttachParentImmediately);
                vh = new SleepViewHolder(view);
                ((SleepViewHolder) vh).setCtx(parent.getContext());
                break;

            case NOTE_VIEW_HOLDER:
                view = layoutInflater.inflate(R.layout.log_list_note,
                        parent,
                        shouldAttachParentImmediately);
                vh = new NoteViewHolder(view);
                break;

            default: throw new IllegalArgumentException("Invalid viewType:" + vh);
        }
        return vh;
    }

    // TODO: Rework it to use STRATEGY.
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
        } else if (entriesMap.get(position) instanceof Sleep) {
            return SLEEP_VIEW_HOLDER;
        } else if (entriesMap.get(position) instanceof Note) {
            return NOTE_VIEW_HOLDER;
        } else {
            throw new IllegalArgumentException("Item at position(" + position +") is neither FeedingItem or DateItem");
        }
    }

    // TODO: Refactor this.
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case FEEDING_VIEW_HOLDER: ((FeedingViewHolder) holder).bind(position); break;
            case DATE_VIEW_HOLDER: ((DateViewHolder) holder).bind(position); break;
            case NAPPY_VIEW_HOLDER: ((NappyViewHolder) holder).bind(position); break;
            case SUMMARY_VIEW_HOLDER: ((SummaryViewHolder) holder).bind(position); break;
            case SLEEP_VIEW_HOLDER: ((SleepViewHolder) holder).bind(position); break;
            case NOTE_VIEW_HOLDER: ((NoteViewHolder) holder).bind(position); break;
            default: break;
        }
    }

    @Override
    public int getItemCount() {
        if (entriesMap == null) {
            return 0;
        }
        return entriesMap.size();
    }
}
