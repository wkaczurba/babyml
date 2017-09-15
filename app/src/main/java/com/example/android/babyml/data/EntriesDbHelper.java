package com.example.android.babyml.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.example.android.babyml.data.FeedContract.FeedingEntry;
import com.example.android.babyml.data.NappyContract.NappyEntry;
import com.example.android.babyml.data.NoteContract.NoteEntry;

/**
 * Created by WKaczurb on 8/5/2017.
 *
 * Refer to docs/sqlite_database.sql to understand how things are arranged.
 */

public class EntriesDbHelper extends SQLiteOpenHelper {

    static final String TAG = EntriesDbHelper.class.getSimpleName();
    static final String DB_NAME = "entries.db"; //"feeds.db"; // TODO: Rename it.
    static final int DB_VERSION = 1; // TODO: Rename it.

    // TODO: Move it to a separate contract.
    public static final String COLUMN_TB = "TB";
    public static final String COLUMN_TS = "TS";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_FEED_AMOUNT = "FEED_AMOUNT";
    public static final String COLUMN_NAPPY_DIRTY = "NAPPY_DIRTY";
    public static final String COLUMN_NOTE_VALUE = "NOTE_VALUE";
    public static final String ENTRIES_ALL_VIEW = "ENTRIES_ALL_V1";


    public static enum EntryType {
        Feed(FeedingEntry.TABLE_NAME),
        Nappy(NappyEntry.TABLE_NAME),
        Note(NoteEntry.TABLE_NAME);

        private String dbTableName;
        private EntryType(String dbTableName) {
            dbTableName = dbTableName;
        }

        public String getTableName() {
            return dbTableName;
        }

        public static EntryType getEntryType(String tb) {
            switch (tb) {
                case "FEED_TB":
                    return EntriesDbHelper.EntryType.Feed;
                case "NOTE_TB":
                    return EntriesDbHelper.EntryType.Note;
                case "NAPPY_TB":
                    return EntriesDbHelper.EntryType.Nappy;
                default: throw new IllegalArgumentException("Unknown value: " + tb);
            }
        }
    }

    public EntriesDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    private void createFeedTable(SQLiteDatabase db) {
        String SQL_CREATE_FEED_TB =
                "CREATE TABLE " + FeedingEntry.TABLE_NAME + " (\n"
                        + FeedingEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                        + FeedingEntry.COLUMN_FEED_TS + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,\n"
                        + FeedingEntry.COLUMN_FEED_AMOUNT + " INTEGER CONSTRAINT FEED_AMOUNT_CK CHECK (FEED_AMOUNT > 0) NOT NULL\n" +
                        ");";
        db.execSQL(SQL_CREATE_FEED_TB);

        String SQL_CREATE_FEED_TS_INDEX = "CREATE INDEX " +
                FeedingEntry.INDEX_TS + " ON " +
                FeedingEntry.TABLE_NAME+"("+FeedingEntry.COLUMN_FEED_AMOUNT+")";
        db.execSQL(SQL_CREATE_FEED_TS_INDEX);
    }

    private void createNappyTable(SQLiteDatabase db) {
        String SQL_CREATE_NAPPY_TB =
                "CREATE TABLE " + NappyEntry.TABLE_NAME + " (\n" +
                        "  " + NappyEntry._ID +" INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                        "  " + NappyEntry.COLUMN_NAPPY_TS + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,\n" +
                        "  " + NappyEntry.COLUMN_NAPPY_DIRTY + " INTEGER (1, 0) NOT NULL\n" +
                        ");\n";

        db.execSQL(SQL_CREATE_NAPPY_TB);

        String SQL_CREATE_NAPPY_TS_INDEX = "" +
                "CREATE INDEX " + NappyEntry.INDEX_TS + " ON " +
                NappyEntry.TABLE_NAME + "(" + NappyEntry.COLUMN_NAPPY_TS + ");";

        db.execSQL(SQL_CREATE_NAPPY_TS_INDEX);
    }

