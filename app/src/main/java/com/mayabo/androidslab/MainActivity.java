package com.mayabo.androidslab;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences prefs;
    private EditText email;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        prefs = getSharedPreferences("EmailFile", MODE_PRIVATE);
        String previous = prefs.getString("ReservedEmail", "");
        editor = prefs.edit();

        email.setText(previous);

        Button logIn = findViewById(R.id.login);
        if(logIn != null){
            logIn.setOnClickListener(clk -> {
                Intent goToProfileActivity = new Intent(MainActivity.this, ProfileActivity.class);
                goToProfileActivity.putExtra("ReservedEmail", email.getText().toString());
                startActivityForResult(goToProfileActivity, 11);
            });
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        editor.putString("ReservedEmail", email.getText().toString());
        editor.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "You came back from ProfileActivity by hitting the back button",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
