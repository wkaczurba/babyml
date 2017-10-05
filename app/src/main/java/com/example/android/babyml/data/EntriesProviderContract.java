package com.example.android.babyml.data;

import android.net.Uri;

/**
 * Created by WKaczurb on 10/4/2017.
 *
 * Constants that are used by ContentProvider and ContentResolver.
 */

@SuppressWarnings("WeakerAccess")
public class EntriesProviderContract {
    public static final String AUTHORITY = "com.example.android.babyml.provider";
    public static final String SCHEME = "content://";

    // URIs
    public static final String PATH_ENTRY = "entry";
    public static final int CODE_ENTRY = 100;
    public static final int CODE_ENTRY_WITH_ID = 101;
    public static final String ENTRIES = SCHEME + AUTHORITY + "/entry";
    public static final Uri URI_ENTRIES = Uri.parse(ENTRIES);
    //    public static final String ENTRY_BASE = ENTRIES + "/"; // This is for one ENTRY;
    public static final String PATH_FEED = "feed";

    public static final int CODE_FEED = 200;
    public static final int CODE_FEED_WITH_ID = 201;

    public static final String FEEDS = SCHEME + AUTHORITY + "/feed";
    public static final Uri URI_FEEDS = Uri.parse(FEEDS);
//    public static final String FEED_BASE = FEEDS + "/"; // This is for one FEED;

    public static final String PATH_NAPPY = "nappy";
    public static final int CODE_NAPPY = 300;
    public static final int CODE_NAPPY_WITH_ID = 301;
    public static final String NAPPIES = SCHEME + AUTHORITY + "/nappy";
    public static final Uri URI_NAPPIES = Uri.parse(NAPPIES);
//    public static final String NAPPY_BASE = NAPPIES + "/"; // This is for one NAPPY.

    public static final String PATH_SLEEP = "sleep";
    public static final int CODE_SLEEP = 400;
    public static final int CODE_SLEEP_WITH_ID = 401;
    public static final String SLEEPS = SCHEME + AUTHORITY + "/sleep";
    public static final Uri URI_SLEEPS = Uri.parse(SLEEPS);

    public static final String PATH_NOTE = "note";
    public static final int CODE_NOTE = 500;
    public static final int CODE_NOTE_WITH_ID = 501;
    public static final String NOTES = SCHEME + AUTHORITY + "/note";
    public static final Uri URI_NOTES = Uri.parse(NOTES);
}
