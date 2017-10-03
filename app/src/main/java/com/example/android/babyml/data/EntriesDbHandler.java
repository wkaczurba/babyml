package com.example.android.babyml.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.view.WindowContentFrameStats;

/**
 * Created by WKaczurb on 8/5/2017.
 *
 * Refer to docs/sqlite_database.sql to understand how things are arranged.
 */

public class EntriesDbHandler extends SQLiteOpenHelper {

    private static final String TAG = EntriesDbHandler.class.getSimpleName();
    private static final String DB_NAME = "entries.db";
    private static final int DB_VERSION = 1;

    private static final String VIEW_ENTRIES_V_ALL = "ENTRIES_V_ALL";
    private final Context context;

    public static enum EntryType {
        Feed(com.example.android.babyml.data.Feed.TABLE_NAME),
        Nappy(com.example.android.babyml.data.Nappy.TABLE_NAME),
        Note(com.example.android.babyml.data.Note.TABLE_NAME),
        Sleep(com.example.android.babyml.data.Sleep.TABLE_NAME);

        private String dbTableName;
        private EntryType(String dbTableName) {
            dbTableName = dbTableName;
        }

        public String getTableName() {
            return dbTableName;
        }

        public static EntryType getEntryType(String tb) {
            switch (tb) {
                case com.example.android.babyml.data.Feed.TABLE_NAME: //"FEED_TB":
                    return EntryType.Feed;
                case com.example.android.babyml.data.Note.TABLE_NAME: //"NOTE_TB":
                    return EntryType.Note;
                case com.example.android.babyml.data.Nappy.TABLE_NAME: // "NAPPY_TB":
                    return EntryType.Nappy;
                case com.example.android.babyml.data.Sleep.TABLE_NAME: // "SLEEP_TB":
                    return EntryType.Sleep;

                default: throw new IllegalArgumentException("Unknown value: " + tb);
            }
        }
    }

    private static EntriesDbHandler instance;

    public static EntriesDbHandler getInstance(Context context) {
        if (instance == null) {
            synchronized(EntriesDbHandler.class) {
                if (instance == null) {
                    instance = new EntriesDbHandler(context);
                }
            }
        }
        return instance;
    }

    public EntriesDbHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
        if (EntriesDbHandler.instance != null) {
            throw new UnsupportedOperationException("This constructor should not be called explicitly; use getInstance() instead.");
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Entry.createEntryTable(db); // Master-table.

        Feed.createFeedTable(db);
        Nappy.createNappyTable(db);
        Note.createNoteTable(db);
        Sleep.createSleepTable(db);
        createAllEntriesView(db);
        Log.d(TAG, "DB Created ok.");
//        EntriesDbHandler.getInstance(context).printOrphans();
    }

