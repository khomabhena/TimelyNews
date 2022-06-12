package com.colwanymab.timelynews;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class ShareNews extends AppCompatActivity {

    RecyclerView recyclerView;
    ProgressBar progressBar;

    static List list;
    List listChecked;
    List listToShare;
    DBOperations dbOperations;
    SQLiteDatabase db;
    List<String> messageKeys;
    Adapter adapter;
    String headlines = "";

    SharedPreferences prefs;
    boolean allChecked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_news);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        prefs = getSharedPreferences(AppInfo.USER_INFO, Context.MODE_PRIVATE);
        dbOperations = new DBOperations(ShareNews.this);
        db = dbOperations.getWritableDatabase();
        list = new ArrayList();
        listToShare = new ArrayList();
        listChecked = new ArrayList();
        recyclerView = findViewById(R.id.recyclerView);
        messageKeys = dbOperations.getNewsKeys(db);
        progressBar = findViewById(R.id.progressBar);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        new BackTask().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_check, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_menu) {
            if (allChecked) {
                listChecked = new ArrayList();
                listToShare = new ArrayList();
                allChecked = false;
            } else {
                listChecked = messageKeys;
                listToShare = list;
                allChecked = true;
            }

            adapter.notifyDataSetChanged();
        }

        if (id == R.id.action_share) {
            JSONArray jsonArray = putNewsStories(listToShare);

            JSONObject jsonObject = new JSONObject();
            try {
                //jsonObject.put("timelyStoriesKeys", jsonArrayKey);
                jsonObject.put("timelyNewsStories", jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String link = "https://play.google.com/store/apps/details?id=com.colwanymab.timelynews";
            String details = "1) Copy this text.\n" +
                    "2) Open the app.\n" +
                    "3) Go to MENU.\n" +
                    "4) Click Receive Stories and then paste the text.\n" +
                    "\nDownload app here. " + link + "\n\n*Headlines*" + headlines;
            String message = details + "\n\n" +encryptString(jsonObject.toString());

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
        }

        return super.onOptionsItemSelected(item);
    }

    private JSONArray putNewsStoriesl(List list) {
        JSONArray jsonArrayStory = new JSONArray();

        for (int x = 0; x < list.size(); x++) {
            SetterNews setterNews = (SetterNews) list.get(x);
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("key", setterNews.getKey());
                jsonObject.put("writerUid", setterNews.getWriterUid());
                jsonObject.put("editorUid", setterNews.getEditorUid());
                jsonObject.put("headline", setterNews.getHeadline());
                jsonObject.put("story", setterNews.getStory().substring(0, setterNews.getStory().length() - 1));
                jsonObject.put("readTime", setterNews.getReadTime());
                jsonObject.put("timestamp", setterNews.getTimeStamp());
                jsonObject.put("read", false);
                jsonObject.put("link", setterNews.getLink());
                jsonObject.put("tag", setterNews.getTag());
                jsonObject.put("free", setterNews.isFree());
            } catch (JSONException e) {
                //
            }
            jsonArrayStory.put(jsonObject);
        }

        progressBar.setVisibility(View.GONE);

        return jsonArrayStory;
    }

    private String encryptString(String sample) {
        List<String> listOG = new LinkedList<>(Arrays.asList(Read.originalLetters));
        StringBuilder newString = new StringBuilder();
        char[] charArray = sample.toCharArray();
        for (char aCharArray : charArray) {
            String sam = String.valueOf(aCharArray);
            int index = listOG.indexOf(sam);
            if (index == -1)
                newString.append(aCharArray);
            else
                newString.append(Read.encrypy1[index]);
        }

        return newString.toString();
    }

    private class BackTask extends AsyncTask<Void, SetterNews, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {
            Cursor cursor;
            cursor = dbOperations.getNews(db);
            int count = cursor.getCount();

            String key, writerUid, editorUid, headline, story, link, tag;
            long readTime, timeStamp;
            boolean read, free;
            int countHead = 1;

            while (cursor.moveToNext()) {
                key = cursor.getString(cursor.getColumnIndex(DBContract.News.KEY));
                writerUid = cursor.getString(cursor.getColumnIndex(DBContract.News.WRITER_UID));
                editorUid = cursor.getString(cursor.getColumnIndex(DBContract.News.EDITOR_UID));
                headline = cursor.getString(cursor.getColumnIndex(DBContract.News.HEADLINE));
                story = cursor.getString(cursor.getColumnIndex(DBContract.News.STORY));
                readTime = Long.parseLong(cursor.getString(cursor.getColumnIndex(DBContract.News.READ_TIME)));
                timeStamp = Long.parseLong(cursor.getString(cursor.getColumnIndex(DBContract.News.TIMESTAMP)));
                read = cursor.getString(cursor.getColumnIndex(DBContract.News.READ)).equals("yes");
                free = cursor.getString(cursor.getColumnIndex(DBContract.News.FREE)).equals("yes");
                link = cursor.getString(cursor.getColumnIndex(DBContract.News.LINK));
                tag = cursor.getString(cursor.getColumnIndex(DBContract.News.TAG));

                SetterNews setter = new SetterNews(key, writerUid, editorUid, headline, story,
                        readTime, timeStamp, read, link, tag, free);

                if (countHead < 6) {
                    headlines += "\n"+ countHead + ") *" + setter.getHeadline() + "*.";
                    countHead++;
                }

                publishProgress(setter);
            }

            return count;
        }

        @Override
        protected void onProgressUpdate(SetterNews... values) {
            list.add(values[0]);
        }

        @Override
        protected void onPostExecute(Integer count) {
            if (count != 0) {
                adapter = new Adapter(list);
                recyclerView.setAdapter(adapter);
            }
        }
    }

    class Adapter extends RecyclerView.Adapter<Adapter.Holder> {

        private List listAdapter;
        Toast toast;

        public Adapter(List listAdapter) {
            this.listAdapter = listAdapter;
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {

            Context context = parent.getContext();
            int layoutIdForListItem = R.layout.row_share;
            LayoutInflater inflater = LayoutInflater.from(context);
            boolean shouldAttachToParentImmediately = false;

            View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
            Holder holder = new Holder(view);

            return holder;
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            holder.bind((SetterNews) listAdapter.get(position));
        }

        @Override
        public int getItemCount() {
            return listAdapter.size();
        }

        class Holder extends RecyclerView.ViewHolder {
            ImageView ivProfile,  ivRead;
            TextView tvHeadline, tvIntro, tvTag, tvTime;
            CardView cardRow;
            CheckBox checkBox;

            Holder(View itemView) {
                super(itemView);
                ivProfile = itemView.findViewById(R.id.ivProfile);
                tvHeadline = itemView.findViewById(R.id.tvHeadline);
                tvIntro = itemView.findViewById(R.id.tvIntro);
                tvTag = itemView.findViewById(R.id.tvTag);
                tvTime = itemView.findViewById(R.id.tvTime);
                ivRead = itemView.findViewById(R.id.ivRead);
                cardRow = itemView.findViewById(R.id.cardRow);
                checkBox = itemView.findViewById(R.id.checkBox);
            }

            void bind(final SetterNews setter) {
                tvHeadline.setText(setter.getHeadline());
                tvTag.setText(setter.getTag());
                tvTime.setText(getDate(setter.getTimeStamp()));

                if (listChecked.contains(setter.getKey()))
                    checkBox.setChecked(true);
                else
                    checkBox.setChecked(false);

                checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (checkBox.isChecked()) {
                            listChecked.add(setter.getKey());
                            listToShare.add(setter);
                        } else {
                            if (listChecked.contains(setter.getKey())) {
                                int index = listChecked.indexOf(setter.getKey());
                                listChecked.remove(setter.getKey());
                                listToShare.remove(index);
                            }
                        }
                    }
                });
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
        }

    }

}
