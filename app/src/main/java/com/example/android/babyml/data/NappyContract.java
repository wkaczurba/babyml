package com.example.android.babyml.data;

import android.provider.BaseColumns;

/**
 * Created by wkaczurb on 9/14/2017.
 */

public class NappyContract {

    public class NappyEntry implements BaseColumns {
        public static final String TABLE_NAME = "NAPPY_TB";
        public static final String COLUMN_NAPPY_TS = "NAPPY_TS";
        public static final String COLUMN_NAPPY_DIRTY = "NAPPY_DIRTY"; // NUMBER(1,0);
        public static final String INDEX_TS = "NAPPY_TS_IDX";
    }
}
