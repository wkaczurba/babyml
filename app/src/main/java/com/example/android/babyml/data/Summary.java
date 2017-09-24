package com.example.android.babyml.data;

import org.joda.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

/**
 * Class for summarizing values from consecutive entries
 * TODO: Use decorator pattern for Summary
 */
public class Summary {
    //DateTime day;
    private final LocalDate date;
    @Getter
    @Setter
    int feedAmount;
    @Getter @Setter
    int feedCounts;
    @Getter @Setter
    int dirtyNappies;
    private int string;

    Summary(LocalDate date)
    {
        this.date = date;
    }

    public int getString() {
        return string;
    }
}