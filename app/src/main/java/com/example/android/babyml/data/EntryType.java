package com.example.android.babyml.data;

/**
 * Created by WKaczurb on 10/4/2017.
 *
 * Enum for EntryType.
 */
public enum EntryType {
    Feed(com.example.android.babyml.data.Feed.TABLE_NAME),
    Nappy(com.example.android.babyml.data.Nappy.TABLE_NAME),
    Note(com.example.android.babyml.data.Note.TABLE_NAME),
    Sleep(com.example.android.babyml.data.Sleep.TABLE_NAME);

    private String dbTableName;

    EntryType(String dbTableName) {
        this.dbTableName = dbTableName;
    }

    @SuppressWarnings("unused")
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
            default:
                throw new IllegalArgumentException("Unknown value: " + tb);
        }
    }
}
