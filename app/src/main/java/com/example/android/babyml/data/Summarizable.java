package com.example.android.babyml.data;

import com.example.android.babyml.LogAdapter;

import org.joda.time.LocalDate;

import java.sql.Date;

/**
 * Created by wkaczurb on 9/18/2017.
 */

public interface Summarizable {
    public void addSummary(Summary summary);
    public long getTs();
}
