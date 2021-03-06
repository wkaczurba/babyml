package com.example.android.babyml.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@SuppressWarnings("WeakerAccess")
@ToString
@EqualsAndHashCode(exclude={"_id"})
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class Sleep implements Summarizable {
    // DB-related
    public static final String TABLE_NAME = "SLEEP_TB";
    public static final String COLUMN_SLEEP_ID = "_ID";
    public static final String COLUMN_SLEEP_TB = "SLEEP_TB";
    public static final String COLUMN_SLEEP_TS = "SLEEP_TS";
    public static final String COLUMN_SLEEP_END_TS = "SLEEP_END_TS";
    public static final String COLUMN_SLEEP_NOTE = "SLEEP_NOTE";

    @Getter
    private final long _id; // from the database.

    @Getter
    private final String tb; // table name for entry

    @Getter
    private final long ts;

    @Getter
    private final long endTs; // end of sleep

    @Getter
    private final String note;

    // Sleep
    public Sleep(long _id, String tb, long ts, long endTs, String note) {
        if (!tb.equals(TABLE_NAME)) {
            throw new IllegalArgumentException("tb.equals(" + tb +"); must be "+ TABLE_NAME + "; ");
        }
        this._id = _id;
        this.tb = tb;
        this.ts = ts;
        this.endTs = endTs;
        this.note = note;
    }

    @Override
    public void addSummary(Summary summary) {
        // TODO: Add any summary-related stuff.
    }

    public ContentValues asContentValues() {
        if (!tb.equals(TABLE_NAME)) {
            throw new IllegalArgumentException("Sleep.tb must equal " + TABLE_NAME + "; got= '" + tb + "'" );
        }

        ContentValues values = new ContentValues();
        values.put(COLUMN_SLEEP_ID, _id);
        values.put(COLUMN_SLEEP_TB, tb);
        values.put(COLUMN_SLEEP_TS, ts);
        values.put(COLUMN_SLEEP_END_TS, endTs);
        values.put(COLUMN_SLEEP_NOTE, note);
        return values;
    }

    static Sleep fromContentValues(ContentValues values) {
        return new Sleep(
                values.getAsLong(COLUMN_SLEEP_ID),
                values.getAsString(COLUMN_SLEEP_TB),
                values.getAsLong(COLUMN_SLEEP_TS),
                values.getAsLong(COLUMN_SLEEP_END_TS),
                values.getAsString(COLUMN_SLEEP_NOTE));
    }

    public static Sleep fromCursor(Cursor cursor) {
        return new Sleep(
                cursor.getLong(cursor.getColumnIndex(COLUMN_SLEEP_ID)),
                cursor.getString(cursor.getColumnIndex(COLUMN_SLEEP_TB)),
                cursor.getLong(cursor.getColumnIndex(COLUMN_SLEEP_TS)),
                cursor.getLong(cursor.getColumnIndex(COLUMN_SLEEP_END_TS)),
                cursor.getString(cursor.getColumnIndex(COLUMN_SLEEP_NOTE)));
    }

    public Entry asEntry() {
        return new Entry(_id, this.tb, ts);
    }

    static void createSleepTable(SQLiteDatabase db) {
        // Notes
        String SQL_CREATE_SLEEP_TB =
                "CREATE TABLE "+TABLE_NAME+" (\n" +
                "  "+COLUMN_SLEEP_ID+" INTEGER PRIMARY KEY,\n" +
                "  "+COLUMN_SLEEP_TB+" VARCHAR2(30) CONSTRAINT SLEEP_TB_CK CHECK ("+COLUMN_SLEEP_TB+"='"+TABLE_NAME+"') DEFAULT('"+TABLE_NAME+"'),\n" +
                "  "+COLUMN_SLEEP_TS+" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,\n" +
                "  "+COLUMN_SLEEP_END_TS+" TIMESTAMP NOT NULL, -- (?)\n" +
                "  "+COLUMN_SLEEP_NOTE+" TEXT,\n" +
                "  CONSTRAINT SLEEP_ID_FK FOREIGN KEY ("+COLUMN_SLEEP_ID+") REFERENCES " + Entry.TABLE_NAME+ "("+Entry.COLUMN_ENTRY_ID +") ON DELETE CASCADE\n" +
                ");\n";

        db.execSQL(SQL_CREATE_SLEEP_TB);

        String SQL_CREATE_SLEEP_TS_INDEX =
                "CREATE INDEX SLEEP_TB_IDX ON "+TABLE_NAME+"("+COLUMN_SLEEP_TS+");\n";

        db.execSQL(SQL_CREATE_SLEEP_TS_INDEX);
    }
}
