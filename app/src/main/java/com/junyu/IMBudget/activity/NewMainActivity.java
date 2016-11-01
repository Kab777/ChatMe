package com.junyu.IMBudget.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.junyu.IMBudget.MMConstant;
import com.junyu.IMBudget.MMUserPreference;
import com.junyu.IMBudget.R;
import com.junyu.IMBudget.fragment.FragmentChat;
import com.junyu.IMBudget.fragment.FragmentFriendRequests;
import com.junyu.IMBudget.widgets.CpCircleImage;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static com.junyu.IMBudget.MMConstant.FRIENDS;
import static com.junyu.IMBudget.MMConstant.IMAGE_URL;
import static com.junyu.IMBudget.MMConstant.ONLINE;
import static com.junyu.IMBudget.MMConstant.PROFILE_IMAGES;

/**
 * Created by Junyu on 10/17/2016.
 */

public class NewMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    @BindView(R.id.nav_view) NavigationView navigationView;
    @BindView(R.id.progress) ProgressWheel progressWheel;


    private static final int IMAGE_REQUEST = 77;
    private DatabaseReference fireDb;
    private StorageReference fireStore;
    private String userId;
    private File imageFile;
    private CpCircleImage profileImg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        fireDb = FirebaseDatabase.getInstance().getReference();
        fireStore = FirebaseStorage.getInstance().getReferenceFromUrl("gs://imbudget-55727.appspot.com");

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        View navHeader = navigationView.getHeaderView(0);
        TextView userName = (TextView) navHeader.findViewById(R.id.userName);
        TextView userEmail = (TextView) navHeader.findViewById(R.id.userEmail);
        profileImg = (CpCircleImage) navHeader.findViewById(R.id.userImage);
        userName.setText(MMUserPreference.getUserName(this));
        userEmail.setText(MMUserPreference.getUserEmail(this));

        userId = MMUserPreference.getUserId(this);
        navigationView.setNavigationItemSelectedListener(this);
        notifyFriendOnlineStatus(true);

        /*
         **load default fragment
         */
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.frame, new FragmentChat());
        tx.commit();
        setTitle(navigationView.getMenu().getItem(0).getTitle());

        String imgUrl = MMUserPreference.getUserImg(this);
        Timber.v(imgUrl);
        if (imgUrl != null && !imgUrl.equals("")) {
            Picasso.with(this).load(imgUrl).into(profileImg);
        }
//        profileImg.setImageBitmap(profilePic);

        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), IMAGE_REQUEST);
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        selectDrawerItem(item);
        return false;
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Class fragmentClass;
        switch (menuItem.getItemId()) {
//            case R.id.nav_home:
//                fragmentClass = FragmentHome.class;
//                break;
            case R.id.navChat:
                fragmentClass = FragmentChat.class;
                break;
            case R.id.navFriendRequests:
                fragmentClass = FragmentFriendRequests.class;
                break;
            case R.id.navShare:
                shareApp();
                return;
//            case R.id.navSetting:
//                Intent settingIntent = new Intent(NewMainActivity.this, ActivitySetting.class);
//                startActivity(settingIntent);
//                return;
            case R.id.navLogOut:
                MMUserPreference.cleanSharedPreferences(this);
                //Firebase
                FirebaseAuth.getInstance().signOut();
                notifyFriendOnlineStatus(false);
                Intent intent = new Intent(NewMainActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                return;
            default:
                fragmentClass = FragmentChat.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame, fragment).commit();

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        drawer.closeDrawers();
    }


    private void notifyFriendOnlineStatus(final Boolean ifOnline) {
        final DatabaseReference users = fireDb.child(MMConstant.USERS);
        DatabaseReference myFriends = users.child(userId).child(FRIENDS);
        myFriends.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Timber.v(child.toString());
                    users.child(child.getKey()).child(FRIENDS).child(userId).child(ONLINE).setValue(ifOnline);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void shareApp() {
        String msg = "Let's meet on ChatMe !\n";
        String url = "https://play.google.com/store/apps/details?id=com.junyu.IMBudget";
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, msg + url);
        intent.setType("text/plain");
        startActivity(Intent.createChooser(intent, "Choose a way to share"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                progressWheel.setVisibility(View.VISIBLE);
                imageFile = new File(getFilesDir(), userId + ".jpg");

                try (
                        InputStream iStream = getContentResolver().openInputStream(data.getData());
                        FileOutputStream oStream = new FileOutputStream(imageFile)) {
                    copyStream(iStream, oStream);
                    uploadFile(imageFile);


                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (resultCode == Activity.RESULT_CANCELED) {

            }
        }
    }

    private void uploadFile(File imageFile) {

        final Bitmap original = BitmapFactory.decodeFile(imageFile.getPath());
        final Bitmap compressed = Bitmap.createScaledBitmap(original,
                100, 100, false);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        compressed.compress(Bitmap.CompressFormat.JPEG, 100, out);

        StorageReference riversRef = fireStore.child(PROFILE_IMAGES).child(imageFile.getName());
        UploadTask uploadTask = riversRef.putStream(new ByteArrayInputStream(out.toByteArray()));

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(NewMainActivity.this, "Upload failed, please check your Internet Connection",
                        Toast.LENGTH_SHORT).show();
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressWheel.setVisibility(View.GONE);
                Toast.makeText(NewMainActivity.this, "Profile image updated successfully",
                        Toast.LENGTH_SHORT).show();
                profileImg.setImageBitmap(compressed);
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                String imgUrl = taskSnapshot.getDownloadUrl().toString();
                notifyFriends(imgUrl);
            }
        });
    }

    private void notifyFriends(final String imgUrl) {
        final DatabaseReference users = fireDb.child(MMConstant.USERS);
        //update myself
        users.child(userId).child(IMAGE_URL).setValue(imgUrl);
        DatabaseReference myFriends = users.child(userId).child(FRIENDS);
        //update friend
        myFriends.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Timber.v(child.toString());
                    users.child(child.getKey()).child(FRIENDS).child(userId).child(IMAGE_URL).setValue(imgUrl);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(Uri.parse(imgUrl))
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Timber.v("self Auth img updated");
                            MMUserPreference.updateImg(NewMainActivity.this, imgUrl);
                        }
                    }
                });
    }

    public static void copyStream(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }
}
