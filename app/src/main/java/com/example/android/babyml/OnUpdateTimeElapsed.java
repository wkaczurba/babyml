package com.example.android.babyml;

import android.database.Cursor;

/**
 * Created by WKaczurb on 10/4/2017.
 *
 * Interface used for MainActivity / TimeElapsedFragment
 */

public interface OnUpdateTimeElapsed {
    public void onUpdateTimeElapsed(Cursor data);
}
