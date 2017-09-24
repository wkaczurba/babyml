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
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.babyml.data.EntriesProvider;
import com.example.android.babyml.data.Sleep;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import lombok.Getter;
import lombok.Setter;


/**
 * A simple {@link Fragment} subclass.
 */
public class SleepAdderFragment extends Fragment implements View.OnClickListener {
    EditText sleepTimeEditText;
    Button storeSleepButton;
    TimeTextWatcher ttw;

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
        if (sleepTimeEditText == null)
            return;

        DateTimeFormatter dtf = DateTimeFormat.forPattern("HH:mm");
        sleepTimeEditText.setText(dtf.print(LocalTime.now()));
        ttw = new TimeTextWatcher(sleepTimeEditText);
        sleepTimeEditText.addTextChangedListener(ttw);

        storeSleepButton = (Button) view.findViewById(R.id.store_sleep_button);
        storeSleepButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Context ctx = getActivity();

        if (v.equals(this.storeSleepButton)) {

            long timemilis = ttw.getTimeMilis();
            long endTs = ttw.getTimeMilis() + 200000;
            String note = null;

            ContentValues contentValues = new Sleep(-1, Sleep.TABLE_NAME, timemilis, endTs, note).asContentValues();
                    Uri uri = getActivity().getContentResolver().insert(
                    EntriesProvider.URI_SLEEPS,
                    contentValues);
//            long id = Long.valueOf(uri.getLastPathSegment());
//            Toast.makeText(ctx, "ROWID=" + id, Toast.LENGTH_LONG).show();

            onCloseListener.close();
        } else {
            Toast.makeText(ctx, "Invalid clik",Toast.LENGTH_LONG).show();
        }
    }
}
