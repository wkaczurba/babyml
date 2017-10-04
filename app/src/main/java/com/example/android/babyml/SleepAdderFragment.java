package com.example.android.babyml;


import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.babyml.data.EntriesProvider;
import com.example.android.babyml.data.EntriesProviderContract;
import com.example.android.babyml.data.Sleep;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import lombok.Getter;
import lombok.Setter;


/**
 * A simple {@link Fragment} subclass.
 *
 * FIXME: Datetime "sleepTimeEditText" is after date "sleepTimeEndEditText". FIX THIS.
 */
public class SleepAdderFragment extends Fragment implements View.OnClickListener {
    EditText sleepTimeEditText;
    EditText sleepTimeEndEditText;
    Button storeSleepButton;
    TimeTextWatcher ttw, ttw2;
    CheckBox sleepIncludeNoteCheckBox; // sleep_include_note_cb
    EditText sleepTimeNoteEditText; // sleep_time_note_et

    @Getter
    @Setter
    OnCloseListener onCloseListener = new OnCloseListener() {
        @Override
        public void close() {
            getActivity().finish();
        }
    };

    public SleepAdderFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sleep_adder, container, false);
    }

    @Override
    public void onStart() {
        Context context = getActivity();
        View view = getView();
        super.onStart();

        assert view != null;
        sleepTimeEditText = (EditText) view.findViewById(R.id.sleep_time_et);
        sleepTimeEndEditText = (EditText) view.findViewById(R.id.sleep_time_end_et);
        sleepTimeNoteEditText = (EditText) view.findViewById(R.id.sleep_time_note_et);
        sleepIncludeNoteCheckBox = (CheckBox) view.findViewById(R.id.sleep_include_note_cb); // CheckBox sleep_include_note_cb

        // TODO: Consider throwing an exception if things are not found.
        if (sleepTimeEditText == null ||
            sleepTimeEndEditText == null ||
            sleepTimeNoteEditText == null)
            return;

        DateTimeFormatter dtf = DateTimeFormat.forPattern("HH:mm");
        sleepTimeEditText.setText(dtf.print(LocalTime.now()));
        ttw = new TimeTextWatcher(sleepTimeEditText);
// FIXME: Handle starting end in next day; make sure that end is not before start, etc...
        ttw2 = new TimeTextWatcher(sleepTimeEndEditText);
        sleepTimeEditText.addTextChangedListener(ttw);
        sleepTimeEndEditText.addTextChangedListener(ttw2);

        storeSleepButton = (Button) view.findViewById(R.id.store_sleep_button);
        storeSleepButton.setOnClickListener(this);
        sleepIncludeNoteCheckBox.setOnClickListener(this);

        updateVisibilityOfNoteEditText();
    }

    @Override
    public void onClick(View v) {
        Context ctx = getActivity();

        if (v.equals(this.storeSleepButton)) {
            storeSleepAndClose();
        } else if (v.equals(this.sleepIncludeNoteCheckBox)) {
            updateVisibilityOfNoteEditText();
        } else {
            Toast.makeText(ctx, "Invalid clik",Toast.LENGTH_LONG).show();
        }
    }

    public void updateVisibilityOfNoteEditText() {
        if (sleepIncludeNoteCheckBox.isChecked()) {
            sleepTimeNoteEditText.setVisibility(View.VISIBLE);
        } else {
            sleepTimeNoteEditText.setVisibility(View.GONE);
        }
    }

    private String getNote() {
        if (sleepTimeEditText.getVisibility() == View.VISIBLE) {
            return sleepTimeNoteEditText.getText().toString();
        } else {
            return null;
        }
    }

    private void storeSleepAndClose() {
        Context context = getContext();
        long timemilis = ttw.getTimeMilis();
        long endTs = ttw2.getTimeMilis();

        ContentValues contentValues = new Sleep(-1, Sleep.TABLE_NAME, timemilis, endTs, getNote()).asContentValues();
        Uri uri = context.getContentResolver().insert(
                EntriesProviderContract.URI_SLEEPS,
                contentValues);
//            long id = Long.valueOf(uri.getLastPathSegment());
//            Toast.makeText(ctx, "ROWID=" + id, Toast.LENGTH_LONG).show();

        onCloseListener.close();
    }
}
