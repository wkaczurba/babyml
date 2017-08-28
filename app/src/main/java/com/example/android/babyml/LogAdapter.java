package com.example.android.babyml;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.babyml.data.FeedingContract;


// TODO: C:\git\android\ud851-Exercises\Lesson07-Waitlist\T07.04-Exercise-UpdateTheAdapter
// READ: https://developer.android.com/training/material/lists-cards.html
public class LogAdapter extends RecyclerView.Adapter<LogAdapter.LogViewHolder> {

    //List<String> mData = new ArrayList<>();
    Cursor mCursor;

    public final ListItemClickListener mOnClickListener;

    // TODO: Put into separate file or something.
    public interface ListItemClickListener {
        public void onListItemClick(int clickItemIndex);
    }

    public LogAdapter(ListItemClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    /**
     * Created by WKaczurb on 8/6/2017.
     */

    public class LogViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView logTextView;

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

        private long dbId;
        private int mFeedAmount;
        private long mFeedTimestamp;
        private CharSequence mCsTime;
        private String mStringInfo;

        //public ListItemClickListener onClickListener;
        public LogViewHolder(View itemView) {
            super(itemView);
            //mTextView = itemView;
            logTextView = (TextView) itemView.findViewById(R.id.log_item_tv);
            itemView.setOnClickListener(this);
        }


        private void readCursor() {
            dbId = mCursor.getLong(mCursor.getColumnIndex(FeedingContract.FeedingEntry._ID));
            mFeedAmount = mCursor.getInt(mCursor.getColumnIndex(FeedingContract.FeedingEntry.COLUMN_FEED_AMOUNT));
            mFeedTimestamp = mCursor.getLong(mCursor.getColumnIndex(FeedingContract.FeedingEntry.COLUMN_FEED_TIMESTAMP));

            android.text.format.DateFormat df = new android.text.format.DateFormat();
            mCsTime = df.format("E yyyy-MM-dd HH:mm", mFeedTimestamp); // TODO: Consider adding option to select 12 vs 24 hours.

            StringBuilder sb = new StringBuilder();
            sb.append(mCsTime).append(" : milk= ").append(mFeedAmount);
            mStringInfo = sb.toString();
        }

        private void bind(int position) {
            if (position < 0 || position >= mCursor.getCount())
                return;

            mCursor.moveToPosition(position);
            readCursor();

//            list.add(sb.toString());

//            } while (cursor.moveToNext());

//            StringBuilder sb = new StringBuilder();
//            List<String> list = cursorAsStringList(cursor);
//            for (String mStringInfo : list) {
//                sb.append(mStringInfo).append("\n");
//            }
//            return sb.toString();


//            if (position < 0 || position >= mData.size())
//                return; // TODO: Handle an error;

            //logTextView.setText(LogAdapter.this.mData.get(position));
            logTextView.setText(mStringInfo);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }

    public void swapCursor(Cursor cursor) {
        mCursor = cursor;
        notifyDataSetChanged();
    }

//    public void setData(List<String> list) {
//        this.mData = list;
//
//        // FIXME: This should be replaced with more specific notification.
//        notifyDataSetChanged();
//    }

// FIXME: Finish this stuff off.
    @Override
    public LogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForLogListItem = R.layout.log_list_item;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        boolean shouldAttachParentImmediately = false;

        View view = layoutInflater.inflate(layoutIdForLogListItem,
                parent,
                shouldAttachParentImmediately);

        LogViewHolder viewHolder = new LogViewHolder(view);

        return viewHolder;
        //return null;
    }

    @Override
    public void onBindViewHolder(LogViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (mCursor == null)
            return 0;

        return mCursor.getCount();
        //if (mData == null)
            //return 0;
        //return mData.size();
    }


}
