package com.colwanymab.timelynews;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kosalgeek.android.photoutil.GalleryPhoto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Publish extends AppCompatActivity {

    EditText etID, etStory, etHeadline;
    ImageView ivImage;
    AutoCompleteTextView autoTag;
    ProgressBar progressBar;
    CheckBox checkBox;

    GalleryPhoto galleryPhoto;
    final int GALLERY_REQUEST = 27277;
    private static final int REQUEST_WRITE_IMAGE = 1994;
    private static String PROFILE_LINK = "";

    String databaseRoot = "news";
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    boolean isFree = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        prefs = getSharedPreferences(AppInfo.USER_INFO, Context.MODE_PRIVATE);
        galleryPhoto = new GalleryPhoto(this);
        ivImage = findViewById(R.id.ivImage);

        etID = findViewById(R.id.etID);
        etStory = findViewById(R.id.etStory);
        etHeadline = findViewById(R.id.etHeadline);
        progressBar = findViewById(R.id.progressBar);
        autoTag= findViewById(R.id.autoTag);
        checkBox = findViewById(R.id.checkBox);

        etStory.setText(prefs.getString(AppInfo.PUBLISH_STORY, ""));
        etHeadline.setText(prefs.getString(AppInfo.PUBLISH_HEADLINE, ""));
        etID.setText(prefs.getString(AppInfo.PUBLISH_UID, ""));
        insertImage();

        FirebaseDatabase.getInstance().getReference()
                .child(databaseRoot)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists())
                            return;
                        String[] array = new String[]{""};
                        List<String> list = new LinkedList<>(Arrays.asList(array));
                        for (DataSnapshot snap: dataSnapshot.getChildren()) {
                            SetterNews setter = snap.getValue(SetterNews.class);
                            list.add(setter.getTag());
                        }
                        array = list.toArray(new String[list.size()]);
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Publish.this, android.R.layout.simple_list_item_1, array);
                        autoTag.setAdapter(arrayAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFree) {
                    checkBox.setChecked(false);
                    isFree = false;
                } else {
                    checkBox.setChecked(true);
                    isFree = true;
                }
            }
        });
    }

    private void insertImage() {
        try {
            StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(PROFILE_LINK);
            Glide.with(Publish.this)
                    .using(new FirebaseImageLoader())
                    .load(storageReference)
                    .crossFade()
                    .into(ivImage);
        } catch (Exception e) {
            //
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        editor = prefs.edit();
        editor.putString(AppInfo.PUBLISH_UID, etID.getText().toString());
        editor.putString(AppInfo.PUBLISH_STORY, etStory.getText().toString());
        editor.putString(AppInfo.PUBLISH_HEADLINE, etHeadline.getText().toString());
        editor.apply();
    }

    public void publish(View view) {
        String writerId = etID.getText().toString();
        String headline = etHeadline.getText().toString();
        String story = etStory.getText().toString();
        String tag = autoTag.getText().toString();

        if (writerId.equals("") || headline.equals("") || story.equals("") || tag.equals(""))
            Toast.makeText(getApplicationContext(), "Fill all fields", Toast.LENGTH_SHORT).show();
        else {
            progressBar.setVisibility(View.VISIBLE);
            String key = FirebaseDatabase.getInstance().getReference().child(databaseRoot).push().getKey(),
                    editorUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            long timeStamp = System.currentTimeMillis();

            SetterNews setterNews = new SetterNews(key, writerId, editorUid, headline,
                    story, 0, timeStamp, false, PROFILE_LINK, tag, isFree);

            FirebaseDatabase.getInstance().getReference()
                    .child(databaseRoot)
                    .child(key)
                    .setValue(setterNews)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressBar.setVisibility(View.GONE);
                                ivImage.setImageResource(0);
                                etID.setText(""); etHeadline.setText(""); etStory.setText("");

                                PROFILE_LINK = "";
                                editor = prefs.edit();
                                editor.putString(AppInfo.PUBLISH_UID, "");
                                editor.putString(AppInfo.PUBLISH_HEADLINE, "");
                                editor.putString(AppInfo.PUBLISH_STORY, "");
                                editor.apply();

                                Toast.makeText(getApplicationContext(), "Published", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    public void pickImage(View view) {
        getImageFromStorage();
    }


    private void getImageFromStorage() {
        if (Build.VERSION.SDK_INT >= 23) {
            boolean hasPermission = (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
            if (!hasPermission) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_WRITE_IMAGE);
            } else {
                startActivityForResult(galleryPhoto.openGalleryIntent(), GALLERY_REQUEST);
            }
        } else {
            startActivityForResult(galleryPhoto.openGalleryIntent(), GALLERY_REQUEST);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            handlePosterResult(data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_WRITE_IMAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //reload my activity with permission granted or use the features that required the permission
                    startActivityForResult(galleryPhoto.openGalleryIntent(), GALLERY_REQUEST);
                } else {
                    Toast.makeText(getApplicationContext(), "Allowed access to your storage.", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void handlePosterResult(Intent data) {
        Uri uri = data.getData();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        final StorageReference imageRef = storageRef.child("meal-pics/"+ uri.getLastPathSegment());
        UploadTask uploadTask = imageRef.putFile(uri);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(getApplicationContext(), "Image UploadFailed", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-accType, and download URL.
                Uri downloadUrl = taskSnapshot.getUploadSessionUri();
                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        PROFILE_LINK = uri.toString();

                        try {
                            StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(uri.toString());
                            Glide.with(Publish.this)
                                    .using(new FirebaseImageLoader())
                                    .load(storageReference)
                                    .crossFade()
                                    .into(ivImage);
                        } catch (Exception e) {
                            //
                        }
                    }
                });
            }
        });

    }
}
