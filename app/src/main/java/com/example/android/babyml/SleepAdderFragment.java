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
import com.example.android.babyml.data.Sleep;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


/**
 * A simple {@link Fragment} subclass.
 */
public class SleepAdderFragment extends Fragment implements View.OnClickListener {

    // TODO: Rework everything.

    EditText sleepTimeEditText;
    Button storeSleepButton;
    MainActivity.TimeTextWatcher ttw;

    private EntriesDbHandler mDb;


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
        // Required empty public constructor
        EntriesDbHandler mDb = EntriesDbHandler.getInstance(context);
        View view = getView();
        super.onStart();
        sleepTimeEditText = (EditText) view.findViewById(R.id.sleep_time_et);

        DateTimeFormatter dtf = DateTimeFormat.forPattern("HH:mm");
        sleepTimeEditText.setText(dtf.print(LocalTime.now()));
        ttw = new MainActivity.TimeTextWatcher(sleepTimeEditText);
        sleepTimeEditText.addTextChangedListener(ttw);
        setIme(context);

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
            //long id = EntriesUtils.insertSleep(mDb, dirty, timemilis);


            // mDb:
            if (mDb == null) { // NOT SURE WHY THIS HAPPENS.
                mDb = EntriesDbHandler.getInstance(ctx);
            }

            ContentValues contentValues = new Sleep(-1, Sleep.TABLE_NAME, timemilis, endTs, note).asContentValues();
                    Uri uri = getActivity().getContentResolver().insert(
                    EntriesProvider.URI_SLEEPS,
                    contentValues);
            long id = Long.valueOf(uri.getLastPathSegment());
            //long id = mDb.insertSleep(dirty, timemilis);

            Toast.makeText(ctx, "ROWID=" + id, Toast.LENGTH_LONG).show();

            getActivity().finish();
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

        View view = sleepTimeEditText;

        inputManager.hideSoftInputFromWindow(
                view.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
