package com.example.android.babyml.data;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.example.android.babyml.legacy.FeedingContract;

import java.io.Serializable;

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
@EqualsAndHashCode(exclude={"_id"})
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class Nappy implements Summarizable {

    // DB-Related:
    public static final String TABLE_NAME = "NAPPY_TB";
    public static final String COLUMN_ID = "_ID";
    public static final String COLUMN_NAPPY_TB = "NAPPY_TB";
    public static final String COLUMN_NAPPY_TS = "NAPPY_TS";
    public static final String COLUMN_NAPPY_DIRTY = "NAPPY_DIRTY";
    public static final String COLUMN_NAPPY_WET = "NAPPY_WET";
    public static final String COLUMN_NAPPY_NOTE = "NAPPY_NOTE";

    @Getter
    private final long _id; // from the database.

    @Getter
    private final String tb; // table name for entry

    @Getter
    private final long ts;

    @Getter
    private final int dirty;

    @Getter
    private final int wet;

    @Getter
    private final String note;

    public Nappy(long _id, String tb, long ts, int dirty, int wet, String note) {
        if (!tb.equals(TABLE_NAME)) {
            throw new IllegalArgumentException("tb must equal " + TABLE_NAME + "; got= '" + tb + "'" );
        }
        this._id = _id;
        this.tb = tb;
        this.ts = ts;
        this.dirty = dirty;
        this.wet = wet;
        this.note = note;
    }

    @Override
    public void addSummary(Summary summary) {
        summary.setDirtyNappies(summary.getDirtyNappies() + 1);
    }

    public ContentValues asContentValues() {
        ContentValues values = new ContentValues();
        assert (values.getAsString(COLUMN_NAPPY_TB).equals(TABLE_NAME));

        values.put(COLUMN_ID, _id);
        values.put(COLUMN_NAPPY_TB, tb);
        values.put(COLUMN_NAPPY_TS, ts);
        values.put(COLUMN_NAPPY_DIRTY, dirty);
        values.put(COLUMN_NAPPY_WET, wet);
        values.put(COLUMN_NAPPY_NOTE, note);
        return values;
    }

    static Nappy fromContentValues(ContentValues values) {
        return new Nappy(
                values.getAsLong(COLUMN_ID),
                values.getAsString(COLUMN_NAPPY_TB),
                values.getAsLong(COLUMN_NAPPY_TS),
                values.getAsInteger(COLUMN_NAPPY_DIRTY),
                values.getAsInteger(COLUMN_NAPPY_WET),
                values.getAsString(COLUMN_NAPPY_NOTE)
        );
    }

    public Entry asEntry() {
        return new Entry(_id, this.tb, ts);
    }

    // DB-Related ---------------
    static void createNappyTable(SQLiteDatabase db) {
        // TODO: Rework the stuff below.
        String SQL_CREATE_NAPPY_TB =
                "CREATE TABLE NAPPY_TB (\n" +
                "  _ID INTEGER PRIMARY KEY,\n" +
                "  NAPPY_TB VARCHAR2(30) CONSTRAINT NAPPY_TB_CK CHECK (NAPPY_TB='NAPPY_TB') DEFAULT('NAPPY_TB'),\n" +
                "  NAPPY_TS TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,\n" +
                "  NAPPY_DIRTY INTEGER NOT NULL,\n" +
                "  NAPPY_WET INTEGER NOT NULL,\n" +
                "  NAPPY_NOTE TEXT,\n" +
                "  CONSTRAINT NAPPY_ID_FK FOREIGN KEY (_ID) REFERENCES ENTRY_TB(_ID) ON DELETE CASCADE\n" +
                ");";
        db.execSQL(SQL_CREATE_NAPPY_TB);

        String SQL_CREATE_NAPPY_TS_INDEX =
                "CREATE INDEX NAPPY_TS_IDX ON NAPPY_TB(NAPPY_TS);\n";

        db.execSQL(SQL_CREATE_NAPPY_TS_INDEX);
    }

}
