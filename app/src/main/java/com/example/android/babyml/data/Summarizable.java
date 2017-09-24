package com.example.android.babyml.data;

import com.example.android.babyml.LogAdapter;

import org.joda.time.LocalDate;

import java.sql.Date;

public interface Summarizable {
    void addSummary(Summary summary);
    long getTs();
}
