package com.example.android.babyml;


import android.icu.text.DateFormat;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
//import android.app.Fragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import java.text.SimpleDateFormat;


/**
 * A simple {@link Fragment} subclass.
 */
public class TimeElapsedFragment extends Fragment {

    // TODO: TextViews, States -> running...
    long timeZero; //System.currentTimeMillis() - 180; // FIXME: Provide proper real time.
    TextView timeElapsedTv, timeElapsedLabelTv;

    public static final String TAG = TimeElapsedFragment.class.getSimpleName();

    public TimeElapsedFragment() {
        // Required empty public constructor
    }

    public void setTimeZero(long millis) {
        timeZero = millis;
        refreshTimeElapsed();
    }

    public void refreshTimeElapsed() {
        CharSequence cs =  "--:--";

        if (timeElapsedTv == null)
            return;

        long now = System.currentTimeMillis();
        if (timeZero >= now - 24*3600*1000) {
            LocalTime hhmm = LocalTime.fromMillisOfDay(now-timeZero);

            DateTimeFormatter dtf = DateTimeFormat.forPattern("HH:mm");
            cs = dtf.print(hhmm);
            //cs = String.format("%02d:%02d", hhmm.getHourOfDay(), hhmm.getMinuteOfHour());
        }
        timeElapsedTv.setText(cs);
    }

    public void runTimer() {
        //final TextView textView = (TextView) getView().findViewById()
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                refreshTimeElapsed();
                handler.postDelayed(this, 60000); // every 60 seconds; TODO: Change to 60000; (1000 millis * 60 seconds)
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_time_elapsed, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            setTimeZero(savedInstanceState.getLong("timeZero", -1));
        }
        runTimer();
    }

    @Override
    public void onStart() {
        View view = getView();

        if (view != null) {
            timeElapsedTv = (TextView) view.findViewById(R.id.time_elapsed_tv);
            timeElapsedLabelTv = (TextView) view.findViewById(R.id.time_elapsed_label_tv);
            refreshTimeElapsed();
        }

        super.onStart();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putLong("timeZero", timeZero);
        super.onSaveInstanceState(outState);
    }
}
