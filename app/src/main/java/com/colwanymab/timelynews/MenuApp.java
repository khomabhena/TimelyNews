package com.colwanymab.timelynews;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.Group;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class MenuApp extends AppCompatActivity {

    Group groupAdmin;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    String databaseRootAdmin = "administrators";
    String databaseRootPublished = "publishedWriters";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_app);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        prefs = getSharedPreferences(AppInfo.USER_INFO, Context.MODE_PRIVATE);
        groupAdmin = findViewById(R.id.groupAdmin);

        checkStuffGroups();

        FirebaseDatabase.getInstance().getReference()
                .child(databaseRootAdmin)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists())
                            return;
                        boolean admin = false;
                        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                        for (DataSnapshot snap : dataSnapshot.getChildren()) {
                            SetterAdmin setter = snap.getValue(SetterAdmin.class);
                            if (setter.getEmail().equals(email))
                                admin = true;
                        }

                        editor = prefs.edit();
                        editor.putBoolean(AppInfo.BOOL_ADMIN, admin);
                        editor.apply();

                        checkStuffGroups();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

        FirebaseDatabase.getInstance().getReference()
                .child(databaseRootPublished)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists())
                            return;
                        boolean isPublished = false;
                        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                        for (DataSnapshot snap : dataSnapshot.getChildren()) {
                            SetterAdmin setter = snap.getValue(SetterAdmin.class);
                            if (setter.getEmail().equals(email))
                                isPublished = true;
                        }

                        editor = prefs.edit();
                        editor.putBoolean(AppInfo.BOOL_WRITER, isPublished);
                        editor.apply();

                        checkStuffGroups();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void checkStuffGroups() {
        if (prefs.getBoolean(AppInfo.BOOL_ADMIN, false)) {
            groupAdmin.setVisibility(View.VISIBLE);
        } else
            groupAdmin.setVisibility(View.GONE);
    }

    public void launchMenu(View view) {
        switch (view.getId()) {
            case R.id.tvAccount:
                startActivity(new Intent(this, TopUp.class));
                break;
            case R.id.tvReceive:
                startActivity(new Intent(this, Receive.class));
                break;
            case R.id.tvPublish:
                startActivity(new Intent(this, Publish.class));
                break;
            case R.id.tvReceivedStories:
                startActivity(new Intent(this, ReceivedStories.class));
                break;
            case R.id.tvSend:
                startActivity(new Intent(this, SendStory.class));
                break;
            case R.id.tvShare:
                startActivity(new Intent(this, ShareNews.class));
                break;
            case R.id.tvJoin:
                if (prefs.getBoolean(AppInfo.BOOL_WRITER, false))
                    startActivity(new Intent(this, JoinNetwork.class));
                else
                    startActivity(new Intent(this, SignUp.class));
                break;
        }
    }

    public void receiveNotifications(View view) {
        String finalMessage = "_Timely News Notifications_\n\nI want to receive WhatsApp notifications of latest news updates.";
        Intent i = new Intent();
        i.putExtra(Intent.EXTRA_TEXT, finalMessage);
        i.setAction(Intent.ACTION_VIEW);
        i.setData(Uri.parse("https://api.whatsapp.com/send?phone=263774876886&text=" + urlEncode(finalMessage)));
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
