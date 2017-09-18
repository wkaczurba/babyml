package com.example.android.babyml.data;

import com.example.android.babyml.LogAdapter;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.Value;
import lombok.experimental.FieldDefaults;

/**
 * Created by wkaczurb on 9/15/2017.
 */

@ToString
@EqualsAndHashCode(exclude={"id"})
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class Feed implements Summarizable {

    @Getter
    private final long id; // from the database.

    @Getter
    private final long timestamp;

    @Getter
    private final int amount;

    @Override
    public void addSummary(Summary summary) {
        summary.setFeedAmount(summary.getFeedAmount() + amount);
        summary.setFeedCounts(summary.getFeedCounts() + 1);
    }
}
