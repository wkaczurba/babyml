package com.example.android.babyml.data;

import android.provider.BaseColumns;

/**
 * Created by WKaczurb on 8/5/2017.
 */

// TODO: Rename to FeedContract or similar.
public class FeedContract {

    public class FeedingEntry implements BaseColumns {
        public static final String TABLE_NAME = "FEED_TB";
        public static final String COLUMN_FEED_TS = "FEED_TS";
        public static final String COLUMN_FEED_AMOUNT = "FEED_AMOUNT";
        public static final String INDEX_TS = "FEED_TS_IDX";
    }
}
