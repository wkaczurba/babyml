package com.example.android.babyml;


import android.app.Fragment;
import android.content.ContentProvider;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.babyml.data.EntriesDbHandler;
import com.example.android.babyml.data.EntriesProvider;
import com.example.android.babyml.data.EntriesUtils;
import com.example.android.babyml.data.Feed;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


/**
 * A simple {@link Fragment} subclass.
 */
// TODO: Sasving state and contents of elements (e.g. milkAmount)

public class MilkAdderFragment extends Fragment implements View.OnClickListener {

    private static final String LOG = MilkAdderFragment.class.getSimpleName();
    // Milk-related items:
    int milkAmountValue = 0; // FIXME: Save instance.
    static final String MILK_AMOUNT_VALUE_KEY = "milkAmountValue";
    EntriesDbHandler mDb;

    //AddEntryActivity.ItemSelectedListener itemSelectedListener = new AddEntryActivity.ItemSelectedListener(this);

    TextView milkAmountTextView;
    EditText milkTimeEditText;
    Button clearButton, plusMilkButton, plusNappyButton, add10mlButton, add20mlButton,
            add50mlButton, add100mlButton, storeMilkButton, deleteAllButton;
    MainActivity.TimeTextWatcher milkTimeTextWatcher;
    private String TAG = MilkAdderFragment.class.getSimpleName();

    public MilkAdderFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(LOG, "Inflating...");
        return inflater.inflate(R.layout.fragment_milk_adder, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(MILK_AMOUNT_VALUE_KEY)) {
                milkAmountValue = savedInstanceState.getInt(MILK_AMOUNT_VALUE_KEY);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Context context = getActivity(); //getActivity();

        View view = getView();
        // Getting database instance:
        EntriesDbHandler dbHelper = EntriesDbHandler.getInstance(context); // FIXME.
        mDb = dbHelper.getInstance(context); // TODO: Add closing of the Database. ref. ,to: https://developer.android.com/reference/android/database/sqlite/SQLiteOpenHelper.html#getWritableDatabase()

        // Added from MainActivity
        milkAmountTextView = (TextView) view.findViewById(R.id.milk_amount_tv);
        milkTimeEditText = (EditText) view.findViewById(R.id.milk_time_et);
        clearButton = (Button) view.findViewById(R.id.clear_button);
//        plusMilkButton = (Button) view.findViewById(R.id.plus_milk_button);
//        plusNappyButton = (Button) view.findViewById(R.id.plus_nappy_button);
        add10mlButton = (Button) view.findViewById(R.id.add_10_ml_button);
        add20mlButton = (Button) view.findViewById(R.id.add_20_ml_button);
        add50mlButton = (Button) view.findViewById(R.id.add_50_ml_button);
        add100mlButton = (Button) view.findViewById(R.id.add_100_ml_button);
        storeMilkButton = (Button) view.findViewById(R.id.store_milk_button);
        deleteAllButton = (Button) view.findViewById(R.id.delete_all);

        // Set on click listener for all buttons.
        Button[] buttons = new Button[] {
                clearButton, /*plusMilkButton, plusNappyButton,*/ add10mlButton,
                add20mlButton, add50mlButton, add100mlButton, storeMilkButton,
                deleteAllButton
        };
        for (Button b : buttons) {
            b.setOnClickListener(this);
        }

        // Setting:
        // Add EditText field milkTimeEditText:
        // Set current time:
        DateTimeFormatter dtf = DateTimeFormat.forPattern("HH:mm");
        milkTimeEditText.setText(dtf.print(LocalTime.now()));

        // Seting Time-related things:
        milkTimeTextWatcher = new MainActivity.TimeTextWatcher(milkTimeEditText);
        milkTimeEditText.addTextChangedListener(milkTimeTextWatcher);
        setIme(context);

        // Setting milk value:
        updateMilkAmount();

    }

    public static class ItemSelectedListener implements AdapterView.OnItemSelectedListener {

        Context context;
        Toast mToast;
        public ItemSelectedListener(Context ctx) {
            this.context = ctx;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (mToast != null) {
                mToast.cancel();
            }
            mToast = Toast.makeText(context, "Item: " + position + " selected by the spinner", Toast.LENGTH_LONG);
            mToast.show();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(MILK_AMOUNT_VALUE_KEY, milkAmountValue);
        Log.d(TAG, "Saved milkAmountValue: " + milkAmountValue);
    }

    protected void updateMilkAmount() {
        milkAmountTextView.setError(null);
        milkAmountTextView.setText(milkAmountValue + " ml");
    }

    // This is from: https://stackoverflow.com/questions/2342620/how-to-hide-keyboard-after-typing-in-edittext-in-android
    // TODO: Consider bundling it together with TimeTextWatcher
    private void setIme(Context context) {
        InputMethodManager inputManager =
                (InputMethodManager) context.
                        getSystemService(Context.INPUT_METHOD_SERVICE);

        View view = milkTimeEditText;

        inputManager.hideSoftInputFromWindow(
                view.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void onClick(View v) {
        Context context = getActivity();

        if (v == null) {
            throw new UnsupportedOperationException("Onclick provided with null value");
        }

        if (v.equals(clearButton)) {
            milkAmountValue = 0;
            updateMilkAmount();
        } else if (v.equals(plusMilkButton)) {
            Toast.makeText(context, "plusMilkButton to be handled yet", Toast.LENGTH_LONG).show();
        } else if (v.equals(plusNappyButton)) {
            Toast.makeText(context, "plusNappyButton to be handled yet", Toast.LENGTH_LONG).show();
        } else if (v.equals(add10mlButton)) {
            milkAmountValue += 10;
            updateMilkAmount();
        } else if (v.equals(add20mlButton)) {
            milkAmountValue += 20;
            updateMilkAmount();
        } else if (v.equals(add50mlButton)) {
            milkAmountValue += 50;
            updateMilkAmount();
        } else if (v.equals(add100mlButton)) {
            milkAmountValue += 100;
            updateMilkAmount();
        } else if (v.equals(storeMilkButton)) {
            // TODO: Check if not 0 ml first.
            //addMilkEntry(milkAmountValue , System.currentTimeMillis());
            if (milkAmountValue == 0) {
                milkAmountTextView.setError("Cannot be 0.");
                return;
            }
            milkTimeTextWatcher.setDefaultTimeIfEntryInvalid();
            long timeMillis = milkTimeTextWatcher.getTimeMilis();


            Uri uri = context.getContentResolver().insert(EntriesProvider.URI_FEEDS,
                    new Feed(-1, Feed.COLUMN_FEED_TB, timeMillis, milkAmountValue, null).asContentValues());
                    //new Feed(-1, timeMillis, milkAmountValue).asContentValues());
            Log.d(TAG, "Inserted: " + uri.toString());

            //mDb.insertFeeding(milkAmountValue, timeMillis);
            Toast.makeText(context, "Item inserted ok.", Toast.LENGTH_LONG).show();
//          updateLogRecyclerView();

            //Toast.makeText(this, "storeMilkButton to be handled yet", Toast.LENGTH_LONG).show();
            milkAmountValue = 0;
            updateMilkAmount();
            getActivity().finish(); // FIXME: This should go to the upper.
        } else if (v.equals(deleteAllButton)) {
            // TODO: Add question first.

            context.getContentResolver().delete(EntriesProvider.URI_FEEDS, null, null);
            //mDb.deleteAllFeedings();
        } else {
            Log.d(TAG, "Unknown item clicked: " + v);
        }
    }
}
