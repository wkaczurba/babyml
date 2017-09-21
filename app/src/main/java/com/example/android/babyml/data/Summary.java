package com.example.android.babyml.data;

import org.joda.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by wkaczurb on 9/18/2017.
 */

// TODO: Use decorator pattern for Summary;
public class Summary {
    //DateTime day;
    public final LocalDate date;
    @Getter
    @Setter
    int feedAmount;
    @Getter @Setter
    int feedCounts;
    @Getter @Setter
    int dirtyNappies;
    private int string;

    public Summary(LocalDate date)
    {
        this.date = date;
    }

    public int getString() {
        return string;
    }
}