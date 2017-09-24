package com.example.android.babyml.data;

import com.example.android.babyml.utils.DateUtils;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by wkaczurb on 9/18/2017.
 */

public class EntriesMap {
    private Map<LocalDate, List<Summarizable>> lit = new LinkedHashMap<>();
    private List<Object> flat = null;

    public void addSummarizable(Summarizable o) {
        LocalDate date = DateUtils.summarizableToLocalDate(o);

        if (lit.containsKey(date)) {
            List<Summarizable> list = lit.get(date);
            list.add(o);
        } else {
            List<Summarizable> list = new ArrayList<>();
            list.add(o);
            lit.put(date, list);
        }
        flat = null;
    }

    private Summary summaryFromList(LocalDate date, List<Summarizable> list) {
        if (list.size() == 0)
            throw new IllegalArgumentException("Provided list was empty");

        Summary summary = new Summary(date);
        for (Summarizable s : list) {
            s.addSummary(summary);
        }
        return summary;
    }

    // This should also add summary;
    private List<Object> toFlat() {
        List<Object> list = new ArrayList<>();

        Set<LocalDate> dates = lit.keySet();
        for (LocalDate date : dates) {
            List<Summarizable> dl = lit.get(date);

            list.add(date);

            for (Object o : dl) {
                // Calculate summaries;
                list.add(o);
            }

            list.add(summaryFromList(date, dl));
        }
        return list;
    }

    public Object get(int position) {
        if (flat == null) {
            flat = toFlat();
        }
        return flat.get(position);
    }

    public int size() {
        if (flat == null) {
            flat = toFlat();
        }
        return flat.size();
    }
}
