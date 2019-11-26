package com.mayabo.androidslab;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MessageDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty);

        Bundle dataToPass = getIntent().getExtras();


        FragmentMessageDetails dFragment = new FragmentMessageDetails();
        dFragment.setArguments( dataToPass );
        dFragment.setTablet(false);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentLocation, dFragment)
                .addToBackStack("AnyName")
                .commit();
    }

}
