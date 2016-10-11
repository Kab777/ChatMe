package com.junyu.moneymanager;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * Created by Junyu on 10/10/2016.
 */

public class AddNewMemberDialog extends Dialog {
    @BindView(R.id.inviteFriend) Button inviteFriend;
    @BindView(R.id.friendEmail) EditText friendEmail;

    private Context context;
    private DatabaseReference fireDb;
    private String groupId = "";
    private String groupName = "";

    @OnClick(R.id.inviteFriend)
    public void submitAdding() {
        final String fEmail = friendEmail.getText().toString().trim();
        if (fEmail.isEmpty()) {
            Toast.makeText(context, "Email can't be empty",
                    Toast.LENGTH_SHORT).show();
        } else {
            //check if there is user
            Query myRef = fireDb.child(MMConstant.USERS).orderByChild(MMConstant.EMAIL).equalTo(fEmail);//.child(GROUP_ID)
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() == null) {
                        Toast.makeText(context, "User doesn't exist",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            String userId = child.getKey();
                            for (DataSnapshot userChild : child.getChildren()) {
                                if (userChild.getKey().toString().equals(MMConstant.GROUP_ID)) {
                                    for (DataSnapshot groupChild : userChild.getChildren()) {
                                        if (groupChild.getKey().toString().equals(groupId)) {
                                            Toast.makeText(context, "User already exists in your group",
                                                    Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                    }
                                }
                            }

                            addMemberWithUserId(userId, child.child(MMConstant.NAME).getValue().toString());
                        }

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void addMemberWithUserId(String userId, String userName) {
        fireDb.child(MMConstant.USERS).child(userId).child(MMConstant.GROUP_ID).child(groupId).setValue(groupName);
        fireDb.child(MMConstant.Groups).child(groupId).child(MMConstant.MEMBERS).child(userName).setValue(true);
        Toast.makeText(context, "new member added",
                Toast.LENGTH_SHORT).show();
        dismiss();
    }

    public AddNewMemberDialog(Context context, String groupId, String groupName) {
        super(context);
        this.context = context;
        this.groupId = groupId;
        this.groupName = groupName;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add_new_member);
        ButterKnife.bind(this);
        fireDb = FirebaseDatabase.getInstance().getReference();
    }


}
