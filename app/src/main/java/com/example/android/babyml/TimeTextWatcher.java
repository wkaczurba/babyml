package com.example.android.babyml;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.example.android.babyml.utils.DateUtils;
import com.example.android.babyml.utils.MiscUiUtils;

import org.joda.time.LocalDateTime;

/**
 * Created by wkaczurb on 9/24/2017.
 *
 * The following is a textWatcher for milkTimeEditText
 *
 * TODO: Implement something similar to this one;
 * TODO: Make it as a separate class/file related to resources/UI; this class is used for Nappies
 *   as well as for Milk and is likely to be used elsewhere
 * http://www.techrepublic.com/article/pro-tip-write-a-validate-as-you-go-android-textwatcher-for-date-entry-fields/
 */
class TimeTextWatcher implements TextWatcher {
    private static final String TAG = TimeTextWatcher.class.getSimpleName();
    private EditText et;

    public TimeTextWatcher(EditText et) {
        this.et = et;
        MiscUiUtils.setIme(et.getContext(), et);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    public void setDefaultTimeIfEntryInvalid() {
        String entry = et.getText().toString();
        int[] hhmm = DateUtils.toHoursMinArray(entry);
        boolean isEntryValid = (hhmm != null);
        if (!isEntryValid) {
            setDefaultTime();
        } // otherwise - dont change.

    }

    private int[] getHoursMins() {
        String entry = et.getText().toString();
        return DateUtils.toHoursMinArray(entry);
    }

    long getTimeMilis() {
        int[] hhmm = getHoursMins();
        if (hhmm == null) {
            throw new NullPointerException("getHoursMins returned null; field contains invalid time. Try using setDefaultTimeIfEntryInvalid before using getHoursMins");
        }

        LocalDateTime ldt = DateUtils.applyTimeToCurrentLocalDate(hhmm[0], hhmm[1]);
        return ldt.toDateTime().getMillis();
    }

    private void setDefaultTime() {
        //android.text.format.DateFormat df = new android.text.format.DateFormat();
        long timeInMillis = System.currentTimeMillis();

        CharSequence csTime = DateFormat.format("hh:mm", timeInMillis); // was: df.
        et.setText(csTime);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        int[] hhmm = DateUtils.toHoursMinArray(s.toString());
        boolean isEntryValid = (hhmm != null);
        if (!isEntryValid) {
            Log.d(TAG, "Invalid time: " + s.toString());
            et.setError("Invalid time");
        } else {
            Log.d(TAG, "Time ok: " + s.toString());
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    // TODO: Consider moving it to MiscUiUtils.
//    public static void setIme(Context context, View view) {
//        InputMethodManager inputManager =
//                (InputMethodManager) context.
//                        getSystemService(Context.INPUT_METHOD_SERVICE);
//
//        inputManager.hideSoftInputFromWindow(
//                view.getWindowToken(),
//                InputMethodManager.HIDE_NOT_ALWAYS);
//    }
}
