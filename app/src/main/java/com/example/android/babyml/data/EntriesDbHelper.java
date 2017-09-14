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

    public static final String ENTRIES_VIEW = "ENTRIES_V1";

    //public EntriesDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
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

    private void createEntriesView(SQLiteDatabase db) {
        String SQL_CREATE_ENTRIES_VIEW =
                "CREATE VIEW " + ENTRIES_VIEW + " AS\n" +
                        "  SELECT * FROM\n" +
                        "    (SELECT " + FeedingEntry.COLUMN_FEED_TS + " AS TS, " + FeedingEntry._ID + " AS ID, '" + FeedingEntry.TABLE_NAME + "' AS TB FROM " + FeedingEntry.TABLE_NAME + "\n" +
                        "    UNION\n" +
                        "    SELECT " + NappyEntry.COLUMN_NAPPY_TS + " AS TS, " + NappyEntry._ID + " AS ID, '" + NappyEntry.TABLE_NAME + "' AS TB FROM " + NappyEntry.TABLE_NAME + "\n" +
                        "    UNION\n" +
                        "    SELECT " + NoteEntry.COLUMN_NOTE_TS + " AS TS, " + NoteEntry._ID + " AS ID, '" + NoteEntry.TABLE_NAME + "' AS TB FROM " + NoteEntry.TABLE_NAME + ")\n" +
                        "  ORDER BY TS DESC;\n";
        db.execSQL(SQL_CREATE_ENTRIES_VIEW);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        String SQL_CREATE_FEEDING_TABLE = "CREATE TABLE " + FeedContract.FeedingEntry.TABLE_NAME + " (" +
//                FeedContract.FeedingEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
//                FeedContract.FeedingEntry.COLUMN_FEED_AMOUNT + " NUMBER(3, 0)," +
//                FeedContract.FeedingEntry.COLUMN_FEED_TS + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
//                ")";
        createFeedTable(db);
        createNappyTable(db);
        createNoteTable(db);
        createEntriesView(db);

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