    private void createAllEntriesView(SQLiteDatabase db) {
        String CREATE_VIEW =
                "CREATE VIEW ENTRIES_V_ALL AS\n" +
                  "SELECT * FROM ENTRY_TB LEFT JOIN FEED_TB USING (_ID)\n" +
                    "LEFT JOIN NAPPY_TB USING(_ID)\n" +
                    "LEFT JOIN SLEEP_TB USING(_ID)\n" + "LEFT JOIN NOTE_TB USING(_ID);\n";
        db.execSQL(CREATE_VIEW);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.rawQuery("PRAGMA foreign_keys = ON;", null).close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, String.format("Upgrading of DB from %0d to %0d should take place here...; for now - old version will be dropped.", oldVersion, newVersion));
        String SQL_DROP_OLD_TABLE = "DROP TABLE IF EXISTS " + Feed.TABLE_NAME;
        db.execSQL(SQL_DROP_OLD_TABLE);
        onCreate(db);
        //}
    }

    synchronized long insertFeed(Feed feed) {
        SQLiteDatabase db = instance.getWritableDatabase();
        long rowId;

        ContentValues values = feed.asContentValues();
        if (values.containsKey(Feed.COLUMN_ID)) {
            values.remove(Feed.COLUMN_ID);
        }

        db.beginTransaction();
        try {
            ContentValues entryCv = feed.asEntry().asContentValues();
            entryCv.remove(Entry.COLUMN_ID);
            rowId = db.insert(Entry.TABLE_NAME, null, entryCv);

            ContentValues feedCv = feed.asContentValues();
            feedCv.put(Feed.COLUMN_ID, rowId);

            db.insert(Feed.TABLE_NAME, null, feedCv);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        if (rowId != -1) {
            notifyOnFeedChange();
            notifyOnEntriesChange();
        }
        return rowId;
    }

    // Delets all feeds.
    synchronized int deleteAllFeeds() {
        int rowsAffected = instance.getWritableDatabase()
                .delete(
                        Entry.TABLE_NAME,
                        null,
                        null ); // equivalent to "_ID =" + id

        if (rowsAffected > 0) {
            notifyOnFeedChange(); // what about other ones, e.g. NOTES, ETC... (?)
            notifyOnEntriesChange();
        }
        return rowsAffected;
    }

    synchronized int deleteFeedById(long id) {
        int rowsAffected = instance.getWritableDatabase()
                .delete(
                        Entry.TABLE_NAME,
                        Entry.COLUMN_ID + "=? AND " +
                        Entry.COLUMN_ENTRY_TB + "=?",
                        new String[] { String.valueOf( id ), Feed.TABLE_NAME } ); // equivalent to "_ID =" + id

        if (rowsAffected > 0) {
            notifyOnFeedChange();
            notifyOnEntriesChange();
        } else {
            throw new IllegalArgumentException("Could not delete Feed.id" + id);
        }
        return rowsAffected;
    }

    /**
     *
     * @param projection - usually null
     * @param selection - usually null
     * @param selectionArgs - usually null
     * @param sortOrder - usually: Feed.COLUMN_FEED_TS + " DESC"
     * @param limit - null or 1 for the latest one;
     * @return
     */
    synchronized Cursor getAllFeedingsCursor(
            String[] projection, // null
            String selection, //= null
            String[] selectionArgs, //= null; // not used by the content provider
            String sortOrder, // Feed.COLUMN_FEED_TS + " DESC"
            String limit // 1 for latest feed
    ) {

        if (sortOrder == null) {
            throw new IllegalArgumentException("Unexpected call -> getAllFeedinsCursor with empty sort order");
        }

        printOrphans();

        return instance.getReadableDatabase()
                .query(
                        Feed.TABLE_NAME,
                        null,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder,
                        limit
                        );
    }

    synchronized Cursor getAllEntriesCursor(
            String[] projection, // null
            String selection, //= null
            String[] selectionArgs, //= null; // not used by the content provider
            String sortOrder, // TRY: Entries.DbHandler.COLUMN_TS  + " DESC"
            String limit // 1 for latest feed
    ) {
        printOrphans();

        return instance.getReadableDatabase()
                .query(
                    EntriesDbHandler.VIEW_ENTRIES_V_ALL,
                    null,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder,
                    limit); // The View is already sorted.
    }

    synchronized long insertNappy(Nappy nappy) {
            SQLiteDatabase db = instance.getWritableDatabase();
            long rowId;

            ContentValues values = nappy.asContentValues();
            if (values.containsKey(Nappy.COLUMN_ID)) {
                values.remove(Nappy.COLUMN_ID);
            }

            db.beginTransaction();
            try {
                ContentValues entryCv = nappy.asEntry().asContentValues();
                entryCv.remove(Entry.COLUMN_ID);
                rowId = db.insert(Entry.TABLE_NAME, null, entryCv);

                ContentValues nappyCv = nappy.asContentValues();
                nappyCv.put(Nappy.COLUMN_ID, rowId);
// FIXME: ?(NOT SURE) Fails constraints: ENTRY_TB_CK
                db.insert(Nappy.TABLE_NAME, null, nappyCv);
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }

            if (rowId != -1) {
                notifyOnNappyChange();
                notifyOnEntriesChange();
            }
            return rowId;
        }

        synchronized long insertSleep(Sleep sleep) {
            SQLiteDatabase db = instance.getWritableDatabase();
            long rowId;

            ContentValues values = sleep.asContentValues();
            if (values.containsKey(Sleep.COLUMN_ID)) {
                values.remove(Sleep.COLUMN_ID);
            }

            db.beginTransaction();
            try {
                ContentValues entryCv = sleep.asEntry().asContentValues();
                entryCv.remove(Entry.COLUMN_ID);
                rowId = db.insert(Entry.TABLE_NAME, null, entryCv);

                ContentValues sleepCv = sleep.asContentValues();
                sleepCv.put(Sleep.COLUMN_ID, rowId);
                db.insert(Sleep.TABLE_NAME, null, sleepCv);
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }

            if (rowId != -1) {
                notifyOnSleepChange();
                notifyOnEntriesChange();
            }
            return rowId;
        }

        synchronized long insertNote(Note note) {
            SQLiteDatabase db = instance.getWritableDatabase();
            long rowId;

            ContentValues values = note.asContentValues();
            if (values.containsKey(Note.COLUMN_ID)) {
                values.remove(Note.COLUMN_ID);
            }

            db.beginTransaction();
            try {
                ContentValues entryCv = note.asEntry().asContentValues();
                entryCv.remove(Entry.COLUMN_ID);
                rowId = db.insert(Entry.TABLE_NAME, null, entryCv);

                ContentValues noteCv = note.asContentValues();
                noteCv.put(Note.COLUMN_ID, rowId);
// FIXME: (NOT SURE) Fails constraints: ENTRY_TB_CK
                db.insert(Note.TABLE_NAME, null, noteCv);
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }

            if (rowId != -1) {
                notifyOnNoteChange();
                notifyOnEntriesChange();
            }
            return rowId;
        }

//        ContentValues cv = nappy.asContentValues();
//        cv.remove("_id"); // if _id is specified -> it most likely give an error.
//
//        long rowId = instance.getWritableDatabase()
//                .insert(Nappy.TABLE_NAME, null, cv);
//
//        if (rowId != -1) {
//            notifyOnNappyChange();
//            notifyOnEntriesChange();
//        }
//        return rowId;
//    }

    // FIXME
    synchronized int deleteNappyById(long id) {
        int rowsAffected = instance.getWritableDatabase()
                .delete(
                        Entry.TABLE_NAME,
                        Entry.COLUMN_ID + "=? AND " +
                                Entry.COLUMN_ENTRY_TB + "=?",
                        new String[] { String.valueOf( id ), Nappy.TABLE_NAME } ); // equivalent to "_ID =" + id

        if (rowsAffected > 0) {
            notifyOnNappyChange();
            notifyOnEntriesChange();
        } else {
            throw new IllegalArgumentException("Could not delete Nappy.id=" + id);
        }
        return rowsAffected;
    }

    synchronized int deleteSleepById(long id) {
        int rowsAffected = instance.getWritableDatabase()
                .delete(
                        Entry.TABLE_NAME,
                        Entry.COLUMN_ID + "=? AND " +
                                Entry.COLUMN_ENTRY_TB + "=?",
                        new String[] { String.valueOf( id ), Sleep.TABLE_NAME } ); // equivalent to "_ID =" + id

        if (rowsAffected > 0) {
            notifyOnSleepChange();
            notifyOnEntriesChange();
        } else {
            throw new IllegalArgumentException("Could not delete Sleep.id=" + id);
        }
        return rowsAffected;
    }

    synchronized int deleteNoteById(long id) {
        int rowsAffected = instance.getWritableDatabase()
                .delete(
                        Entry.TABLE_NAME,
                        Entry.COLUMN_ID + "=? AND " +
                                Entry.COLUMN_ENTRY_TB + "=?",
                        new String[] { String.valueOf( id ), Note.TABLE_NAME } ); // equivalent to "_ID =" + id

        if (rowsAffected > 0) {
            notifyOnNoteChange();
            notifyOnEntriesChange();
        } else {
            throw new IllegalArgumentException("Could not delete Note.id=" + id);
        }
        return rowsAffected;
    }

    private void notifyOnEntriesChange() {
        context.getContentResolver().notifyChange(EntriesProvider.URI_ENTRIES, null, false);
    }

    private void notifyOnFeedChange() {
        context.getContentResolver().notifyChange(EntriesProvider.URI_FEEDS, null, false);
    }

    private void notifyOnNappyChange() {
        context.getContentResolver().notifyChange(EntriesProvider.URI_NAPPIES, null, false);
    }

    private void notifyOnSleepChange() {
        context.getContentResolver().notifyChange(EntriesProvider.URI_SLEEPS, null, false);
    }

    private void notifyOnNoteChange() {
        context.getContentResolver().notifyChange(EntriesProvider.URI_NOTES, null, false);
    }

    public void printOrphans() {
        Cursor cursor = getInstance(context).getReadableDatabase().rawQuery(
            "SELECT _ID, ENTRY_TB FROM ENTRY_TB WHERE\n" +
                    "    (ENTRY_TB='SLEEP_TB' AND _ID NOT IN (SELECT _ID FROM SLEEP_TB)) OR\n" +
                    "    (ENTRY_TB='FEED_TB' AND _ID NOT IN (SELECT _ID FROM FEED_TB)) OR\n" +
                    "    (ENTRY_TB='NAPPY_TB' AND _ID NOT IN (SELECT _ID FROM NAPPY_TB)) OR\n" +
                    "    (ENTRY_TB='NOTE_TB' AND _ID NOT IN (SELECT _ID FROM NOTE_TB));", null);

        Log.d(TAG, String.format("printOrphans.cursor.getCount()= " + cursor.getCount()));

        if (cursor.getCount() > 0) {
            Log.d(TAG, String.format("Found : %d orphans", cursor.getCount()));

            while (cursor.moveToNext()) {
                long id   = cursor.getLong(cursor.getColumnIndex("_ID"));
                String tb = cursor.getString(cursor.getColumnIndex("ENTRY_TB"));
                Log.d(TAG, String.format("ID=%d; %s", id, tb));
            }
        } else {

        }
    }
//
//    private void notifyOnNoteChange() {
//        context.getContentResolver().notifyChange(EntriesProvider.URI_ENTRIES, null, false);
//    }

}
