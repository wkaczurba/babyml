package com.example.android.babyml;

import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class AddEntryActivity extends AppCompatActivity {

    public static String TAG = AddEntryActivity.class.getSimpleName();
    AddEntryActivity.ItemSelectedListener itemSelectedListener = new AddEntryActivity.ItemSelectedListener(this);

    // TODO: This is for spinner -> it should select fragments.
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
        setContentView(R.layout.activity_add_entry);

        // Selection of spinners.
        MilkAdderFragment milkAdderFragment = new MilkAdderFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.activity_frag, milkAdderFragment);
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();

        Spinner addSpinner = (Spinner) findViewById(R.id.add_spinner);

        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.add_drop_down_array,
                R.layout.add_spinner_item);

        addSpinner.setAdapter(adapter);
        addSpinner.setOnItemSelectedListener(itemSelectedListener);
    }
}
