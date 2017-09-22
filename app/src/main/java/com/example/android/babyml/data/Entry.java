package com.example.android.babyml.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.android.babyml.legacy.FeedingContract;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

/**
 * Created by wkaczurb on 9/20/2017.
 */

@ToString
@EqualsAndHashCode(exclude={"_id"})
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
public class Entry {
    @Getter
    long _id; // _id;

    @Getter
    String tb; // table name for entry

    @Getter
    long ts; // time stamp for entry

    public ContentValues asContentValues() {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, _id);
        values.put(COLUMN_ENTRY_TB, tb);
        values.put(COLUMN_ENTRY_TS, ts);
        return values;
    }

    static Entry fromContentValues(ContentValues values) {
        return new Entry(
                values.getAsLong(COLUMN_ID),
                values.getAsString(COLUMN_ENTRY_TB),
                values.getAsLong(COLUMN_ENTRY_TS));
    }

    public static Entry fromCursor(Cursor cursor) {
        return new Entry(
                cursor.getLong(cursor.getColumnIndex(COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(COLUMN_ENTRY_TB)),
                cursor.getLong(cursor.getColumnIndex(COLUMN_ENTRY_TS)));
    }


    // DB-Related stuff:
    public static final String TABLE_NAME = "ENTRY_TB";
    public static final String COLUMN_ID = "_ID";
    public static final String COLUMN_ENTRY_TB = "ENTRY_TB";
    public static final String COLUMN_ENTRY_TS = "ENTRY_TS";

    static void createEntryTable(SQLiteDatabase db) {
        String SQL_CREATE_ENTRY_TB =
                "CREATE TABLE ENTRY_TB (\n" +
                "  _ID INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "  ENTRY_TB VARCHAR2(30) NOT NULL,\n" +
                "  ENTRY_TS TIMESTAMP NOT NULL,\n" +
                "  CONSTRAINT ENTRY_TB_CK CHECK (ENTRY_TB IN ('FEED_TB', 'NOTE_TB', 'NAPPY_TB', 'SLEEP_TB'))\n" +
                ");";
        db.execSQL(SQL_CREATE_ENTRY_TB);

        String SQL_CREATE_ENTRY_TS_INDEX = "CREATE INDEX ENTRY_TB_TS_IDX ON ENTRY_TB(ENTRY_TS);";
        db.execSQL(SQL_CREATE_ENTRY_TS_INDEX);
    }
}
