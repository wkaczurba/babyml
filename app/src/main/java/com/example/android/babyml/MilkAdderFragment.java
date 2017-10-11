package com.example.android.babyml;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.babyml.data.EntriesProviderContract;
import com.example.android.babyml.data.Feed;
import com.example.android.babyml.utils.MiscUiUtils;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import lombok.Getter;
import lombok.Setter;

/**
 * A simple {@link Fragment} subclass.
 */
// TODO: Sasving state and contents of elements (e.g. milkAmount)

public class MilkAdderFragment extends Fragment implements View.OnClickListener {

    @Getter
    @Setter
    private OnCloseListener onCloseListener = new OnCloseListener() {
        @Override
        public void close() {
            getActivity().finish();
        }
    };

    private static final String LOG = MilkAdderFragment.class.getSimpleName();
    // Milk-related items:
    private int milkAmountValue = 0; // FIXME: Save instance.
    private static final String MILK_AMOUNT_VALUE_KEY = "milkAmountValue";

    private TextView milkAmountTextView;
    private EditText milkTimeEditText;
    private CheckBox milkIncludeNoteCheckBox; // milk_include_note_cb;
    private EditText milkNoteEditText; // milk_note_et;
    private Button clearButton, plusMilkButton, plusNappyButton, add10mlButton, add20mlButton,
            add50mlButton, add100mlButton, storeMilkButton, deleteAllButton;
    private TimeTextWatcher milkTimeTextWatcher;
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
        if (view == null) {
            throw new IllegalStateException("milkAdderFragment.onStart() does not produce valid view.");
        }

        // Added from MainActivity
        milkAmountTextView = view.findViewById(R.id.milk_amount_tv);
        milkTimeEditText = view.findViewById(R.id.milk_time_et);
        clearButton = view.findViewById(R.id.clear_button);
        add10mlButton = view.findViewById(R.id.add_10_ml_button);
        add20mlButton = view.findViewById(R.id.add_20_ml_button);
        add50mlButton = view.findViewById(R.id.add_50_ml_button);
        add100mlButton = view.findViewById(R.id.add_100_ml_button);
        storeMilkButton = view.findViewById(R.id.store_milk_button);
        deleteAllButton = view.findViewById(R.id.delete_all);

        milkIncludeNoteCheckBox = view.findViewById(R.id.milk_include_note_cb); // milk_include_note_cb;
        milkNoteEditText = view.findViewById(R.id.milk_note_et); // milk_note_et;

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

        milkIncludeNoteCheckBox.setOnClickListener(this);

        // Seting Time-related things:
        milkTimeTextWatcher = new TimeTextWatcher(milkTimeEditText);
        milkTimeEditText.addTextChangedListener(milkTimeTextWatcher);

        // Setting milk value:
        updateMilkAmount();
        updateVisibilityOfNoteEditText();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(MILK_AMOUNT_VALUE_KEY, milkAmountValue);
        Log.d(TAG, "Saved milkAmountValue: " + milkAmountValue);
    }

    private void updateMilkAmount() {
        milkAmountTextView.setError(null);

        String sb = String.valueOf(milkAmountValue) +
                " ml";

        milkAmountTextView.setText(sb);
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
            storeMilkAndClose();
        } else if (v.equals(deleteAllButton)) {
            // TODO: Add question first.
            context.getContentResolver().delete(EntriesProviderContract.URI_FEEDS, null, null);
        } else if (v.equals(milkIncludeNoteCheckBox)) {
            updateVisibilityOfNoteEditText();
        } else {
            Log.d(TAG, "Unknown item clicked: " + v);
        }
    }

    public void updateVisibilityOfNoteEditText() {
        if (milkIncludeNoteCheckBox.isChecked()) {
            milkNoteEditText.setVisibility(View.VISIBLE);
        } else {
            milkNoteEditText.setVisibility(View.GONE);
        }
    }

    private String getNote() {
        if (milkNoteEditText.getVisibility() == View.VISIBLE) {
            return milkNoteEditText.getText().toString();
        } else {
            return null;
        }
    }

    private void storeMilkAndClose() {
        Context context = getActivity();
        if (milkAmountValue == 0) {
            milkAmountTextView.setError("Cannot be 0.");
            return;
        }
        milkTimeTextWatcher.setDefaultTimeIfEntryInvalid();
        long timeMillis = milkTimeTextWatcher.getTimeMilis();

        Uri uri = context.getContentResolver().insert(EntriesProviderContract.URI_FEEDS,
                new Feed(-1, Feed.COLUMN_FEED_TB, timeMillis, milkAmountValue, getNote()).asContentValues());
        if (uri == null) {
            throw new NullPointerException("Returned uri is null.");
        }
        Log.d(TAG, "Inserted: " + uri.toString());

        Toast.makeText(context, "Item inserted ok.", Toast.LENGTH_LONG).show();
        milkAmountValue = 0;
        updateMilkAmount();

        MiscUiUtils.closeKeyboard(getActivity());
        onCloseListener.close();
    }
}
