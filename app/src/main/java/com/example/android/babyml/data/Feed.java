package com.example.android.babyml.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

/**
 * Created by wkaczurb on 9/15/2017.
 *
 * Feed for mapping.
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
        if (!tb.equals(TABLE_NAME)) {
            throw new IllegalArgumentException("Feed.tb must equal " + TABLE_NAME + "; got= '" + tb + "'" );
        }
        ContentValues values = new ContentValues();
        values.put(COLUMN_FEED_ID, _id); // FeedingContract.FeedingEntry._ID
        values.put(COLUMN_FEED_TS, ts);
        values.put(COLUMN_FEED_TB, tb);
        values.put(COLUMN_FEED_AMOUNT, amount);
        values.put(COLUMN_FEED_NOTE, note);
        return values;
    }

    static Feed fromContentValues(ContentValues values) {
        return new Feed(
                values.getAsLong(COLUMN_FEED_ID),
                values.getAsString(COLUMN_FEED_TB),
                values.getAsLong(COLUMN_FEED_TS),
                values.getAsInteger(COLUMN_FEED_AMOUNT),
                values.getAsString(COLUMN_FEED_NOTE)
        );
    }

    public static Feed fromCursor(Cursor cursor) {
        return new Feed(
                        cursor.getLong(cursor.getColumnIndex(Feed.COLUMN_FEED_ID)),
                        cursor.getString(cursor.getColumnIndex(Feed.COLUMN_FEED_TB)),
                        cursor.getLong(cursor.getColumnIndex(Feed.COLUMN_FEED_TS)),
                        cursor.getInt(cursor.getColumnIndex(Feed.COLUMN_FEED_AMOUNT)),
                        cursor.getString(cursor.getColumnIndex(Feed.COLUMN_FEED_NOTE)));
    }

    @SuppressWarnings("WeakerAccess")
    public Entry asEntry() {
        return new Entry(_id, this.tb, ts);
    }

    // DB-Functions:
    public static final String TABLE_NAME = "FEED_TB";
    public static final String COLUMN_FEED_ID = "_ID";
    public static final String COLUMN_FEED_TB = "FEED_TB";
    public static final String COLUMN_FEED_TS = "FEED_TS";
    public static final String COLUMN_FEED_AMOUNT = "FEED_AMOUNT";
    public static final String COLUMN_FEED_NOTE = "FEED_NOTE";

    static void createFeedTable(SQLiteDatabase db) {
        String SQL_CREATE_FEED_TB =
                "CREATE TABLE "+TABLE_NAME+" (\n" +
                "  "+ COLUMN_FEED_ID +" INTEGER PRIMARY KEY,\n" +
                "  "+COLUMN_FEED_TB+" VARCHAR2(30) CONSTRAINT FEED_TB_CK CHECK ("+COLUMN_FEED_TB+"='"+TABLE_NAME+"') DEFAULT('"+TABLE_NAME+"'),\n" +
                "  "+COLUMN_FEED_TS+" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,\n" +
                "  "+COLUMN_FEED_AMOUNT+" INTEGER CONSTRAINT FEED_AMOUNT_CK CHECK ("+COLUMN_FEED_AMOUNT+" > 0) NOT NULL,\n" +
                "  "+COLUMN_FEED_NOTE+" TEXT,\n" +
                "  CONSTRAINT FEED_ID_FK FOREIGN KEY ("+COLUMN_FEED_ID+") REFERENCES " + Entry.TABLE_NAME + "(" + Entry.COLUMN_ENTRY_ID + ") ON DELETE CASCADE\n" +
                ");";
        db.execSQL(SQL_CREATE_FEED_TB);

        String SQL_CREATE_FEED_TS_INDEX =
                "CREATE INDEX FEED_TB_TS_IDX ON "+TABLE_NAME+"("+COLUMN_FEED_TS+");";
        db.execSQL(SQL_CREATE_FEED_TS_INDEX);
    }
}
