package com.colwanymab.timelynews;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Receive extends AppCompatActivity {

    List list;
    EditText etText;
    DBOperations dbOperations;
    SQLiteDatabase db;
    List<String> messageKeys;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbOperations = new DBOperations(Receive.this);
        db = dbOperations.getWritableDatabase();
        messageKeys = dbOperations.getNewsKeys(db);
        list = new ArrayList();
        etText = (EditText) findViewById(R.id.etText);

    }

    public void decrypt(View view) {
        String sample = etText.getText().toString();
        String decrypted = decryptString(sample);
        //etText.setText(decrypted);

        int start = decrypted.indexOf("{");


        getNewsObjects(decrypted.substring(start));
    }

    private void getNewsObjects(String decrypted) {
        try {
            JSONObject jsonObjectDecrypted = new JSONObject(decrypted);
            JSONArray jsonArrayStories = jsonObjectDecrypted.getJSONArray("timelyNewsStories");

            int countStory = 0;
            for (int x = 0;  x < jsonArrayStories.length(); x++) {
                JSONObject jo = jsonArrayStories.getJSONObject(countStory);
                SetterNews setter = new SetterNews(jo.getString("key"), jo.getString("writerUid"),
                        jo.getString("editorUid"), jo.getString("headline"), jo.getString("story"),
                        jo.getLong("readTime"), jo.getLong("timestamp"), jo.getBoolean("read"),
                        jo.getString("link"), jo.getString("tag"), jo.getBoolean("free"));

                if (messageKeys.contains(setter.getKey()))
                    continue;
                else
                    new Insert(Receive.this).execute(setter);

                countStory++;
            }
            etText.setText("");
            if (countStory == 0)
                Toast.makeText(getApplicationContext(), "No new stories", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(getApplicationContext(), "Stories added: " + countStory, Toast.LENGTH_LONG).show();

        } catch (JSONException e) {
            finish();
        }
    }

    private String decryptString(String sample) {
        List<String> listOG = new LinkedList<>(Arrays.asList(Read.encrypy1));
        StringBuilder newString = new StringBuilder();
        char[] charArray = sample.toCharArray();
        for (char aCharArray : charArray) {
            String sam = String.valueOf(aCharArray);
            int index = listOG.indexOf(sam);
            if (index == -1)
                newString.append(aCharArray);
            else
                newString.append(Read.originalLetters[index]);
        }

        return newString.toString();
    }

    private class Insert extends AsyncTask<SetterNews, Void, Void> {

        Context context;

        Insert(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(SetterNews... params) {
            SetterNews setter = params[0];

            if (!messageKeys.contains(setter.getKey())) {
                //makeAdImpression(setter);
                ContentValues values = new ContentValues();
                values.put(DBContract.News.KEY, setter.getKey());
                values.put(DBContract.News.WRITER_UID, setter.getWriterUid());
                values.put(DBContract.News.EDITOR_UID, setter.getEditorUid());
                values.put(DBContract.News.HEADLINE, setter.getHeadline());
                values.put(DBContract.News.STORY, setter.getStory());
                values.put(DBContract.News.READ_TIME, setter.getReadTime());
                values.put(DBContract.News.LINK, setter.getLink());
                values.put(DBContract.News.READ, setter.isRead() ? "yes" : "no");
                values.put(DBContract.News.FREE, setter.isFree() ? "yes" : "no");
                values.put(DBContract.News.TIMESTAMP, setter.getTimeStamp());
                values.put(DBContract.News.TAG, setter.getTag());

                db.insert(DBContract.News.TABLE_NAME, null, values);
            }

            messageKeys = dbOperations.getNewsKeys(db);

            return null;
        }

    }

}
