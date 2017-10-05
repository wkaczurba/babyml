package com.example.android.babyml.ui;

import android.content.Context;
import android.widget.Toast;

import com.example.android.babyml.data.Feed;
import com.example.android.babyml.data.Nappy;
import com.example.android.babyml.data.Note;
import com.example.android.babyml.data.Sleep;

/**
 * Created by WKaczurb on 10/4/2017.
 *
 * Implementaiton of ListItemClickListener that displays type of object clicked.
 */

public class ToastListItemClickListener implements ListItemClickListener {
    private Context context;
    private Toast mToast;
    private LogAdapter mAdapter;

    public ToastListItemClickListener(Context context) {
        this.context = context;
    }

    public void setAdapter(LogAdapter adapter) {
        mAdapter = adapter;
    }

    @Override
    public void onListItemClick(int clickItemIndex) {
        // get from cursor + output as Toast.
        if (mToast != null) {
            mToast.cancel();
        }
        //mToast = Toast.makeText(context, "Item #" + clickItemIndex + " clicked", Toast.LENGTH_LONG);
        if (mAdapter != null) {
            // TODO: Disable this if not ready.
            Object o = mAdapter.get(clickItemIndex);
            handleItemClick(o, clickItemIndex);
        }
    }

    private void handleItemClick(Object o, int pos) {
        long id;
        
        // TODO: This should be handled using Strategy Pattern.
        String type;
        if (o == null) {
            return;
        } else if (o instanceof Feed) {
            id = ((Feed) o).get_id();
            type = "Feed; id=" + id;
        } else if (o instanceof Nappy) {
            id = ((Nappy) o).get_id();
            type = "Nappy; id=" + id;
        } else if (o instanceof Note) {
            id = ((Note) o).get_id();
            type = "Note; id=" + id;
        } else if (o instanceof Sleep) {
            id = ((Sleep) o).get_id();
            type = "Sleep; id=" + id;
        } else {
            type = "Other";
        }
        mToast = Toast.makeText(context, "Item #" + pos + " clicked ("+ type +")", Toast.LENGTH_LONG);
        mToast.show();
    }
}
