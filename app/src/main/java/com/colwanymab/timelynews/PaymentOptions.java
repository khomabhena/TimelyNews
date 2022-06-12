package com.colwanymab.timelynews;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

public class PaymentOptions extends AppCompatActivity implements View.OnClickListener  {

    View view1Click, view2Click;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_options);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        view1Click = (View) findViewById(R.id.view1Click);
        view2Click = (View) findViewById(R.id.view2Click);

        view1Click.setOnClickListener(this);
        view2Click.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.view1Click:
                String message = "_Shared via MyEvents App_\n\n" +
                        "Hey, Check out *MyEvents App* to see events happening in the Zimbabwe." +
                        "\nBuy and Sell event tickets directly on the app." +
                        "\n\n*Download* it today from: " +
                        "\nhttps://play.google.com/store/apps/details?id=tech.myevents.app&hl=en" + "\n\n" +
                        "Like us on Facebook at *MyEventsAppZW*\n" +
                        "https://m.facebook.com/MyEventsAppZW" +
                        "\n\nFollow us on Twitter *@myevents_app*\n" +
                        "https://mobile.twitter.com/myevents_app";
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, message);
                intent.setPackage("com.whatsapp");
                startActivity(intent);
                break;
            case R.id.view2Click:
                String messageT = "Via MyEvents App @myevents_app\n" +
                        "\nBuy and Sell event tickets directly on the app." +
                        "\n\nDOWNLOAD it today from: " +
                        "\nhttps://play.google.com/store/apps/details?id=tech.myevents.app&hl=en";

                if (messageT.length() > 280)
                    messageT = messageT.substring(0, 280);

                Intent tweetIntent = new Intent(Intent.ACTION_SEND);
                tweetIntent.putExtra(Intent.EXTRA_TEXT, messageT);
                tweetIntent.setType("text/plain");

                PackageManager packageManager = getPackageManager();
                List<ResolveInfo> resolveInfoList = packageManager.queryIntentActivities(tweetIntent, PackageManager.MATCH_DEFAULT_ONLY);

                boolean resolved = false;
                for (ResolveInfo resolveInfo: resolveInfoList) {
                    if (resolveInfo.activityInfo.packageName.startsWith("com.twitter.android")) {
                        tweetIntent.setClassName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name);
                        resolved = true;
                        break;
                    }
                }
                if (resolved) {
                    startActivity(tweetIntent);
                } else {
                    Intent i = new Intent();
                    i.putExtra(Intent.EXTRA_TEXT, messageT);
                    i.setAction(Intent.ACTION_VIEW);
                    i.setData(Uri.parse("https://twitter.com/intent/tweet?text=" + urlEncode(messageT)));
                    startActivity(i);
                    Toast.makeText(getApplicationContext(), "Twitter app not found", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }






    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
