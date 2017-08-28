package com.example.android.babyml.data;

import android.provider.BaseColumns;

/**
 * Created by WKaczurb on 8/5/2017.
 */

public class FeedingContract {

    public class FeedingEntry implements BaseColumns {
        public static final String TABLE_NAME = "feeding";
        public static final String COLUMN_FEED_AMOUNT = "feed_amount";
        public static final String COLUMN_FEED_TIMESTAMP = "feed_timestamp";

    }
}
