package com.example.android.babyml.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

/**
 * Created by wkaczurb on 9/15/2017.
 */

@ToString
//@AllArgsConstructor (implemented manually to check for tb.
@EqualsAndHashCode(exclude={"_id"})
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
//public class Feed extends Entry implements Summarizable {
public final class Feed implements Summarizable {

    @Getter
    private final long _id; // in the Entry.

    @Getter
    private final String tb; // table name for entry

    @Getter
    private final long ts; // re

    @Getter
    private final int amount;

    @Getter
    private final String note;

    public Feed(long _id, String tb, long ts, int amount, String note) {
        if (!tb.equals(TABLE_NAME)) {
            throw new IllegalArgumentException("tb.equals(" + tb +"); must be "+ TABLE_NAME + "; ");
        }
        this._id = _id;
        this.tb = tb;
        this.ts = ts;
        this.amount = amount;
        this.note = note;
    }

    @Override
    public void addSummary(Summary summary) {
        summary.setFeedAmount(summary.getFeedAmount() + amount);
        summary.setFeedCounts(summary.getFeedCounts() + 1);
    }

    public ContentValues asContentValues() {
        assert ( tb == TABLE_NAME );

        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, _id); // FeedingContract.FeedingEntry._ID
        values.put(COLUMN_FEED_TS, ts);
        values.put(COLUMN_FEED_TB, tb);
        values.put(COLUMN_FEED_AMOUNT, amount);
        values.put(COLUMN_FEED_NOTE, note);
        return values;
    }

    static Feed fromContentValues(ContentValues values) {
        return new Feed(
                values.getAsLong(COLUMN_ID),
                values.getAsString(COLUMN_FEED_TB),
                values.getAsLong(COLUMN_FEED_TS),
                values.getAsInteger(COLUMN_FEED_AMOUNT),
                values.getAsString(COLUMN_FEED_NOTE)
        );
    }

    public static Feed fromCursor(Cursor cursor) {
        return new Feed(
                        cursor.getLong(cursor.getColumnIndex(Feed.COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndex(Feed.COLUMN_FEED_TB)),
                        cursor.getLong(cursor.getColumnIndex(Feed.COLUMN_FEED_TS)),
                        cursor.getInt(cursor.getColumnIndex(Feed.COLUMN_FEED_AMOUNT)),
                        cursor.getString(cursor.getColumnIndex(Feed.COLUMN_FEED_NOTE)));
    }

    public Entry asEntry() {
        return new Entry(_id, this.tb, ts);
    }

    // DB-Functions:
    public static final String TABLE_NAME = "FEED_TB";
    public static final String COLUMN_ID = "_ID";
    public static final String COLUMN_FEED_TB = "FEED_TB";
    public static final String COLUMN_FEED_TS = "FEED_TS";
    public static final String COLUMN_FEED_AMOUNT = "FEED_AMOUNT";
    public static final String COLUMN_FEED_NOTE = "FEED_NOTE";

    static void createFeedTable(SQLiteDatabase db) {
        // TODO: Rework to use constatn Strings:

        String SQL_CREATE_FEED_TB =
                "CREATE TABLE FEED_TB (\n" +
                "  _ID INTEGER PRIMARY KEY,\n" +
                "  FEED_TB VARCHAR2(30) CONSTRAINT FEED_TB_CK CHECK (FEED_TB='FEED_TB') DEFAULT('FEED_TB'),\n" +
                "  FEED_TS TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,\n" +
                "  FEED_AMOUNT INTEGER CONSTRAINT FEED_AMOUNT_CK CHECK (FEED_AMOUNT > 0) NOT NULL,\n" +
                "  FEED_NOTE TEXT,\n" +
                "  CONSTRAINT FEED_ID_FK FOREIGN KEY (_ID) REFERENCES ENTRY_TB(_ID) ON DELETE CASCADE\n" +
                ");";
        db.execSQL(SQL_CREATE_FEED_TB);

        String SQL_CREATE_FEED_TS_INDEX =
                "CREATE INDEX FEED_TB_TS_IDX ON FEED_TB(FEED_TS);";
        db.execSQL(SQL_CREATE_FEED_TS_INDEX);
    }
}
