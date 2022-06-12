package com.colwanymab.timelynews;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class SendStory extends AppCompatActivity {

    ProgressBar progressBar;
    EditText etText;
    CardView cardSend, cardSendWA;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    String databaseRoot = "developingStories";
    String databaseRootEditor = "editorDetails";
    String editorNumber = "263774876886";

    boolean isNetworkAvailable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_story);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        prefs = getSharedPreferences(AppInfo.USER_INFO, Context.MODE_PRIVATE);
        progressBar = findViewById(R.id.progressBar);
        etText = findViewById(R.id.etText);
        cardSend = findViewById(R.id.cardSend);
        cardSendWA = findViewById(R.id.cardSendWA);

        if (prefs.getBoolean(AppInfo.BOOL_WRITER, false)) {
            cardSendWA.setVisibility(View.INVISIBLE);
            cardSend.setVisibility(View.VISIBLE);
        } else {
            cardSend.setVisibility(View.INVISIBLE);
            cardSendWA.setVisibility(View.VISIBLE);
        }

        FirebaseDatabase.getInstance().getReference()
                .child(databaseRootEditor)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        isNetworkAvailable = true;
                        if (!dataSnapshot.exists())
                            return;
                        SetterEditorDetails setter = dataSnapshot.getValue(SetterEditorDetails.class);
                        editorNumber = setter.getNumber();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        etText.setText(prefs.getString(AppInfo.SAMPLE_STORY, ""));
    }

    @Override
    protected void onPause() {
        super.onPause();
        editor = prefs.edit();
        editor.putString(AppInfo.SAMPLE_STORY, etText.getText().toString());
        editor.apply();
    }

    public void uploadStory(View view) {
        if (etText.getText().toString().trim().equals(""))
            finish();
        else {
            progressBar.setVisibility(View.VISIBLE);
            String key = FirebaseDatabase.getInstance().getReference().child(databaseRoot).push().getKey();
            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            SetterNews setter = new SetterNews(key, uid + "\n" + email, "", "", etText.getText().toString(),
                    0, System.currentTimeMillis(), false, "", "", false);

            if (isNetworkAvailable)
                FirebaseDatabase.getInstance().getReference()
                        .child(databaseRoot)
                        .child(key)
                        .setValue(setter)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    progressBar.setVisibility(View.GONE);
                                    etText.setText("");

                                    Toast.makeText(getApplicationContext(), "Thank you", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            String finalMessage;
            finalMessage = "_Timely News Story Report_\n\n" +
                    "\n\nWriter ID: " + uid +
                    "\nName: " + prefs.getString(AppInfo.NAME, "") +
                    "\nEcoCash: " + prefs.getString(AppInfo.ECOCASH_NUMBER, "") +
                    "\nTwitter: " + prefs.getString(AppInfo.TWITTER, "") +
                    "\nFacebook: " + prefs.getString(AppInfo.FACEBOOK, "") +
                    "\nEmail: " + email +
                    "\n\n" + etText.getText().toString();

            Intent i = new Intent();
            i.putExtra(Intent.EXTRA_TEXT, finalMessage);
            i.setAction(Intent.ACTION_VIEW);
            i.setData(Uri.parse("https://api.whatsapp.com/send?phone=" + editorNumber + "&text=" + urlEncode(finalMessage)));
            startActivity(i);
        }
    }

    public void sendViaWhatsApp(View view) {
        String finalMessage;
        finalMessage = "_Timely News Story Report_\n\n" + etText.getText().toString();

        Intent i = new Intent();
        i.putExtra(Intent.EXTRA_TEXT, finalMessage);
        i.setAction(Intent.ACTION_VIEW);
        i.setData(Uri.parse("https://api.whatsapp.com/send?phone=" + editorNumber + "&text=" + urlEncode(finalMessage)));
        startActivity(i);
    }

    private String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

}
