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
import com.junyu.IMBudget.MMUserPreference;
import com.junyu.IMBudget.R;
import com.junyu.IMBudget.utils.Time;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

import static com.junyu.IMBudget.MMConstant.CONTENT;
import static com.junyu.IMBudget.MMConstant.MESSAGES;
import static com.junyu.IMBudget.MMConstant.MUNDO_ID;
import static com.junyu.IMBudget.MMConstant.MUNDO_IMAGE;
import static com.junyu.IMBudget.MMConstant.MUNDO_NAME;
import static com.junyu.IMBudget.MMConstant.CHAT_ID;
import static com.junyu.IMBudget.MMConstant.EMAIL;
import static com.junyu.IMBudget.MMConstant.FRIENDS;
import static com.junyu.IMBudget.MMConstant.IMAGE_URL;
import static com.junyu.IMBudget.MMConstant.NAME;
import static com.junyu.IMBudget.MMConstant.ONLINE;
import static com.junyu.IMBudget.MMConstant.PREFERENCE_NAME;
import static com.junyu.IMBudget.MMConstant.TIMESTAMP;
import static com.junyu.IMBudget.MMConstant.USER_ID;

/**
 * Created by Junyu on 10/9/2016.
 */

public class SignUpActivity extends AppCompatActivity {
    @BindView(R.id.userName) EditText userName;
    @BindView(R.id.userEmail) EditText userEmail;
    @BindView(R.id.userPassword) EditText userPassWord;
    @BindView(R.id.signUpButton) Button signUpButton;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference fireDb;
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

                                fireDb = FirebaseDatabase.getInstance().getReference();
                                DatabaseReference myRef = fireDb.child(MMConstant.USERS).child(userId);
                                Map<String, String> userInfo = new HashMap<>();
                                userInfo.put(NAME, userPersonalName);
                                userInfo.put(EMAIL,userEmailAddress);
                                myRef.setValue(userInfo);

                                fireDb.child(MMConstant.ID_EMAIL_MAP).child(userId).setValue(userEmailAddress);

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
                                addAliceAsFirstFriend(userId);
                                Intent intent = new Intent(SignUpActivity.this, NewMainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
        }
    }

    private void addAliceAsFirstFriend(String myUserId) {
        DatabaseReference friend = fireDb.child(MMConstant.USERS)
                .child(MUNDO_ID).child(FRIENDS).child(myUserId);
        String chatId = friend.push().getKey();
        //update updateAliceFriendList
        Map<String, Object> myInfo = new HashMap<>();
        myInfo.put(USER_ID, myUserId);
        myInfo.put(NAME, userPersonalName);
        myInfo.put(CHAT_ID, chatId);
        myInfo.put(ONLINE, true);
        fireDb.child(MMConstant.USERS).child(MUNDO_ID).child(FRIENDS).child(myUserId).setValue(myInfo);

        //update new user's friendList
        Map<String, Object> aliceInfo = new HashMap<>();
        aliceInfo.put(USER_ID, MUNDO_ID);
        aliceInfo.put(NAME, MUNDO_NAME);
        aliceInfo.put(CHAT_ID, chatId);
        aliceInfo.put(IMAGE_URL, MUNDO_IMAGE);
        aliceInfo.put(ONLINE, true);
        fireDb.child(MMConstant.USERS).child(myUserId).child(FRIENDS).child(MUNDO_ID).setValue(aliceInfo);



        DatabaseReference friendDb = fireDb.child(MESSAGES).child(chatId);
        String newMsgId = friendDb.push().getKey();
        Map<String, String> msgInfo = new HashMap<>();
        msgInfo.put(USER_ID, MUNDO_ID);
        msgInfo.put(NAME, MUNDO_NAME);
        msgInfo.put(CONTENT, "Hello! I am your friend Chatmundo, feel free to chat with me : ).");
        msgInfo.put(TIMESTAMP, Time.getCurTimeAsString());
        friendDb.child(newMsgId).setValue(msgInfo);


        //create first Msg
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
