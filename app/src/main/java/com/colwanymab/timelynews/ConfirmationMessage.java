package com.colwanymab.timelynews;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ConfirmationMessage extends AppCompatActivity {

    ListView listView;
    ArrayList<String> smsList;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    DBOperations dbOperations;
    SQLiteDatabase db;
    List<String> messageKeys;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation_message);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbOperations = new DBOperations(ConfirmationMessage.this);
        db = dbOperations.getWritableDatabase();
        messageKeys = dbOperations.getConfirmationKeys(db);
        prefs = getSharedPreferences(AppInfo.USER_INFO, Context.MODE_PRIVATE);
        listView= findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                finish();
            }
        });

        showMessages();
    }

    private void showMessages() {
        Uri inboxURI = Uri.parse("content://sms/inbox");
        smsList = new ArrayList<>();
        String name = "KHOLWANI MABHENA";
        ContentResolver cr = getContentResolver();

        Cursor c = cr.query(inboxURI, null, null, null, null);

        if (c != null) {
            while (c.moveToNext()) {
                if (c.getPosition() < 10) {
                    String number = c.getString(c.getColumnIndexOrThrow("address"));
                    String body = c.getString(c.getColumnIndexOrThrow("body"));

                    if (number.contains("+263164") && body.toLowerCase().contains(name.toLowerCase())
                            && body.contains("1.00") && body.contains(getDate())) {
                        body = body.substring(0, body.indexOf("New wallet"));
                        smsList.add("\nNumber: " + number + "\nMessage: " + body + "\n");

                        long days30 = 86400000L * 30;
                        days30 += System.currentTimeMillis();

                        if (!messageKeys.contains(body)) {
                            editor = prefs.edit();
                            editor.putInt(AppInfo.INT_CREDITS, prefs.getInt(AppInfo.INT_CREDITS, 0) + 25);
                            editor.putLong(AppInfo.LONG_EXPIRY_STAMP, prefs.getLong(AppInfo.LONG_EXPIRY_STAMP, 0) + days30);
                            editor.apply();

                            new Insert(ConfirmationMessage.this).execute(body);
                        }
                    }
                }
            }
            c.close();
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, smsList);
            listView.setAdapter(adapter);
        }
    }

    String getDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);

        String day = getTheValue(calendar.get(Calendar.DAY_OF_MONTH));
        String month = getTheValue(calendar.get(Calendar.MONTH) +1);
        String year = String.valueOf(calendar.get(Calendar.YEAR)).substring(2, 4);

        return  year + month + day;
    }

    public String getTheValue(int value){
        String theValue = String.valueOf(value);
        if (theValue.length() == 1){
            return "0"+theValue;
        } else {
            return theValue;
        }
    }

    private class Insert extends AsyncTask<String, Void, Void> {

        Context context;

        Insert(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(String... params) {
            String message = params[0];

            if (!messageKeys.contains(message)) {
                //makeAdImpression(setter);
                ContentValues values = new ContentValues();
                values.put(DBContract.ConfirmationMessage.MESSAGE, message);

                db.insert(DBContract.ConfirmationMessage.TABLE_NAME, null, values);
            }

            messageKeys = dbOperations.getConfirmationKeys(db);

            return null;
        }

    }

}
