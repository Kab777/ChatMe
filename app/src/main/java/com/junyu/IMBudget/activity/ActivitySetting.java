package com.junyu.IMBudget.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.junyu.IMBudget.R;

import java.io.ByteArrayOutputStream;
import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static android.R.attr.bitmap;


public class ActivitySetting extends AppCompatActivity {
    @BindView(R.id.testImg) ImageView imageView;
    private FirebaseStorage fbStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fbStore = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        StorageReference storageRef = fbStore.getReferenceFromUrl("gs://imbudget-55727.appspot.com");

// Create a reference to "mountains.jpg"
        StorageReference mountainsRef = storageRef.child("mountains.jpg");

// Create a reference to 'images/mountains.jpg'
        StorageReference mountainImagesRef = storageRef.child("images/mountains.jpg");

// While the file names are the same, the references point to different files
        mountainsRef.getName().equals(mountainImagesRef.getName());    // true
        mountainsRef.getPath().equals(mountainImagesRef.getPath());    // false


//        String path = Environment.getExternalStorageDirectory().toString() + "/Pictures/Screenshots";
//        Log.d("Files", "Path: " + path);
//        File directory = new File(path);
//        File[] files = directory.listFiles();
//        Log.d("Files", "Size: " + files.length);
//        for (int i = 0; i < files.length; i++) {
//            Log.d("Files", "FileName:" + files[i].getName());
//        }
//
//        Uri file = Uri.fromFile(new File("/storage/emulated/0/Pictures/Screenshots/Screenshot_20160601-171809.png"));
//        StorageReference riversRef = storageRef.child("images/" + file.getLastPathSegment());
//        UploadTask uploadTask = riversRef.putFile(file);
//
//// Register observers to listen for when the download is done or if it fails
//        uploadTask.addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                // Handle unsuccessful uploads
//            }
//        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
//                Uri downloadUrl = taskSnapshot.getDownloadUrl();
//
//                Timber.v(downloadUrl.toString());
//            }
//        });
    }

    public void upload(View view) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
