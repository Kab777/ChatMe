package com.junyu.moneymanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

import static android.R.attr.name;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;
import static com.junyu.moneymanager.MMConstant.BALANCE;
import static com.junyu.moneymanager.MMConstant.GROUPNAME;
import static com.junyu.moneymanager.MMConstant.GROUPNAMEC;
import static com.junyu.moneymanager.MMConstant.GROUP_ID;
import static com.junyu.moneymanager.MMConstant.MEMBERS;
import static com.junyu.moneymanager.MMConstant.NAME;
import static com.junyu.moneymanager.MMConstant.USERS;

/**
 * Created by Junyu on 10/10/2016.
 */

public class GroupPageActivity extends AppCompatActivity {
    @BindView(R.id.goToTranH) Button gotoTranH;
    @BindView(R.id.groupName) TextView groupName;
    @BindView(R.id.groupBalance) TextView groupBalance;
    @BindView(R.id.groupMember) ListView groupMembers;
    @BindView(R.id.progressBar) ProgressBar progressBar;

    private DatabaseReference fireDb;
    private String groupId = "";
    private ArrayList<String> groupMems = new ArrayList<>();




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
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    if (child.getKey().equals(MEMBERS)) {
                        for (DataSnapshot groupChild : child.getChildren()) {
                            groupMems.add(groupChild.getKey());
                        }
                        itemsAdapter.notifyDataSetChanged();
                    } else if (child.getKey().equals(GROUPNAME)) {
                        String name = child.getValue().toString();
                        groupName.setText(name);
                    } else if (child.getKey().equals(BALANCE)) {
                        String blance = child.getValue().toString();
                        groupBalance.setText(blance);
                    }

                }

                progressBar.setVisibility(View.GONE);

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }
}
