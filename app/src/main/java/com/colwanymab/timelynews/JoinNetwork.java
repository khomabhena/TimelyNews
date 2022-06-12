package com.colwanymab.timelynews;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.Group;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class JoinNetwork extends AppCompatActivity {

    EditText etName, etSurname, etTwitter, etFacebook, etNumber;
    ProgressBar progressBar;
    String databaseRoot = "writerProfile";

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_network);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        prefs = getSharedPreferences(AppInfo.USER_INFO, Context.MODE_PRIVATE);
        etName = findViewById(R.id.etName);
        etSurname = findViewById(R.id.etSurname);
        etTwitter = findViewById(R.id.etTwitter);
        etFacebook = findViewById(R.id.etFacebook);
        progressBar = findViewById(R.id.progressBar);
        etNumber = findViewById(R.id.etNumber);

        setValues();
    }

    private void setValues() {
        etName.setText(prefs.getString(AppInfo.NAME, ""));
        etSurname.setText(prefs.getString(AppInfo.SURNAME, ""));
        etTwitter.setText(prefs.getString(AppInfo.TWITTER, ""));
        etFacebook.setText(prefs.getString(AppInfo.FACEBOOK, ""));
        etNumber.setText(prefs.getString(AppInfo.ECOCASH_NUMBER, ""));
    }

    public void joinNetwork(View view) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final String name = etName.getText().toString();
        final String surname = etSurname.getText().toString();
        final String twitter = etTwitter.getText().toString();
        final String facebook = etFacebook.getText().toString();
        final String number = etNumber.getText().toString();
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String link = prefs.getString(AppInfo.PROFILE_LINK, "");

        if (name.equals("") || surname.equals("") || twitter.equals("") || facebook.equals("") || number.equals(""))
            Toast.makeText(getApplicationContext(), "Fill all the fields", Toast.LENGTH_SHORT).show();
        else {
            progressBar.setVisibility(View.VISIBLE);
            SetterProfile setter = new SetterProfile(uid, email, name, surname, twitter, facebook, link, number);
            FirebaseDatabase.getInstance().getReference()
                    .child(databaseRoot)
                    .child(uid)
                    .setValue(setter)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressBar.setVisibility(View.GONE);
                            editor = prefs.edit();
                            editor.putString(AppInfo.NAME, name);
                            editor.putString(AppInfo.SURNAME, surname);
                            editor.putString(AppInfo.TWITTER, twitter);
                            editor.putString(AppInfo.FACEBOOK, facebook);
                            editor.putString(AppInfo.ECOCASH_NUMBER, number);
                            editor.apply();

                            Toast.makeText(getApplicationContext(), "Your profile has been created", Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }
}
