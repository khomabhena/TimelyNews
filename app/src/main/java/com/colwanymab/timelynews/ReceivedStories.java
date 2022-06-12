package com.colwanymab.timelynews;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ReceivedStories extends AppCompatActivity {

    RecyclerView recyclerView;

    String databaseRoot = "developingStories";
    static List list;
    DBOperations dbOperations;
    SQLiteDatabase db;
    List<String> messageKeys;
    Adapter adapter;

    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_received_stories);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        prefs = getSharedPreferences(AppInfo.USER_INFO, Context.MODE_PRIVATE);
        dbOperations = new DBOperations(ReceivedStories.this);
        list = new ArrayList();
        recyclerView = findViewById(R.id.recyclerView);
        db = dbOperations.getWritableDatabase();
        messageKeys = dbOperations.getNewsKeys(db);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);


        loadInbox();
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
            int layoutIdForListItem = R.layout.row_read;
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

            Holder(View itemView) {
                super(itemView);
                ivProfile = itemView.findViewById(R.id.ivProfile);
                tvHeadline = itemView.findViewById(R.id.tvHeadline);
                tvIntro = itemView.findViewById(R.id.tvIntro);
                tvTag = itemView.findViewById(R.id.tvTag);
                tvTime = itemView.findViewById(R.id.tvTime);
                ivRead = itemView.findViewById(R.id.ivRead);
                cardRow = itemView.findViewById(R.id.cardRow);
            }

            void bind(final SetterNews setter) {
                String intro = setter.getStory();
                String id = "Writer ID: " + setter.getWriterUid();
                if (setter.getStory().length() > 200)
                    intro = setter.getStory().substring(0, 200) + "...";

                try {
                    StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(setter.getLink());
                    Glide.with(ReceivedStories.this)
                            .using(new FirebaseImageLoader())
                            .load(storageReference)
                            .crossFade()
                            .into(ivProfile);
                } catch (Exception e) {
                    //
                }

                tvHeadline.setText(id);
                tvIntro.setText(intro);
                tvTime.setText(getDate(setter.getTimeStamp()));
                tvHeadline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip;
                        clip = ClipData.newPlainText("label", setter.getWriterUid());

                        assert clipboard != null;
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(getApplicationContext(), "ID copied to clipboard", Toast.LENGTH_SHORT).show();
                    }
                });
                cardRow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(ReceivedStories.this, Read.class);
                        intent.putExtra("from", "received");
                        intent.putExtra("position", getAdapterPosition());
                        startActivity(intent);

                        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip;
                        clip = ClipData.newPlainText("label", setter.getStory());

                        assert clipboard != null;
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(getApplicationContext(), "Story copied to clipboard", Toast.LENGTH_SHORT).show();
                    }
                });


                /**/
            }
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
                return "" + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ", " +
                        calendar.get(Calendar.DAY_OF_MONTH) + "-" + monthsSmall[calendar.get(Calendar.MONTH) +1] + "-" +
                        calendar.get(Calendar.YEAR);
            }
            //

        }

    }

}
