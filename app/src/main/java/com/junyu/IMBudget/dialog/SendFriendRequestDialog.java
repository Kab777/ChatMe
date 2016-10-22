package com.junyu.IMBudget.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.junyu.IMBudget.MMConstant;
import com.junyu.IMBudget.MMUserPreference;
import com.junyu.IMBudget.R;
import com.junyu.IMBudget.activity.NewMainActivity;

import java.util.HashMap;
import java.util.Map;

import static com.junyu.IMBudget.MMConstant.ACCEPTED;
import static com.junyu.IMBudget.MMConstant.CHAT_ID;
import static com.junyu.IMBudget.MMConstant.IMAGE_URL;
import static com.junyu.IMBudget.MMConstant.NAME;
import static com.junyu.IMBudget.MMConstant.REQUEST_MSG;

/**
 * Created by Junyu on 10/17/2016.
 */

public class SendFriendRequestDialog {
    private Context context;
    private AlertDialog alertDialog;
    private DatabaseReference fireDb;
    private String userId;

    public SendFriendRequestDialog(Context context) {
        this.context = context;
        fireDb = FirebaseDatabase.getInstance().getReference();
        userId = MMUserPreference.getUserId(context);
        initDialog();
    }

    private void initDialog() {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(context);
        View mView = layoutInflaterAndroid.inflate(R.layout.dialog_send_friend_request, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(context);
        alertDialogBuilderUserInput.setView(mView);

        final EditText userInputDialogEditText = (EditText) mView.findViewById(R.id.userInputDialog);
        final EditText userMessage = (EditText) mView.findViewById(R.id.userMessage);
        userMessage.setText("I'm " + MMUserPreference.getUserName(context));
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        String email = userInputDialogEditText.getText().toString().trim();
                        if (email.isEmpty()) {
                            Toast.makeText(context, "Please Enter valid email address",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            sendFriendRequest(email,
                                    userMessage.getText().toString().trim());
                        }

                    }
                })

                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        alertDialog = alertDialogBuilderUserInput.create();
    }

    private void sendFriendRequest(String friendEmail, final String friendMessage) {
        Query friendId = fireDb.child(MMConstant.ID_EMAIL_MAP).orderByValue().limitToFirst(1).equalTo(friendEmail);
        friendId.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    Toast.makeText(context, "User doesn't exist",
                            Toast.LENGTH_SHORT).show();
                } else {
                    // add friend to each other

                    Toast.makeText(context, "request has sent!",
                            Toast.LENGTH_SHORT).show();
                    Map<String, String> map = (Map) dataSnapshot.getValue();
                    String id = map.keySet().toArray()[0].toString();
                    validateAndRequest(id, friendMessage);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //Query myRef = fireDb.child(MMConstant.USERS).child(userId).orderByChild(MMConstant.EMAIL).equalTo(fEmail)
    }

    private void validateAndRequest(String friendId, String friendMessage) {

        DatabaseReference friend = fireDb.child(MMConstant.USERS)
                .child(friendId).child(MMConstant.FRIEND_REQUESTS).child(userId);
        String chatId = friend.push().getKey();
        Map<String, Object> myInfo = new HashMap<>();
        myInfo.put(CHAT_ID, chatId);
        myInfo.put(ACCEPTED, false);
        myInfo.put(NAME, MMUserPreference.getUserName(context));
        myInfo.put(IMAGE_URL, MMUserPreference.getUserImg(context));
        myInfo.put(REQUEST_MSG, friendMessage);
        friend.setValue(myInfo);

    }

    public void show() {
        alertDialog.show();
    }


}
