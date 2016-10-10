package com.junyu.moneymanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

import static android.R.attr.password;
import static com.junyu.moneymanager.MMConstant.PREFERENCE_NAME;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.userEmail) EditText userEmail;
    @BindView(R.id.userPassword) EditText userPassWord;
    @BindView(R.id.loginButton) TextView loginBtn;
    @BindView(R.id.signUpButton) TextView signUpBtn;

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
            Intent intent = new Intent(MainActivity.this, MainPage.class);
            startActivity(intent);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            finish();
            return;
        }


        firebaseAuth = FirebaseAuth.getInstance();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = userPassWord.getText().toString();
                String email = userEmail.getText().toString().trim();
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "can't be empty",
                            Toast.LENGTH_SHORT).show();
                } else {
                    firebaseAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {


                                    // If sign in fails, display a message to the user. If sign in succeeds
                                    // the auth state listener will be notified and logic to handle the
                                    // signed in user can be handled in the listener.
                                    if (!task.isSuccessful()) {
                                        Timber.v("failed");
                                    } else {
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString(MMConstant.USER_ID, task.getResult().getUser().getUid());
                                        editor.commit();

                                        Intent intent = new Intent(MainActivity.this, MainPage.class);
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
