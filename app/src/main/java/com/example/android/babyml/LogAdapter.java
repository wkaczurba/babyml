package com.example.android.babyml;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.babyml.data.FeedingContract;

import java.util.ArrayList;
import java.util.List;


// TODO: Add different viewHolders as per: https://stackoverflow.com/questions/26245139/how-to-create-recyclerview-with-multiple-view-type
// TODO: C:\git\android\ud851-Exercises\Lesson07-Waitlist\T07.04-Exercise-UpdateTheAdapter
// READ: https://developer.android.com/training/material/lists-cards.html

public class LogAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = LogAdapter.class.getSimpleName();
    public final static int FEEDING_VIEW_HOLDER = 0;
    public final static int DATE_VIEW_HOLDER = 1;

    public interface ListItemClickListener {
        public void onListItemClick(int clickItemIndex);
    }

    static abstract class Item {};
    static class FeedingItem extends Item {
        private long dbId;

        public static FeedingItem fromCursor(Cursor cursor) {
            long dbId = cursor.getLong(cursor.getColumnIndex(FeedingContract.FeedingEntry._ID));
            int mFeedAmount = cursor.getInt(cursor.getColumnIndex(FeedingContract.FeedingEntry.COLUMN_FEED_AMOUNT));
            long mFeedTimestamp = cursor.getLong(cursor.getColumnIndex(FeedingContract.FeedingEntry.COLUMN_FEED_TIMESTAMP));
            CharSequence mCsTime = DateFormat.format("E yyyy-MM-dd HH:mm", mFeedTimestamp); // TODO: Consider adding option to select 12 vs 24 hours.

            CharSequence time = DateFormat.format("HH:mm", mFeedTimestamp);
            String mStringInfo = String.format(time + " milk = %d", mFeedAmount);

            return new FeedingItem(dbId, mFeedAmount, mFeedTimestamp, mCsTime, mStringInfo);
        }

        public long getDbId() {
            return dbId;
        }

        public int getmFeedAmount() {
            return mFeedAmount;
        }

        public long getmFeedTimestamp() {
            return mFeedTimestamp;
        }

        public CharSequence getmCsTime() {
            return mCsTime;
        }

        public String getmStringInfo() {
            return mStringInfo;
        }

        private int mFeedAmount;
        private long mFeedTimestamp;
        private CharSequence mCsTime;
        private String mStringInfo;

        public FeedingItem(long dbId, int mFeedAmount, long mFeedTimestamp, CharSequence mCsTime, String mStringInfo) {
            this.dbId = dbId;
            this.mFeedAmount = mFeedAmount;
            this.mFeedTimestamp = mFeedTimestamp;
            this.mCsTime = mCsTime;
            this.mStringInfo = mStringInfo;
        }
    }

    static class DateItem extends Item {
        public DateItem(String sDate) {
            this.sDate = sDate;
        }

        public String getsDate() {
            return sDate;
        }

        private String sDate;
    }

    private List<Item> items;
    public final ListItemClickListener mOnClickListener;

    public LogAdapter(ListItemClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    /**
     * Created by WKaczurb on 8/6/2017.
     */

    public class FeedingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Item item;
        public TextView logTextView;

        public boolean isFeedingItem() {
            if (item instanceof FeedingItem)
                return true;
            return false;
        }

        public Item getItem() {
            return item;
        }

        public FeedingViewHolder(View itemView) {
            super(itemView);
            logTextView = (TextView) itemView.findViewById(R.id.log_item_tv);
            itemView.setOnClickListener(this);
        }

        private void bind(int position) {
            if (position < 0 || position >= items.size())
                return;

            item = items.get(position);

            if (item instanceof FeedingItem) {
                logTextView.setText(((FeedingItem) item).mStringInfo);
            } else if (item instanceof DateItem) {
                throw new IllegalArgumentException("This statement should never be reached");
            }
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }

    public class DateViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Item item;
        public TextView dateTextView;

        public boolean isFeedingItem() {
            if (item instanceof FeedingItem)
                return true;
            return false;
        }

        public Item getItem() {
            return item;
        }

        public DateViewHolder(View itemView) {
            super(itemView);
            dateTextView = (TextView) itemView.findViewById(R.id.date_item_tv);
            itemView.setOnClickListener(this);
        }

        private void bind(int position) {
            if (position < 0 || position >= items.size())
                return;

            Log.d(TAG, "Binding item (date): " + position);
            item = items.get(position);
            dateTextView.setText(((DateItem) item).sDate);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }

    public void swapCursor(Cursor cursor) {
        CharSequence dateString, oldDateString = null;

        items = new ArrayList<>();

        // Updating all items;
        while (cursor.moveToNext()) {
            FeedingItem item = FeedingItem.fromCursor(cursor);

            // Process date + add date if needed
            dateString = DateFormat.format("yyyy-MM-dd", item.mFeedTimestamp); // FIXME: use LocalDate for date.
            if (!dateString.equals(oldDateString)) {

                // Create new entry with date before inserting feeding entry itself.
                DateItem d = new DateItem(dateString.toString()); // FIXME: Change TODAY, YESTERDAY, then dates...
                items.add(d);
                oldDateString = dateString;
            }
            items.add(item);
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

            default: throw new IllegalArgumentException("Invalid viewType:" + vh);
        }
        return vh;
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof FeedingItem) {
            return 0;
        } else if (items.get(position) instanceof DateItem) {
            return 1;
        } else {
            throw new IllegalArgumentException("Item at position(" + position +") is neither FeedingItem or DateItem");
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case FEEDING_VIEW_HOLDER: ((FeedingViewHolder) holder).bind(position); break;
            case DATE_VIEW_HOLDER: ((DateViewHolder) holder).bind(position); break;
            default: break;
        }
    }

    @Override
    public int getItemCount() {
        if (items == null)
            return 0;
        return items.size();
    }

}
