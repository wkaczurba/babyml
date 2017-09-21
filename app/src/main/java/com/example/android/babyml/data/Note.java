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
@EqualsAndHashCode(exclude={"id"})
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
// TODO: make it extending from Entry
public class Note implements Summarizable {

    public static final String TABLE_NAME = "NOTE_TB";
    public static final String COLUMN_ID = "_ID";
    public static final String COLUMN_NOTE_TB = "NOTE_TB";
    public static final String COLUMN_NOTE_TS = "NOTE_TS";
    public static final String COLUMN_NOTE_VALUE = "NOTE_VALUE";

    @Getter
    private final long _id; // from the database.

    @Getter
    private final String tb; // table name for entry

    @Getter
    private final long ts;

    @Getter
    private final String value;

    public Note(long _id, String tb, long ts, String value) {
        if (!tb.equals(TABLE_NAME)) {
            throw new IllegalArgumentException("tb must equal " + TABLE_NAME + "; got= '" + tb + "'" );
        }
        this._id = _id;
        this.tb = tb;
        this.ts = ts;
        this.value = value;
    }

    @Override
    public void addSummary(Summary summary) {
        // TODO: Add any summary-related stuff.
        return;
    }

    public ContentValues asContentValues() {
        ContentValues values = new ContentValues();
        assert (values.getAsString(COLUMN_NOTE_TB).equals(TABLE_NAME));

        values.put(COLUMN_ID, _id);
        values.put(COLUMN_NOTE_TB, tb);
        values.put(COLUMN_NOTE_TS, ts);
        values.put(COLUMN_NOTE_VALUE, value);
        return values;
    }

    static Note fromContentValues(ContentValues values) {
        return new Note(
                values.getAsLong(COLUMN_ID),
                values.getAsString(COLUMN_NOTE_TB),
                values.getAsLong(COLUMN_NOTE_TS),
                values.getAsString(COLUMN_NOTE_VALUE));
    }

    public static Note fromCursor(Cursor cursor) {
        return new Note(
                cursor.getLong(cursor.getColumnIndex(COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(COLUMN_NOTE_TB)),
                cursor.getLong(cursor.getColumnIndex(COLUMN_NOTE_TS)),
                cursor.getString(cursor.getColumnIndex(COLUMN_NOTE_VALUE)));
    }

    public Entry asEntry() {
        return new Entry(_id, this.tb, ts);
    }

    // Database-related:
    static void createNoteTable(SQLiteDatabase db) {
        // Notes
        String SQL_CREATE_NOTE_TB = "CREATE TABLE NOTE_TB (\n" +
                "  _ID INTEGER PRIMARY KEY,\n" +
                "  NOTE_TB VARCHAR2(30) CONSTRAINT NOTE_TB_CK CHECK (NOTE_TB='NOTE_TB') DEFAULT('NOTE_TB'),\n" +
                "  NOTE_TS TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,\n" +
                "  NOTE_VALUE TEXT,\n" +
                "  CONSTRAINT NOTE_ID_FK FOREIGN KEY (_ID) REFERENCES ENTRY_TB(_ID) ON DELETE CASCADE\n" +
                ");";

        db.execSQL(SQL_CREATE_NOTE_TB);

        String SQL_CREATE_NOTE_TS_INDEX =
                "CREATE INDEX NOTE_TB_IDX ON NOTE_TB(NOTE_TS);\n";

        db.execSQL(SQL_CREATE_NOTE_TS_INDEX);
    }
}
