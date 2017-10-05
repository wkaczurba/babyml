package com.example.android.babyml;

import android.database.Cursor;

/**
 * Created by WKaczurb on 10/4/2017.
 *
 * Interface used for MainActivity / TimeElapsedFragment
 */

interface OnUpdateTimeElapsed {
    void onUpdateTimeElapsed(Cursor data);
}
