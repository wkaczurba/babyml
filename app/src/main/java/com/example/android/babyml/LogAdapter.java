package com.example.android.babyml;

import android.app.LauncherActivity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


// TODO: C:\git\android\ud851-Exercises\Lesson07-Waitlist\T07.04-Exercise-UpdateTheAdapter
// READ: https://developer.android.com/training/material/lists-cards.html
public class LogAdapter extends RecyclerView.Adapter<LogAdapter.LogViewHolder> {

    List<String> mData = new ArrayList<>();

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
        //public ListItemClickListener onClickListener;

        public LogViewHolder(View itemView) {
            super(itemView);
            //mTextView = itemView;
            logTextView = (TextView) itemView.findViewById(R.id.log_item_tv);
            itemView.setOnClickListener(this);
        }

        private void bind(int position) {
            if (position < 0 || position >= mData.size())
                return; // TODO: Handle an error;

            logTextView.setText(LogAdapter.this.mData.get(position));
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }

    public void setData(List<String> list) {
        this.mData = list;

        // FIXME: This should be replaced with more specific notification.
        notifyDataSetChanged();
    }

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
        if (mData == null)
            return 0;
        return mData.size();
    }


}
