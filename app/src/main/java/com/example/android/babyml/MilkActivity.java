package com.example.android.babyml;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.babyml.data.FeedingDbHelper;
import com.example.android.babyml.data.FeedingUtils;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;

public class MilkActivity extends AppCompatActivity implements View.OnClickListener {

    //private TextView mInitialTextView;
    private Spinner addSpinner;
    int milkAmountValue = 0; // FIXME: Save instance.
    public static String TAG = MilkActivity.class.getSimpleName();
    static final String MILK_AMOUNT_VALUE_KEY = "milkAmountValue";
    SQLiteDatabase mDb;

    ItemSelectedListener itemSelectedListener = new ItemSelectedListener(this);

    //TextView logTextView;
    TextView milkAmountTextView;
    EditText milkTimeEditText;
    Button clearButton, plusMilkButton, plusNappyButton, add10mlButton, add20mlButton,
            add50mlButton, add100mlButton, storeMilkButton, deleteAllButton;
    MainActivity.TimeTextWatcher milkTimeTextWatcher;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_milk);

        // Getting database instance:
        FeedingDbHelper dbHelper = new FeedingDbHelper(this);
        mDb = dbHelper.getWritableDatabase();

        // Added from MainActivity
        milkAmountTextView = (TextView) findViewById(R.id.milk_amount_tv);
        milkTimeEditText = (EditText) findViewById(R.id.milk_time_et);
        clearButton = (Button) findViewById(R.id.clear_button);
        plusMilkButton = (Button) findViewById(R.id.plus_milk_button);
        plusNappyButton = (Button) findViewById(R.id.plus_nappy_button);
        add10mlButton = (Button) findViewById(R.id.add_10_ml_button);
        add20mlButton = (Button) findViewById(R.id.add_20_ml_button);
        add50mlButton = (Button) findViewById(R.id.add_50_ml_button);
        add100mlButton = (Button) findViewById(R.id.add_100_ml_button);
        storeMilkButton = (Button) findViewById(R.id.store_milk_button);
        deleteAllButton = (Button) findViewById(R.id.delete_all);

        // Set on click listener for all buttons.
        Button[] buttons = new Button[] {
                clearButton, plusMilkButton, plusNappyButton, add10mlButton,
                add20mlButton, add50mlButton, add100mlButton, storeMilkButton,
                deleteAllButton
        };
        for (Button b : buttons) {
            b.setOnClickListener(this);
        }


        //mInitialTextView = (TextView) findViewById(R.id.initial_tv);
        addSpinner = (Spinner) findViewById(R.id.add_spinner);

        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.add_drop_down_array,
                R.layout.add_spinner_item);

        addSpinner.setAdapter(adapter);
        addSpinner.setOnItemSelectedListener(itemSelectedListener);


        // Setting:
        // Add EditText field milkTimeEditText:
        // Set current time:
        DateTimeFormatter dtf = DateTimeFormat.forPattern("HH:mm");
        milkTimeEditText.setText(dtf.print(LocalTime.now()));

        milkTimeTextWatcher = new MainActivity.TimeTextWatcher(milkTimeEditText);
        milkTimeEditText.addTextChangedListener(milkTimeTextWatcher);


        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(MILK_AMOUNT_VALUE_KEY)) {
                milkAmountValue = savedInstanceState.getInt(MILK_AMOUNT_VALUE_KEY);
            }
        }

        updateMilkAmount();
        //FeedingUtils.deleteAllFeedings(mDb);
//LOG        updateLogTextView();
        //updateLogRecyclerView();
        setIme(this);

    }

//    protected void updateMilkAmount() {
//        milkAmountTextView.setError(null);
//        milkAmountTextView.setText(milkAmountValue + " ml");
//    }
//
//    // This is from: https://stackoverflow.com/questions/2342620/how-to-hide-keyboard-after-typing-in-edittext-in-android
//    private void setIme(Context context) {
//        InputMethodManager inputManager =
//                (InputMethodManager) context.
//                        getSystemService(Context.INPUT_METHOD_SERVICE);
//
//        View view = milkTimeEditText;
//
//        inputManager.hideSoftInputFromWindow(
//                view.getWindowToken(),
//                InputMethodManager.HIDE_NOT_ALWAYS);
//    }

    @Override
    public void onClick(View v) {
        if (v == null) {
            throw new UnsupportedOperationException("Onclick provided with null value");
        }

        if (v.equals(clearButton)) {
            milkAmountValue = 0;
            updateMilkAmount();
        } else if (v.equals(plusMilkButton)) {
            Toast.makeText(this, "plusMilkButton to be handled yet", Toast.LENGTH_LONG).show();
        } else if (v.equals(plusNappyButton)) {
            Toast.makeText(this, "plusNappyButton to be handled yet", Toast.LENGTH_LONG).show();
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
            FeedingUtils.insertFeeding(mDb, milkAmountValue, timeMillis);
            Toast.makeText(this, "Item inserted ok.", Toast.LENGTH_LONG).show();
//          updateLogRecyclerView();

            //Toast.makeText(this, "storeMilkButton to be handled yet", Toast.LENGTH_LONG).show();
            milkAmountValue = 0;
            updateMilkAmount();
        } else if (v.equals(deleteAllButton)) {
            // TODO: Add question first.
            FeedingUtils.deleteAllFeedings(mDb);
        } else {
            Log.d(TAG, "Unknown item clicked: " + v);
        }
    }

    // FIXME: This should not be here...
//    private void updateLogRecyclerView() {
//        Cursor cursor = FeedingUtils.getAllFeedingsCursor(mDb);
//
//
//        List<String> list = FeedingUtils.cursorAsStringList(cursor);
//
////        RecyclerView logRecyclerView = (RecyclerView) findViewById(R.id.log_rv);
////        LogAdapter mAdapter = (LogAdapter) logRecyclerView.getAdapter();
////
////        mAdapter.setData(list);
//
//        //logTextView.setText(s);
//    }


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
    private void setIme(Context context) {
        InputMethodManager inputManager =
                (InputMethodManager) context.
                        getSystemService(Context.INPUT_METHOD_SERVICE);

        View view = milkTimeEditText;

        inputManager.hideSoftInputFromWindow(
                view.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }


}
