package com.junyu.moneymanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

import static com.junyu.moneymanager.MMConstant.GROUP_ID;
import static com.junyu.moneymanager.MMConstant.NAME;
import static com.junyu.moneymanager.MMConstant.PREFERENCE_NAME;

/**
 * Created by Junyu on 10/9/2016.
 */

public class MainPage extends AppCompatActivity {
    @BindView(R.id.userName) TextView userPersonalName;
    @BindView(R.id.groupSpinner) Spinner groupSpinner;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.submitGroupId) Button submitGroupId;

    private DatabaseReference fireDb;
    private Map<String, String> groupIdNames = new HashMap<>();
    private List<String> groupNames = new ArrayList<>();
    SharedPreferences sharedPreferences;

    @OnClick(R.id.submitGroupId)
    public void goToGroupPage() {
        String gName = groupSpinner.getSelectedItem().toString();
        String groupId = groupIdNames.get(gName);
        Intent intent = new Intent(MainPage.this, GroupPageActivity.class);
        intent.putExtra(GROUP_ID, groupId);
        startActivity(intent);

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        ButterKnife.bind(this);
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, R.layout.group_spinner_item, groupNames);
        groupSpinner.setAdapter(spinnerArrayAdapter);

        sharedPreferences = getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        fireDb = FirebaseDatabase.getInstance().getReference();

        DatabaseReference myRef = fireDb.child(MMConstant.USERS).child(MMUserPreference.getUserId(this));//.child(GROUP_ID)
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    if (child.getKey().equals(GROUP_ID)) {
                        for (DataSnapshot groupChild : child.getChildren()) {
                            groupIdNames.put(groupChild.getValue().toString(),groupChild.getKey());
                            groupNames.add(groupChild.getValue().toString());
                        }
                        Gson gson = new Gson();
                        String result = gson.toJson(groupIdNames);
                        editor.putString(MMConstant.GROUP_ID, result);
                        editor.commit();
                    } else if (child.getKey().equals(NAME)) {
                        String name = child.getValue().toString();
                        userPersonalName.setText(name);
                        editor.putString(MMConstant.NAME, name);
                        editor.commit();
                    }

                }

                spinnerArrayAdapter.notifyDataSetChanged();
                if(!groupNames.isEmpty()){
                    groupSpinner.setSelection(0);
                }
                progressBar.setVisibility(View.GONE);

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
}
