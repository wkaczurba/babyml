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
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.babyml.data.EntriesProvider;
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

        storeNappyButton = (Button) view.findViewById(R.id.store_nappy_button);
        storeNappyButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Context ctx = getActivity();

        if (v.equals(this.storeNappyButton)) {

            long timemilis = ttw.getTimeMilis();
            int dirty = 1; // Possible extension: for weirdly coloured nappies.
            String note = null;
            int wet = 0;

            ContentValues contentValues = new Nappy(-1, Nappy.COLUMN_NAPPY_TB, timemilis, dirty, wet, note).asContentValues();
            Uri uri = getActivity().getContentResolver().insert(
                    EntriesProvider.URI_NAPPIES,
                    contentValues);

            onCloseListener.close();
        } else {
            Toast.makeText(ctx, "Invalid clik",Toast.LENGTH_LONG).show();
        }
    }
}
