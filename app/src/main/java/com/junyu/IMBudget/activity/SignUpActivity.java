package com.junyu.IMBudget.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.junyu.IMBudget.MMConstant;
import com.junyu.IMBudget.R;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

import static com.junyu.IMBudget.MMConstant.EMAIL;
import static com.junyu.IMBudget.MMConstant.NAME;
import static com.junyu.IMBudget.MMConstant.PREFERENCE_NAME;

/**
 * Created by Junyu on 10/9/2016.
 */

public class SignUpActivity extends AppCompatActivity {
    @BindView(R.id.userName) EditText userName;
    @BindView(R.id.userEmail) EditText userEmail;
    @BindView(R.id.userPassword) EditText userPassWord;
    @BindView(R.id.signUpButton) Button signUpButton;
    private FirebaseAuth firebaseAuth;
    private String userPersonalName = "";
    private String userEmailAddress = "";
    SharedPreferences sharedpreferences;

    @OnClick(R.id.signUpButton)
    public void signUp() {
        String password = userPassWord.getText().toString();
        userEmailAddress = userEmail.getText().toString().trim();
        userPersonalName = userName.getText().toString().trim();


        int spaceIndex = userPersonalName.indexOf(" ");

        if (spaceIndex == -1) {
            Toast toast = Toast.makeText(SignUpActivity.this,
                    "Please enter first and last names.", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else if (password.isEmpty() || userEmailAddress.isEmpty() || userPersonalName.isEmpty()) {
            Toast.makeText(SignUpActivity.this, "can't be empty",
                    Toast.LENGTH_SHORT).show();
        } else {
            firebaseAuth.createUserWithEmailAndPassword(userEmailAddress, password)
                    .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {

                                Timber.v(task.getException().toString());
                                Toast.makeText(SignUpActivity.this, task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                FirebaseUser fbUser = task.getResult().getUser();
                                String userId = fbUser.getUid().toString();

                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference myRef = database.getReference().child(MMConstant.USERS).child(userId);
                                Map<String, String> userInfo = new HashMap<>();
                                userInfo.put(NAME, userPersonalName);
                                userInfo.put(EMAIL,userEmailAddress);
                                myRef.setValue(userInfo);

                                database.getReference().child(MMConstant.ID_EMAIL_MAP).child(userId).setValue(userEmailAddress);

                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(userPersonalName).build();
                                fbUser.updateProfile(profileUpdates);

                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.putString(MMConstant.NAME, userPersonalName);
                                editor.putString(MMConstant.USER_ID, userId);
                                editor.putString(MMConstant.EMAIL, userEmailAddress);
                                editor.commit();

                                Toast.makeText(SignUpActivity.this, "sign up successful",
                                        Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(SignUpActivity.this, NewMainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
        }
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        firebaseAuth = FirebaseAuth.getInstance();
        sharedpreferences = getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);

    }
}
