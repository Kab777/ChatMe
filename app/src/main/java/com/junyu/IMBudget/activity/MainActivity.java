package com.junyu.IMBudget.activity;

import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.junyu.IMBudget.MMConstant;
import com.junyu.IMBudget.MMUserPreference;
import com.junyu.IMBudget.R;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.junyu.IMBudget.MMConstant.PREFERENCE_NAME;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.userEmail) EditText userEmail;
    @BindView(R.id.userPassword) EditText userPassWord;
    @BindView(R.id.loginButton) Button loginBtn;
    @BindView(R.id.signUpButton) Button signUpBtn;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authListener;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);
        ButterKnife.bind(this);

        sharedPreferences = getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);


        if (MMUserPreference.ifUserRegistered(this)) {
            Intent intent = new Intent(MainActivity.this, NewMainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            finish();
            return;
        }


        firebaseAuth = FirebaseAuth.getInstance();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = userPassWord.getText().toString();
                final String email = userEmail.getText().toString().trim();
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "can't be empty",
                            Toast.LENGTH_SHORT).show();
                } else {
                    firebaseAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(MainActivity.this, task.getException().getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString(MMConstant.USER_ID, task.getResult().getUser().getUid());
                                        editor.putString(MMConstant.EMAIL, email);
                                        editor.putString(MMConstant.NAME, task.getResult().getUser().getDisplayName());
                                        Uri imgUrl = task.getResult().getUser().getPhotoUrl();
                                        if (imgUrl == null) {
                                            // do nothing, user hasnt submitted yet
                                            editor.putString(MMConstant.PROFILE_IMAGE, "");
                                        } else {
                                            editor.putString(MMConstant.PROFILE_IMAGE, imgUrl.toString());
                                        }


                                        editor.commit();

                                        Intent intent = new Intent(MainActivity.this, NewMainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }


                                }
                            });
                }


            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(intent);

            }
        });


    }
}
