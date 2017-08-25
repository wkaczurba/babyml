package com.example.android.babyml;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.joda.time.*;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.example.android.babyml.data.FeedingDbHelper;
import com.example.android.babyml.data.FeedingUtils;
import com.example.android.babyml.utils.DateUtils;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // FIXME: Do we need mDb here (it has been already added to MilkActivity.

    public static String TAG = MainActivity.class.getSimpleName();

//    static final String MILK_AMOUNT_VALUE_KEY = "milkAmountValue";
//    int milkAmountValue = 0; // FIXME: Save instance.

    // Database:
    SQLiteDatabase mDb;

//    //TextView logTextView;
//    TextView milkAmountTextView;
//    EditText milkTimeEditText;
//    Button clearButton, plusMilkButton, plusNappyButton, add10mlButton, add20mlButton,
//            add50mlButton, add100mlButton, storeMilkButton, deleteAllButton;
//    TimeTextWatcher milkTimeTextWatcher;
    RecyclerView logRecyclerView;
    FloatingActionButton fab;

    // Log-related items:
    LogAdapter mAdapter;
    LogListItemClickListener mLogListItemClickListener;

//    /**
//     * Function gets current date time and applies particular time to it.
//     * If hour/min are ahead of current time - a day earlier is returned.
//     *
//     * TODO: Move this to Utils class.
//     *
//     * @return
//     */
//    public static LocalDateTime applyTimeToCurrentLocalDate(int hour, int min) {
//        LocalDateTime ldtNow = LocalDateTime.now();
//        LocalTime ltNow = ldtNow.toLocalTime();
//
//        LocalDate ld; // date to be used (either today or yesterday)
//        LocalTime lt = new LocalTime(hour, min, 0); // time to be used
//
//        if (lt.isAfter(ltNow)) {
//            // Use yesterday's date as hour/time specifies time before midnight.
//            ld = ldtNow.toLocalDate().minusDays(1);
//        } else {
//            // Use yesterday's date as hour/time specifies today's time during.
//            ld = ldtNow.toLocalDate();
//        }
//
//        return new LocalDateTime(
//                ld.getYear(),
//                ld.getMonthOfYear(),
//                ld.getDayOfMonth(),
//                lt.getHourOfDay(),
//                lt.getMinuteOfHour());
//    }

    public static class LogListItemClickListener implements LogAdapter.ListItemClickListener {
        Context context;
        Toast mToast;

        public LogListItemClickListener(Context context) {
            this.context = context;
        }

        @Override
        public void onListItemClick(int clickItemIndex) {
            // get from cursor + output as Toast.
            if (mToast != null) {
                mToast.cancel();
            }
            mToast = Toast.makeText(context, "Item #" + clickItemIndex + " clicked", Toast.LENGTH_LONG);
            mToast.show();
        }
    }

    /**
     * The following is a textWatcher for milkTimeEditText
     *
     * TODO: Implement something similar to this one;
     * http://www.techrepublic.com/article/pro-tip-write-a-validate-as-you-go-android-textwatcher-for-date-entry-fields/
     */
    public static class TimeTextWatcher implements TextWatcher {
        EditText et;

        public TimeTextWatcher(EditText et) {
            this.et = et;
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
            } else {
                // empty.
            }
        }

        public int[] getHoursMins() {
            String entry = et.getText().toString();
            return DateUtils.toHoursMinArray(entry);
        }

        public long getTimeMilis() {
            int[] hhmm = getHoursMins();
            if (hhmm == null) {
                throw new NullPointerException("getHoursMins returned null; field contains invalid time. Try using setDefaultTimeIfEntryInvalid before using getHoursMins");
            }

            LocalDateTime ldt = DateUtils.applyTimeToCurrentLocalDate(hhmm[0], hhmm[1]);
            return ldt.toDateTime().getMillis();
        }

        public void setDefaultTime() {
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
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        // Getting database instance:
        FeedingDbHelper dbHelper = new FeedingDbHelper(this);
        mDb = dbHelper.getWritableDatabase();

//        milkAmountTextView = (TextView) findViewById(R.id.milk_amount_tv);
//        milkTimeEditText = (EditText) findViewById(R.id.milk_time_et);
//        clearButton = (Button) findViewById(R.id.clear_button);
//        plusMilkButton = (Button) findViewById(R.id.plus_milk_button);
//        plusNappyButton = (Button) findViewById(R.id.plus_nappy_button);
//        add10mlButton = (Button) findViewById(R.id.add_10_ml_button);
//        add20mlButton = (Button) findViewById(R.id.add_20_ml_button);
//        add50mlButton = (Button) findViewById(R.id.add_50_ml_button);
//        add100mlButton = (Button) findViewById(R.id.add_100_ml_button);
//        storeMilkButton = (Button) findViewById(R.id.store_milk_button);
//        deleteAllButton = (Button) findViewById(R.id.delete_all);
//
//        // Set on click listener for all buttons.
//        Button[] buttons = new Button[] {
//            clearButton, plusMilkButton, plusNappyButton, add10mlButton,
//            add20mlButton, add50mlButton, add100mlButton, storeMilkButton,
//            deleteAllButton
//        };
//        for (Button b : buttons) {
//            b.setOnClickListener(this);
//        }

        // RecyclerView:
        logRecyclerView = (RecyclerView) findViewById(R.id.log_rv);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this); //  to display stuff linearly.
        logRecyclerView.setLayoutManager(layoutManager);
        logRecyclerView.setHasFixedSize(true);

        mLogListItemClickListener = new LogListItemClickListener(this);
        mAdapter = new LogAdapter(mLogListItemClickListener);
        logRecyclerView.setAdapter(mAdapter);

        // Getting FloatingActionButton:
        fab = (FloatingActionButton) findViewById(R.id.fab_add);
        fab.setOnClickListener(this);

        // Add EditText field milkTimeEditText:
        // Set current time:
