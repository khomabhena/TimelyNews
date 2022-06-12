package com.colwanymab.timelynews;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.util.ArrayUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class Read extends AppCompatActivity {

    TextView tvStory, tvTime, tvHeadline;
    ImageView ivShare;
    ProgressBar progressBar;

    String from;
    int position;
    SetterNews setterNews;

    static String[] originalLetters = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
    static String[] encrypy1 =        {"q", "w", "e", "r", "t", "y", "u", "i", "o", "p", "a", "s", "d", "f", "g", "h", "j", "k", "l", "z", "x", "c", "v", "b", "n", "m", "M", "N", "B", "V", "C", "X", "Z", "L", "K", "J", "H", "G", "F", "D", "S", "A", "P", "O", "I", "U", "Y", "T", "R", "E", "W", "Q"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        from = getIntent().getExtras().getString("from");
        position = getIntent().getExtras().getInt("position");

        if (from.equals("received"))
            setterNews = (SetterNews) ReceivedStories.list.get(position);
        else
            setterNews = (SetterNews) MainActivity.list.get(position);

        tvStory = (TextView) findViewById(R.id.tvStory);
        tvTime = (TextView) findViewById(R.id.tvTime);
        tvHeadline = (TextView) findViewById(R.id.tvHeadline);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        tvStory.setText(setterNews.getStory());
        tvHeadline.setText(setterNews.getHeadline());
        tvTime.setText(getDate(setterNews.getTimeStamp()));

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
    }


    String getDate(long timeReceived) {
        String[] monthsSmall = {"", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);

        long midnight = calendar.getTimeInMillis();

        if (midnight > System.currentTimeMillis()) {
            calendar.setTimeInMillis(timeReceived);
            return "" + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
        } else {
            calendar.setTimeInMillis(timeReceived);
            return "" + calendar.get(Calendar.DAY_OF_MONTH) + "-" + monthsSmall[calendar.get(Calendar.MONTH) +1] + "-" +
                    calendar.get(Calendar.YEAR);
        }
        //

    }

    public void shareArticle(View view) {
        progressBar.setVisibility(View.VISIBLE);
        //JSONObject jsonObjectKey = putNewsKey(setterNews);

        //JSONArray jsonArrayKey = new JSONArray();
        JSONArray jsonArrayStory = putNewsStories(setterNews);

        //jsonArrayKey.put(jsonObjectKey);
        //jsonArrayStory.put(jsonObjectStory);

        JSONObject jsonObject = new JSONObject();
        try {
            //jsonObject.put("timelyStoriesKeys", jsonArrayKey);
            jsonObject.put("timelyNewsStories", jsonArrayStory);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String link = "https://play.google.com/store/apps/details?id=com.colwanymab.timelynews";
        String headline = "*"+ setterNews.getHeadline() +"*.\n\n";
        String details = "1) Copy this text.\n" +
                "2) Open the app.\n" +
                "3) Go to MENU.\n" +
                "4) Click Receive Stories and then paste the text.\n" +
                "\nDownload app here. " + link + "\n\n*Headline*\n" + headline;
        String message = details + encryptString(jsonObject.toString());

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "_Timely News Stories_\n\n*Paste news here*\n\n");
        intent.setPackage("com.whatsapp");
        startActivity(intent);

        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("label", message);
        assert clipboard != null;
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getApplicationContext(), "Text copied. Paste it in your WhatsApp contact.", Toast.LENGTH_LONG).show();

        /*Uri uriUrl = Uri.parse("whatsapp://send?text="+ message +"");
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);*/
    }

    private JSONArray putNewsStories(SetterNews setterNews) {
        JSONArray jsonArrayStory = new JSONArray();
        String story = setterNews.getStory();

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("key", setterNews.getKey());
            jsonObject.put("writerUid", setterNews.getWriterUid());
            jsonObject.put("editorUid", setterNews.getEditorUid());
            jsonObject.put("headline", setterNews.getHeadline());
            jsonObject.put("story", story);
            jsonObject.put("readTime", setterNews.getReadTime());
            jsonObject.put("timestamp", setterNews.getTimeStamp());
            jsonObject.put("read", false);
            jsonObject.put("link", setterNews.getLink());
            jsonObject.put("tag", setterNews.getTag());
            jsonObject.put("free", setterNews.isFree());

            jsonArrayStory.put(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        progressBar.setVisibility(View.GONE);

        return jsonArrayStory;
    }

    private JSONObject putNewsKey(SetterNews setterNews) {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("key", setterNews.getKey());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    private String encryptString(String sample) {
        List<String> listOG = new LinkedList<>(Arrays.asList(originalLetters));
        StringBuilder newString = new StringBuilder();
        char[] charArray = sample.toCharArray();
        for (char aCharArray : charArray) {
            String sam = String.valueOf(aCharArray);
            int index = listOG.indexOf(sam);
            if (index == -1)
                newString.append(aCharArray);
            else
                newString.append(encrypy1[index]);
        }

        return newString.toString();
    }

    private String decryptString(String sample) {
        List<String> listOG = new LinkedList<>(Arrays.asList(encrypy1));
        StringBuilder newString = new StringBuilder();
        char[] charArray = sample.toCharArray();
        for (char aCharArray : charArray) {
            String sam = String.valueOf(aCharArray);
            int index = listOG.indexOf(sam);
            if (index == -1)
                newString.append(aCharArray);
            else
                newString.append(originalLetters[index]);
        }

        return newString.toString();
    }

    public void finish(View view) {
        finish();
    }

}
