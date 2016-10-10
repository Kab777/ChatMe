package com.junyu.moneymanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static android.R.attr.name;
import static com.junyu.moneymanager.MMConstant.USER_ID;

/**
 * Created by Junyu on 10/9/2016.
 */

public class SignUpActivity extends AppCompatActivity {
    @BindView(R.id.userName) EditText userName;
    @BindView(R.id.userEmail) EditText userEmail;
    @BindView(R.id.userPassword) EditText userPassWord;
    @BindView(R.id.signUpButton) TextView signUpButton;
    private FirebaseAuth firebaseAuth;
    private String userPersonalName = "";
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        firebaseAuth = FirebaseAuth.getInstance();
        sharedpreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        signUpButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                String password = userPassWord.getText().toString();
                                                String email = userEmail.getText().toString().trim();
                                                userPersonalName = userName.getText().toString().trim();


                                                int spaceIndex = userPersonalName.indexOf(" ");

                                                if (spaceIndex == -1) {
                                                    Toast toast = Toast.makeText(SignUpActivity.this,
                                                            "Please enter first and last names.", Toast.LENGTH_SHORT);
                                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                                    toast.show();
                                                } else if (password.isEmpty() || email.isEmpty() || userPersonalName.isEmpty()) {
                                                    Toast.makeText(SignUpActivity.this, "can't be empty",
                                                            Toast.LENGTH_SHORT).show();
                                                } else {
                                                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                                                            .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                                    if (!task.isSuccessful()) {

                                                                        Timber.v(task.getException().toString());
                                                                        Toast.makeText(SignUpActivity.this, task.getException().getMessage(),
                                                                                Toast.LENGTH_SHORT).show();
                                                                    } else {
                                                                        String userId = task.getResult().getUser().getUid().toString();
                                                                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                                                                        DatabaseReference myRef = database.getReference().child(MMConstant.USERS).child(userId);
                                                                        Map<String, String> userInfo = new HashMap<>();
                                                                        userInfo.put(MMConstant.NAME, userPersonalName);
                                                                        myRef.setValue(userInfo);

                                                                        SharedPreferences.Editor editor = sharedpreferences.edit();
                                                                        editor.putString(MMConstant.NAME, userPersonalName);
                                                                        editor.putString(MMConstant.USER_ID, userId);
                                                                        editor.commit();

                                                                        Toast.makeText(SignUpActivity.this, "signUP successFul",
                                                                                Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });
                                                }
                                            }
                                        }

        );
    }


}
