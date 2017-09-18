package com.example.android.babyml.data;

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
@EqualsAndHashCode(exclude={"id"})
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class Nappy implements Summarizable {

    @Getter
    private final long id; // from the database.

    @Getter
    private final long timestamp;

    @Getter
    private final int dirty;

    @Override
    public void addSummary(Summary summary) {
        summary.setDirtyNappies(summary.getDirtyNappies() + 1);
    }
}
