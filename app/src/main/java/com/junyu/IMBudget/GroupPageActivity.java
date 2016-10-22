package com.junyu.IMBudget;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.junyu.IMBudget.MMConstant.BALANCE;
import static com.junyu.IMBudget.MMConstant.GROUPNAME;
import static com.junyu.IMBudget.MMConstant.GROUP_ID;
import static com.junyu.IMBudget.MMConstant.MEMBERS;

/**
 * Created by Junyu on 10/10/2016.
 */

public class GroupPageActivity extends AppCompatActivity {
    @BindView(R.id.goToTranH) Button gotoTranH;
    @BindView(R.id.addFriend) Button addFriend;
    @BindView(R.id.groupName) TextView groupName;
    @BindView(R.id.groupBalance) TextView groupBalance;
    @BindView(R.id.groupMember) ListView groupMembers;
    @BindView(R.id.progressBar) ProgressBar progressBar;


    @OnClick(R.id.addFriend)
    public void addNewMember() {
        addNewMemberDialog = new AddNewMemberDialog(this, groupId, grpName);
        addNewMemberDialog.show();
    }

    private DatabaseReference fireDb;
    private String groupId = "";
    private String grpName = "";
    private ArrayList<String> groupMems = new ArrayList<>();
    private AddNewMemberDialog addNewMemberDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_home_page);
        ButterKnife.bind(this);


        Intent intent = getIntent();
        groupId = intent.getStringExtra(GROUP_ID);
        final ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, groupMems);
        groupMembers.setAdapter(itemsAdapter);
        fireDb = FirebaseDatabase.getInstance().getReference();

        DatabaseReference myRef = fireDb.child(MMConstant.Groups).child(groupId);//.child(GROUP_ID)
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                groupMems.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    if (child.getKey().equals(MEMBERS)) {
                        for (DataSnapshot groupChild : child.getChildren()) {
                            groupMems.add(groupChild.getKey());
                        }
                        itemsAdapter.notifyDataSetChanged();
                    } else if (child.getKey().equals(GROUPNAME)) {
                        grpName = child.getValue().toString();
                        groupName.setText(grpName);
                    } else if (child.getKey().equals(BALANCE)) {
                        String blance = child.getValue().toString();
                        groupBalance.setText("Balance : " + blance);
                    }

                }

                progressBar.setVisibility(View.GONE);

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        gotoTranH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupPageActivity.this, TransactionActivity.class);
                intent.putExtra(GROUP_ID, groupId);
                startActivity(intent);
            }
        });

    }
}
