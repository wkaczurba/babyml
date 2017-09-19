package com.example.android.babyml;


import android.app.Fragment;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.babyml.data.EntriesDbHandler;
import com.example.android.babyml.data.EntriesUtils;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


/**
 * A simple {@link Fragment} subclass.
 */
public class NappyAdderFragment extends Fragment implements View.OnClickListener {

    // TODO: Rename member variables to match MilkAdderFramgent' convention
    EditText nappyTimeEditText;
    Button storeNappyButton;
    MainActivity.TimeTextWatcher ttw;

    private EntriesDbHandler mDb;


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
        EntriesDbHandler mDb = EntriesDbHandler.getInstance(context);
        View view = getView();
        super.onStart();
        nappyTimeEditText = (EditText) view.findViewById(R.id.nappy_time_et);

        DateTimeFormatter dtf = DateTimeFormat.forPattern("HH:mm");
        nappyTimeEditText.setText(dtf.print(LocalTime.now()));
        ttw = new MainActivity.TimeTextWatcher(nappyTimeEditText);
        nappyTimeEditText.addTextChangedListener(ttw);
        setIme(context);

        storeNappyButton = (Button) view.findViewById(R.id.store_nappy_button);
        storeNappyButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Context ctx = getActivity();

        if (v.equals(this.storeNappyButton)) {

            long timemilis = ttw.getTimeMilis();
            int dirty = 1; // Possible extension: for weirdly coloured nappies.
            //long id = EntriesUtils.insertNappy(mDb, dirty, timemilis);


            // mDb:
            if (mDb == null) { // NOT SURE WHY THIS HAPPENS.
                mDb = EntriesDbHandler.getInstance(ctx);
            }
            long id = mDb.insertNappy(dirty, timemilis);


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

        View view = nappyTimeEditText;

        inputManager.hideSoftInputFromWindow(
                view.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
