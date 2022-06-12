package com.colwanymab.timelynews;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

public class Profile extends AppCompatActivity {

    TextView tvName, tvEmail, tvNumber, tvStoriesPublished, tvTotalPay;
    RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvTime);
        tvNumber = findViewById(R.id.tvNumber);
        tvStoriesPublished = findViewById(R.id.tvStoriesPublished);
        tvTotalPay = findViewById(R.id.tvTotalPay);
        recyclerView = findViewById(R.id.recyclerView);

    }

}
