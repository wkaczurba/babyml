package com.example.android.babyml;


import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.babyml.data.EntriesDbHandler;
import com.example.android.babyml.data.EntriesProvider;
import com.example.android.babyml.data.Note;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import lombok.Getter;
import lombok.Setter;


/**
 * A simple {@link Fragment} subclass.
 */
public class NoteAdderFragment extends Fragment implements View.OnClickListener {

    EditText noteTimeEditText;
    Button storeNoteButton;
    TimeTextWatcher ttw;

    @Getter
    @Setter
    OnCloseListener onCloseListener = new OnCloseListener() {
        @Override
        public void close() {
            getActivity().finish();
        }
    };

    public NoteAdderFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_note_adder, container, false);
    }

    @Override
    public void onStart() {
        Context context = getActivity();
        // Required empty public constructor
        View view = getView();
        super.onStart();
        noteTimeEditText = (EditText) view.findViewById(R.id.note_time_et);

        DateTimeFormatter dtf = DateTimeFormat.forPattern("HH:mm");
        noteTimeEditText.setText(dtf.print(LocalTime.now()));
        ttw = new TimeTextWatcher(noteTimeEditText);
        noteTimeEditText.addTextChangedListener(ttw);
        setIme(context);

        storeNoteButton = (Button) view.findViewById(R.id.store_note_button);
        storeNoteButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Context ctx = getActivity();

        if (v.equals(this.storeNoteButton)) {

            long timemilis = ttw.getTimeMilis();
            String note = null;

            ContentValues contentValues = new Note(-1, Note.TABLE_NAME, timemilis, note).asContentValues();
                    Uri uri = getActivity().getContentResolver().insert(
                    EntriesProvider.URI_NOTES,
                    contentValues);
            long id = Long.valueOf(uri.getLastPathSegment());

            Toast.makeText(ctx, "ROWID=" + id, Toast.LENGTH_LONG).show();

            onCloseListener.close();
        } else {
            Toast.makeText(ctx, "Invalid clik",Toast.LENGTH_LONG).show();
        }
    }

    // This is from: https://stackoverflow.com/questions/2342620/how-to-hide-keyboard-after-typing-in-edittext-in-android
    // TODO: Consider bundling it together with TimeTextWatcher
    private void setIme(Context context) {
        InputMethodManager inputManager =
                (InputMethodManager) context.
                        getSystemService(Context.INPUT_METHOD_SERVICE);

        View view = noteTimeEditText;

        inputManager.hideSoftInputFromWindow(
                view.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }
}