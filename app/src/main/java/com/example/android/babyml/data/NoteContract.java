package com.example.android.babyml.data;

import android.provider.BaseColumns;

/**
 * Created by wkaczurb on 9/14/2017.
 */

public class NoteContract {

    public class NoteEntry implements BaseColumns {
        public static final String TABLE_NAME = "NOTE_TB";
        public static final String COLUMN_NOTE_TS = "NOTE_TS";
        public static final String COLUMN_NOTE_VALUE = "NOTE_VALUE"; // NUMBER(1,0);
        public static final String INDEX_TS = "NOTE_TS_IDX";
    }
}
