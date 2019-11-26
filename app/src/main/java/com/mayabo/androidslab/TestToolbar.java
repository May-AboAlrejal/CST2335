package com.mayabo.androidslab;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.app.AlertDialog.Builder;
import com.google.android.material.snackbar.Snackbar;


public class TestToolbar extends AppCompatActivity {

    private String message = "You clicked on the overflow menu!";
    private Toolbar tBar;
    private Context thisApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_toolbar);

        thisApp = this;
        tBar = findViewById(R.id.toolbar);
        setSupportActionBar(tBar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item1:
                Toast.makeText(thisApp, "This is the initial message", Toast.LENGTH_LONG).show();
                break;
            case R.id.item2:
                getBuilder();
                break;
            case R.id.item3:
                Snackbar sb = Snackbar.make(tBar, "You clicked the spider", Snackbar.LENGTH_LONG)
                        .setAction( "Go Back?", e -> finish());
                sb.show();
                break;
            case R.id.item4:
                Toast.makeText(thisApp, message, Toast.LENGTH_LONG).show();
                break;
        }

        return true;
    }

    private void getBuilder() {
        View v = getLayoutInflater().inflate(R.layout.activity_builder, null);

        Builder builder = new Builder(thisApp);

        builder.setView(v)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EditText text = v.findViewById(R.id.typeHere);
                        message = text.getText().toString();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                }).create().show();
    }
}
