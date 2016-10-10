package com.junyu.moneymanager;

import android.content.Intent;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.R.attr.password;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.userEmail) EditText userName;
    @BindView(R.id.userPassword) EditText userPassWord;
    @BindView(R.id.loginButton) TextView loginBtn;
    @BindView(R.id.signUpButton) TextView signUpBtn;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);
        ButterKnife.bind(this);

        firebaseAuth = FirebaseAuth.getInstance();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(intent);

            }
        });
//        testButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Map<String, String> post2 = new HashMap<>();
//                post2.put("name", "junyu");
//                FirebaseDatabase database = FirebaseDatabase.getInstance();
//                DatabaseReference myRef = database.getReference().child("Users");
//                String userID = myRef.push().getKey();
//                myRef.push().setValue(post2);
////                Map<String, HashMap<String, String>> post1 = new HashMap<>();
////                post1.put(userID, post2);
////                myRef.updateChildren(post2);
//            }
//        });
//        testButton2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                FirebaseDatabase database = FirebaseDatabase.getInstance();
//                DatabaseReference myRef = database.getReference().child("Users");
//                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        for (DataSnapshot child : dataSnapshot.getChildren()) {
//                            Timber.v(child.toString());
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
//            }
//        });


    }
}