//        DateTimeFormatter dtf = DateTimeFormat.forPattern("HH:mm");
//        milkTimeEditText.setText(dtf.print(LocalTime.now()));
//
//        milkTimeTextWatcher = new TimeTextWatcher(milkTimeEditText);
//        milkTimeEditText.addTextChangedListener(milkTimeTextWatcher);
        //milkTimeEditText.focus

        // would onRestoreState
//        if (savedInstanceState != null) {
//            if (savedInstanceState.containsKey(MILK_AMOUNT_VALUE_KEY)) {
//                milkAmountValue = savedInstanceState.getInt(MILK_AMOUNT_VALUE_KEY);
//            }
//        }


//        updateMilkAmount();
        //FeedingUtils.deleteAllFeedings(mDb);
//LOG        updateLogTextView();
        updateLogRecyclerView();
//        setIme(this);
    }


    private void updateLogRecyclerView() {
        Cursor cursor = FeedingUtils.getAllFeedingsCursor(mDb);
        //String s = FeedingUtils.cursorAsString(cursor);
        List<String> list = FeedingUtils.cursorAsStringList(cursor);
        mAdapter.setData(list);
        //logTextView.setText(s);
    }




    @Override
    public void onClick(View v) {
        if (v == null) {
            throw new UnsupportedOperationException("Onclick provided with null value");
        }

//        if (v.equals(clearButton)) {
//            milkAmountValue = 0;
//            updateMilkAmount();
//        } else if (v.equals(plusMilkButton)) {
//            Toast.makeText(this, "plusMilkButton to be handled yet", Toast.LENGTH_LONG).show();
//        } else if (v.equals(plusNappyButton)) {
//            Toast.makeText(this, "plusNappyButton to be handled yet", Toast.LENGTH_LONG).show();
//        } else if (v.equals(add10mlButton)) {
//            milkAmountValue += 10;
//            updateMilkAmount();
//        } else if (v.equals(add20mlButton)) {
//            milkAmountValue += 20;
//            updateMilkAmount();
//        } else if (v.equals(add50mlButton)) {
//            milkAmountValue += 50;
//            updateMilkAmount();
//        } else if (v.equals(add100mlButton)) {
//            milkAmountValue += 100;
//            updateMilkAmount();
//        } else if (v.equals(storeMilkButton)) {
//            // TODO: Check if not 0 ml first.
//            //addMilkEntry(milkAmountValue , System.currentTimeMillis());
//            if (milkAmountValue == 0) {
//                milkAmountTextView.setError("Cannot be 0.");
//                return;
//            }
//            milkTimeTextWatcher.setDefaultTimeIfEntryInvalid();
//            long timeMillis = milkTimeTextWatcher.getTimeMilis();
//            FeedingUtils.insertFeeding(mDb, milkAmountValue, timeMillis);
//            updateLogRecyclerView();
//
//            //Toast.makeText(this, "storeMilkButton to be handled yet", Toast.LENGTH_LONG).show();
//            milkAmountValue = 0;
//            updateMilkAmount();
//        } else if (v.equals(deleteAllButton)) {
//            // TODO: Add question first.
//            FeedingUtils.deleteAllFeedings(mDb);
//            updateLogRecyclerView();
//        } else

        if (v.equals(logRecyclerView)) {
            Toast.makeText(this, "logRecyclerView to be handled yet", Toast.LENGTH_LONG).show();
        } else if (v.equals(fab)) {
            Intent intent = new Intent(this, MilkActivity.class);
            // TODO: putExtra parameters here
            startActivity(intent);
        } else {
            Log.d(TAG, "Unknown item clicked: " + v);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        outState.putInt(MILK_AMOUNT_VALUE_KEY, milkAmountValue);
//        Log.d(TAG, "Saved milkAmountValue: " + milkAmountValue);
    }

}
