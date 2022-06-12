package com.colwanymab.timelynews;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class TopUp extends AppCompatActivity {

    TextView tvAvailableCredits, tvExpiryDays;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    private static final int REQUEST_READ_SMS = 1884;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_up);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        prefs = getSharedPreferences(AppInfo.USER_INFO, Context.MODE_PRIVATE);
        tvAvailableCredits = findViewById(R.id.tvAvailableCredits);
        tvExpiryDays = findViewById(R.id.tvExpiryDays);


    }

    @Override
    protected void onResume() {
        super.onResume();
        prefs = getSharedPreferences(AppInfo.USER_INFO, Context.MODE_PRIVATE);
        long day = 86400000;
        long daysLeft = (prefs.getLong(AppInfo.LONG_EXPIRY_STAMP, 0) - System.currentTimeMillis()) / day;

        if (daysLeft >= 0)
            tvExpiryDays.setText(String.valueOf(daysLeft));

        if (prefs.getInt(AppInfo.INT_CREDITS, 0) >= 0)
            tvAvailableCredits.setText(String.valueOf(prefs.getInt(AppInfo.INT_CREDITS, 0)));

        if (daysLeft == 0) {
            editor = prefs.edit();
            if (System.currentTimeMillis() > prefs.getLong(AppInfo.LONG_EXPIRY_STAMP, 0))
                editor.putInt(AppInfo.INT_CREDITS, 0);
            editor.apply();
        }
    }

    public void dialer(View view) {
        String harsh = Uri.encode("#");
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:*151*1*1*0774876886*1" + harsh));
        startActivity(intent);
    }

    public void pickMessage(View view) {
        if (Build.VERSION.SDK_INT >= 23) {
            boolean hasPermission = (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED);
            if (!hasPermission) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.READ_SMS},
                        REQUEST_READ_SMS);
            } else {
                startActivity(new Intent(TopUp.this, ConfirmationMessage.class));
            }
        } else {
            startActivity(new Intent(TopUp.this, ConfirmationMessage.class));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_READ_SMS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //reload my activity with permission granted or use the features that required the permission
                    startActivity(new Intent(TopUp.this, ConfirmationMessage.class));
                } else {
                    Toast.makeText(getApplicationContext(), "Allow app to read the EcoCash message", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
}
