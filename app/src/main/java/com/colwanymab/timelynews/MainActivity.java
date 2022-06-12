package com.colwanymab.timelynews;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //TextView tvDaysLeft, tvCredits;
    RecyclerView recyclerView;

    String databaseRoot = "news";
    static List list;
    DBOperations dbOperations;
    SQLiteDatabase db;
    List<String> messageKeys;
    Adapter adapter;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    ConstraintSet constraintSetMain = new ConstraintSet();
    ConstraintSet constraintSetMenu = new ConstraintSet();
    ConstraintLayout constMain;
    boolean boolSwipe = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextAppearance(this, R.style.mainToolbarText);
        setSupportActionBar(toolbar);

        if (FirebaseAuth.getInstance().getCurrentUser() == null)
            FirebaseAuth.getInstance().signInAnonymously();

        constMain = findViewById(R.id.constMain);
        constraintSetMain.clone(constMain);
        constraintSetMenu.clone(this, R.layout.content_main_clone);

        prefs = getSharedPreferences(AppInfo.USER_INFO, Context.MODE_PRIVATE);
        dbOperations = new DBOperations(MainActivity.this);
        db = dbOperations.getWritableDatabase();
        list = new ArrayList();
        recyclerView = findViewById(R.id.recyclerView);
        messageKeys = dbOperations.getNewsKeys(db);
        //tvDaysLeft = findViewById(R.id.tvDaysLeft);
        //tvCredits = findViewById(R.id.tvCredits);

        //StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        new BackTask().execute();
    }

    public void swipe(View view) {TransitionManager.beginDelayedTransition(constMain);
        if (!boolSwipe) {
            constraintSetMenu.applyTo(constMain);
            boolSwipe = true;
        } else {
            constraintSetMain.applyTo(constMain);
            boolSwipe = false;
        }
    }

    public void openActivity(View view) {
        switch (view.getId()) {
            case R.id.ivSub:
                startActivity(new Intent(this, TopUp.class));
                break;
            case R.id.ivRec:
                startActivity(new Intent(this, Receive.class));
                break;
            case R.id.ivShare:
                startActivity(new Intent(this, ShareNews.class));
                break;
            case R.id.ivSettings:
                startActivity(new Intent(this, MenuApp.class));
                break;

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        prefs = getSharedPreferences(AppInfo.USER_INFO, Context.MODE_PRIVATE);
        long day = 86400000;
        long daysLeft = (prefs.getLong(AppInfo.LONG_EXPIRY_STAMP, 0) - System.currentTimeMillis()) / day;

        /*if (daysLeft >= 0)
            tvDaysLeft.setText(String.valueOf(daysLeft));

        if (prefs.getInt(AppInfo.INT_CREDITS, 0) >= 0)
            tvCredits.setText(String.valueOf(prefs.getInt(AppInfo.INT_CREDITS, 0)));*/

        if (daysLeft == 0) {
            editor = prefs.edit();
            if (System.currentTimeMillis() > prefs.getLong(AppInfo.LONG_EXPIRY_STAMP, 0))
                editor.putInt(AppInfo.INT_CREDITS, 0);
            editor.apply();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_menu) {
            startActivity(new Intent(MainActivity.this, MenuApp.class));
        }

        return super.onOptionsItemSelected(item);
    }

    public void launchRead(View view) {
        startActivity(new Intent(MainActivity.this, Read.class));
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

    private class BackTask extends AsyncTask<Void, SetterNews, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {
            Cursor cursor;
            cursor = dbOperations.getNews(db);
            int count = cursor.getCount();

            String key, writerUid, editorUid, headline, story, link, tag;
            long readTime, timeStamp;
            boolean read, free;

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
            loadInbox();
        }
    }

    private void loadInbox() {
        FirebaseDatabase.getInstance()
                .getReference()
                .child(databaseRoot)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists())
                            return;
                        long count = 0;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            SetterNews setter = snapshot.getValue(SetterNews.class);

                            if (messageKeys.contains(setter.getKey()))
                                continue;

                            count++;
                            list.add(setter);
                            new Insert(MainActivity.this).execute(setter);
                        }

                        if (count != 0) {
                            adapter = new Adapter(list);
                            recyclerView.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //
                    }
                });
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
            int layoutIdForListItem = R.layout.row_read_2;
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
            ImageView ivProfile/*, ivRead*/;
            TextView tvHeadline, tvIntro, tvTime, tvTag/*, tvFree*/;
            CardView cardRow;
            View viewTop, viewBottom;

            Holder(View itemView) {
                super(itemView);
                ivProfile = itemView.findViewById(R.id.ivProfile);
                tvHeadline = itemView.findViewById(R.id.tvHeadline);
                tvIntro = itemView.findViewById(R.id.tvIntro);
                tvTag = itemView.findViewById(R.id.tvTag);
                tvTime = itemView.findViewById(R.id.tvTime);
                //ivRead = itemView.findViewById(R.id.ivRead);
                cardRow = itemView.findViewById(R.id.cardRow);
                //tvFree = itemView.findViewById(R.id.tvFree);
                viewTop = itemView.findViewById(R.id.viewTop);
                viewBottom = itemView.findViewById(R.id.viewBottom);
            }

            void bind(final SetterNews setter) {
                String intro = setter.getStory().replace("\n", " ");
                if (setter.getStory().length() > 200)
                    intro = intro.substring(0, 200) + "...";
                /*if (setter.isRead())
                    ivRead.setVisibility(View.GONE);
                else
                    ivRead.setVisibility(View.VISIBLE);
                if (setter.isFree())
                    tvFree.setVisibility(View.VISIBLE);
                else
                    tvFree.setVisibility(View.GONE);*/

                try {
                    StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(setter.getLink());
                    Glide.with(MainActivity.this)
                            .using(new FirebaseImageLoader())
                            .load(storageReference)
                            .placeholder(R.drawable.placeholder)
                            .crossFade()
                            .into(ivProfile);
                } catch (Exception e) {
                    Glide.with(MainActivity.this)
                            .load(R.drawable.placeholder)
                            .crossFade()
                            .into(ivProfile);
                }

                tvHeadline.setText(setter.getHeadline());
                tvIntro.setText(intro);
                tvTag.setText(setter.getTag());
                tvTime.setText(getDate(setter.getTimeStamp()));
                cardRow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        click(setter);
                    }
                });

                if (getAdapterPosition() == 0) {
                    viewTop.setVisibility(View.VISIBLE);
                    viewBottom.setVisibility(View.GONE);
                } else if (getAdapterPosition() == listAdapter.size()-1) {
                    viewTop.setVisibility(View.GONE);
                    viewBottom.setVisibility(View.VISIBLE);
                } else {
                    viewTop.setVisibility(View.GONE);
                    viewBottom.setVisibility(View.GONE);
                }
            }

            private void click(SetterNews setter) {
                if (setter.isFree()) {
                    Intent intent = new Intent(MainActivity.this, Read.class);
                    intent.putExtra("from", "main");
                    intent.putExtra("position", getAdapterPosition());
                    startActivity(intent);
                } else {
                    if (!setter.isRead()) {
                        if (prefs.getInt(AppInfo.INT_CREDITS, 0) == 0) {
                            startActivity(new Intent(MainActivity.this, TopUp.class));
                            return;
                        } else {
                            editor = prefs.edit();
                            editor.putInt(AppInfo.INT_CREDITS, (prefs.getInt(AppInfo.INT_CREDITS, 0) - 1));
                            editor.apply();
                        }
                        setter.setRead(true);
                        adapter.notifyDataSetChanged();

                        ContentValues values = new ContentValues();
                        values.put(DBContract.News.READ, "yes");
                        db.update(DBContract.News.TABLE_NAME, values,
                                DBContract.News.KEY + "='" + setter.getKey() + "'", null);
                    }

                    Intent intent = new Intent(MainActivity.this, Read.class);
                    intent.putExtra("from", "main");
                    intent.putExtra("position", getAdapterPosition());
                    startActivity(intent);
                }
            }

            String getDate(long timeReceived) {
                String[] monthsSmall = {"", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);

                    calendar.setTimeInMillis(timeReceived);
                    return "" + calendar.get(Calendar.DAY_OF_MONTH) + "-" + monthsSmall[calendar.get(Calendar.MONTH) +1] + "-" +
                            String.valueOf(calendar.get(Calendar.YEAR)).substring(2, 4);
                //

            }

        }

    }

}
