package com.example.android.babyml;


import android.support.v4.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.babyml.data.EntriesProvider;
import com.example.android.babyml.data.EntriesProviderContract;
import com.example.android.babyml.data.Nappy;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import lombok.Getter;
import lombok.Setter;


/**
 * A simple {@link Fragment} subclass.
 */
public class NappyAdderFragment extends Fragment implements View.OnClickListener {

    @Getter
    @Setter
    OnCloseListener onCloseListener = new OnCloseListener() {
        @Override
        public void close() {
            getActivity().finish();
        }
    };

    // TODO: Rename member variables to match MilkAdderFramgent' convention
    EditText nappyTimeEditText;
    Button storeNappyButton;
    TimeTextWatcher ttw;
    CheckBox nappyIncludeNoteCheckBox; // nappy_include_note_cb
    EditText nappyNoteEditText; // nappy_time_note_et

    public NappyAdderFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nappy_adder, container, false);
    }

    @Override
    public void onStart() {
        Context context = getActivity();
        // Required empty public constructor
        View view = getView();
        super.onStart();
        nappyTimeEditText = (EditText) view.findViewById(R.id.nappy_time_et);

        DateTimeFormatter dtf = DateTimeFormat.forPattern("HH:mm");
        nappyTimeEditText.setText(dtf.print(LocalTime.now()));
        ttw = new TimeTextWatcher(nappyTimeEditText);
        nappyTimeEditText.addTextChangedListener(ttw);
        nappyIncludeNoteCheckBox = (CheckBox) view.findViewById(R.id.nappy_include_note_cb); // nappy_include_note_cb
        nappyNoteEditText = (EditText) view.findViewById(R.id.nappy_note_et); // nappy_time_note_et
        storeNappyButton = (Button) view.findViewById(R.id.store_nappy_button);
        storeNappyButton.setOnClickListener(this);

        nappyIncludeNoteCheckBox.setOnClickListener(this);
        updateVisibilityOfNoteEditText();
    }

    private String getNote() {
        if (nappyNoteEditText.getVisibility() == View.VISIBLE) {
            return nappyNoteEditText.getText().toString();
        } else {
            return null;
        }
    }

    @Override
    public void onClick(View v) {
        Context ctx = getActivity();

        if (v.equals(this.storeNappyButton)) {
            storeNappyAndClose();
        } else if (v.equals(nappyIncludeNoteCheckBox)) {
            updateVisibilityOfNoteEditText();
        } else {
            Toast.makeText(ctx, "Invalid clik",Toast.LENGTH_LONG).show();
        }
    }

    public void updateVisibilityOfNoteEditText() {
        if (nappyIncludeNoteCheckBox.isChecked()) {
            nappyNoteEditText.setVisibility(View.VISIBLE);
        } else {
            nappyNoteEditText.setVisibility(View.GONE);
        }
    }

    private void storeNappyAndClose() {
        long timemilis = ttw.getTimeMilis();
        int dirty = 1; // Possible extension: for weirdly coloured nappies.
        int wet = 0;

        ContentValues contentValues = new Nappy(-1, Nappy.COLUMN_NAPPY_TB, timemilis, dirty, wet, getNote()).asContentValues();
        Uri uri = getActivity().getContentResolver().insert(
                EntriesProviderContract.URI_NAPPIES,
                contentValues);

        onCloseListener.close();
    }

}
