package com.example.android.babyml;

//import android.app.Fragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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

    //MilkAdderFragment milkAdderFragment;
    NappyAdderFragment nappyAdderFragment;

    // TODO: This is for spinner -> it should select fragments.
    public class ItemSelectedListener implements AdapterView.OnItemSelectedListener {

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

            if (position == 0) {
                displayRightFragment(new MilkAdderFragment());
            } else {
                displayRightFragment(new NappyAdderFragment());
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    public void displayRightFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.activity_frag, fragment);
        //ft.addToBackStack(null); // is this needed?
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);

        // Selection of spinners.
//        MilkAdderFragment milkAdderFragment = new MilkAdderFragment();
//        FragmentTransaction ft = getFragmentManager().beginTransaction();
//        ft.replace(R.id.activity_frag, milkAdderFragment);
//        ft.addToBackStack(null);
//        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
//        ft.commit();
        final MilkAdderFragment milkAdderFragment = new MilkAdderFragment();

        milkAdderFragment.setOnCloseListener(new OnCloseListener() {
            @Override
            public void close() {
                milkAdderFragment.getActivity().finish();
            }
        });
        displayRightFragment(milkAdderFragment);

        Spinner addSpinner = (Spinner) findViewById(R.id.add_spinner);

        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.add_drop_down_array,
                R.layout.add_spinner_item);

        addSpinner.setAdapter(adapter);
        addSpinner.setOnItemSelectedListener(itemSelectedListener);
    }
}