    private void createNoteTable(SQLiteDatabase db) {
        // Notes
        String SQL_CREATE_NOTE_TB = "CREATE TABLE " + NoteEntry.TABLE_NAME + " (\n" +
                "  " + NoteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "  " + NoteEntry.COLUMN_NOTE_TS + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,\n" +
                "  " + NoteEntry.COLUMN_NOTE_VALUE + " VARCHAR2(500) NOT NULL\n" +
                ");";

        db.execSQL(SQL_CREATE_NOTE_TB);

        String SQL_CREATE_NOTE_TS_INDEX =
                "CREATE INDEX " + NoteEntry.INDEX_TS + " ON " + NoteEntry.TABLE_NAME + "(" + NoteEntry.COLUMN_NOTE_TS + ");";

        db.execSQL(SQL_CREATE_NOTE_TS_INDEX);
    }

    private void createEntriesAllView(SQLiteDatabase db) {
        String SQL_CREATE_ALL_ENTRIES_VIEW  =
                "CREATE VIEW " + ENTRIES_ALL_VIEW + "  AS\n" +
                        "  SELECT * FROM\n" +
                        "    (SELECT " + FeedingEntry.COLUMN_FEED_TS + " AS " + COLUMN_TS + ",\n" +
                        "            " + FeedingEntry._ID + " AS " + COLUMN_ID + ",\n" +
                        "            '"+ FeedingEntry.TABLE_NAME + "' AS "+ COLUMN_TB + ",\n" +
                        "            " + FeedingEntry.COLUMN_FEED_AMOUNT +" AS " + COLUMN_FEED_AMOUNT+ ",\n" +
                        "            NULL AS " + COLUMN_NAPPY_DIRTY + ",\n" +
                        "            NULL AS " + COLUMN_NOTE_VALUE + "\n" +
                        "         FROM "+ FeedingEntry.TABLE_NAME +"\n" +
                        "    UNION\n" +
                        "    SELECT " + NappyEntry.COLUMN_NAPPY_TS +" AS " + COLUMN_TS + ",\n" +
                        "           " + NappyEntry._ID + " AS " + COLUMN_ID + ",\n" +
                        "           '"+ NappyEntry.TABLE_NAME + "' AS "+ COLUMN_TB + ",\n" +
                        "           NULL AS " + COLUMN_FEED_AMOUNT + ",\n" +
                        "           " + NappyEntry.COLUMN_NAPPY_DIRTY +" AS " + COLUMN_NAPPY_DIRTY + ",\n" +
                        "           NULL AS " + COLUMN_NOTE_VALUE + "\n" +
                        "         FROM "+ NappyEntry.TABLE_NAME +"\n" +
                        "    UNION\n" +
                        "    SELECT "+ NoteEntry.COLUMN_NOTE_TS +" AS " + COLUMN_TS +",\n" +
                        "           "+ NoteEntry._ID+" AS " + COLUMN_ID + ",\n" +
                        "           '"+ NoteEntry.TABLE_NAME+ "' AS "+ COLUMN_TB + ",\n" +
                        "           NULL AS " + COLUMN_FEED_AMOUNT+ ",\n" +
                        "           NULL AS " + COLUMN_NAPPY_DIRTY + ",\n" +
                        "           "+ NoteEntry.COLUMN_NOTE_VALUE +" AS NOTE_VALUE\n" +
                        "         FROM "+ NoteEntry.TABLE_NAME +")\n" +
                        "  ORDER BY TS DESC;";
        db.execSQL(SQL_CREATE_ALL_ENTRIES_VIEW);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createFeedTable(db);
        createNappyTable(db);
        createNoteTable(db);
        createEntriesAllView(db);

        Log.d(TAG, "DB Created ok.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //if (oldVersion != newVersion) {
        Log.d(TAG, String.format("Upgrading of DB from %0d to %0d should take place here...; for now - old version will be dropped.", oldVersion, newVersion));
        String SQL_DROP_OLD_TABLE = "DROP TABLE IF EXISTS " + FeedContract.FeedingEntry.TABLE_NAME;
        db.execSQL(SQL_DROP_OLD_TABLE);
        onCreate(db);
        //}
    }
}
